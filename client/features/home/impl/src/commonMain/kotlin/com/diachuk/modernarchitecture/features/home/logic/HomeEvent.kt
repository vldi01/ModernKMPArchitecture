package com.diachuk.modernarchitecture.features.home.logic

sealed interface HomeEvent {
    data object OnLogoutClick : HomeEvent
}
