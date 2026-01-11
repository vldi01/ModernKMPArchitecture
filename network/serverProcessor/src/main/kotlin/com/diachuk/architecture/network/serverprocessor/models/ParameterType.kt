package com.diachuk.architecture.network.serverprocessor.models

sealed class ParameterType {
    data object Body : ParameterType()
    data object Path : ParameterType()
    data object Query : ParameterType()
    data object QueryMap : ParameterType()
    data object QueryName : ParameterType()
    data object Part : ParameterType()
}