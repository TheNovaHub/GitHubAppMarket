package zed.rainxch.apps.presentation.starred

sealed interface StarredPickerEvent {
    data class NavigateToDetails(
        val repoId: Long,
        val owner: String,
        val repo: String,
    ) : StarredPickerEvent

    data object NavigateBack : StarredPickerEvent
}
