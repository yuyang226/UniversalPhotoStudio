Introduction
====================
An Android photo viewer program for Flickr, Instagram and Facebook.

Dependencies
====================
Please also download the following two projects and import into your Eclipse workspace:
* jInstagram:	https://github.com/sachin-handiekar/jInstagram
* SlidingMenu:	https://github.com/jfeinstein10/SlidingMenu

Development Environment
====================
Maven 3.0.X
JDK 1.6+
Maven Eclipse Plugin: search "Maven" in the Eclipse marketplace
ADT
M2E Connector: http://rgladwell.github.com/m2e-android/ or search for "android m2e" in the Eclipse marketplace
Execute the setup_deps.sh to setup everything


To build your apk, simply:
     mvn clean install
To deploy your apk to the connected device:
     mvn android:deploy

Developers
====================
Charles Zhang: charleszq@gmail.com
Toby Yu: yuyang226@gmail.com