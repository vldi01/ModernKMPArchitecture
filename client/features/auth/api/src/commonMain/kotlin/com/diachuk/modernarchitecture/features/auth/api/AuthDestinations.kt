package com.diachuk.modernarchitecture.features.auth.api

import com.diachuk.modernarchitecture.navigaion.Destination
import kotlinx.serialization.Serializable

@Serializable
data object LoginDestination : Destination

@Serializable
data object RegisterDestination : Destination

@Serializable
data object SplashDestination : Destination
