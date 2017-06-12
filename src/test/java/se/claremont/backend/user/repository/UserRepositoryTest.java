package se.claremont.backend.user.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import se.claremont.backend.user.repository.entities.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Before
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void canSaveWithAllArgs() {
        assertNotNull(userRepository.save(new User("username")));
    }

    @Test
    public void canSaveWithNoArgs() {
        User user = new User();
        user.setUsername("username2");
        assertNotNull(userRepository.save(user));
    }

    @Test
    public void canFindByUsername() {
        userRepository.save(new User("username3"));
        assertEquals("username3", userRepository.findByUsername("username3").getUsername());
    }

    @Test
    public void canDeleteByUsername() {
        userRepository.save(new User("username"));
        userRepository.deleteByUsername("username");
        assertNull(userRepository.findByUsername("username"));
    }
}
