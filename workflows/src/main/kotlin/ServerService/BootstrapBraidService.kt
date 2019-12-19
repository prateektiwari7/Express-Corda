package ServerService

import io.bluebank.braid.corda.BraidConfig
import io.vertx.core.http.HttpServerOptions
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken
import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByService
import net.corda.core.node.ServiceHub

@CordaService
class BootstrapBraidService(val serviceHub: AppServiceHub) : SingletonSerializeAsToken() {
    init {
        BraidConfig()
                // Include a flow on the Braid server.
                .withFlow(WhoAmIFlow::class.java)
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