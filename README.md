# Introduction
An Android photo viewer program for Flickr, Instagram and 500PX.

![Featured image](http://farm9.staticflickr.com/8098/8413565682_f361c09d1f_z.jpg)

# Download

You can either build it by yourself or purchase/donate via the Google Play Store.

<a href="https://play.google.com/store/apps/details?id=com.gmail.charleszq.picorner">![Picorner at Play Store](http://developer.android.com/images/brand/en_generic_rgb_wo_60.png)</a>


# Dependencies
Please also download the following projects and import into your Eclipse workspace:
* [FlickrjApi4Android](https://github.com/yuyang226/FlickrjApi4Android)
* [j500px](https://github.com/yuyang226/j500px)
* [jInstagram](https://github.com/charleszq/jInstagram) (this is forked from https://github.com/sachin-handiekar/jInstagram with some minor changes)
* [SlidingMenu](https://github.com/jfeinstein10/SlidingMenu)
* [Android-ViewPagerIndicator](https://github.com/JakeWharton/Android-ViewPagerIndicator)
* [Universal Image Loader](https://github.com/nostra13/Android-Universal-Image-Loader)
* [Google play service](http://developer.android.com/google/play-services/setup.html)
* [Android-PullToRefresh](https://github.com/chrisbanes/Android-PullToRefresh)

### Development Environment
* JDK 1.6+
* ADT

Exeucte the command setup_deps.sh for downloading the 3rd party submodules.

The android-support-v4.jar from SlidingMenu and Android-ViewPagerIndicator will cause mismatch issue. Please use the same jar in both projects for resolving this issue.

# Developers
* Charles Zhang: <charleszq@gmail.com>
* Toby Yu: <yuyang226@gmail.com>
