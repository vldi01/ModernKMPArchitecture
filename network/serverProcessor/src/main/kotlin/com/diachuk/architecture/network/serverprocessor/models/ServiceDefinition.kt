package com.diachuk.architecture.network.serverprocessor.models

data class ServiceDefinition(
    val packageName: String,
    val interfaceName: String,
    val simpleName: String,
    val endpoints: List<EndpointDefinition>,
    val containingFile: com.google.devtools.ksp.symbol.KSFile
)
