#SlidingMenu
if [ ! -d "SlidingMenu" ];
then
    git clone git@github.com:jfeinstein10/SlidingMenu.git
else
	echo "pulling the latest code for SlidingMenu"
	#cd SlidingMenu
	#git pull origin master
fi

# jinstagram
if [ ! -d "jInstagram" ];
then
    git clone git@github.com:sachin-handiekar/jInstagram.git
else
	echo "pulling the latest code for jInstagram"
	#cd jInstagram
	#git pull origin master
fi

if [ ! -f jInstagram/jInstagram/pom.xml ]
then
	echo "pom.xml for jInstagram is missing"
    cp dependencies/jInstagram/pom.xml jInstagram/jInstagram
fi

cd jInstagram/jInstagram
mvn clean install -DskipTests
cp target/jInstagram-1.0.0.jar ../../UniversalPhotoStudio/libs