package se.claremont.backend.user.security.utils;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class EmailHelper {

    public void verifyByMail(ExchangeService service, String username) {
        try {
            EmailMessage msg = new EmailMessage(service);
            msg.setSubject("Välkommen till Clarty!");
            msg.setBody(MessageBody.getMessageBodyFromText("Du är nu registrerad på Clarty. Detta mail går inte att svara på."));
            msg.getToRecipients().add(username);
            msg.send();
        } catch (Exception e) {
            throw new BadCredentialsException("Login failed", e);
        }

    }
}
