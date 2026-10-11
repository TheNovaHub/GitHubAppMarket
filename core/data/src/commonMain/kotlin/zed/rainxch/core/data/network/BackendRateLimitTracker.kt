package zed.rainxch.core.data.network

import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Clock

object BackendRateLimitTracker {
    private const val PREEMPT_THRESHOLD = 10

    data class Snapshot(
        val remaining: Int? = null,
        val resetEpochSec: Long? = null,
        val limit: Int? = null,
    )

    private val state = MutableStateFlow(Snapshot())

    fun record(response: HttpResponse) {
        val remaining = response.headers["X-RateLimit-Remaining"]?.toIntOrNull()
        val resetEpoch = response.headers["X-RateLimit-Reset"]?.toLongOrNull()
        val limit = response.headers["X-RateLimit-Limit"]?.toIntOrNull()
        if (remaining == null && resetEpoch == null && limit == null) return
        state.update { current ->
            current.copy(
                remaining = remaining ?: current.remaining,
                resetEpochSec = resetEpoch ?: current.resetEpochSec,
                limit = limit ?: current.limit,
            )
        }
    }

    fun shouldPreempt(): Boolean {
        val snapshot = state.value
        val r = snapshot.remaining ?: return false
        if (r > PREEMPT_THRESHOLD) return false
        val resetSec = snapshot.resetEpochSec ?: return r <= 0
        val now = Clock.System.now().epochSeconds
        return resetSec > now
    }

    fun snapshot(): Snapshot = state.value
}
