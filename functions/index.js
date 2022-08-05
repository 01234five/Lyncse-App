/* eslint-disable no-unused-vars */
/* eslint arrow-parens: "off", quotes: "off" */
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const {Client} = require('@elastic/elasticsearch');

admin.initializeApp(functions.config().firebase);
const env = functions.config();
const auth = {
  username: env.elasticsearch.username,
  password: env.elasticsearch.password,
};

const client = new Client({
  node: env.elasticsearch.url,
  auth: auth,
});

const stripe =
require('stripe')(env.stripe.secret_key);
const endpointSecret = env.stripe.wh_secret;
// exports.createPost = functions.database.ref('/Gigs/Virtual/{gigId}')
//     .onWrite( async (snap, context) => {
//       const beforeData = snap.before.val(); // data before the write
//       const afterData = snap.after.val(); // data after the write
//       console.log("logging snap create value before" + beforeData);
//       console.log("logging snap create value after" + afterData);
//       console.log(context.params.gigId);
//       await client.index({
//         index: 'virtualgigs',
//         id: context.params.gigId,
//         body: afterData,
//       });
//     });
exports.stripeWebhook = functions.https.onRequest(async (request, response) =>{
  let event = request.body;
  // Only verify the event if you have an endpoint secret defined.
  // Otherwise use the basic event deserialized with JSON.parse
  if (endpointSecret) {
    // Get the signature sent by Stripe
    const signature = request.headers['stripe-signature'];
    try {
      event = stripe.webhooks.constructEvent(
          request.rawBody,
          signature,
          endpointSecret,
      );
    } catch (err) {
      console.log(`⚠️  Webhook signature verification failed.`, err.message);
      return response.sendStatus(400);
    }
  }

  // Handle the event
  switch (event.type) {
    case 'payment_intent.succeeded':
      // eslint-disable-next-line no-case-declarations
      const paymentIntent = event.data.object;
      console.log(`PaymentIntent for ${paymentIntent.amount} was successful!`);
      // Then define and call a method to handle the successful payment intent.
      // handlePaymentIntentSucceeded(paymentIntent);
      if (paymentIntent.metadata.order_type=="Request") {
        admin.database().ref("/Requests/"+
        paymentIntent.description+"/Stripe/Info/PaymentIntent/id").
            set(paymentIntent.id);
        await admin.database().ref('/Requests/'+
      paymentIntent.description).once("value")
            .then(function(snapshot) {
              if (snapshot.hasChild("paymentIntentCompletedNumber")) {
                const resChargesAmount =
                snapshot.child("paymentIntentCompletedNumber").val()+1;
                const textChargesAmount = resChargesAmount.toString();
                console.log("paymentIntentCompletedNumber,creating extra");
                console.log("CompletedNumber " +textChargesAmount);
                admin.database().ref(
                    "/Stripe/RequestTransactions/"+
        paymentIntent.description+"/PaymentIntent/"+textChargesAmount).
                    set(paymentIntent);
                admin.database().ref(
                    "/Requests/"+paymentIntent.description+
                  "/paymentIntentCompletedNumber").
                    set(textChargesAmount);
                admin.database().ref("/Requests/"+
                  paymentIntent.description+"/Stripe/PaymentIntent/").
                    set("success");
              } else {
                admin.database().ref(
                    "/Stripe/RequestTransactions/"+
          paymentIntent.description+"/PaymentIntent/0").set(
                    paymentIntent);
                admin.database().ref(
                    "/Requests/"+paymentIntent.description+
                  "/paymentIntentCompletedNumber").
                    set("0");
                admin.database().ref("/Requests/"+
                    paymentIntent.description+"/Stripe/PaymentIntent/").
                    set("success");
              }
            });
      } else if (paymentIntent.metadata.order_type=="Inquiry") {
        admin.database().ref("/Inquiries/"+
paymentIntent.description+"/Stripe/Info/PaymentIntent/id").
            set(paymentIntent.id);
        await admin.database().ref('/Inquiries/'+
      paymentIntent.description).once("value")
            .then(function(snapshot) {
              if (snapshot.hasChild("paymentIntentCompletedNumber")) {
                const resChargesAmount =
                snapshot.child("paymentIntentCompletedNumber").val()+1;
                const textChargesAmount = resChargesAmount.toString();
                console.log("paymentIntentCompletedNumber,creating extra");
                console.log("CompletedNumber " +textChargesAmount);
                admin.database().ref(
                    "/Stripe/InquiriesTransactions/"+
        paymentIntent.description+"/PaymentIntent/"+textChargesAmount).
                    set(paymentIntent);
                admin.database().ref(
                    "/Inquiries/"+paymentIntent.description+
                  "/paymentIntentCompletedNumber").
                    set(textChargesAmount);
                admin.database().ref("/Inquiries/"+
                  paymentIntent.description+"/Stripe/PaymentIntent/").
                    set("success");
              } else {
                admin.database().ref(
                    "/Stripe/InquiriesTransactions/"+
          paymentIntent.description+"/PaymentIntent/0").set(
                    paymentIntent);
                admin.database().ref(
                    "/Inquiries/"+paymentIntent.description+
                  "/paymentIntentCompletedNumber").
                    set("0");
                admin.database().ref("/Inquiries/"+
                    paymentIntent.description+"/Stripe/PaymentIntent/").
                    set("success");
              }
            });
      }
      break;
    case 'payment_method.attached':
      // eslint-disable-next-line no-case-declarations
      const paymentMethod = event.data.object;
      // Then define and call a method to handle the
      // successful attachment of a PaymentMethod.
      // handlePaymentMethodAttached(paymentMethod);
      break;
    case 'charge.succeeded':
      // eslint-disable-next-line no-case-declarations
      const charge = event.data.object;
      console.log(`Charge for ${charge.amount} was successful!`);
      // Then define and call a function to handle the event charge.succeeded
      // Update status in request path database
      if (charge.metadata.order_type=="Request") {
        admin.database().ref("/Requests/"+
        charge.description+"/Stripe/Info/Charge/id").
            set(charge.id);
        admin.database().ref("/Requests/"+
            charge.description+"/Stripe/Info/Charge/receipt_url").
            set(charge.receipt_url);
        await admin.database().ref('/Requests/'+charge.description)
            .once("value")
            .then(function(snapshot) {
              if (snapshot.hasChild("chargesAmount")) {
                console.log("order has chargesAmount,creating extra charge");
                const resChargesAmount =
                snapshot.child("chargesAmount").val()+1;
                const textChargesAmount = resChargesAmount.toString();
                console.log("Charges amount" +textChargesAmount);
                admin.database().ref(
                    "/Stripe/RequestTransactions/"+
        charge.description+"/Charge/"+textChargesAmount).set(charge);
                admin.database().ref(
                    "/Requests/"+charge.description+"/chargesAmount").
                    set(textChargesAmount);
              } else {
                console.log("order does not have chargesAmount");
                admin.database().ref(
                    "/Stripe/RequestTransactions/"+
          charge.description+"/Charge/0").set(
                    charge);
                admin.database().ref(
                    "/Requests/"+charge.description+"/chargesAmount").
                    set("0");
                admin.database().ref("/Requests/"+
            charge.description+"/Stripe/Charge").set("success");
                const stringUrl=
        "/IncomingRequests/"+snapshot.val().sId+"/"+
        charge.description+"/status";
                const stringUrl2="/Requests/"+charge.description+"/status";
                const stringUrl3=
        "/OutgoingRequests/"+snapshot.val().euId+"/"+
        charge.description+"/status";
                Promise.all([
                  admin.database().ref(stringUrl).set("Completed"),
                  admin.database().ref(stringUrl2).set("Completed"),
                  admin.database().ref(stringUrl3).set("Completed")])
                    .then(resAry => console.log(resAry))
                    .catch(error => console.log(error));
              }
            });
      } else if (charge.metadata.order_type=="Inquiry") {
        admin.database().ref("/Inquiries/"+
        charge.description+"/Stripe/Info/Charge/id").
            set(charge.id);
        admin.database().ref("/Inquiries/"+
            charge.description+"/Stripe/Info/Charge/receipt_url").
            set(charge.receipt_url);
        await admin.database().ref('/Inquiries/'+charge.description)
            .once("value")
            .then(function(snapshot) {
              if (snapshot.hasChild("chargesAmount")) {
                console.log("order has chargesAmount,creating extra charge");
                const resChargesAmount =
                snapshot.child("chargesAmount").val()+1;
                const textChargesAmount = resChargesAmount.toString();
                console.log("Charges amount" +textChargesAmount);
                admin.database().ref(
                    "/Stripe/InquiriesTransactions/"+
        charge.description+"/Charge/"+textChargesAmount).set(charge);
                admin.database().ref(
                    "/Inquiries/"+charge.description+"/chargesAmount").
                    set(textChargesAmount);
              } else {
                console.log("order does not have chargesAmount");
                admin.database().ref(
                    "/Stripe/InquiriesTransactions/"+
          charge.description+"/Charge/0").set(
                    charge);
                admin.database().ref(
                    "/Inquiries/"+charge.description+"/chargesAmount").
                    set("0");
                admin.database().ref("/Inquiries/"+
            charge.description+"/Stripe/Charge").set("success");
                const stringUrl=
        "/IncomingInquiries/"+snapshot.val().sId+"/"+
        charge.description+"/status";
                const stringUrl2="/Inquiries/"+charge.description+"/status";
                const stringUrl3=
        "/OutgoingInquiries/"+snapshot.val().euId+"/"+
        charge.description+"/status";
                Promise.all([
                  admin.database().ref(stringUrl).set("Completed"),
                  admin.database().ref(stringUrl2).set("Completed"),
                  admin.database().ref(stringUrl3).set("Completed")])
                    .then(resAry => console.log(resAry))
                    .catch(error => console.log(error));
              }
            });
      }
      // --------------------------------------------------------------------
      break;
    default:
      // Unexpected event type
      console.log(`Unhandled event type ${event.type}.`);
  }
  // Return a 200 response to acknowledge receipt of the event
  response.send();
});
exports.messagesRequestsListenerPushNotification = functions.database.
    ref('/Requests/{requestId}/Chat/{messageId}/')
    .onWrite(async (snap, context) => {
      const toId = snap.after.val().idTo;
      const message =snap.after.val().message.substring(0, 8) + "...";
      let FCMToken;
      let titlesnapshot;
      await admin.database().ref('/UsersToken/'+toId+'/token')
          .once("value")
          .then(async function(snapshot) {
            if (snapshot.exists()) {
              FCMToken=snapshot.val();
            }
          });
      await admin.database().ref('/Requests/'+context.params.requestId+'/title')
          .once("value")
          .then(async function(snapshot) {
            if (snapshot.exists()) {
              titlesnapshot=snapshot.val();
            }
          });
      console.log(FCMToken + "FCMToken");
      console.log(message);
      console.log(toId);
      const payload = {
        token: FCMToken,
        notification: {
          title: 'Request: '+titlesnapshot,
          body: message,
        },
        data: {
          title: 'Request: '+titlesnapshot,
          body: message,
        },
      };
      admin.messaging().send(payload).then((response) => {
        // Response is a message ID string.
        console.log('Successfully sent message:', response);
        return {success: true};
      }).catch((error) => {
        return {error: error.code};
      });
    });
exports.messagesInquiriesListenerPushNotification = functions.database.
    ref('/Inquiries/{requestId}/Chat/{messageId}/')
    .onWrite(async (snap, context) => {
      const toId = snap.after.val().idTo;
      const message =snap.after.val().message.substring(0, 8) + "...";
      let FCMToken;
      let titlesnapshot;
      await admin.database().ref('/UsersToken/'+toId+'/token')
          .once("value")
          .then(async function(snapshot) {
            if (snapshot.exists()) {
              FCMToken=snapshot.val();
            }
          });
      await admin.database().
          ref('/Inquiries/'+context.params.requestId+'/title')
          .once("value")
          .then(async function(snapshot) {
            if (snapshot.exists()) {
              titlesnapshot=snapshot.val();
            }
          });
      console.log(FCMToken + "FCMToken");
      console.log(message);
      console.log(toId);
      const payload = {
        token: FCMToken,
        notification: {
          title: 'Inquiry: '+titlesnapshot,
          body: message,
        },
        data: {
          title: 'Inquiry: '+titlesnapshot,
          body: message,
        },
      };
      admin.messaging().send(payload).then((response) => {
        // Response is a message ID string.
        console.log('Successfully sent message:', response);
        return {success: true};
      }).catch((error) => {
        return {error: error.code};
      });
    });
exports.requestsStatusListenerPushNotification = functions.database.
    ref('/Requests/{requestId}/status/')
    .onWrite(async (snap, context) => {
      let euId;
      let sId;
      let FCMTokenEuId;
      let FCMTokenSid;
      let titlesnapshot;
      const status = snap.after.val();
      await admin.database().
          ref('/Requests/'+context.params.requestId+'/euId')
          .once("value")
          .then(async function(snapshot) {
            if (snapshot.exists()) {
              euId=snapshot.val();
            }
          });
      await admin.database().
          ref('/Requests/'+context.params.requestId+'/sId')
          .once("value")
          .then(async function(snapshot) {
            if (snapshot.exists()) {
              sId=snapshot.val();
            }
          });
      await admin.database().ref('/UsersToken/'+euId+'/token')
          .once("value")
          .then(async function(snapshot) {
            if (snapshot.exists()) {
              FCMTokenEuId=snapshot.val();
            }
          });
      await admin.database().ref('/UsersToken/'+sId+'/token')
          .once("value")
          .then(async function(snapshot) {
            if (snapshot.exists()) {
              FCMTokenSid=snapshot.val();
            }
          });
      await admin.database().
          ref('/Requests/'+context.params.requestId+'/title')
          .once("value")
          .then(async function(snapshot) {
            if (snapshot.exists()) {
              titlesnapshot=snapshot.val();
            }
          });
      // console.log(FCMToken + "FCMToken");
      // console.log(message);
      // console.log(toId);
      const payload = {
        token: FCMTokenEuId,
        notification: {
          title: 'Request: '+titlesnapshot,
          body: 'Status: '+status,
        },
        data: {
          title: 'Request: '+titlesnapshot,
          body: 'Status: '+status,
        },
      };
      const payload2 = {
        token: FCMTokenSid,
        notification: {
          title: 'Request: '+titlesnapshot,
          body: 'Status: '+status,
        },
        data: {
          title: 'Request: '+titlesnapshot,
          body: 'Status: '+status,
        },
      };
      admin.messaging().send(payload).then((response) => {
        // Response is a message ID string.
        admin.messaging().send(payload2).then((response) => {
          console.log('Successfully sent message:', response);
          return {success: true};
        }).catch((error) => {
          return {error: error.code};
        });
      }).catch((error) => {
        return {error: error.code};
      });
    });
exports.inquiriesStatusListenerPushNotification = functions.database.
    ref('/Inquiries/{requestId}/status/')
    .onWrite(async (snap, context) => {
      let euId;
      let sId;
      let FCMTokenEuId;
      let FCMTokenSid;
      let titlesnapshot;
      const status = snap.after.val();
      await admin.database().
          ref('/Inquiries/'+context.params.requestId+'/euId')
          .once("value")
          .then(async function(snapshot) {
            if (snapshot.exists()) {
              euId=snapshot.val();
            }
          });
      await admin.database().
          ref('/Inquiries/'+context.params.requestId+'/sId')
          .once("value")
          .then(async function(snapshot) {
            if (snapshot.exists()) {
              sId=snapshot.val();
            }
          });
      await admin.database().ref('/UsersToken/'+euId+'/token')
          .once("value")
          .then(async function(snapshot) {
            if (snapshot.exists()) {
              FCMTokenEuId=snapshot.val();
            }
          });
      await admin.database().ref('/UsersToken/'+sId+'/token')
          .once("value")
          .then(async function(snapshot) {
            if (snapshot.exists()) {
              FCMTokenSid=snapshot.val();
            }
          });
      await admin.database().
          ref('/Inquiries/'+context.params.requestId+'/title')
          .once("value")
          .then(async function(snapshot) {
            if (snapshot.exists()) {
              titlesnapshot=snapshot.val();
            }
          });
      // console.log(FCMToken + "FCMToken");
      // console.log(message);
      // console.log(toId);
      const payload = {
        token: FCMTokenEuId,
        notification: {
          title: 'Inquiry: '+titlesnapshot,
          body: 'Status: '+status,
        },
        data: {
          title: 'Inquiry: '+titlesnapshot,
          body: 'Status: '+status,
        },
      };
      const payload2 = {
        token: FCMTokenSid,
        notification: {
          title: 'Inquiry: '+titlesnapshot,
          body: 'Status: '+status,
        },
        data: {
          title: 'Inquiry: '+titlesnapshot,
          body: 'Status: '+status,
        },
      };
      admin.messaging().send(payload).then((response) => {
        // Response is a message ID string.
        admin.messaging().send(payload2).then((response) => {
          console.log('Successfully sent message:', response);
          return {success: true};
        }).catch((error) => {
          return {error: error.code};
        });
      }).catch((error) => {
        return {error: error.code};
      });
    });
exports.MessagesSendListenerPushNotification = functions.database.
    ref('/Messages/Chats/{chatId}/{messageId}/')
    .onWrite(async (snap, context) => {
      const toId = snap.after.val().idTo;
      const message =snap.after.val().message.substring(0, 8) + "...";
      let FCMToken;
      await admin.database().ref('/UsersToken/'+toId+'/token')
          .once("value")
          .then(async function(snapshot) {
            if (snapshot.exists()) {
              FCMToken=snapshot.val();
            }
          });
      console.log(FCMToken + "FCMToken");
      console.log(message);
      console.log(toId);
      const payload = {
        token: FCMToken,
        notification: {
          title: 'Messenger: New Message',
          body: message,
        },
        data: {
          title: 'Messenger: New Message',
          body: message,
        },
      };
      admin.messaging().send(payload).then((response) => {
        // Response is a message ID string.
        console.log('Successfully sent message:', response);
        return {success: true};
      }).catch((error) => {
        return {error: error.code};
      });
    });
exports.createPost = functions.database.ref('/Gigs/Virtual/{gigId}')
    .onCreate( async (snap, context) => {
      console.log("snap only: "+snap.val().title);
      snap.forEach(function(data) {
        console.log("datakey "+data.key);
        console.log("dataval "+data.val());
      });

      await client.index({
        index: 'virtualgigs',
        id: context.params.gigId,
        // body: snap.val(), //saves everything that is created on that gig
        body: {
          title: snap.val().title,
          type: 'Virtual',
          gigId: context.params.gigId,
          date: snap.val().createdOn,
        },
      });
    });

exports.updatePost = functions.database.ref('/Gigs/Virtual/{gigId}/esearch')
    .onUpdate( async (snap, context) => {
      const afterDataTitle = snap.after.val().title; // data after the write
      await client.update({
        index: 'virtualgigs',
        id: context.params.gigId,
        body: {
          doc: {
            title: afterDataTitle,
          },
          doc_as_upsert: true,
        },
      });
    });

exports.deletePost = functions.database.ref('/Gigs/Virtual/{gigId}')
    .onDelete((snap, context) => {
      client.delete({
        index: 'virtualgigs',
        id: context.params.gigId,
      });
    });

exports.createPostOutside = functions.database.ref('/Gigs/Outside/{gigId}')
    .onCreate( async (snap, context) => {
      console.log("snap only: "+snap.val().title);
      snap.forEach(function(data) {
        console.log("datakey "+data.key);
        console.log("dataval "+data.val());
      });

      await client.index({
        index: 'virtualgigs',
        id: context.params.gigId,
        // body: snap.val(), //saves everything that is created on that gig
        body: {
          title: snap.val().title,
          type: 'Outside',
          gigId: context.params.gigId,
          date: snap.val().createdOn,
        },
      });
    });

exports.updatePostOutside =
functions.database.ref('/Gigs/Outside/{gigId}/esearch')
    .onUpdate( async (snap, context) => {
      const afterDataTitle = snap.after.val().title; // data after the write
      await client.update({
        index: 'virtualgigs',
        id: context.params.gigId,
        body: {
          doc: {
            title: afterDataTitle,
          },
          doc_as_upsert: true,
        },
      });
    });

exports.deletePostOutside = functions.database.ref('/Gigs/Outside/{gigId}')
    .onDelete((snap, context) => {
      client.delete({
        index: 'virtualgigs',
        id: context.params.gigId,
      });
    });

exports.createPostFind = functions.database.ref('/Gigs/FindRequests/{gigId}')
    .onCreate( async (snap, context) => {
      console.log("snap only: "+snap.val().title);
      snap.forEach(function(data) {
        console.log("datakey "+data.key);
        console.log("dataval "+data.val());
      });

      await client.index({
        index: 'virtualgigs',
        id: context.params.gigId,
        // body: snap.val(), //saves everything that is created on that gig
        body: {
          title: snap.val().title,
          type: 'Find',
          gigId: context.params.gigId,
          date: snap.val().createdOn,
        },
      });
    });

exports.updatePostFind =
functions.database.ref('/Gigs/FindRequests/{gigId}/esearch')
    .onUpdate( async (snap, context) => {
      const afterDataTitle = snap.after.val().title; // data after the write
      await client.update({
        index: 'virtualgigs',
        id: context.params.gigId,
        body: {
          doc: {
            title: afterDataTitle,
          },
          doc_as_upsert: true,
        },
      });
    });

exports.deletePostFind = functions.database.ref('/Gigs/FindRequests/{gigId}')
    .onDelete((snap, context) => {
      client.delete({
        index: 'virtualgigs',
        id: context.params.gigId,
      });
    });


exports.createIncomingReq = functions.database.ref('/Requests/{reqId}')
    .onCreate( async (snap, context) => {
      console.log("snap only: "+snap.val().sId);
      const sId=snap.val().sId;
      const reqId= context.params.reqId;
      const stringUrl="/IncomingRequests/"+sId+"/"+reqId+"/status";
      const stringUrl2="/Requests/"+reqId+"/status";
      const euId=snap.val().euId;
      const stringUrl3="/OutgoingRequests/"+euId+"/"+reqId+"/status";
      // snap.forEach(function(data) {
      //  console.log("datakey "+data.key);
      //  console.log("dataval "+data.val());
      // });
      await Promise.allSettled([
        admin.database().ref(stringUrl).set("Requesting"),
        admin.database().ref(stringUrl2).set("Requesting"),
        admin.database().ref(stringUrl3).set("Requesting")]);
    });

exports.createIncomingInquiry = functions.database.ref('/Inquiries/{reqId}')
    .onCreate( async (snap, context) => {
      console.log("snap only: "+snap.val().sId);
      const sId=snap.val().sId;
      const reqId= context.params.reqId;
      const stringUrl="/IncomingInquiries/"+sId+"/"+reqId+"/status";
      const stringUrl2="/Inquiries/"+reqId+"/status";
      const euId=snap.val().euId;
      const stringUrl3="/OutgoingInquiries/"+euId+"/"+reqId+"/status";
      // snap.forEach(function(data) {
      //  console.log("datakey "+data.key);
      //  console.log("dataval "+data.val());
      // });
      await Promise.allSettled([
        admin.database().ref(stringUrl).set("Inquiring"),
        admin.database().ref(stringUrl2).set("Inquiring"),
        admin.database().ref(stringUrl3).set("Inquiring")]);
    });

exports.updateIOReq = functions.database.ref('/Requests/{reqId}/uStatus')
    .onUpdate( async (snap, context) => {
      const reqId= context.params.reqId;
      console.log("snap only: "+snap.after.val());
      if (snap.after.val()=="Accepted") {
        await admin.database().ref('/Requests/'+reqId).once("value")
            .then(function(snapshot) {
              if (snapshot.child("status").val()=="Cancelled") {
                console.log("order cancelled, not updating anything");
                return;
              }
              if (snapshot.child("status").val()=="Completed") {
                console.log("order completed, not updating anything");
              } else {
                const stringUrl=
              "/IncomingRequests/"+snapshot.val().sId+"/"+reqId+"/status";
                const stringUrl2="/Requests/"+reqId+"/status";
                const stringUrl3=
              "/OutgoingRequests/"+snapshot.val().euId+"/"+reqId+"/status";
                Promise.all([
                  admin.database().ref(stringUrl).set("Accepted"),
                  admin.database().ref(stringUrl2).set("Accepted"),
                  admin.database().ref(stringUrl3).set("Accepted")])
                    .then(resAry => console.log(resAry))
                    .catch(error => console.log(error));
              }
            });
      } else if (snap.after.val()=="Processing") {
        await admin.database().ref('/Requests/'+reqId).once("value")
            .then(function(snapshot) {
              if (snapshot.child("status").val()=="Cancelled") {
                console.log("order cancelled, not updating anything");
                return;
              }
              if (snapshot.child("status").val()=="Completed") {
                console.log("order completed, not updating anything");
              } else {
                const stringUrl=
              "/IncomingRequests/"+snapshot.val().sId+"/"+reqId+"/status";
                const stringUrl2="/Requests/"+reqId+"/status";
                const stringUrl3=
              "/OutgoingRequests/"+snapshot.val().euId+"/"+reqId+"/status";
                Promise.all([
                  admin.database().ref(stringUrl).set("Processing"),
                  admin.database().ref(stringUrl2).set("Processing"),
                  admin.database().ref(stringUrl3).set("Processing")])
                    .then(resAry => console.log(resAry))
                    .catch(error => console.log(error));
              }
            });
      }
    });
exports.updateIOInq = functions.database.ref('/Inquiries/{reqId}/uStatus')
    .onUpdate( async (snap, context) => {
      const reqId= context.params.reqId;
      console.log("snap only: "+snap.after.val());
      if (snap.after.val()=="Accepted") {
        await admin.database().ref('/Inquiries/'+reqId).once("value")
            .then(function(snapshot) {
              if (snapshot.child("status").val()=="Completed") {
                console.log("order completed, not updating anything");
              } else {
                const stringUrl=
              "/IncomingInquiries/"+snapshot.val().sId+"/"+reqId+"/status";
                const stringUrl2="/Inquiries/"+reqId+"/status";
                const stringUrl3=
              "/OutgoingInquiries/"+snapshot.val().euId+"/"+reqId+"/status";
                Promise.all([
                  admin.database().ref(stringUrl).set("Accepted"),
                  admin.database().ref(stringUrl2).set("Accepted"),
                  admin.database().ref(stringUrl3).set("Accepted")])
                    .then(resAry => console.log(resAry))
                    .catch(error => console.log(error));
              }
            });
      } else if (snap.after.val()=="Processing") {
        await admin.database().ref('/Inquiries/'+reqId).once("value")
            .then(function(snapshot) {
              if (snapshot.child("status").val()=="Completed") {
                console.log("order completed, not updating anything");
              } else {
                const stringUrl=
              "/IncomingInquiries/"+snapshot.val().sId+"/"+reqId+"/status";
                const stringUrl2="/Inquiries/"+reqId+"/status";
                const stringUrl3=
              "/OutgoingInquiries/"+snapshot.val().euId+"/"+reqId+"/status";
                Promise.all([
                  admin.database().ref(stringUrl).set("Processing"),
                  admin.database().ref(stringUrl2).set("Processing"),
                  admin.database().ref(stringUrl3).set("Processing")])
                    .then(resAry => console.log(resAry))
                    .catch(error => console.log(error));
              }
            });
      }
    });
exports.createRequest=
    functions.https.onCall(async (data, context) => {
      if (!context.auth) {
        console.log("Unauthorized");
        // Throwing an HttpsError so that the client gets the error details.
        throw new functions.https.HttpsError('not-authorised',
            'The function must be called while authenticated.');
      } else {
        const gigId=data.gigId;
        const gigOption= data.gigOption;
        const type =data.gigType;
        let status;
        let stringUrl;
        console.log("GigType1 "+ type);
        if (type=="Virtual") {
          console.log("GigType "+ type);
          stringUrl="/Gigs/Virtual/"+gigId+"/";
          status="Requesting";
        } else if (type=="Outside") {
          console.log("GigType "+ type);
          stringUrl="/Gigs/Outside/"+gigId+"/";
          status="Requesting";
        } else {
          console.log("GigType "+ type);
          stringUrl="/Gigs/FindRequests/"+gigId+"/";
          status="Find";
        }
        let dataPush;
        await Promise.allSettled([
          await admin.database()
              .ref(stringUrl).once("value")
              .then(function(snapshot) {
                dataPush = {
                  sId: data.sId,
                  euId: data.euId,
                  gigId: snapshot.child("gigId").val(),
                  gigType: type, uStatus: "Requesting",
                  createdOn: data.gigServerTime,
                  euTimeZoneUTC: data.gigUTC,
                  title: snapshot.child("title").val(),
                  price: snapshot.child(gigOption).child("price").val(),
                  duration: snapshot.child(gigOption).child("duration").val(),
                  durationTime: snapshot.child(gigOption).child("time").val(),
                  uriGig: snapshot.child("uriBanner").val(),
                  creatorName: data.gigCreatorName,
                  uriCreator: data.gigCreatorUri,
                  euName: data.gigEuName,
                  uriEu: data.gigEuUri,
                };
              }),
        ]);
        const postRef = admin.database().ref('/Requests/');
        let result;
        await Promise.allSettled([
          postRef.push(dataPush)
              .then(res => {
                console.log(res.getKey()); // this will return you ID
                result= "success";
              })
              .catch(error =>result=error),
        ]);
        return result;
      }
    });
exports.requestCancel =
    functions.https.onCall(async (data, context) => {
      let result;
      if (!context.auth) {
        result ="Unauthorized";
        console.log("Unauthorized");
        // Throwing an HttpsError so that the client gets the error details.
        throw new functions.https.HttpsError('not-authorised',
            'The function must be called while authenticated.');
      } else {
        const id=data.id;
        const type =data.type;
        let status;
        let currentStatus;
        let stringUrl;
        let stringUrl2;
        let stringUrl3;
        if (type=="Inquiry") {
          console.log("GigType "+ type);
          stringUrl="/Inquiries/"+id+"/status";
          status="Cancelled";
          await admin.database().ref('/Inquiries/'+id).once("value")
              .then(async function(snapshot) {
                stringUrl2=
        "/IncomingInquiries/"+snapshot.val().sId+"/"+id+"/status";
                stringUrl3=
        "/OutgoingInquiries/"+snapshot.val().euId+"/"+id+"/status";
              });
        } else if (type=="Request") {
          console.log("GigType "+ type);
          stringUrl="/Requests/"+id+"/status";
          status="Cancelled";
          await admin.database().ref('/Requests/'+id).once("value")
              .then(async function(snapshot) {
                stringUrl2=
            "/IncomingRequests/"+snapshot.val().sId+"/"+id+"/status";
                stringUrl3=
            "/OutgoingRequests/"+snapshot.val().euId+"/"+id+"/status";
              });
        } else {
          result = "Error wrong type";
        }
        if (result != "Error wrong type" ) {
          await admin.database().
              ref(stringUrl)
              .once("value")
              .then(async function(snapshot) {
                if (snapshot.exists()) {
                  currentStatus=snapshot.val();
                }
              });
          if (currentStatus != "Completed") {
            await Promise.all([
              admin.database().ref(stringUrl).set("Cancelled"),
              admin.database().ref(stringUrl2).set("Cancelled"),
              admin.database().ref(stringUrl3).set("Cancelled")])
                .then(resAry => {
                  console.log(resAry);
                  result= "success";
                })
                .catch(error => {
                  console.log(error);
                  result=error;
                });
          } else {
            result = "Order already completed.";
          }
        }
      }
      return result;
    });
exports.createInquiry=
    functions.https.onCall(async (data, context) => {
      if (!context.auth) {
        console.log("Unauthorized");
        // Throwing an HttpsError so that the client gets the error details.
        throw new functions.https.HttpsError('not-authorised',
            'The function must be called while authenticated.');
      } else {
        const gigId=data.gigId;
        const gigOption= data.gigOption;
        const type =data.gigType;
        let status;
        let stringUrl;
        console.log("GigType1 "+ type);
        if (type=="Virtual") {
          console.log("GigType "+ type);
          stringUrl="/Gigs/Virtual/"+gigId+"/";
          status="Requesting";
        } else if (type=="Outside") {
          console.log("GigType "+ type);
          stringUrl="/Gigs/Outside/"+gigId+"/";
          status="Requesting";
        } else {
          console.log("GigType "+ type);
          stringUrl="/Gigs/FindRequests/"+gigId+"/";
          status="Find";
        }
        let dataPush;
        await Promise.allSettled([
          await admin.database()
              .ref(stringUrl).once("value")
              .then(function(snapshot) {
                dataPush = {
                  sId: data.sId,
                  euId: data.euId,
                  gigId: snapshot.child("gigId").val(),
                  gigType: type, uStatus: "Requesting",
                  createdOn: data.gigServerTime,
                  euTimeZoneUTC: data.gigUTC,
                  title: snapshot.child("title").val(),
                  price: snapshot.child(gigOption).child("price").val(),
                  duration: snapshot.child(gigOption).child("duration").val(),
                  durationTime: snapshot.child(gigOption).child("time").val(),
                  uriGig: snapshot.child("uriBanner").val(),
                  creatorName: data.gigCreatorName,
                  uriCreator: data.gigCreatorUri,
                  euName: data.gigEuName,
                  uriEu: data.gigEuUri,
                };
              }),
        ]);
        const postRef = admin.database().ref('/Inquiries/');
        let result;
        await Promise.allSettled([
          postRef.push(dataPush)
              .then(res => {
                console.log(res.getKey()); // this will return you ID
                result= "success";
              })
              .catch(error =>result=error),
        ]);
        return result;
      }
    });
exports.createStripeLogInLink=
functions.https.onCall(async (data, context) => {
// Authentication / user information is automatically added to the request.
  if (!context.auth) {
  // Throwing an HttpsError so that the client gets the error details.
    throw new functions.https.HttpsError('not-authorised',
        'The function must be called while authenticated.');
  } else {
    let response;
    try {
      const uid = context.auth.uid;
      const stringUrl1="/StripeUsersId/"+uid+"/Stripe";
      await admin.database().ref(stringUrl1).once("value")
          .then(async function(snapshot) {
            if (snapshot.child("id").val() == null) {
              console.log("No account");
            } else {
              const stripeId=snapshot.child("id").val();
              response = await getStripeAccountLink(stripeId);
            }
          });
    } catch (err) {
      response = err;
      return err;
    }
    return response;
  }
});

exports.createStripeConnectedAccount =
functions.https.onCall(async (data, context) => {
// Authentication / user information is automatically added to the request.
  if (!context.auth) {
  // Throwing an HttpsError so that the client gets the error details.
    throw new functions.https.HttpsError('not-authorised',
        'The function must be called while authenticated.');
  } else {
    let response;
    try {
      const uid = context.auth.uid;
      const stringUrl1="/StripeUsersId/"+uid+"/Stripe";
      await admin.database().ref(stringUrl1).once("value")
          .then(async function(snapshot) {
            const stripeId=snapshot.child("id").val();
            if (snapshot.child("id").val() == null) {
              console.log("No account");
              try {
                const uid = context.auth.uid;
                const account= await createAccount();
                if (account == "failed") {
                  throw new functions.https
                      .HttpsError("failed",
                          "Failed",
                          {code: '1337',
                            message: 'my custom message'},
                      );
                }
                console.log("End");
                console.log(account.id);
                const stringUrl="/StripeUsers/"+uid+"/Stripe";
                await Promise.allSettled([
                  admin.database().ref(stringUrl).set(account),
                  admin.database().ref(stringUrl1).child("id")
                      .set(account.id)]);
                response = "createdAccount";
                return account;
              } catch (err) {
                return err;
              }
            } else {
              const accountRetrieve =
              await retrieveAccount(stripeId);
              if (accountRetrieve == "failed") {
                throw new functions.https
                    .HttpsError("failed",
                        "Failed",
                        {code: '1337',
                          message: 'my custom message'},
                    );
              }
              const stringUrl="/StripeUsers/"+uid+"/Stripe";
              await Promise.allSettled([
                admin.database().ref(stringUrl).set(accountRetrieve)]);
              const stringUrl2=
              "/StripeUsers/"+uid+"/Stripe";
              await admin.database().ref(stringUrl2).once("value")
                  .then(async function(snapshot) {
                    if (snapshot.child("charges_enabled").val() != true) {
                      console.log("Account needs immediate action");
                      console.log("id "+ stripeId);
                      const acctLink =
                      await linkAccount(stripeId);
                      console.log("accountLink " +acctLink);
                      response=acctLink;
                      return acctLink;
                    } else {
                      response = "noActionNeeded";
                      console.log("Account no immediate action required");
                    }
                  });
            }
          });
    } catch (err) {
      return err;
    }
    return response;
  }
});

// eslint-disable-next-line require-jsdoc
async function connectAccount() {
// Set your secret key. Remember to switch to your live secret
// key in production.
// See your keys here: https://dashboard.stripe.com/apikeys
  // eslint-disable-next-line no-unused-vars
  const accounts = await stripe.accounts.create({type: 'express'})
      .then(async () => {
        const accountRetrieve = await retrieveAccount('acct_1KjsHIGhlOGH8q59');
        console.log("retrieve "+accountRetrieve);
      }).then(() => {
        console.log('New Message written');
        // Returning the sanitized message to the client.
        return;
      });
}

// eslint-disable-next-line require-jsdoc
async function createAccount() {
  try {
  // eslint-disable-next-line no-unused-vars
    const accounts = await stripe.accounts.create({type: 'express'});
    return accounts;
  } catch (err) {
    console.log(err.message);
    return "failed";
  }
}
// eslint-disable-next-line require-jsdoc
async function getStripeAccountLink(data) {
  const response = await stripe.accounts.createLoginLink(data);
  return response.url;
}
// eslint-disable-next-line require-jsdoc
async function retrieveAccount(acc) {
  try {
    const account = await stripe.accounts.retrieve(
        acc);
    return account;
  } catch (err) {
    console.log(err.message);
    return "failed";
  }
}

// eslint-disable-next-line require-jsdoc
exports.stripePaymentIntentInquiry =
functions.https.onCall(async (data, context) => {
  // Message text passed from the client.
  const reqId = data.reqId;
  let paymentAmount;
  let recipientId;
  let creatorId;
  let acctId;
  let requestStatus;
  let chargesEnabled;
  let recipientEmail;
  console.log("InquiryId "+reqId);
  await admin.database().ref("/Inquiries/"+reqId).once("value")
      .then(async function(snapshot) {
        if (snapshot.hasChild("price")) {
          paymentAmount=snapshot.child("price").val();
        } else {
          paymentAmount=null;
        }
        if (snapshot.hasChild("euId")) {
          recipientId=snapshot.child("euId").val();
        } else {
          recipientId=null;
        }
        if (snapshot.hasChild("sId")) {
          creatorId=snapshot.child("sId").val();
        } else {
          creatorId=null;
        }
        if (snapshot.hasChild("status")) {
          console.log("status "+snapshot.child("status").val());
          requestStatus=snapshot.child("status").val();
        } else {
          requestStatus=null;
        }
      });
  await admin.database().ref("/StripeUsers/"+recipientId+"/Stripe/id")
      .once("value")
      .then(async function(snapshot) {
        if (snapshot.exists()) {
          acctId=snapshot.val();
        } else {
          acctId=null;
        }
      });
  await admin.database().ref("/Users/"+creatorId+"/email").once("value")
      .then(async function(snapshot) {
        if (snapshot.exists()) {
          recipientEmail=snapshot.val();
        } else {
          recipientEmail=null;
        }
      });
  await admin.database()
      .ref("/StripeUsers/"+recipientId+"/Stripe/charges_enabled")
      .once("value")
      .then(async function(snapshot) {
        if (snapshot.exists()) {
          chargesEnabled=snapshot.val();
        } else {
          chargesEnabled=null;
        }
      });
  if (chargesEnabled==false) {
    console.log("Stripe charges are not enabled");

    throw new functions.https
        .HttpsError("not-found",
            "Their Stripe account charges are not enabled",
            {code: '1337',
              message: 'my custom message'},
        );
  }
  if (requestStatus=="Completed") {
    console.log("order Completed");

    throw new functions.https
        .HttpsError("not-found",
            "Order status is completed",
            {code: '1337',
              message: 'my custom message'},
        );
  }

  if ( requestStatus != "Accepted") {
    console.log("order status is not Accepted");
    throw new functions.https
        .HttpsError("not-found",
            "Order status is not Accepted, order payment is only  "+
        "available when status is accepted",
            {code: '1337',
              message: 'my custom message'},
        );
  }
  if ( requestStatus == "Processing") {
    console.log("order status is Processing");
    throw new functions.https
        .HttpsError("not-found",
            "Order status is Proccessing, order payment is only  "+
        "available when status is accepted",
            {code: '1337',
              message: 'my custom message'},
        );
  }
  if (requestStatus==null) {
    console.log("order Status is null");

    throw new functions.https
        .HttpsError("not-found",
            "Order status is completed",
            {code: '1337',
              message: 'my custom message'},
        );
  }

  if (paymentAmount==null) {
    console.log("Payment Amount is null");

    throw new functions.https
        .HttpsError("not-found",
            "Payment Amount is null",
            {code: '1337',
              message: 'my custom message'},
        );
  }
  if (recipientId== null) {
    console.log("Unknown Recipeint");

    throw new functions.https
        .HttpsError("not-found",
            "Unknown Recipient",
            {code: '1337',
              message: 'my custom message'},
        );
  }

  if (acctId==null) {
    console.log("Unknown Stripe Account");

    throw new functions.https
        .HttpsError("not-found",
            "Unknown Stripe Account",
            {code: '1337',
              message: 'my custom message'},
        );
  }

  try {
    const paymentIntent = await stripe.paymentIntents.create({
      amount: paymentAmount * 100,
      currency: 'usd',
      description: reqId,
      receipt_email: recipientEmail,
      automatic_payment_methods: {
        enabled: true,
      },
      metadata: {
        'request_id': reqId,
        'order_type': "Inquiry",
      },
      application_fee_amount: (paymentAmount * 100)*0.10,
      transfer_data: {
        destination: acctId,
      },
    });
    // Change to processing here. (Status of request)-> then the webhook will
    // change it to completed.
    const response ={
      paymentIntent: paymentIntent.client_secret,
      publishableKey: 'pk_live_'+
      '51Ki7mvGrG4xXtMiM7Tm0FqSoubPDytBzVBBTq7jna7i8weVNb8KNTb'+
      'z0xHaMOtjGXJxl57TXjkXmGFg0amXs6kO100iALW2neY',
    };
    const responseJson= JSON.stringify(response);
    console.log("response: "+responseJson);
    return responseJson;
  } catch (err) {
    return err;
  }
});

// eslint-disable-next-line require-jsdoc
exports.stripePaymentIntent =
functions.https.onCall(async (data, context) => {
  // Message text passed from the client.
  const reqId = data.reqId;
  let paymentAmount;
  let sellerId;
  let acctId;
  let requestStatus;
  let chargesEnabled;
  let buyerEmail;
  let buyerId;
  await admin.database().ref("/Requests/"+reqId).once("value")
      .then(async function(snapshot) {
        if (snapshot.hasChild("price")) {
          paymentAmount=snapshot.child("price").val();
        } else {
          paymentAmount=null;
        }
        if (snapshot.hasChild("sId")) {
          sellerId=snapshot.child("sId").val();
        } else {
          sellerId=null;
        }
        if (snapshot.hasChild("euId")) {
          buyerId=snapshot.child("euId").val();
        } else {
          buyerId=null;
        }
        if (snapshot.hasChild("status")) {
          console.log("status "+snapshot.child("status").val());
          requestStatus=snapshot.child("status").val();
        } else {
          requestStatus=null;
        }
      });
  await admin.database()
      .ref("/StripeUsers/"+sellerId+"/Stripe/id").once("value")
      .then(async function(snapshot) {
        if (snapshot.exists()) {
          acctId=snapshot.val();
        } else {
          acctId=null;
        }
      });
  await admin.database().ref("/Users/"+buyerId+"/email").once("value")
      .then(async function(snapshot) {
        if (snapshot.exists()) {
          buyerEmail=snapshot.val();
        } else {
          buyerEmail=null;
        }
      });
  await admin.database()
      .ref("/StripeUsers/"+sellerId+"/Stripe/charges_enabled")
      .once("value")
      .then(async function(snapshot) {
        if (snapshot.exists()) {
          chargesEnabled=snapshot.val();
        } else {
          chargesEnabled=null;
        }
      });
  if (chargesEnabled=="false") {
    console.log("Stripe charges are not enabled for recepient");

    throw new functions.https
        .HttpsError("not-found",
            "Stripe account charges are not enabled for seller/recepient",
            {code: '1337',
              message: 'my custom message'},
        );
  }

  if (requestStatus=="Completed") {
    console.log("order Completed");

    throw new functions.https
        .HttpsError("not-found",
            "Order status is completed",
            {code: '1337',
              message: 'my custom message'},
        );
  }

  if ( requestStatus != "Accepted") {
    console.log("order status is not Accepted");
    throw new functions.https
        .HttpsError("not-found",
            "Order status is not Accepted, order payment is only  "+
        "available when status is accepted",
            {code: '1337',
              message: 'my custom message'},
        );
  }
  if ( requestStatus == "Processing") {
    console.log("order status is Processing");
    throw new functions.https
        .HttpsError("not-found",
            "Order status is Proccessing, order payment is only  "+
        "available when status is accepted",
            {code: '1337',
              message: 'my custom message'},
        );
  }
  if (requestStatus==null) {
    console.log("order Status is null");

    throw new functions.https
        .HttpsError("not-found",
            "Order status is completed",
            {code: '1337',
              message: 'my custom message'},
        );
  }

  if (paymentAmount==null) {
    console.log("Payment Amount is null");

    throw new functions.https
        .HttpsError("not-found",
            "Payment Amount is null",
            {code: '1337',
              message: 'my custom message'},
        );
  }
  if (sellerId== null) {
    console.log("Unknown Seller");

    throw new functions.https
        .HttpsError("not-found",
            "Unknown seller",
            {code: '1337',
              message: 'my custom message'},
        );
  }

  if (acctId==null) {
    console.log("Unknown Stripe Account");

    throw new functions.https
        .HttpsError("not-found",
            "Unknown Stripe Account",
            {code: '1337',
              message: 'my custom message'},
        );
  }

  try {
    const paymentIntent = await stripe.paymentIntents.create({
      amount: paymentAmount * 100,
      currency: 'usd',
      description: reqId,
      receipt_email: buyerEmail,
      automatic_payment_methods: {
        enabled: true,
      },
      metadata: {
        'request_id': reqId,
        'order_type': "Request",
      },
      application_fee_amount: (paymentAmount * 100)*0.10,
      transfer_data: {
        destination: acctId,
      },
    });
    const response ={
      paymentIntent: paymentIntent.client_secret,
      publishableKey: 'pk_live_'+
      '51Ki7mvGrG4xXtMiM7Tm0FqSoubPDytBzVBBTq7jna7i8weVNb8KNTb'+
      'z0xHaMOtjGXJxl57TXjkXmGFg0amXs6kO100iALW2neY',
    };
    const responseJson= JSON.stringify(response);
    console.log("response: "+responseJson);
    return responseJson;
  } catch (err) {
    return err;
  }
});

// eslint-disable-next-line require-jsdoc
async function linkAccount(acc) {
  try {
    const accountLink = await stripe.accountLinks.create({
      account: acc,
      refresh_url: 'https://example.com/reauth',
      return_url: 'https://example.com/return',
      type: 'account_onboarding',
    });
    // Returning the sanitized message to the client.
    return accountLink.url;
  } catch (err) {
    return err.message;
  }
}
