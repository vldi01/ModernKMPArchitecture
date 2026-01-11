package com.diachuk.architecture.network.serverprocessor.models

import com.squareup.kotlinpoet.TypeName

data class EndpointDefinition(
    val name: String,
    val method: String, // GET, POST, etc.
    val path: String,
    val parameters: List<EndpointParameter>,
    val returnType: TypeName,
    val authConfig: AuthConfig,
    val isMultipart: Boolean
)