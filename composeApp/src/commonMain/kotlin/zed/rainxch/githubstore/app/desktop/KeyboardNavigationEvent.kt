package zed.rainxch.githubstore.app.desktop

sealed interface KeyboardNavigationEvent {
    data object OnCtrlFClick : KeyboardNavigationEvent
}
