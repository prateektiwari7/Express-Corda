package ServerService

import io.bluebank.braid.corda.BraidConfig
import io.vertx.core.http.HttpServerOptions
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken
import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.workflows.accountService
import com.r3.corda.lib.ci.workflows.RequestKeyForAccount
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByService
import net.corda.core.node.ServiceHub
import net.corda.core.utilities.getOrThrow

@CordaService
class BootstrapBraidService(val serviceHub: AppServiceHub) : SingletonSerializeAsToken() {
    init {
        BraidConfig()
                // Include a flow on the Braid server.
                .withFlow(WhoAmIFlow::class.java)
               // .withFlow(CreateAccount::class.java)
                // Include a service on the Braid server.
                .withService("myService", BraidService(serviceHub))
                // The port the Braid server listens on.

                .withPort(8080)
                // Using http instead of https.
                .withHttpServerOptions(HttpServerOptions().setSsl(false))
                // Start the Braid server.
                .bootstrapBraid(serviceHub)
    }
}

class BraidService(val serviceHub: ServiceHub) {
    fun whoAmI() : String {
        return serviceHub.myInfo.legalIdentities.first().name.organisation
    }


}

@InitiatingFlow
@StartableByService
class WhoAmIFlow : FlowLogic<String>() {
    @Suspendable
    override fun call() : String {
        return ourIdentity.name.organisation
    }
}

@InitiatingFlow
@StartableByService
class CreateAccount(val acctName: String) : FlowLogic<String>() {

    @Suspendable
    override fun call(): String {

        val newAccount = accountService.createAccount(name = acctName).toCompletableFuture().getOrThrow()
        val acct = newAccount.state.data

        val party = serviceHub.myInfo.identityFromX500Name(ourIdentity.name)

        var key =  subFlow(RequestKeyForAccount(party,newAccount.state.data.identifier.id))

        return ""+acct.name + " team's account was created. UUID is : " + acct.identifier+ "Private Key of your account as"+key

    }

}


