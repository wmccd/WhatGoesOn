package com.wmccd.whatgoeson.presentation.screens.common

sealed class NavigationEvent {
    object NavigateToNextScreen : NavigationEvent()
}