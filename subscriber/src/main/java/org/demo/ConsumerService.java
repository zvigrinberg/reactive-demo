package org.demo;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.demo.model.Account;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ConsumerService {

    Logger logger = Logger.getLogger(this.getClass().getName());

    @Incoming("accounts")
    public Uni<Void> consumer(Account account) {
        logger.info("Received from broker [account holder name]>>>>>>>>" + account.accountHolderName);

        account.status = "APPROVED";
        account.isActive = true;

        return Account.update(account); // If the update was not successful, Message will not be ack as processed.
    }
}
