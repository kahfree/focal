<p align="center"><img src="https://user-images.githubusercontent.com/38580052/234065285-0aa49961-3fcb-4a2c-9457-c3bdce86876a.png" width="30%"></p>

# Focal: Computer-Vision based mobile application for exercise analysis

## Setup
1. Download and install <a href="https://developer.android.com/studio?gclid=CjwKCAjw0ZiiBhBKEiwA4PT9z417l1Z-SQHSILJKnuccr50AomTJ00VMUMG_ps6ABQPdDmzgWcuDqxoC2s8QAvD_BwE&gclsrc=aw.ds">Android Studio</a>.
2. Download project as a zip.
3. Open project in Android Studio.
4. Run Gradle build to download all dependencies.
## Running the Application
There are two options when running the app:
1. Plug an android phone into the computer (ensure that debug mode is enabled on the phone). The phone used for the development of this application was a Google Pixel 3 XL running Android 12, API 31.
2. Setup an emulator to run on the computer. Note the computer will need to have a camera accessible for the appliation to function properly.

Once ready, select the device in the top right and click the play icon. This will either start the emulator (if chosen that option) or will load the application onto the android phone. The first time building may take longer than subsequent builds if the Gradle sync was not performed during setup.

## Account for Testing
The main account used for the development and demonstration of the application is user with the ID `U1`. Their login details are:
email:`gwagon@gmail.com`
password:`test22`

<img src="https://user-images.githubusercontent.com/38580052/234067920-a8cb9d6f-a73e-4b75-a8f1-d53e7f7c92a9.png" width="30%">

## How to use
1. Open App.
2. Login with details (Or register).
3. Upon successful login you will be brought to the home screen. From here you can go to the profile, the goals, or choose one of three exercises to analyze.
4. Doing exercise analysis will have you performing the exercise for 20 seconds, then you can view the post-exercise dashboard.
5. The profile is read-only.
6. Goals can be made and completed, the system updates them after every execise analysis attempt.
