package com.template.flows

import net.corda.core.flows.*
import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.workflows.accountService
import com.r3.corda.lib.ci.workflows.RequestKeyForAccount
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.utilities.getOrThrow



@StartableByRPC
@StartableByService
@InitiatingFlow
class CreateNewAccount(private val acctName:String, val party : Party) : FlowLogic<String>() {

    @Suspendable
    override fun call(): String {
        //Create a new account
        val newAccount = accountService.createAccount(name = acctName).toCompletableFuture().getOrThrow()
        val acct = newAccount.state.data

        var key =  subFlow(RequestKeyForAccount(party,newAccount.state.data.identifier.id))




        return ""+acct.name + " team's account was created. UUID is : " + acct.identifier+ "Private Key of your account as"+key
    }
}

@InitiatedBy(CreateNewAccount::class)
class CreateNewAccount_Responder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Responder flow logic goes here.
    }
}