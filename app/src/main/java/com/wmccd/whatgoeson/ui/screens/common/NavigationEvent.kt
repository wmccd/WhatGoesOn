package com.wmccd.whatgoeson.ui.screens.common

sealed class NavigationEvent {
    object NavigateToNextScreen : NavigationEvent()
}