package se.claremont.backend.user.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import se.claremont.backend.user.repository.UserRepository;
import se.claremont.backend.user.repository.entities.User;
import se.claremont.backend.user.security.utils.EmailHelper;
import se.claremont.backend.user.security.utils.OutlookService;

// TODO fix real integration to Outlook API

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	@Autowired
	private UserRepository userDao;
	@Autowired
	private OutlookService outlookService;
	@Autowired
	private EmailHelper emailHelper;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = (String)authentication.getCredentials();

		// set up Exchange service
		ExchangeService service = outlookService.verifyOutlook(username, password);
		
		try {
			/*
			 * 1. Check if the username exists in database. If so, the user has successfully logged in before and has
			 * received a confirmation email.
			 */
			if(userDao.findByUsername(username) != null){
				/* try binding a well known folder to verify username and password without sending a mail to the user.
				 * If binding fails exception will be catched and BadCredentialsException will be thrown.
				 */
				Folder.bind(service, WellKnownFolderName.Inbox);
				return authentication;
			}
		} catch(Exception e){
			throw new BadCredentialsException("login failed", e);
		}


		/*
		 * 2. If the user logins for the first time: Try to send a mail to the
		 * user for confirmation that login was successful.
		 *
		 * <!-- -------------START "login first time"------------ -->
		 */
		emailHelper.verifyByMail(service, username);
		// <!-- -------------END "login first time"------------ -->


		/*
		 * 3. If the user has successfully sent a mail, the username will be
		 * stored in a database. This will enable the backend to check against
		 * the database if there has been a successful login at a previous
		 * login attempt.
		 */
		userDao.save(new User(username));
		
		return new UsernamePasswordAuthenticationToken(username, password);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
