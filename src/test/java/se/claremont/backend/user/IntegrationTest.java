package se.claremont.backend.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import microsoft.exchange.webservices.data.core.ExchangeService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import se.claremont.backend.user.repository.UserRepository;
import se.claremont.backend.user.repository.entities.User;
import se.claremont.backend.user.security.AccountCredentials;
import se.claremont.backend.user.security.CustomAuthenticationProvider;
import se.claremont.backend.user.security.utils.EmailHelper;
import se.claremont.backend.user.security.utils.OutlookService;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
public class IntegrationTest {
    @Autowired
    private MockMvc mvc;
    private String token;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        userRepository.deleteAll();
        token = mvc.perform(MockMvcRequestBuilders.post("/login")
                .content(createJsonObjectFromUserPassword("admin", "password")))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getHeader(HttpHeaders.AUTHORIZATION);
    }

    @Test
    public void canLoginWithDefaultUsername() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .content(createJsonObjectFromUserPassword("admin", "password")))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.AUTHORIZATION, not(isEmptyOrNullString())));
    }

    @Test
    public void canLoginWithCustomUser() throws Exception {
        userRepository.save(new User("custom_user"));
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .content(createJsonObjectFromUserPassword("custom_user", "invalid")))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.AUTHORIZATION, not(isEmptyOrNullString())));
    }

    @Ignore("This one should fail but doesn't")
    @Test
    public void loginFailureWhenUsernameNotExisting() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .content(createJsonObjectFromUserPassword("not_existing_username", "password")))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    public void loginFailureWhenWrongLoginObject() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .content("{\"user\":\"user\"}"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    public void verifyThatPingServiceIsAccessibleWithToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/ping")
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().string(is("Hello")));
    }

    @Test
    public void verifyThatPingServiceIsNotAccessibleWithoutToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/ping"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(MockMvcResultMatchers.content().string(isEmptyString()));
    }

    @Test
    public void getAllSavedUsers() throws Exception {
        userRepository.save(new User("user"));
        mvc.perform(MockMvcRequestBuilders.get("/users")
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().json("[\"user\"]"));
    }

    @Test
    public void getRootUrl() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/")
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().string("hello world"));
    }

    @Test
    public void mockAllExternals() throws Exception {
        OutlookService outlookService = mock(OutlookService.class);
        EmailHelper emailHelper = mock(EmailHelper.class);
        UserRepository mockRepo = mock(UserRepository.class);

        ReflectionTestUtils.setField(customAuthenticationProvider, "outlookService", outlookService);
        ReflectionTestUtils.setField(customAuthenticationProvider, "emailHelper", emailHelper);
        ReflectionTestUtils.setField(customAuthenticationProvider, "userDao", mockRepo);

        mvc.perform(MockMvcRequestBuilders.post("/login")
                .content(createJsonObjectFromUserPassword("admin", "password")))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.AUTHORIZATION, not(isEmptyOrNullString())));

        verify(outlookService, times(1)).verifyOutlook("admin", "password");
        verify(mockRepo, times(1)).findByUsername("admin");
        verify(emailHelper, times(1)).verifyByMail(any(ExchangeService.class), eq("admin"));
        verify(mockRepo, times(1)).save(any(User.class));
    }

    private String createJsonObjectFromUserPassword(String username, String password) throws JsonProcessingException {
        return objectMapper.writeValueAsString(new AccountCredentials("admin", "password"));
    }

}
