$\color{lightgreen}{Urgent \ Shield \ - \ SOS \ Emergency\  App\ 🚨}$

Urgent Shield is an Android application designed to help users send SOS alerts during emergencies. With a simple interface, the app sends an SMS with the user's location to predefined contacts and enables video calls for immediate assistance.

$\color{lightgreen}{Features:}$

    - Real-time Location Sharing: Sends the user's current location to predefined emergency contacts through SMS.
    - SOS Alerts: Sends an SMS with a Google Maps link to show the user's location.
    - Video Call Integration: Allows the user to make video calls using the default phone app for added safety.
    - Location Updates: The app updates location data in Firebase Realtime Database for accurate tracking.
    - Contacts Management: Stores emergency contacts using SharedPreferences, allowing easy access without the need for constant input.

$\color{lightgreen}{Screenshots:}$
<p>
  <img src="https://github.com/arjunkr9693/Urgent-Shield-SOS-APP-/assets/147978484/84bd47e1-35fa-400e-b102-6e42b4a10583" width="45%" />
  <img src="https://github.com/arjunkr9693/Urgent-Shield-SOS-APP-/assets/147978484/7d1ae913-6cab-47ae-abe0-d8c3c71db005" width="45%" />
  
</p>

<p>
  <img src="https://github.com/arjunkr9693/Urgent-Shield-SOS-APP-/assets/147978484/c7093268-6e1c-4065-b641-166137915e38" width="45%" />
  <img src="https://github.com/arjunkr9693/Urgent-Shield-SOS-APP-/assets/147978484/cfe715c3-997d-4f98-bd78-2dc03ab40848" width="45%" />
</p>

$\color{lightgreen}{TechStacks:}$

    - Kotlin: Primary language used for Android development.
    - Jetpack Compose: For building a modern and responsive user interface.
    - Firebase Realtime Database: Stores and updates the user's location in real time.
    - SharedPreferences: Manages and stores the user's emergency contacts.
    - APIs:
        Location API: Retrieves the device's real-time location.
        SMS API: Sends SOS alerts via SMS to emergency contacts.
        Contact API: Fetches and manages emergency contacts.

$\color{lightgreen}{Installation:}$

    git clone https://github.com/arjunkr9693/Urgent-Shield-SOS-APP-.git

$\color{lightgreen}{How \ It \ Works:}$

    - Add Emergency Contacts: Users can add contacts to the app, which will be stored using SharedPreferences.
    - Trigger SOS Alert: In case of an emergency, users can send an SOS alert to their saved contacts.
    - Location Tracking: The app retrieves and shares the user's location through an SMS link.
    - Video Call: Users can make video calls to emergency contacts through the phone's default video call feature.

$\color{lightgreen}{Dependencies:}$
```
implementation 'com.google.firebase:firebase-database:20.0.0'
implementation 'androidx.compose.ui:ui:1.0.5'
implementation 'androidx.compose.material:material:1.0.5'
implementation 'androidx.compose.ui:ui-tooling-preview:1.0.5'
implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.3.1'
implementation 'com.google.android.gms:play-services-location:18.0.0'
```

$\color{lightgreen}{Permissions:}$
```
<uses-permission android:name="android.permission.SEND_SMS"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.CALL_PHONE"/>
<uses-permission android:name="android.permission.READ_CONTACTS"/>
<uses-permission android:name="android.permission.INTERNET"/><p>
```


