# DataSetu — Bridging Your Digital World

A modern, monochrome Android file transfer application.
Built with Kotlin, Jetpack Compose, and Clean Architecture.

## About the Project

**DataSetu** is an Android application project focused on making file sharing between devices simple, intuitive, and reliable. Inspired by the meaning of *Setu* (सेतु), the Hindi word for bridge, DataSetu aims to bridge devices through a seamless file transfer experience.

The application is being developed with a premium black, white, and grey visual identity, smooth animations, and a scalable architecture designed to support advanced transfer capabilities as development progresses.

Rather than relying on a monolithic codebase, DataSetu follows modern Android development practices with Jetpack Compose, MVVM, Clean Architecture, and dependency injection.

### Project Goals

- Build a modern and intuitive file-sharing experience.
- Develop a scalable, maintainable Android architecture.
- Support offline-first profile and onboarding workflows.
- Create smooth animations and a consistent monochrome design system.
- Introduce reliable peer-to-peer file transfer and device discovery.
- Prioritize user privacy and secure data handling.

## Features

### Implemented Foundation

- **Animated splash screen** — A custom-branded introduction using Jetpack Compose and Lottie.
- **Onboarding experience** — A multi-page introduction with animated illustrations.
- **Monochrome design system** — A consistent black, white, and grey visual language.
- **Modern UI development** — Declarative UI built with Jetpack Compose.
- **Scalable navigation** — Navigation Compose with a centralized navigation structure.
- **Dependency injection** — Hilt for managing dependencies.
- **Local persistence foundation** — DataStore for onboarding status, preferences, and session-related state.

### Planned Features

- **Peer-to-peer file sharing** between compatible nearby devices.
- **Nearby device discovery** and connection management.
- **Multi-file transfers** for photos, videos, documents, and other supported file types.
- **Live transfer progress**, speed, and estimated remaining time.
- **Transfer history** with locally persisted records.
- **Transfer recovery** for supported interrupted transfers.
- **QR-based device pairing** for convenient connections.
- **Encrypted transfer sessions** and secure device pairing.
- **Background transfer support** using appropriate Android services.
- **Offline user profiles** and local account management.

> Note: Planned features are part of the development roadmap and should not be considered production-ready until implemented and tested.

## Screenshots

Add screenshots or GIFs of the actual application as development progresses.

| Splash Screen             | Onboarding                    | Home Screen             |
|---------------------------|-------------------------------|-------------------------|
| `screenshots/Splash.jpeg` | `screenshots/Onboarding.jpeg` | `screenshots/Home.jpeg` |


## Tech Stack

| Technology                                | Purpose                                      |
| ----------------------------------------- | -------------------------------------------- |
| Kotlin                                    | Primary programming language                 |
| Jetpack Compose                           | Declarative UI development                   |
| Material 3                                | UI components and design foundations         |
| Navigation Compose                        | Screen navigation                            |
| MVVM                                      | Presentation architecture                    |
| Clean Architecture                        | Separation of responsibilities               |
| Hilt                                      | Dependency injection                         |
| Kotlin Coroutines                         | Asynchronous operations                      |
| Kotlin Flow / StateFlow                   | Reactive state management                    |
| DataStore                                 | Preferences and local session state          |
| Lottie Compose                            | Animated splash and onboarding illustrations |
| Room                                      | Planned local database for transfer history  |
| Ktor or another suitable networking stack | Planned peer-to-peer communication           |

## Architecture

DataSetu follows a separation-of-concerns approach intended to keep presentation, business logic, and data access independently maintainable.

```text
DataSetu
│
├── Presentation
│   ├── Jetpack Compose Screens
│   ├── ViewModels
│   ├── UI State
│   └── UI Events
│
├── Domain
│   ├── Models
│   ├── Repository Interfaces
│   └── Use Cases
│
├── Data
│   ├── Repository Implementations
│   ├── Local Data Sources
│   ├── Remote Data Sources
│   └── Data Mappers
│
└── Core
    ├── Design System
    ├── DataStore
    ├── Navigation
    ├── Utilities
    └── Dependency Injection
```

### Architectural Principles

- **Separation of concerns:** Each layer has a clearly defined responsibility.
- **Dependency inversion:** Domain logic depends on abstractions rather than implementation details.
- **Unidirectional data flow:** UI state is observed from ViewModels, while user actions are sent back as events.
- **Repository pattern:** Data access is abstracted behind repository interfaces.
- **Modularity by feature:** Features can evolve independently as the application grows.
- **Testability:** Business logic and data access can be tested independently.

## Project Structure

The package namespace is `com.sonisoft.datasetu`.

```text
app/src/main/java/com/sonisoft/datasetu/
│
├── DataSetuApplication.kt
├── MainActivity.kt
│
├── core/
│   ├── datastore/
│   ├── network/
│   ├── permission/
│   ├── ui/
│   │   ├── components/
│   │   └── theme/
│   └── util/
│
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
│
├── data/
│   ├── local/
│   ├── remote/
│   ├── mapper/
│   └── repository/
│
├── feature/
│   ├── splash/
│   ├── onboarding/
│   ├── auth/
│   ├── home/
│   ├── discovery/
│   ├── send/
│   ├── receive/
│   ├── transfer/
│   ├── history/
│   └── settings/
│
├── navigation/
│   ├── AppNavGraph.kt
│   ├── AppNavigator.kt
│   └── route/
│
└── di/
```

The structure represents the intended architecture; directories and features may be introduced incrementally as implementation progresses.

## Design Philosophy

DataSetu uses a deliberately restrained visual system.

| Token                | Color     |
| -------------------- | --------- |
| Primary background   | `#000000` |
| Secondary background | `#121212` |
| Card surface         | `#1A1A1A` |
| Borders and dividers | `#2A2A2A` |
| Secondary text       | `#9E9E9E` |
| Primary text         | `#FFFFFF` |

Design priorities include:

- Minimalist layouts with clear visual hierarchy.
- Consistent spacing and rounded components.
- Smooth, purposeful transitions.
- High-contrast text and controls.
- Reusable Compose components.
- Responsive layouts for different Android screen sizes.

## Getting Started

### Prerequisites

Install the following development tools:

- Android Studio.
- JDK compatible with the project's Gradle configuration.
- Android SDK and platform tools required by the project.
- Git.

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/Vinayak-Soni-Git/DataSetu.git
cd DataSetu
```

Replace the placeholder with your actual GitHub repository URL.

**2. Open the project**

Open the cloned directory in Android Studio and allow Gradle to sync.

**3. Configure the project**

Verify that your Android SDK, Gradle configuration, Kotlin plugin, Compose dependencies, and Hilt setup are configured correctly.

**4. Build the application**

On Windows:

```bash
gradlew.bat assembleDebug
```

On macOS or Linux:

```bash
./gradlew assembleDebug
```

**5. Run the app**

Connect an Android device with USB debugging enabled or start an Android emulator. Select the `app` configuration in Android Studio and click **Run**.

> The exact SDK requirements depend on the versions configured in the repository's Gradle files.

## Development Roadmap

- [x] Establish project identity and monochrome design direction.
- [x] Implement the initial splash screen.
- [x] Implement the animated onboarding UI.
- [x] Establish the navigation and dependency injection foundations.
- [x] Begin the DataStore persistence foundation.
- [x] Complete offline signup and login or local profile management.
- [x] Implement startup routing based on persisted onboarding and session state.
- [x] Build the main dashboard.
- [x] Implement file selection and storage access.
- [x] Implement nearby device discovery and connection handling.
- [x] Implement peer-to-peer file sending and receiving.
- [x] Add real-time transfer progress and history.
- [x] Add secure pairing and encrypted transfer.
- [x] Implement supported background and resumable transfers.
- [x] Add automated tests, performance checks, and release validation.

*Roadmap items should be checked off only when implemented and verified.*

## Security and Privacy

DataSetu is intended to support local-first file sharing. The final security model will depend on the implemented transfer protocol and pairing mechanism.

Security priorities include:

- Avoiding plaintext password storage.
- Restricting file access to user-selected content where possible.
- Authenticating transfer peers before accepting sensitive transfers.
- Encrypting data in transit when supported by the implemented protocol.
- Validating incoming file metadata and transfer requests.
- Handling permissions and temporary files responsibly.

Local transfer does not automatically guarantee encryption or device authentication. These protections must be implemented and tested before the app is described as fully secure.

## Contributing

Contributions, suggestions, bug reports, and feature ideas are welcome.

1. Fork the repository.
2. Create a feature branch.
3. Implement your changes with clear naming and separation of concerns.
4. Run relevant tests and verify the app builds.
5. Submit a pull request describing the changes.

For larger changes, open an issue first to discuss the proposed implementation.

## Author

**Vinayak Soni**

Android Developer | Kotlin | Jetpack Compose

- GitHub: [Vinayak-Soni-Git](https://github.com/Vinayak-Soni-Git)

---

DataSetu — Bridging Your Digital World.
Built with Kotlin. Designed for simplicity. Engineered to scale.
