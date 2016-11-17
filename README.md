killbill-dwolla-plugin
======================

Plugin to use [Dwolla](https://www.dwolla.com/) as a gateway. It supports both Dwolla Direct (co-branded product, easier to setup), as well as Dwolla White Label (for deeper integrations). Funding sources can be verified through both IAV (Instant Account Verification) and Micro-deposit verifications. You can even configure it to listen to webhooks, to get real-time notifications of the deposits status.

Release builds are available on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.kill-bill.billing.plugin.java%22%20AND%20a%3A%22dwolla-plugin%22) with coordinates `org.kill-bill.billing.plugin.java:dwolla-plugin`.

Kill Bill compatibility
-----------------------

| Plugin version | Kill Bill version |
| -------------: | ----------------: |
| 0.1.y          | 0.18.z            |

Requirements
------------

The plugin needs a database. The latest version of the schema can be found [here](https://github.com/killbill/killbill-dwolla-plugin/blob/master/src/main/resources/ddl.sql).

Merchants need to create a Dwolla [application](https://developers.dwolla.com/guides/sandbox-setup/02-create-application.html).
The first time a token pair needs to be created from Dwolla Application page, and save into plugin database. The plugin offers two endpoints to creante and update this token pair.
```
// TO CREATE A NEW TOKEN PAIR
curl -v \
     -X POST \
     -u admin:password \
     -H 'X-Killbill-ApiKey: bob' \
     -H 'X-Killbill-ApiSecret: lazar' \
     -H 'X-Killbill-CreatedBy: admin' \
     -H 'Content-Type: text/plain' \
     -d '{
           "accessToken": "<ACCESS-TOKEN>",
           "refreshToken": "<REFRESH-TOKEN>"
         }' \
     http://127.0.0.1:8080/plugins/killbill-dwolla

// TO UPDATE AN EXISTING TOKEN PAIR
curl -v \
     -X PUT \
     -u admin:password \
     -H 'X-Killbill-ApiKey: bob' \
     -H 'X-Killbill-ApiSecret: lazar' \
     -H 'X-Killbill-CreatedBy: admin' \
     -H 'Content-Type: text/plain' \
     -d '{
           "accessToken": "<ACCESS-TOKEN>",
           "refreshToken": "<REFRESH-TOKEN>"
         }' \
     http://127.0.0.1:8080/plugins/killbill-dwolla
    
```

A *[Webhook Subscription](https://docsv2.dwolla.com/#webhook-subscriptions)* is needed to receive Dwolla notifications.

Build
-----

To build the plugin locally, see https://github.com/Dwolla/dwolla-swagger-java/blob/master/README.md.

Configuration
-------------

The following properties are required:

* `org.killbill.billing.plugin.dwolla.baseUrl` : REST Payment service url (i.e. `https://api-uat.dwolla.com/` or `https://api.dwolla.com/`)
* `org.killbill.billing.plugin.dwolla.baseOAuthUrl` : REST Auth service url (i.e. `https://uat.dwolla.com/oauth/v2` or `https://dwolla.com/oauth/v2`)
* `org.killbill.billing.plugin.dwolla.scopes` : The application scopes (i.e. `Send|AccountInfoFull|Funding`)
* `org.killbill.billing.plugin.dwolla.clientId` : Your merchant application key.
* `org.killbill.billing.plugin.dwolla.clientSecret` : Your merchant application secret.
* `org.killbill.billing.plugin.dwolla.accountId` : Your merchant account id.


These properties can be specified globally via System Properties or on a per tenant basis:

```
curl -v \
     -X POST \
     -u admin:password \
     -H 'X-Killbill-ApiKey: bob' \
     -H 'X-Killbill-ApiSecret: lazar' \
     -H 'X-Killbill-CreatedBy: admin' \
     -H 'Content-Type: text/plain' \
     -d 'org.killbill.billing.plugin.dwolla.baseUrl=UUU
     org.killbill.billing.plugin.dwolla.baseOAuthUrl=VVV
     org.killbill.billing.plugin.dwolla.scopes=WWW
     org.killbill.billing.plugin.dwolla.clientId=XXX'
     org.killbill.billing.plugin.dwolla.clientSecret=YYY'
     org.killbill.billing.plugin.dwolla.accountId=ZZZ' \
     http://127.0.0.1:8080/1.0/kb/tenants/uploadPluginConfig/killbill-dwolla
```

### Kill Bill


Usage
-----

A full end-to-end integration demo is also available [here](https://github.com/killbill/killbill-dwolla-demo).

### Bank account

Add a payment method:

```
curl -v \
     -u admin:password \
     -H "X-Killbill-ApiKey: bob" \
     -H "X-Killbill-ApiSecret: lazar" \
     -H "Content-Type: application/json" \
     -H "X-Killbill-CreatedBy: demo" \
     -X POST \
     --data-binary '{
       "pluginName": "killbill-dwolla",
       "pluginInfo": {
         "properties": [
           {
             "key": "fundingSource",
             "value": "9ece9660-aa34-41eb-80d7-0125d53b45e8"
           },
           {
             "key": "customerId",
             "value": "ca32853c-48fa-40be-ae75-77b37504581b"
           }
         ]
       }
     }' \
     "http://127.0.0.1:8080/1.0/kb/accounts/<ACCOUNT_ID>/paymentMethods?isDefault=true"
```

Notes:
* Make sure to replace *ACCOUNT_ID* with the id of the Kill Bill account
* Details for working payment methods are available here: https://docsv2.dwolla.com/#funding-sources

To trigger a payment:

```
curl -v \
     -u admin:password \
     -H "X-Killbill-ApiKey: bob" \
     -H "X-Killbill-ApiSecret: lazar" \
     -H "Content-Type: application/json" \
     -H "X-Killbill-CreatedBy: demo" \
     -X POST \
     --data-binary '{"transactionType":"PURCHASE","amount":"5","currency":"EUR","transactionExternalKey":"INV-'$(uuidgen)'-PURCHASE"}' \
    "http://127.0.0.1:8080/1.0/kb/accounts/<ACCOUNT_ID>/payments"
```

Notes:
* Make sure to replace *ACCOUNT_ID* with the id of the Kill Bill account

At this point, the payment will be in *PENDING* state, until we receive a notification from Dwolla. You can verify the state of the transaction by listing the payments:

```
curl -v \
     -u admin:password \
     -H "X-Killbill-ApiKey: bob" \
     -H "X-Killbill-ApiSecret: lazar" \
     -H "Content-Type: application/json" \
     -H "X-Killbill-CreatedBy: demo" \
    "http://127.0.0.1:8080/1.0/kb/accounts/<ACCOUNT_ID>/payments?withPluginInfo=true"
```

You can simulate a notification from Dwolla as follows:

```
curl -v \
     -u admin:password \
     -H "X-Killbill-ApiKey: bob" \
     -H "X-Killbill-ApiSecret: lazar" \
     -H "Content-Type: application/json" \
     -H "X-Killbill-CreatedBy: demo" \
     -X POST \
     --data-binary '
     {
      "id": "80d8ff7d-7e5a-4975-ade8-9e97306d6c16",
      "topic": "customer_transfer_completed",
      "_links": {
        "self": {
          "href": "https://api-uat.dwolla.com/events/80d8ff7d-7e5a-4975-ade8-9e97306d6c16"
        },
        "account": {
          "href": "https://api-uat.dwolla.com/accounts/b3153e29-b25a-4ccd-9c1a-5b05b606e50c"
        },
        "resource": {
          "href": "https://api-uat.dwolla.com/transfers/7c8e7484-2184-e611-80e8-0aa34a9b2388"
        },
        "customer": {
          "href": "https://api-uat.dwolla.com/customers/b3153e29-b25a-4ccd-9c1a-5b05b606e50c"
        }
      }
    }' \
    "http://127.0.0.1:8080/1.0/kb/paymentGateways/notification/killbill-dwolla"
```

Notes:
* Make sure to replace *resource.href* with the Dwolla transfer id reference of your payment (see the *dwolla_responses* table)
* If *topic* is `customer_transfer_completed`, the payment transaction state will be *SUCCESS* and the payment state *PROCESSED*
* If *topic* is `customer_transfer_failed` or `customer_transfer_canceled`, the payment transaction state will be *PAYMENT_FAILURE* and the payment state *ERROR*
