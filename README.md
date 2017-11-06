# Team Ammonia - Catchapp

CatchApp is an Android application currently under development by Team Ammonia of the
University of Melbourne for the subject IT Project in the second semester of 2017.
It provides the users with augmented reality and map based features to locate their 
friends in events. It also allow users to chat in groups (in an event) and with individual
users (with friends).

Pre-requirements
--------------------------
* Java version "1.8.0"

* Java(TM) SE Runtime Environment (build 1.8.0)

* Android Studio 2.3.3 (Tested on Ubuntu and Mac OS)

* An Android Device or emulator with Android SDK 21 or later installed
    
Build Instructions
--------------------------

(1) Download all the files in a new folder.

(2) Open Android Studio.

(3) Select existing Project.

(4) Within Android Studio, select the folder in which the files were downloaded.

(5) You will be asked to use graddle wrapper to configure gradle settings. 
Press okay and wait for the project to build.

Test Instructions:
-----------------------------

(1) Within Android Studio, go to the package: com.ammonia.catchapp (test)

(2) Right click NetworkHandlerTesting

(3) Select "Run NetworkHandlerTesting"

Deployment Instructions:
-----------------------------
Using an Android device:

* First, enable developer options in your Android device
https://developer.android.com/studio/debug/dev-options.html

* Plug in your Android device to the computer, accept the dialog that your phone displays regarding
trusting your computer.

* In Android Studio's Toolbar Run -> Run 'app' and choose your device.


Using and Android emulator:

* Create an Android Virtual Device (AVD). Make sure to select one that has Google Play Services.
For example, as of October 2017 the Google Nexus 5 API 24 should work.
https://developer.android.com/studio/run/managing-avds.html

* In Android Studio's Toolbar Run -> Run 'app' and choose your device.







