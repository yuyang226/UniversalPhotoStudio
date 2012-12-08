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

install the followings to your Maven local repo via https://github.com/mosabua/maven-android-sdk-deployer
git clone git@github.com:mosabua/maven-android-sdk-deployer.git
cd add-ons\google-apis-17
mvn clean install
cd ../../extras/compatibility-v13
mvn clean install

To build your apk, simply:
     mvn clean install
To deploy your apk to the connected device:
     mvn android:deploy

Developers
====================
Charles Zhang: charleszq@gmail.com
Toby Yu: yuyang226@gmail.com