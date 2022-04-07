/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package das.omegaterapia.visits.utils

import android.app.Activity
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetricsCalculator

/**
 * Opinionated set of viewport breakpoints
 *     - Compact: Most phones in portrait mode
 *     - Medium: Most foldables and tablets in portrait mode
 *     - Expanded: Most tablets in landscape mode
 *
 * More info: https://material.io/archive/guidelines/layout/responsive-ui.html
 */
enum class WindowSizeFormat { Compact, Medium, Expanded }

data class WindowSize(val width: WindowSizeFormat, val height: WindowSizeFormat, val isLandscape: Boolean)

/**
 * Remembers the [WindowSizeFormat] class for the window corresponding to the current window metrics.
 */
@Composable
fun Activity.rememberWindowSizeClass(): WindowSize {
    // Get the size (in pixels) of the window
    val windowSize = rememberWindowSize()

    // Convert the window size to [Dp]
    val windowDpSize = with(LocalDensity.current) {
        windowSize.toDpSize()
    }

    // Calculate the window size class
    return WindowSize(
        getWindowWidthSizeClass(windowDpSize),
        getWindowHeightSizeClass(windowDpSize),
        windowDpSize.width > windowDpSize.height
    )
}

/**
 * Remembers the [Size] in pixels of the window corresponding to the current window metrics.
 */
@Composable
private fun Activity.rememberWindowSize(): Size {
    val configuration = LocalConfiguration.current
    // WindowMetricsCalculator implicitly depends on the configuration through the activity,
    // so re-calculate it upon changes.
    val windowMetrics = remember(configuration) {
        WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
    }
    return windowMetrics.bounds.toComposeRect().size
}

/**
 * Partitions a [DpSize] into a enumerated [WindowSizeFormat] class.
 */
@VisibleForTesting
fun getWindowWidthSizeClass(windowDpSize: DpSize): WindowSizeFormat = when {
    /*
        Los breakpoint y nombres están establecidos según:
        https://developer.android.com/guide/topics/large-screens/support-different-screen-sizes#window_size_classes
    */
    windowDpSize.width < 0.dp -> throw IllegalArgumentException("Dp value cannot be negative")
    windowDpSize.width < 600.dp -> WindowSizeFormat.Compact
    windowDpSize.width < 840.dp -> WindowSizeFormat.Medium
    else -> WindowSizeFormat.Expanded
}

@VisibleForTesting
fun getWindowHeightSizeClass(windowDpSize: DpSize): WindowSizeFormat = when {
    /*
        Los breakpoint y nombres están establecidos según:
        https://developer.android.com/guide/topics/large-screens/support-different-screen-sizes#window_size_classes
    */
    windowDpSize.height < 0.dp -> throw IllegalArgumentException("Dp value cannot be negative")
    windowDpSize.height < 600.dp -> WindowSizeFormat.Compact
    windowDpSize.height < 840.dp -> WindowSizeFormat.Medium
    else -> WindowSizeFormat.Expanded
}
