# Android Currency Converter App

This Android application is built using the MVVM architecture with Base classes and Helper classes
on top of the clean architecture principles. It utilizes various technologies and libraries to
provide a seamless currency conversion experience.

## Key Features

- **Architecture**: Follows the Clean Arch (data,domain,presentation) + MVVM architecture pattern,
  ensuring separation of concerns and maintainability.
- **Tech Stack**: Utilizes Kotlin, Coroutines, Flows, Room Database, Retrofit, Okhttp, and Latest
  Android Jetpack, Hilt for dependency injection, Material UI components.
- **Data Persistence**: Required data is persisted locally, enabling offline usage after initial
  data fetch.
- **Bandwidth Optimization**: Data refresh from the API is limited to once every 30 minutes to
  minimize bandwidth usage.
- **Currency Selection**: Users can select a currency from a list of currencies provided by open
  exchange rates.
- **Desired Amount Entry**: Users can enter the desired amount for the selected currency.
- **Currency Conversion**: Displays a list showing the desired amount converted into amounts in each
  currency provided by open exchange rates.
- **Fallback Conversion**: If exchange rates for the selected currency are not available,
  conversions are performed on the app side.
- **Floating Point Handling**: Floating point errors are acceptable in conversions.
- **Additional Functionality**:
  - Individual currency conversions: Users can convert individual currencies.
  - Work Manager integration: Updates data periodically in the background with a foreground
    notification, ensuring the data is up to date even when the app is not in use.
- **Unit Tests**: Includes unit tests to ensure the correct operation of the application.
- **Code Comments**: Codebase is well-commented for better readability and understanding.

## Getting Started

To run the app, simply clone the repository and open it in Android Studio. Make sure to have the
latest Android SDK and dependencies installed.
