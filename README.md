# Zebra TC520K Demo App

* This is a demo app for the Zebra TC520K device. It is a simple app that scans a barcode and displays the result on the
  screen.
* The app is written in Kotlin and uses the [Zebra EMDK](https://techdocs.zebra.com/emdk-for-android/7-0/) for barcode
  scanning.

## Building the app

The app can be built using Android Studio, IntelliJ IDEA or the Gradle command line tool.

### Building with Android Studio or IntelliJ IDEA

1. Open the project in Android Studio or IntelliJ IDEA.
2. Select the `app` module in the project view.
3. Select `Build -> Build Bundle(s) / APK(s) -> Build APK(s)` from the menu bar.
4. The APK will be generated in the `app/build/outputs/apk/debug` directory.

### Building with Gradle

1. Open a terminal window.
2. Navigate to the project directory.
3. Run `./gradlew assembleDebug`.
4. The APK will be generated in the `app/build/outputs/apk/debug` directory.
5. The APK can be installed on the device using `adb install app/build/outputs/apk/debug/app-debug.apk`.

## Running the app

1. Install the APK on the device.
2. Open the app.
3. From the menu, select `Scan Barcode`.
4. Scan a barcode.
5. The barcode data will be displayed on the screen.

## Troubleshooting

* If the app crashes when the `Scan Barcode` option is selected, it is likely that the device does not have
  the `DataWedge` app installed. The `DataWedge` app is required for barcode scanning. The app can be downloaded from
  the [Zebra support site](https://www.zebra.com/us/en/support-downloads/software/developer-tools/datawedge-v6-9.html).
* If the app crashes with an error message indicating that the `EMDK` service is not available, it is likely that the
  device does not have the `EMDK` app installed. The `EMDK` app is required for barcode scanning. The app can be
  downloaded from
  the [Zebra support site](https://www.zebra.com/us/en/support-downloads/software/developer-tools/emdk-for-android.html).
* If the app crashes with an error message indicating that the `EMDK` service is not available, it is possible that
  the `EMDK` app is installed but has not been enabled. The `EMDK` app must be enabled before it can be used. To enable
  the `EMDK` app, go to `Settings -> Apps -> EMDK Service -> Enable`.
* If the app crashes with an error message indicating that the `EMDK` service is not available, it is possible that
  the `EMDK` app is installed but has not been enabled. The `EMDK` app must be enabled before it can be used. To enable
  the `EMDK` app, go to `Settings -> Apps -> EMDK Service -> Enable`.
* If the app crashes with an error message indicating that the `EMDK` service is not available, it is possible that
  the `EMDK` app is installed but has not been enabled. The `EMDK` app must be enabled before it can be used. To enable
  the `EMDK` app, go to `Settings -> Apps -> EMDK Service -> Enable`.
* If the app crashes with an error message indicating that the `EMDK` service is not available, it is possible that
  the `EMDK` app is installed but has not been enabled. The `EMDK` app must be enabled before it can be used. To enable
  the `EMDK` app, go to `Settings -> Apps -> EMDK Service -> Enable`.

## Debugging the app

* The app can be debugged using Android Studio, IntelliJ IDEA or the Gradle command line tool.
* To debug the app using Android Studio or IntelliJ IDEA, select `Run -> Debug 'app'` from the menu bar.
* To debug the app using the Gradle command line tool, run `./gradlew assembleDebug` and then run
  `adb shell am start -D -n com.zebra.tc520k.demoapp/.MainActivity`.
