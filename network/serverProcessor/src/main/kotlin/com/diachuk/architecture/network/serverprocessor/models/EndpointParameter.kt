package com.diachuk.architecture.network.serverprocessor.models

import com.squareup.kotlinpoet.TypeName

data class EndpointParameter(
    val name: String,
    val typeName: TypeName,
    val type: ParameterType,
    val keyName: String // For @Query("key"), @Part("key")
)