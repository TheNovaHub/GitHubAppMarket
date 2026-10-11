package zed.rainxch.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BackendExploreResponse(
    val items: List<BackendRepoResponse>,
    val page: Int,
    val hasMore: Boolean,
)
