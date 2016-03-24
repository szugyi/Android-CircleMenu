Circle Menu
==========
This is a simple implementation of a circle menu for Android applications.

Screenshots
-----------
[![Circle menu screenshot](https://github.com/szugyi/CircleMenu/raw/master/screenshots/shot1.png)](#Screenshot)

Download
-----------
Grab via Maven
```xml
<dependency>
    <groupId>com.github.szugyi</groupId>
    <artifactId>Android-CircleMenu</artifactId>
    <version>1.1.1</version>
</dependency>
```
or Gradle:
```groovy
compile 'com.github.szugyi:Android-CircleMenu:1.1.1'
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository](https://oss.sonatype.org/content/repositories/snapshots/).

Android-CircleMenu requires Android 4.0.

Attributes
-----------
* `firstChildPosition` ([`East`, `South`, `West`, `North`]) - The angle where the first child of the CircleLayout will be put. Possible values are: `East` - to the right, `South` - to the bottom, `West` - to the left, `North` - to the top. Default: `South`
* `isRotating` (boolean) - Determines wether the child views are rotatable or not. Default: true
* `speed` (integer) - You can set the speed of the rotation. NOTE: The higher the value, the faster the rotation. It must be greater than 0. Values between 10 - 100 should work well. Default: 25
* `circleBackground` (reference) - The background image's resource to be used for the CircleLayout. Deprecated

EventListeners
--------------
* `OnItemClickListener` - Called when a child view is tapped. If the `isRotating` attribute is set to true, then called only if the tapped view is already on the `firstChildPosition`.
* `OnItemSelectedListener` - If the `isRotating` attribute is set to true, then it is called when the view is rotated to the `firstChildPosition`. Otherwise it is called with the `OnItemClickListener` when the child is tapped.
* `OnCenterClickListener` - Called when the center of the CircleLayout is tapped.
* `OnRotationFinishedListener` -  If the `isRotating` attribute is set to true, then it is called when the rotation is finished, and passes the view which is on the `firstChildPosition`. Otherwise it is never called.

Credits
-------
Special thanks to [Balázs Varga](https://github.com/warnyul), who helped me during the implementation of this custom view.

A lot of code snippets have been used from this great tutorial:
http://mobile.tutsplus.com/tutorials/android/android-sdk-creating-a-rotating-dialer/

The icons used in the example app are from:
http://flaticons.net/
