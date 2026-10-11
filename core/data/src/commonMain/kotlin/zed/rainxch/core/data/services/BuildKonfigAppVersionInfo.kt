package zed.rainxch.core.data.services

import zed.rainxch.core.data.BuildKonfig
import zed.rainxch.core.domain.system.AppVersionInfo

class BuildKonfigAppVersionInfo : AppVersionInfo {
    override val versionCode: Int = BuildKonfig.VERSION_CODE
    override val versionName: String = BuildKonfig.VERSION_NAME
}
