package zed.rainxch.core.presentation.utils

import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import zed.rainxch.core.domain.model.error.RateLimitException
import zed.rainxch.githubstore.core.presentation.res.Res
import zed.rainxch.githubstore.core.presentation.res.user_error_generic
import zed.rainxch.githubstore.core.presentation.res.user_error_not_found
import zed.rainxch.githubstore.core.presentation.res.user_error_offline
import zed.rainxch.githubstore.core.presentation.res.user_error_rate_limited
import zed.rainxch.githubstore.core.presentation.res.user_error_secure_connection
import zed.rainxch.githubstore.core.presentation.res.user_error_server
import zed.rainxch.githubstore.core.presentation.res.user_error_timeout

/**
 * Plain-language categories for failures that reach the UI. Classification is text based on purpose:
 * it works in common code (no java.net / Ktor types) and for every platform's exception names.
 */
enum class UserErrorKind {
    Offline,
    Timeout,
    RateLimited,
    NotFound,
    SecureConnection,
    Server,
    Generic,
}

private val RATE_LIMIT_PATTERN = Regex("""rate.?limit|too many requests|\b429\b|\b403\b""")
private val OFFLINE_PATTERN =
    Regex(
        "unknownhost|unresolvedaddress|unable to resolve host|no address associated|failed to connect|" +
            "connectexception|network is unreachable|no route to host|connection refused|connection reset|" +
            "software caused connection abort|nodataexception|unexpected end of stream|offline",
    )
private val TIMEOUT_PATTERN = Regex("""timeout|timed out""")
private val SECURE_PATTERN = Regex("""\bssl|certificate|handshake|trust anchor|cert path""")
private val NOT_FOUND_PATTERN = Regex("""\b404\b|not found""")
private val SERVER_PATTERN = Regex("""\b50[0-4]\b|bad gateway|service unavailable|internal server error""")

private const val MAX_CAUSE_DEPTH = 4

private fun Throwable.classificationText(): String =
    buildString {
        var current: Throwable? = this@classificationText
        var depth = 0
        while (current != null && depth < MAX_CAUSE_DEPTH) {
            append(current::class.simpleName).append(' ').append(current.message).append(' ')
            current = current.cause
            depth++
        }
    }.lowercase()

fun Throwable.userErrorKind(): UserErrorKind {
    if (this is RateLimitException) return UserErrorKind.RateLimited
    val text = classificationText()
    return when {
        RATE_LIMIT_PATTERN.containsMatchIn(text) -> UserErrorKind.RateLimited
        OFFLINE_PATTERN.containsMatchIn(text) -> UserErrorKind.Offline
        TIMEOUT_PATTERN.containsMatchIn(text) -> UserErrorKind.Timeout
        SECURE_PATTERN.containsMatchIn(text) -> UserErrorKind.SecureConnection
        NOT_FOUND_PATTERN.containsMatchIn(text) -> UserErrorKind.NotFound
        SERVER_PATTERN.containsMatchIn(text) -> UserErrorKind.Server
        else -> UserErrorKind.Generic
    }
}

/**
 * A short, friendly sentence that is safe to show to normal users (never a raw exception message).
 * [fallback] is used for failures we cannot classify, so screens can keep their own wording
 * (e.g. "Search failed"); without it the generic "Something went wrong while loading this page." is used.
 */
suspend fun Throwable.toUserMessage(fallback: StringResource? = null): String =
    when (userErrorKind()) {
        UserErrorKind.Offline -> getString(Res.string.user_error_offline)
        UserErrorKind.Timeout -> getString(Res.string.user_error_timeout)
        UserErrorKind.RateLimited -> getString(Res.string.user_error_rate_limited)
        UserErrorKind.NotFound -> getString(Res.string.user_error_not_found)
        UserErrorKind.SecureConnection -> getString(Res.string.user_error_secure_connection)
        UserErrorKind.Server -> getString(Res.string.user_error_server)
        UserErrorKind.Generic -> getString(fallback ?: Res.string.user_error_generic)
    }

/** Raw details for the optional "Technical details" section and for bug reports. */
fun Throwable.technicalDetails(): String =
    buildString {
        var current: Throwable? = this@technicalDetails
        var depth = 0
        while (current != null && depth < MAX_CAUSE_DEPTH) {
            if (depth > 0) append("\ncaused by: ")
            append(current::class.simpleName ?: "Throwable")
            current.message?.takeIf { it.isNotBlank() }?.let { append(": ").append(it.take(500)) }
            current = current.cause
            depth++
        }
    }
