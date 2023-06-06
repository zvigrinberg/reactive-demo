package org.demo;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.demo.model.Account;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

@ApplicationScoped
public class BrokerService {

    Logger logger = Logger.getLogger(this.getClass().getName());

    @Inject
    @Channel("accounts")
    MutinyEmitter<Account> mutinyEmitter;

    public Uni<String> publisher(Account account) {
        logger.info("Sending to broker [account holder name]>>>>>>>>" + account.accountHolderName);

        return mutinyEmitter.send(account)
                .map(x -> "ok")
                .onFailure().retry().atMost(3)
                .onFailure().recoverWithItem("NOT-OK");
    }
}
