
# Ktorfit Server Processor

This KSP (Kotlin Symbol Processing) processor bridges the gap between client and server by generating Ktor server-side routing code directly from your Ktorfit-annotated interfaces.

By defining your API once in a shared module, you establish a **Single Source of Truth**. The processor automatically generates the boilerplate required to bind these interfaces to Ktor's routing system, handling request parsing, type conversion, and authentication.

## Features

-   **Shared Contract**: Use the exact same interface for your Retrofit/Ktorfit client and your Ktor server.

-   **Automated Routing**: Generates Ktor `Route` extension functions (e.g., `bindUserApi`) to wire up endpoints.

-   **Smart Request Parsing**: Automatically parses `@Path`, `@Query`, `@Body`, and `@Multipart` arguments.

-   **Integrated Authentication**: Seamless support for JWT-based auth via `@AuthJwt`, mapping tokens directly to strongly-typed objects.

-   **Context Propagation**: accessible `ApplicationCall` context within your service implementation.


## Project Structure

To use this effectively, your project should ideally follow a multi-module structure:

1.  **`:network:core`**: Contains your custom auth annotations (`@AuthJwt`, `@JwtType`) and the `CallContext` utility.

2.  **`:shared` (or `:common`)**: Contains the API interfaces annotated with Ktorfit. Depends on `:network:core`.

3.  **`:server`**: Implements the interfaces and runs the Ktor application. Applies the KSP processor here.


## Setup

### 1. Apply KSP Plugin

In your **server** module's `build.gradle.kts`:

```
plugins {
    id("com.google.devtools.ksp") version "x.x.x"
}

dependencies {
    // KSP Processor
    ksp(project(":network:serverProcessor"))

    // Implementation of the shared interface
    implementation(project(":shared"))
    
    // Core definitions (Auth annotations, CallContext)
    implementation(project(":network:core"))
}

```

### 2. Define Custom Annotations (in `:network:core`)

Ensure you have the core annotations available:

-   `@AuthJwt`: Annotates interface methods to require specific token types.

-   `@NoAuth`: Excludes a method from the default authentication.

-   `@JwtType`: Marks a class as a JWT token payload.

-   `@DefaultJwtType`: Marks the default JWT token payload.


## Usage Guide

### 1. Define the Shared Interface

In your shared module, define the API using Ktorfit annotations.

**`commonMain/kotlin/.../UserApi.kt`**

```
import com.diachuk.architecture.network.core.annotations.* import de.jensklingenberg.ktorfit.http.* import io.ktor.http.content.PartData

interface UserApi {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Long): User

    @POST("users")
    suspend fun createUser(@Body user: User): User

    @NoAuth
    @GET("users/public")
    suspend fun getPublicInfo(): String

    @Multipart
    @POST("users/avatar")
    suspend fun uploadAvatar(@Part("avatar") content: List<PartData>): String
}

```

### 2. Implement the Service

In your server module, implement the interface.

**`server/src/.../UserApiImpl.kt`**

```
import com.diachuk.architecture.network.core.getCall // Helper function

class UserApiImpl(private val userDao: UserDao) : UserApi {
    
    override suspend fun getUser(userId: Long): User {
        // You can access the raw Ktor call if needed (e.g. for headers/cookies)
        val call = getCall() 
        return userDao.getUserById(userId) ?: throw NotFoundException()
    }

    override suspend fun createUser(user: User): User {
        return userDao.insertUser(user)
    }

    override suspend fun getPublicInfo(): String {
        return "Public Data"
    }

    override suspend fun uploadAvatar(content: List<PartData>): String {
        // Handle multipart data...
        return "Uploaded"
    }
}

```

### 3. Define Token Models

Define your JWT payload models. These are used to strongly type the `principal` inside the generated code.

**`UserToken.kt`**

```
import com.diachuk.architecture.network.core.annotations.DefaultJwtType
import kotlinx.serialization.Serializable

@DefaultJwtType 
@Serializable
data class UserToken(
    val userId: Long,
    val username: String
)

```

### 4. Wire It Up

In your Ktor Application module, configure authentication and routing.

**`Application.kt`**

```
import com.diachuk.architecture.network.api.user.bindUserApi // Generated
import com.diachuk.architecture.network.server.configureJwt // Generated

fun Application.module() {
    install(Authentication) {
        // configureJwt is generated based on your @JwtType classes
        configureJwt(
            verifier = JWT.require(Algorithm.HMAC256("secret")).build(),
            // Named arguments correspond to your token classes (camelCase)
            userToken = { token ->
                // Validate the parsed token object
                if (userDao.exists(token.userId)) token else null
            }
        )
    }

    routing {
        // Bind the implementation to the routes
        bindUserApi(UserApiImpl(userDao))
    }
}

```

## Advanced Topics

### Accessing the `ApplicationCall`

The processor wraps every implementation call in a `CallContext`. To access headers, cookies, or the raw request from within your `UserApiImpl`, use the `getCallContext<UserToken>()` or `getCall()` helper function:
