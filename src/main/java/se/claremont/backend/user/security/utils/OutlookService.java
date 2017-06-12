package se.claremont.backend.user.security.utils;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class OutlookService {
    private static final String OUTLOOK_API = "https://outlook.office365.com/EWS/Exchange.asmx";

    public ExchangeService verifyOutlook(String username, String password) {
        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        service.setCredentials(new WebCredentials(username, password));

        try {
            service.setUrl(new URI(OUTLOOK_API));
        } catch (URISyntaxException e) {
            throw new AuthenticationServiceException("Invalid outlook endpoint", e);
        }

        return service;
    }
}
