package zed.rainxch.core.domain.system

interface AggressiveOemDetector {
    fun isAggressiveOem(): Boolean

    fun isBatteryOptimizationIgnored(): Boolean

    fun openBatteryOptimizationSettings(): Boolean
}
