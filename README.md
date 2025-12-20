
# 🏛️ ModernArchitecture KMP Template

> "Architecture is the art of organizing chaos. A great architect knows when to use a tool, and when to keep the toolbox closed."

This project is a high-performance, opinionated **Kotlin Multiplatform (KMP)** template designed for enterprise-scale applications. It bridges the gap between frontend and backend using a **"Contract-First"** philosophy, ensuring that your Android, iOS, Desktop, and Ktor Server are always in perfect sync with zero boilerplate.

## 🏗 Top-Level Module Structure

The project is organized into logical scopes to separate the "What" from the "How."

-   **`client/`**: The frontend applications (Android, iOS, Desktop).

    -   **`:core`**: The client entry point and generic utilities. _Kept_ small to avoid _frequent recompilation._

    -   **`database/`**: Centralized Room (KMP) configuration.

    -   **`:navigation`**: The decoupled navigation engine.

    -   **`:features`**: The vertical business slices of the application.

-   **`:composeApp`**: The platform entry points and UI shell.

-   **`server/`**: The Ktor backend implementation.

-   **`network/`**: The **"Shared Contract"** containing DTOs and API interfaces.

-   **`buildSrc/`**: The "Conductor" using Gradle Convention Plugins to centralize build logic.


## 🧩 The API/Impl Split Pattern

To ensure lightning-fast incremental builds and strict encapsulation, every feature in `:features` is divided into two Gradle modules.

### 1. API (`:features:x:api`)

-   **Purpose:** The public contract.

-   **Contents:** Navigation Destinations, public Interfaces, and database entities.

-   **Why Entities here?** Placing `@Entity` classes in the API allows the central `:database` module to see them without depending on the feature's internal logic.


### 2. Implementation (`:features:x:impl`)

-   **Purpose:** The "Private Brain" of the feature.

-   **Contents:** UI (Compose), ViewModels, Logic, and DI.

-   **Rule:** Other features **never** depend on implementation modules. This prevents dependency "spaghetti."


## 📦 Functional Package Structure

Inside an `:impl` module, we avoid "Architecture Ceremony." We use a flat, functional structure that grows only when needed.

```
:features:auth:impl
├── ui/           # Pure, stateless Compose screens (@Composable)
├── logic/        # The "Brain". ViewModels and State models.
│                 # API calls happen here directly by default.
├── data/         # (Optional) Repositories/DAOs. 
│                 # Only create this if you have complex "Offline-First" logic.
├── navigation/   # ScreenInjector registering UI to Destination.
└── di/           # Koin @Module configuration.
```
## 🚀 Key Philosophy: "Just-in-Time" Architecture

We reject the dogma of "Clean Architecture" that forces you to create empty files like `UseCases`, `Repositories`, or `DataStores` for simple features.

**Simplicity is the default:**

-   **No Boilerplate:** We do not enforce strict dependency rules (Domain vs. Data vs. Presentation) _inside_ a feature module. Since it is compiled as a single `:impl` module, these artificial boundaries only add noise.

-   **Direct Access:** It is perfectly acceptable for a `ViewModel` to call an API interface directly.

-   **Evolve, Don't Pre-optimize:** You create a `Repository` or `UseCase` **only** if the logic becomes complex or requires offline-first handling. If you are just fetching data and showing it, keep the toolbox closed.

## 🌐 Shared Network (Full-Stack Type Safety)

This is the **"Killer Feature"** of this template. We define our API once as a Kotlin Interface in the `:network` module.

1.  **Contract-First:** You write the interface first.

2.  **Client (Mobile/Desktop):** Uses **Ktorfit** to auto-generate the network client implementation.

3.  **Server (Ktor):** A **Custom KSP Processor** generates the Ktor Server routing blocks (GET, POST, Auth validation) directly from the interface.


**Result:** A change in the network interface is a compile-time error in both the Backend and the Mobile App. No more "404 Not Found" or parsing bugs.

## 🧭 Decoupled Navigation (The Registry)

Features do not know about each other; they only know about **Destinations** defined in API modules.

-   **Registry Pattern:** Features register their screens into a global scope using a `ScreenInjector`.

-   **Zero-Knowledge:** Feature A navigates to Feature B by passing a `DestinationB` object. The navigator finds the correct Composable via Koin-powered map injection.

## 🛠 Tech Stack

-   **UI:** Compose Multiplatform (Android, iOS, Desktop)

-   **DI:** Koin + Koin Annotations

-   **Networking:** Ktor + Ktorfit + Custom KSP
