# Instructions to build Umbrella by yourself

## Requirements

- Android Studio, fairly recent (version 2.2+, because of Gradle wrapper)
- SDK24 which we build against
- Android device with SDK 10 (IceCream Sandwich, 4.0.3) or above

## Steps

- clone the repository into a folder

```
git clone git@github.com:securityfirst/Umbrella_android.git
```
- for development build check out develop branch
```
git checkout develop
```

- Open Android Studio
- Open existing Android Studio Project
- Navigate to the folder you have cloned the repository into, select folder Umbrella_android (it will have a green Android Studio icon) and press OK
- Wait until the grandle builds and indexes
- Connect an Android device with [development mode enabled](http://www.greenbot.com/article/2457986/how-to-enable-developer-options-on-your-android-phone-or-tablet.html) or Create a virtual device (I strongly suggest a hardware device for several reasons, not least the speed)
- Go to Run -> Run App (or press Ctrl+r)
- Wait until the build finishes

That is it. Please let us know if any of these steps change or don't work for you.