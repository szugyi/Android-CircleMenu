Circle Menu
==========
This is a simple implementation of a circle menu to be used in Android applications. 

Screenshots
-----------
[![Circle menu screenshot](https://github.com/szugyi/CircleMenu/raw/master/screenshots/shot1.png)](#Screenshot)

Basic usage
-----------
The files you will need: 
	
	/src/com/szugyi/circlemenu/view/CircleLayout.java
	/src/com/szugyi/circlemenu/view/CircleImageView.java
	/res/values/attrs.xml
	
 You can copy and paste them into your project, or make a library project of this one, by selecting the Properties menu from the context menu of the project folder, then selecting Android and checking in the Is Library checkbox. Do not forget to reference this project as a library project if you choose the second way to use the files.

Attributes
-----------
 * `circleBackground` (integer) - The background image's resource to be used for the CircleLayout.
 * `firstChildPosition` ([`East`, `South`, `West`, `North`]) - The angle where the first child of the CircleLayout will be put. Possible values are: `East` - to the right, `South` - to the bottom, `West` - to the left, `North` - to the top. Default: `South`
 * `rotateToCenter` (boolean) - Determines wether the child views should be rotated to "a whole rotation step". For example if you have only two child views it will always rotate them to be on 0°-180° or on 90°-270°. It will also rotate the tapped child view to the `firstChildPosition` if it is not there yet, otherwise the onItemClicked event listener will be called. Deafult: true
 * `isRotating` (boolean) - Determines wether the child views are rotatable or not. If this attribute is set to false then the `rotateToCenter` attribute will also be false. Deafult: true
 * `speed` (integer) - You can set the speed of the rotation. NOTE: The higher the value, the slower the rotation. Values between 10 - 1000 should work well. Default: 75
	
EventListeners
--------------
* `OnItemClickListener` - Called when a child view is tapped. If the `rotateToCenter` attribute is set to true, then called only if the tapped view is already on the `firstChildPosition`, otherwise the view is first rotated to the `firstChildPosition` and the `OnItemSelectedListener` is called.
* `OnItemSelectedListener` - If the `rotateToCenter` attribute is set to true, then it is called when the view is rotated to the `firstChildPosition`. Otherwise it is called with the `OnItemClickListener` when the child is tapped.
* `OnCenterClickListener` - Called when the center of the CircleLayout is tapped.


Credits
-------
Special thanks to Balázs Varga, who helped me during the first implementations of this custom view.

A lot of code snippets have been used from this great tutorial:
http://mobile.tutsplus.com/tutorials/android/android-sdk-creating-a-rotating-dialer/

The icons used in the example app are from: http://icons.mysitemyway.com/blue-white-pearls-icons-social-media-logos/
