package net.xaethos.quicker.cloud.test

import android.os.Build
import net.xaethos.quicker.cloud.BuildConfig
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
abstract class RobolectricTest
