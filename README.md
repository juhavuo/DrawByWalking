# DrawByWalking

Draw by walking is application for Android phones and tablets with minimum SDK set to 23. Internal sensors rotation sensor and acceleration sensor are needed to make the application work. In building one needs Kotlin support.

Main activity uses fragments to show application title image or instructions. From main activity one gets to three different Activities from three different buttons. If one chooses new canvas, one goes to draw activity. If one chooses draw on photo, one goes to camera activity. If one chooses load from file, one goes to load activity.

<img src="http://users.metropolia.fi/~juhavuo/images/2018_10_10_01_22_19.png" title="main_activity" width="200">
                                                                                                                
In camera activity one must write down file name that must be at least four characters long, then one can take the photo using external camera application. Only way to keep file is to press save and proceed to go to draw view. In all other cases file is removed because photo is only for background of drawing and this is not meant to be photoapp. From camera activity one can go to draw activity or come back to main activity.

<img src="http://users.metropolia.fi/~juhavuo/images/2018_10_10_01_24_37.png" title="main_activity" width="200">

In load activity one can see all possible files to load in the list view. One chooses the file by pressing the image of the file, one can also remove the file by pressing the trash can symbol. One can back away using cancel button. From load activity one can go to draw activity or come back to main activity. Cancel button is located at the same position as it is located in camera application to make use of it more confortable.

<img src="http://users.metropolia.fi/~juhavuo/images/2018_10_10_12_10_24.png" title="main_activity" width="200">

To draw activity one can come from three activities. If one comes directly from main activity, one must first choose background color. Otherwise the bitmap will be loaded using filename, that is obtained from intents extras. In draw activity one can change pen color and pen size. All color changing dialogs are using alert dialog with three seek bars and view for previewing the color. There is OK button to accept the color.

One presses the drawing view, when wants to start drawing. One draws by movement. Acceleration sensors is sensing movement in phones in axes in direction of phones longer side. Rotation of phone is detected by rotation sensor and that decides to what direction is drawn and the acceleration sensor decides how much is drawn. When one presses another time draw view drawing stops.
One can save a file. If file has already name when one presses save button it saves with same name. If file has yet no name (first save when came directly from main activity by pressing new canvas button), the save file dialog will open and one can insert file name and save or one can also cancel the action.

If one presses devices back button or close button one goes directly to main activity without saving the file. From draw activity one can go only to main activity no matter what way one has got into draw activity.

<img src="http://users.metropolia.fi/~juhavuo/images/2018_10_10_01_25_55.png" title="main_activity" width="200">

<h1>Accessibility</h1>

Accessibility was checked with scanner. Scannings resulted notes about button size, that they could be increased a bit. In some buttons and in draw view pen size label it was stated, that contrast ratio between text and background could be larger. There were notes about image buttons, that they don't have descriptions for readers.

What was done. Descriptions for imagebutton was added. Resolutions were changed. Button sizes was left the same. Adjusting of those could lead to problems with layout on smaller devices.

<img src="http://users.metropolia.fi/~juhavuo/images/UI_problems.jpg" title="main_activity" width="1000">

Examples:

Item label<br>
fi.metropolia.juhavuo.drawbywalking:id/imageView<br>
This item may not have a label readable by screen readers.<br>

Text contrast<br>
fi.metropolia.juhavuo.drawbywalking:id/file_name_edittext<br>
The item's text contrast ratio is 3.33. This ratio is based on an estimated foreground colour of #FF4081 and an estimated background colour of #FFFFFF. Consider using a contrast ratio greater than 4.50 for small text or 3.00 for large text.<br>

Touch target<br>
fi.metropolia.juhavuo.drawbywalking:id/camera_activity_hide_keyboard_button<br>
This item's height is 41dp. Consider making the height of this touch target 48dp or larger.<br>


