# Capture-and-pick-image-for-ocr-to-text-file-and-pdf
android application for Capture and pick image for ocr to text file and pdf using tesseract and iTextg
---------------------------------------------------------------------------------------------------------------------------------
How to import tesseract in android studio?
  1) Download and install the ndk from here https://developer.android.com/tools/sdk/ndk/index.html. I had some trouble with its path in following steps so i put it in “C:\”.
  2) Add that path to environment variables of the system (eg: “C:\android_ndk_r10d”) and then reboot so your machine can find it.
  3) Download “tess-two-master” from here https://github.com/rmtheis/tess-two, extract it (for example in “C:\”) and rename it in “tess”.
  4) Open “tess” folder and then open “tess-two” folder. Click on a blank space while pressing the shift button and select “Open command window here”.
  5) Write “ndk-build” and wait until it completes (about 20min).
  6) Go back in the parent folder, select “eyes-two” folder and again click on a blank space while pressing the shift button in order to open the command window.
  7) Write “ndk-build” and wait.
  8) Download Apache ant from here >> http://ant.apache.org/  and put it in C:\
  9) In folder eyes-two write this in command
  C:\tess\eyes-two>C:\Users\....\AppData\Local\Android\sdk\tools\android update project --target 1 --path C:tess\tess-two
  10) Come back to tess-two folder in command and write here  
  C:\tess\tess-two>apache-ant-1.9.7\bin\ant release
  IF IT COMPLAINS go where you change your system environment variables as you did in step 2 and ADD a new variable with name “JAVA_HOME” and value the path to your jdk (eg: “C:\Program Files\Java\jdk1.8.0_40”)
  11) Open a completely new Android Studio project and follow these instructions https://coderwall.com/p/eurvaq/tesseract-with-andoird-and-gradle from section “Configure tess-two with gradle” but for safety don’t delete any folder or file even if he suggests to do so.
  I had some trouble with “build.gradle” file in “libraries\tess-two” directory, but it is sufficient to change some value on it. In my case I have: In gradle tesseract file that I upload inthis github
  >>https://github.com/junpisa/Capture-and-pick-image-for-ocr-to-text-file-and-pdf/blob/master/libraries/tesseract/build.gradle
    
  
  Note that the last step means you have to go in “File -> Project Structure -> Select a module from the left subwindow -> Dependencies (last tab) ->Press the green “+” on your right -> Module Dependency -> OK”
  
  12) Create a libraries folder underneath your project's main directory. For example, if your project is FirstProject, you would create a FirstProject/libraries folder
  13) Now copy the entire tess-two directory into the libraries folder you just created.
  14) Delete the libs folder in the tess-two directory. If you like, delete the project.properties, build.xml, .classpath, and .project. files as well. You don't need them.
  15) Edit your settings.gradle file in your application’s(FirstProject/settings.gradle) main directory and add this line
      include ':libraries:tess-two'
  16) Then sync the project in Android Studio and add the new tess-two library as module dependency to you main project(after sync tess-two library should appear as a module, you can add it to your project from project settings in android studio)
--------------------------------------------------------------------------------------------------------------------------------------
How to get path of external storage?
String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                     File dir = new File(path);
                     File file = new File(path + "/excel.txt");
It returns storage/emulated/0 in Emulator and in real device you wil access file in sdcard but you should install driver of your device.
--------------------------------------------------------------------------------------------------------------------------------------
  
  
  

