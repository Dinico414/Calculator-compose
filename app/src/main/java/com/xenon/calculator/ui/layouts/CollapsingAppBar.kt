package com.xenon.calculator.ui.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingAppBarLayout(
    modifier: Modifier = Modifier,
    collapsedHeight: Dp = 54.dp,
    expandedHeight: Dp = 200.dp,
    navigationIcon: @Composable () -> Unit = {},
    expandedTextColor: Color = MaterialTheme.colorScheme.primary,
    collapsedTextColor: Color = MaterialTheme.colorScheme.onBackground,
    expandedContainerColor: Color = MaterialTheme.colorScheme.background,
    collapsedContainerColor: Color = MaterialTheme.colorScheme.background,
    navigationIconContentColor: Color = MaterialTheme.colorScheme.onBackground,
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {},
                collapsedHeight = collapsedHeight,
                expandedHeight = expandedHeight,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = expandedContainerColor,
                    scrolledContainerColor = collapsedContainerColor,
                    navigationIconContentColor = navigationIconContentColor,
                    titleContentColor = Color.Transparent,
                    actionIconContentColor = navigationIconContentColor,
                ),
                scrollBehavior = scrollBehavior
            )

            val fraction = scrollBehavior.state.collapsedFraction
            val curHeight by remember(fraction) { derivedStateOf { collapsedHeight.times(fraction) + expandedHeight.times(1 - fraction) } }
            val curFontSize by remember(fraction) { derivedStateOf { (24 * fraction + 45 * (1 - fraction)).sp } }

            val interpolatedTextColor by remember(fraction, expandedTextColor, collapsedTextColor) {
                derivedStateOf {
                    lerp(expandedTextColor, collapsedTextColor, fraction)
                }
            }

            CenterAlignedTopAppBar(
                expandedHeight = curHeight,
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Settings", fontSize = curFontSize, color = interpolatedTextColor)
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(top=curHeight-collapsedHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        navigationIcon()
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = navigationIconContentColor,
                    titleContentColor = interpolatedTextColor,
                    actionIconContentColor = navigationIconContentColor
                ),
            )
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}