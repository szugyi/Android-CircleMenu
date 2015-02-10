Circle Menu
==========
This is a simple implementation of a circle menu to be used in Android applications.

Screenshots
-----------
[![Circle menu screenshot](https://github.com/szugyi/CircleMenu/raw/master/screenshots/shot1.png)](#Screenshot)

Basic usage
-----------
The files you will need:

	CircleMenuLib/src/com/szugyi/circlemenu/view/CircleLayout.java
	CircleMenuLib/src/com/szugyi/circlemenu/view/CircleImageView.java
	CircleMenuLib/res/values/attrs.xml

 You can copy and paste them into your project, or import the library project. Do not forget to reference this project as a library project if you choose the second way to use the files.

Attributes
-----------
* `firstChildPosition` ([`East`, `South`, `West`, `North`]) - The angle where the first child of the CircleLayout will be put. Possible values are: `East` - to the right, `South` - to the bottom, `West` - to the left, `North` - to the top. Default: `South`
* `isRotating` (boolean) - Determines wether the child views are rotatable or not. If this attribute is set to false then the `rotateToCenter` attribute will also be false. Deafult: true
* `speed` (integer) - You can set the speed of the rotation. NOTE: The higher the value, the faster the rotation. Values between 10 - 500 should work well. Default: 25
* `circleBackground` (integer) - The background image's resource to be used for the CircleLayout.

EventListeners
--------------
* `OnItemClickListener` - Called when a child view is tapped. If the `isRotating` attribute is set to true, then called only if the tapped view is already on the `firstChildPosition`.
* `OnItemSelectedListener` - If the `isRotating` attribute is set to true, then it is called when the view is rotated to the `firstChildPosition`. Otherwise it is called with the `OnItemClickListener` when the child is tapped.
* `OnCenterClickListener` - Called when the center of the CircleLayout is tapped.
* `OnRotationFinishedListener` -  If the `isRotating` attribute is set to true, then it is called when the rotation is finished, and passes the view and the name of the view which is on the `firstChildPosition`. Otherwise it is never called.

Credits
-------
Special thanks to Balázs Varga, who helped me during the first implementations of this custom view.

A lot of code snippets have been used from this great tutorial:
http://mobile.tutsplus.com/tutorials/android/android-sdk-creating-a-rotating-dialer/

The icons used in the example app are from:
http://flaticons.net/

Colors from:
http://flatuicolors.com/
