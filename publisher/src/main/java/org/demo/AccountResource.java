package org.demo;

import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.demo.model.Account;
import org.jboss.logging.Logger;

import java.net.URI;
import java.time.LocalDateTime;

@Path("/account")
public class AccountResource {

    Logger logger = Logger.getLogger(AccountResource.class.getName());
    @Inject
    BrokerService brokerService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Account> getAccounts() {
        return Account.streamAll(Sort.ascending("accountHolderName"));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> createAccount(Account account) {
        account.createdAt = LocalDateTime.now();
        return account.<Account>persist()
                .map(a -> Response.created(URI.create("/account/" + a.id.toString())).entity(a).build());
    }

    @Path("/activate")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> activateAccount(Account account) {
        account.status = "APPROVAL-PENDING";

        Uni<String> messageUni = brokerService.publisher(account);
        Uni<Account> accountUni = account.update();

        return Uni.combine().all().unis(messageUni, accountUni)
                .asTuple().map(objects -> {
                    String messageSentToBroker = objects.getItem1();
                    logger.info("Message acknowledged: " + messageSentToBroker);
                    Account updatedAccount = objects.getItem2();
                    return Response.ok(updatedAccount).build();
                });
    }
}
