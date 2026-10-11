package zed.rainxch.core.data.services

import zed.rainxch.core.domain.system.AggressiveOemDetector

class DesktopAggressiveOemDetector : AggressiveOemDetector {
    override fun isAggressiveOem(): Boolean = false

    override fun isBatteryOptimizationIgnored(): Boolean = true

    override fun openBatteryOptimizationSettings(): Boolean = false
}
