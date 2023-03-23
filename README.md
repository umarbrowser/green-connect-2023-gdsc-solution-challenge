<h1 align="center"> Green Connect 2023 Google Developer Student Club (Gombe State University, Nigeria), Solution Challenge</h1>
<img align="center" src="https://github.com/umarbrowser/green-connect-2023-gdsc-solution-challenge/blob/master/screenshot/sdgs-pr.png?raw=true" width="100%">


## Introduction 🙋‍♂️
* The challenge we are addressing is the limited access to markets, training, and financing faced by smallholder farmers in developing countries, like Nigeria. Smallholder farmers are typically operating on a small scale, with limited resources and access to information, which often makes it difficult for them to increase their productivity and income.


* Smallholder farmers often lack access to financing, making it difficult for them to invest in their farms and improve their production. This lack of financing can lead to a cycle of low productivity and low income, further exacerbating the challenges faced by smallholder farmers.

* To address these challenges, we propose the development of Green Connect which is a mobile app that provides smallholder farmers with access to markets, training, and financing. The Green Connect will allow farmers to connect with potential buyers for their produce, access training on best practices for agricultural production, and apply for loans or grants to help them invest in their farms.

* By providing smallholder farmers with access to these critical resources, we aim to increase their agricultural productivity and income, improving their standard of living and helping to drive economic growth in their communities.

[![Kotlin](https://img.shields.io/badge/Kotlin-1.8.0-green.svg)]() [![Compose](https://img.shields.io/badge/Compose-1.3.3-green.svg)]() [![Open Source Love](https://badges.frapsoft.com/os/v1/open-source.svg?v=102)](https://opensource.org/licenses/Apache-2.0)

## The app Includes the Following:
* **User Login System** that allows farmers to with their Google Account and securely access the app.

* **Marketplace** feature that connects farmers with buyers such as processors and traders, to sell their products.

* **Forum** feature that allows farmers to connect with other farmers and experts in the field for support and advice.

* **Training** module that provides educational resources and videos on best practices for farming, including information on crop management, soil health, and market trends.

* **Loan** module that allows farmers to apply for and receive loans for investments in their farms.

* **keep Note** feature that provides farmers with insights on their farming operations, such as crop yields and costs, to help them make more informed decisions.

* **Weather** feature that provide farmers with up-to-date information on the amount and intensity of rainfall in their area, helping them to make informed decisions about when to plant, irrigate, or harvest their crops.

## Download and Install APK File
> [![Download](https://img.shields.io/badge/Green%20Connect-v1.0.0-brightgreen?style=for-the-badge)](https://github.com/umarbrowser/green-connect-2023-gdsc-solution-challenge/raw/master/build/green-connect.apk)
* Download the above File to your Android Device
* Initiate the installation either through the download notification or a file browser.
* You’ll receive a warning at the bottom of the phone letting you know that you need to give that app permission to install the file.
* Tap the Settings button to proceed.
* On the next page, find the app from the previous step and toggle it on.
* A prompt should pop up, giving you the option to install the app. Go ahead and follow the instructions to install.
* **Note** — Sometimes, you may need to initiate installation again. For example, if you tap the notification and use your browser to do it, you sometimes need to tap the notification again to get the install prompt after giving the app permission.

## How to build from Source

1. Copy this ```https://github.com/umarbrowser/green-connect-2023-gdsc-solution-challenge.git``` for follow the picture below.

![clone](screenshot/repo.png)

2. Install Lattest version of Android Studio from [https://developer.android.com/studio](https://developer.android.com/studio)

3. Open Android Studio and select **GET FROM VCS**.

4. Choose "Git" from the drop-down menu and paste this ```https://github.com/umarbrowser/green-connect-2023-gdsc-solution-challenge.git``` into the "URL" field.

5. Choose the directory where you want to save the project and click "Clone".

![clone](screenshot/clone.png)

6. Once the cloning process is complete, Android Studio will open the project.

7. Add API key to local.properties.
```
# local.properties
GOOGLE_MAPS_API_KEY=<Your Map API Key>
```

8. Sync your Gradle files to download the necessary dependencies.

9. You should now be able to run the Green Connect by clicking on the "Run" button in Android Studio. If you encounter any issues, make sure that you have the latest version of Android Studio and that your Android SDK is up-to-date. 

## Application Screenshots

| Login with Google  | Home Page | Market Place |
| ------------- | ------------- | ------------- |
| ![login](screenshot/1.jpg)  | ![home page](screenshot/2.jpg)  | ![market place](screenshot/3.jpg)  |

| Sell a Product  | Training | Forum |
| ------------- | ------------- | ------------- |
| ![sell a product](screenshot/4.jpg)  | ![training](screenshot/5.jpg)  | ![forum](screenshot/6.jpg)  |

| Loans  | Keep Notes | Weather |
| ------------- | ------------- | ------------- |
| ![loans](screenshot/7.jpg)  | ![notes](screenshot/8.jpg)  | ![weather](screenshot/9.jpg)  |

## New to Compose?
Check [Compose for Android Developers](https://foso.github.io/Jetpack-Compose-Playground/compose_for/android_devs/) or [Hello World Compose](https://foso.github.io/Jetpack-Compose-Playground/general/helloworld/)
