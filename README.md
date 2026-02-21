# Scanner

Scanner is a simple yet powerful Android application written in Kotlin for scanning Barcodes and QR codes. It leverages the ZXing library to provide fast and reliable scanning capabilities.

## Features

*   **Fast Scanning**: Quickly scan QR codes and Barcodes.
*   **Flashlight Support**: Toggle the flashlight for scanning in low-light environments.
*   **Camera Switching**: Easily switch between the front and back cameras.
*   **Result Actions**:
    *   View the raw scanned text.
    *   Generate and view a visual barcode/QR code of the scanned content.
    *   Share scanned content with other apps.
*   **Dark Mode Support**: Fully supports light and dark themes.
*   **Available in English and Polish**.

## Tech Stack

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **Core Libraries**:
    *   [ZXing Android Embedded](https://github.com/journeyapps/zxing-android-embedded)
    *   [ZXing Core](https://github.com/zxing/zxing)
*   **UI Components**:
    *   [Jetpack Compose](https://developer.android.com/jetpack/compose)
    *   Material Design 3 (Material3)
    *   AndroidX AppCompat

## Requirements

*   Android SDK 24 (Nougat) or higher.
*   Camera permission.
