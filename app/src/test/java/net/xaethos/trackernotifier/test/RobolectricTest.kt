package net.xaethos.trackernotifier.test

import android.os.Build

import net.xaethos.trackernotifier.BuildConfig

import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
abstract class RobolectricTest
