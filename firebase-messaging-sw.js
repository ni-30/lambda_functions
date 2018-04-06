importScripts('https://www.gstatic.com/firebasejs/4.1.1/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/4.1.1/firebase-messaging.js');
importScripts('https://www.gstatic.com/firebasejs/4.1.1/firebase.js');


var config = {
    apiKey: "AIzaSyBnEmZSsebYQ-7FW0vFkUY6HhYiSSNwag8",
    authDomain: "alexafusionplay.firebaseapp.com",
    projectId: "alexafusionplay",
    messagingSenderId: "972543288120"
  };
firebase.initializeApp(config);
const messaging = firebase.messaging();

messaging.setBackgroundMessageHandler(function(payload) {
    console.log('Received background message', payload);
    // // Customize notification here
    // const notificationTitle = 'Background Message Title';
    // const notificationOptions = {
    //     body: 'Background Message body.',
    //     icon: '/firebase-logo.png'
    // };
    
    // return self.registration.showNotification(notificationTitle, notificationOptions);
    return null;
});

