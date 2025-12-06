package com.diachuk.architecture.network.api.user

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(val results: List<User>, val count: Int)