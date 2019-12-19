"use strict";

const express = require('express')
const Proxy = require('braid-client').Proxy;

var bodyParser = require("body-parser");



const app = express()

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
var urlencodedParser = bodyParser.urlencoded({ extended: false })


// Connects to Braid running on the node.
let braid = new Proxy({
  url: "http://localhost:8080/api/"
}, onOpen, onClose, onError, { strictSSL: false });

function onOpen() { console.log('Connected to node.'); }
function onClose() { console.log('Disconnected from node.'); }
function onError(err) { console.error(err); process.exit(); }

// Uses Braid to call the WhoAmI flow on the node, and handles the response
// using callbacks.
app.get('/whoami-flow-callback', (req, res) => {
    braid.flows.whoAmIFlow(
        result => res.send("Hey, you're speaking to " + result + "!"),
        err => res.status(500).send(err));
});

// Uses Braid to call the WhoAmI flow on the node, and handles the response
// using promises.
app.get('/whoami-flow-promise', (req, res) => {
    braid.flows.whoAmIFlow()
    .then(result => res.send("Hey, you're speaking to " + result + "!"))
    .catch(err => res.status(500).send(err));
});

// Uses Braid to call the BraidService on the node, and handles the response
// using callbacks.
app.get('/whoami-service', (req, res) => {
    braid.myService.whoAmI(
        result => res.send("Hey, you're speaking to " + result + "!"),
        err => res.status(500).send(err));
});

app.post('/CreateAccount',urlencodedParser,function(request,response){

/*   response = {
       first_name:request.body.name
   };
   console.log(response);*/

    var query1=request.body.name;
   console.log("$$$$$$$$$$$$$"+query1);
   braid.flows.CreateAccount(
   result => res.send(query1), err => res.status(500).send(err))



});



app.listen(3000, () => console.log('Server listening on port 3000!'))