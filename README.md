# Digital Frontier OE Line Softphone Application

![Digita Frontier O.E. logo](https://github.com/nickbosnv/DFLS/blob/master/app/src/main/res/drawable/digital_frontier_oe_1.png)

## About

This application developed to communicate with PBX Asterisk service. It is an application for 
android devices.

## What can you do:

1. Register
    * Login/Logout
2. Call someone
    1. Forward Call
    2. Pause Call
    3. Hold/Unhold Call
    4. Open the Speaker
    5. Hangup
3. Incoming Calls
    1. Answer
    2. Mute/Unmute
    3. Hangup
 
## Packages

There is 3 packages. One for the interface, the other for the Sip Service and the last for the pjsip
project.

* com.visual.dfls
* dfls.sipservice
* org.pjsip

## Used Libraries Version

* NDK = r20b
* ANDROID_BUILD_TOOLS = "30.0.3"
* PJSIP = 2.11
* SWIG = 4.0.2
* OPENSSL = 1.1.1f
* OPENH264 = 2.1.0
* OPUS = 1.3.1

The PJSIP Project build it from pjsip-android-builder where you can find it here:
[pjsip-android-builder](https://github.com/VoiSmart/pjsip-android-builder)
