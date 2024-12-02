#Overview

The Event Lottery System Application is a mobile platform designed to simplify the registration process for popular events at community centers, using a lottery-based system. 
This system ensures that all potential participants have an equal opportunity to join events, even if they cannot constantly refresh a webpage or compete with others for a spot. 
The application allows people with various limitations—such as work schedules or disabilities—to participate fairly, and ensures that the registration process is equitable.

#Key Features
Lottery System: A fair, randomized way of selecting participants from a waiting list after a registration period ends.
QR Code Integration: Entrants can scan a QR code to view event details and join the waiting list.
Firebase Integration: Event details, waiting lists, and notifications are stored and managed using Firebase.
Multi-Role System: The application distinguishes between entrants, organizers, and administrators, with distinct privileges for each role.
Geolocation Verification (Optional): An optional feature that verifies the entrant's location when joining an event’s waiting list.
Image Upload: Organizers can upload event poster images to display event information.

#Use Cases and User Stories
User Types
Entrant: Individuals who want to sign up for events.
Organizer: Event organizers who manage event registration and lotteries.
Administrator: System administrators who manage the overall app infrastructure and ensure compliance.
Key User Stories
Entrant User Stories

Sign up for events: Entrants can join the waiting list of an event by scanning a QR code, receiving notifications about lottery results, and accepting or declining event invitations.
Profile management: Entrants can create and update their profiles, including personal details and profile pictures.
Notification preferences: Entrants can opt out of receiving notifications.
Lottery updates: Entrants will receive notifications when they are chosen or rejected in the lottery, and they will have a chance to join if someone declines.
Organizer User Stories

Event creation and management: Organizers can create events, upload posters, manage the waiting list, and select entrants using the lottery system.
Geolocation control: Organizers can enable or disable geolocation requirements for specific events.
Notification management: Organizers can send notifications to chosen, cancelled, and waiting entrants.
Event selection: Organizers can select the number of entrants to register and manage cancellations.
Administrator User Stories

System maintenance: Administrators can remove events, profiles, and images that violate app policies.
Monitoring: Administrators can browse events, profiles, and images.
Scenario
An entrant named Alex wants to sign up for a swimming lesson. After scanning a QR code linked to the event, they can join the waiting list. 
After the registration period ends, a lottery is run, and Alex is notified that they were not selected. 
If someone declines, Alex will have another chance. Eventually, a few selected entrants may decline, and Alex could still be offered a chance to sign up.

Flow of the Application
Event Creation by Organizer:
Organizers create events with details such as the event title, description, price, and number of available spots.
A unique QR code is generated that links to the event page where entrants can view the event details and join the waiting list.
Entrant Registration:
Entrants scan the QR code or manually browse the event details.
They can join the waiting list, providing necessary information like their name, email, and phone number.
Geolocation is optionally required during registration.

#Lottery Process:
After the registration period closes, the organizer initiates the lottery process.
Randomly selected entrants are notified and invited to sign up for the event.
If any selected entrants decline, replacements are drawn from the waiting list.

#Notifications:
Entrants receive notifications for lottery results (whether they were chosen or not).
Organizers can notify entrants about event cancellations, sign-ups, or rejections.

Technologies Used
Android: Native mobile application developed for Android devices.
Firebase: Used for event data storage, user profiles, notifications, and real-time updates.
QR Code Scanning: Integrated QR code scanning functionality using the Zebra Crossing library (ZXing) or Google's ML Kit.
Geolocation (Optional): Uses device geolocation to validate where entrants are joining from.
Installation and Setup

Clone the Repository: Clone the project from GitHub (replace the URL with the actual one if available):
git clone https://github.com/your-username/event-lottery-system.git

Prerequisites:
Android Studio: Ensure that you have Android Studio installed.
Firebase Project: Set up a Firebase project and integrate the google-services.json into your app directory.
Dependencies: Install necessary dependencies for Firebase, QR code scanning, and geolocation handling by adding them to your build.gradle file.
Running the Application:
Open the project in Android Studio.
Connect an Android device or use an emulator.
Build and run the application.
Firebase Setup:
Create a Firebase project and enable Firestore, Firebase Authentication, and Firebase Cloud Messaging for notifications.
Ensure proper Firebase rules are set for security and access.
Generate QR Codes:
When an organizer creates an event, generate a unique QR code for the event that entrants can scan to join the waiting list.
File Structure
/EventLotteryApp
    /app
        /src
            /main
                /java
                    /com
                        /example
                            /eventlottery
                                /activities
                                    MainActivity.java
                                    EventDetailsActivity.java
                                /fragments
                                    EventListFragment.java
                                    ProfileFragment.java
                                /models
                                    Event.java
                                    Entrant.java
                                /services
                                    FirebaseService.java
                                    LotteryService.java
                                /utils
                                    QRCodeGenerator.java
                                    NotificationService.java
                /res
                    /layout
                        activity_main.xml
                        fragment_event_list.xml
                        fragment_profile.xml
                    /drawable
                        logo.png
                    /values
                        strings.xml
                        colors.xml
                        styles.xml
                    /mipmap
                        ic_launcher.png
    /build.gradle
    /google-services.json
    /README.md
    
#Future Enhancements
Payment Integration: Integrate payment systems for paid events.
Real-Time Updates: Use Firebase Realtime Database for instant updates to event and participant status.
Advanced Notification System: Integrate push notifications for immediate updates to entrants and organizers.
Analytics: Track engagement with events and the lottery process.
