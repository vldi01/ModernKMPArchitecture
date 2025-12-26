# Technical Documentation

## Getting Started

### Prerequisites
* **JDK:** 17 (Required for Android/Gradle).
* **Android Studio:** Ladybug (2024.2.1) or newer.
* **Xcode:** 15+ (Required only if building the iOS target).

### Setup Guide
1.  **Clone the Repo:**
    ```bash
    git clone https://github.com/vladyslavdiachuk/ModernArchitecture.git
    ```
2.  **Sync Gradle:** Open the project in Android Studio. Wait for the indexing to complete.
3.  **Verify Environment:**
    * Ensure your `local.properties` has `sdk.dir` pointing to your Android SDK.
    * (Optional) If you plan to run iOS from Android Studio, ensure the KMM plugin is active.

### Running the Application
#### 1. Backend (Server)
The client needs the backend to be running to function correctly.
* **Terminal:** Run `./gradlew :server:run`
* **Address:** The server defaults to `0.0.0.0:8080`.
    * *Note:* The Android Emulator will access this via `10.0.2.2:8080`.
    * *Note:* The iOS Simulator will access this via `localhost:8080`.

#### 2. Client (Android)
* Select the `composeApp` configuration in the toolbar.
* Select an Emulator (API 26+).
* Click **Run**.

#### 3. Client (iOS)
* Open `iosApp/iosApp.xcodeproj` in Xcode OR select the `iosApp` configuration in Android Studio.
* Select a Simulator.
* Click **Run**.

---

## Architecture Overview

This project implements a **Contract-First**, **Kotlin Multiplatform (KMP)** architecture. It is designed to maximize code sharing between Client (Android, iOS, Desktop) and Server (Ktor), while ensuring strict separation of concerns within the client application.

### Key Philosophy
1.  **Contract-First:** API interfaces are defined once in a shared module and drive both the Client's HTTP requests and the Server's routing logic.
2.  **API/Impl Separation:** Feature modules are split into `:api` (public) and `:impl` (private) to enforce encapsulation and speed up build times.
3.  **Functional Package Structure:** The internal structure of features follows a functional approach (UI, Logic, Data) rather than a rigid "Clean Architecture" layering, minimizing boilerplate.

---

## Module Structure

The project is divided into several high-level scopes:

### 1. `network/` (The Shared Contract)
This is the heart of the full-stack architecture.
*   **`api/`**: Contains pure Kotlin interfaces annotated with **Ktorfit** annotations (`@GET`, `@POST`). These interfaces define the contract between Client and Server.
    *   *Example:* `UserApi`, `AuthApi`.
*   **`core/`**: Networking utilities and infrastructure.
    *   **Client Builder**: `ClientBuilder` configures the standard Ktor `HttpClient` and the **Ktorfit** instance with JSON serialization, logging, and base URL.
    *   **Error Handling**: Provides `safeApiCall` to wrap network requests and map Ktor exceptions to a strongly typed `NetworkException` hierarchy.
    *   **Security Annotations**: Defines markers like `@NoAuth` and `@AuthJwt` used to control access levels on API endpoints.
*   **`serverProcessor/`**: A custom **KSP (Kotlin Symbol Processing)** module.
    *   **Function:** It scans the interfaces in `network/api` for Ktorfit annotations.
    *   **Output:** It generates server-side Ktor routing code. This ensures that the server implementation matches the client's expectation 1:1.
    *   *Key Classes:* `ServerProcessor`, `RouteGenerator`.

### 2. `client/` (Frontend Application)
The client application is composed of a generic core, a centralized database, and modularized features.

*   **`core/`**: Entry point and generic utilities.
*   **`database/`**: Centralized **Room** database configuration.
    *   *Key Class:* `AppDatabase`.
    *   *Configuration:* It aggregates `@Entity` classes from various feature API modules (e.g., `UserEntity` from `features/user/api`).
*   **`resources/`**: Centralized UI resources (Strings, Drawables, Fonts). Used by all feature modules to ensure consistency.
*   **`features/`**: Vertical slices of functionality (Auth, Home, User). Each feature is split into:
    *   **`:api` Module**:
        *   **Purpose**: The public face of the feature.
        *   **Contents**:
            *   **Navigation Destinations**: (e.g., `LoginDestination`)
            *   **Domain Models/Entities**: (e.g., `TokenEntity`). Note: Entities are placed here so the `:database` module can see them.
            *   **Public Interfaces**: (e.g., `TokenDao` interface).
    *   **`:impl` Module**:
        *   **Purpose**: The private implementation. NO other module should depend on this.
        *   **Contents**:
            *   `ui/`: Compose Multiplatform screens (`@Composable`).
            *   `logic/`: ViewModels (`@KoinViewModel`) and State management.
            *   `navigation/`: `ScreenInjector` implementations that register the UI to the Navigation system.
            *   `di/`: Koin modules.

### 3. `composeApp/` (Application Shell)
This is the main entry point for the client applications.
*   **Role**: It acts as the "Shell" that hosts the KMP application.
*   **Responsibilities**:
    *   **Platform Bootstrapping**: Contains `MainActivity` (Android), `Main.kt` (Desktop), and `MainViewController` (iOS).
    *   **Initialization**: Sets up Koin and the root `App` composable from `:client:core`.
    *   **Targets**:
        *   `androidMain`: Generates the Android APK.
        *   `iosMain`: Generates the iOS Framework.
        *   `jvmMain`: Generates the Desktop application.

### 4. `server/` (Backend Application)
*   **Implementation**: A Ktor server application.
*   **Routing**: Instead of manually defining `routing { get(...) }`, it uses the code generated by the `serverProcessor` from the `network` interfaces.
*   **Dependency Injection**: Uses Koin to inject controllers that implement the shared interfaces.

---

## Technical Details

### Client-Side Networking
*   **Library:** [**Ktorfit**](https://foso.github.io/Ktorfit/)
*   **Mechanism:** Ktorfit generates the HTTP client implementation from the interfaces in `network/api` at compile time.
*   **Usage:** ViewModels inject the API interface (e.g., `UserApi`) and call functions directly.

### Server-Side Routing (ServerProcessor)
*   **Mechanism:** The custom KSP processor in `network/serverProcessor` reads the same `UserApi` interface used by the client.
*   **Generation:** It generates a Ktor `Route` extension function (e.g., `bindUserApi`) that handles the HTTP request, deserializes parameters, checks authentication, and invokes the implementation.

#### Example: How it works
1.  **Define the Interface** (`network/api`):
    ```kotlin
    interface AuthApi {
        @POST("auth/login")
        @NoAuth
        suspend fun login(@Body request: LoginRequest): AuthResponse
    }
    ```

2.  **Server-Side Wiring** (`server/Application.kt`):
    Instead of writing routes manually, we just bind the implementation using the generated function.
    ```kotlin
    routing {
        // 'bindAuthApi' is generated by ServerProcessor
        // 'get<AuthApi>()' resolves the actual implementation via Koin
        bindAuthApi(get<AuthApi>())
    }
    ```

3.  **Generated Code (Conceptual)**:
    The processor generates something like this under the hood:
    ```kotlin
    fun Route.bindAuthApi(impl: AuthApi) {
        post("auth/login") {
            val body = call.receive<LoginRequest>()
            val result = impl.login(body)
            call.respond(result)
        }
    }
    ```

4.  **Implementation (Server)**:
    You simply implement the interface as a standard Kotlin class.
    ```kotlin
    @Single
    class AuthApiImpl : AuthApi {
        override suspend fun login(request: LoginRequest): AuthResponse {
            // Logic here...
            return AuthResponse.Authorized(token = "...")
        }
    }
    ```

### Resources (KMP)
* **Library:** `compose.components.resources` (Jetbrains).
* **Location:** `client/resources/src/commonMain/composeResources`.
    * `drawable/`: Images and SVGs.
    * `values/`: `strings.xml` for localization.
* **Usage:**
    * In Compose: `stringResource(Res.string.my_string)`
    * *Note:* Resources are generated at build time into the `Res` object.

### Database (Room KMP)
*   **Library:** **androidx.room** (KMP version) + **SQLite**.
*   **Architecture:**
    *   Entities (e.g., `TokenEntity`) are defined in Feature API modules (`:features:auth:api`).
    *   The Database module (`:client:database`) depends on all Feature API modules.
    *   `AppDatabase` includes these entities in its `entities` array.
    *   DAOs are defined in Feature APIs but implemented by Room in the Database module.
*   **Schema Management:**
    *   Schemas are exported to `client/database/schemas`.
    *   This is configured in `client/database/build.gradle.kts` via `room { schemaDirectory(...) }`.
    *   JSON schema files (e.g., `1.json`) should be committed to version control to track database changes.
*   **Pre-population:**
    *   The database is currently **empty** on first launch (no pre-population).

### Navigation
*   **Library:** **Navigation3** (Compose Multiplatform).
*   **Pattern:** Registry-based.
    *   Features define strictly typed `Destination` classes in their `:api` module.
    *   Features register their UI binders (`ScreenInjector`) in their `:impl` module.
    *   The main application orchestrates navigation without hard dependencies between feature implementations.

### Dependency Injection
*   **Library:** **Koin** + **Koin Annotations**.
*   **Usage:**
    *   `@KoinViewModel` for ViewModels.
    *   `@Single` / `@Factory` for services.
    *   `@Module` for configuration.

---

## Package Structure (Feature Implementation)

Inside a typical `:impl` module (e.g., `client/features/auth/impl`), the package structure is flat and functional:

```
com.diachuk.modernarchitecture.features.auth
├── ui/           # Composable functions (Screens, Components)
├── logic/        # ViewModels, State classes, Event classes
├── navigation/   # ScreenInjector implementation
└── di/           # Koin Module definition
```

## State Management

We use a **Unidirectional Data Flow (UDF)** pattern, similar to MVI (Model-View-Intent).

### Pattern
1.  **State**: An immutable data class (e.g., `LoginState`) holding all data required for the UI.
2.  **Events**: Sealed interfaces (e.g., `LoginEvent`) representing user actions or system events.
3.  **ViewModel**:
    *   Holds the state in a `MutableStateFlow`.
    *   Exposes it as a read-only `StateFlow`.
    *   Processes events via a single public method `onEvent(event: LoginEvent)`.
    *   Updates state using `.update { ... }`.

### Error Handling
*   **Network Errors**: Exceptions are caught in `safeApiCall` and mapped to a `Result`.
*   **UI Consumption**: Errors are currently handled **individually** by each ViewModel. The error message is stored in the State (e.g., `state.error`) and displayed by the UI (e.g., via a Text field or simple conditional rendering).
*   *Note*: There is currently no global "Snackbar Event Bus". Each feature manages its own error presentation locally.

#### Example ViewModel
```kotlin
@KoinViewModel
class LoginViewModel(private val useCase: LoginUseCase) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> login(event)
        }
    }

    private fun login(event: LoginEvent.Login) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val result = useCase.execute(event.email, event.password)
            
            // Handle Success/Error locally
            when (result) {
                LoginResult.Success -> { /* Navigate */ }
                is LoginResult.Error -> _state.update { it.copy(error = result.message) }
            }
            
            _state.update { it.copy(isLoading = false) }
        }
    }
}
```

## Security & Authentication Flow

### Token Storage
Tokens are stored using a **Room Database** table (`TokenEntity`) accessed via a `TokenStore` interface.
*   **Location**: `client/database` (SQLite).
*   **Encryption**: Currently, tokens are stored in **plain text**. For production apps handling sensitive data, this should be replaced with EncryptedSharedPreferences (Android) and Keychain (iOS).

> [!WARNING]
> **Security Critical:**
> Storing tokens in plain text in a database is highly insecure and is done here for **demonstration purposes only**.
> In a production environment, you **MUST** replace this implementation with secure storage solutions:
> *   **Android**: `EncryptedSharedPreferences` or `androidx.security.crypto.EncryptedFile`.
> *   **iOS**: `Keychain` (e.g., via a KMP wrapper library like `Settings` or dedicated Keychain library).

### Interceptor Logic
The application uses a custom Ktor Client Plugin (`AuthPluginProvider`) to automatically inject tokens into HTTP requests.
1.  **Resolution**: The `AuthTokenResolver` inspects the request's Ktorfit annotations.
    *   If `@NoAuth` is present: No header is added.
    *   If `@AuthJwt` is present: It fetches the token for the specified class (e.g., `UserToken`) from `TokenStore`.
    *   Default: Uses `UserToken`.
2.  **Injection**: Adds `Authorization: Bearer <token>` header if a token is found.

### Refresh Logic
There is currently **no automatic token refresh** logic.
*   **Expiration**: If a token expires, the server returns a `401 Unauthorized`.
*   **Handling**: This error is caught by `safeApiCall` and returned as a `NetworkException.ApiError`.
*   **Action**: The ViewModel receives this error and is responsible for handling it (e.g., navigating to Login).

## Testing

Unit tests are an integral part of this architecture, focusing on the business logic layer (UseCases, ViewModels, and Resolvers).

### Stack
*   **Framework**: `kotlin-test` (Standard Kotlin testing library).
*   **Coroutines**: `kotlinx-coroutines-test` (For `runTest` and suspending functions).
*   **Mocking**: [**Mokkery**](https://mokkery.dev/) (A Kotlin Multiplatform mocking library using KSP).

### Location
Tests are located in the `commonTest` source set of the `:impl` modules (e.g., `client/features/auth/impl/src/commonTest`). This ensures that business logic, which resides in `commonMain`, is tested in a platform-independent way.

### Example
Here is how a typical UseCase test looks using Mokkery:

```kotlin
class LoginUseCaseTest {

    // 1. Mock dependencies
    private val authApi = mock<AuthApi>()
    private val tokenStore = mock<TokenStore>()
    private val loginUseCase = LoginUseCase(authApi, tokenStore)

    @Test
    fun `execute returns Success when login is successful`() = runTest {
        // 2. Define behavior
        everySuspend { authApi.login(any()) } returns AuthResponse.Authorized("token")
        everySuspend { tokenStore.saveToken(any(), any()) } returns Unit

        // 3. Execute logic
        val result = loginUseCase.execute("test@example.com", "password")

        // 4. Verify results
        assertTrue(result is LoginResult.Success)
        verifySuspend { tokenStore.saveToken(JwtEntity.UserToken::class, "token") }
    }
}
```

### Configuration
The `BaseMultiplatformPlugin` automatically applies the **Mokkery** plugin and adds the necessary dependencies (`kotlin-test`, `coroutines-test`) to all modules, so you don't need to manually configure build scripts for basic unit testing.

## Build System (Convention Plugins)

The project uses Gradle Convention Plugins in `buildSrc` to centralize build configuration and avoid duplicating logic across the many feature modules.

### `BaseMultiplatformPlugin`
*   **Role**: The foundation for every KMP module.
*   **Applied Plugins**:
    *   `kotlin("multiplatform")`
    *   `com.android.library`
    *   `com.google.devtools.ksp`
    *   `kotlin("plugin.serialization")`
*   **Configuration**:
    *   **Android**: Sets SDK versions (compileSdk 36, minSdk 26) and generates namespaces dynamically based on project path.
    *   **Kotlin**: Configures iOS targets (`iosArm64`, `iosSimulatorArm64`), JVM target (11), and common compiler arguments (`-Xexpect-actual-classes`).
    *   **DI Setup**: Automatically adds **Koin** and **Koin Annotations** dependencies to `commonMain` and sets up the KSP processor for Koin.

### `ComposePlugin`
*   **Role**: Adds UI capabilities to a module.
*   **Applied Plugins**:
    *   `org.jetbrains.compose`
    *   `org.jetbrains.kotlin.plugin.compose` (Compiler)
    *   `org.jetbrains.compose.hot-reload`
*   **Dependencies**: Automatically adds the Compose dependencies to `commonMain`:
    *   Standard libraries: `runtime`, `foundation`, `material3`, `ui`, `components.resources`.
    *   **Navigation3** runtime.
    *   `koin-compose-viewmodel`.
