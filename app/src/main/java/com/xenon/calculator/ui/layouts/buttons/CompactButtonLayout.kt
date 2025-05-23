package com.xenon.calculator.ui.layouts.buttons

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xenon.calculator.R
import com.xenon.calculator.ui.theme.CalculatorTheme
import com.xenon.calculator.viewmodel.CalculatorViewModel

val firaSansFamily = FontFamily(
    Font(R.font.fira_sans, FontWeight.Normal)
)

@Composable
fun CompactButtonLayout(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight(0.7f)
            .padding(10.dp)
    ) {
        ScientificButtonsRow1(
            viewModel, modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
        )

        val spacerHeight by animateDpAsState(
            targetValue = if (viewModel.isScientificMode) 4.dp else 12.dp,
            label = "ScientificModeSpacerHeight"
        )
        Spacer(Modifier.height(spacerHeight))

        Column(
            modifier = Modifier
                .weight(1f)
                .animateContentSize()
        ) {
            val animatedScientificRowsInnerWeight by animateFloatAsState(
                targetValue = if (viewModel.isScientificMode) 0.2f else 0f,
                animationSpec = tween(durationMillis = 300),
                label = "AnimatedScientificRowsInnerWeight"
            )

            val commonButtonsInnerWeight by animateFloatAsState(
                targetValue = if (viewModel.isScientificMode) 0.70f else 1f,
                animationSpec = tween(durationMillis = 300),
                label = "CommonButtonsInnerWeight"
            )

            if (viewModel.isScientificMode || animatedScientificRowsInnerWeight > 0.001f) {
                AnimatedVisibility(
                    visible = viewModel.isScientificMode,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { fullHeight -> fullHeight }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }),
                    modifier = Modifier
                        .weight(animatedScientificRowsInnerWeight.coerceAtLeast(0.001f))
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxHeight()) {
                        ScientificButtonsRow2(viewModel, modifier = Modifier.weight(1f))
                        Spacer(Modifier.height(4.dp))
                        ScientificButtonsRow3(
                            viewModel, viewModel.isInverseMode, modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            if (commonButtonsInnerWeight > 0.001f) {
                Column(
                    modifier = Modifier
                        .weight(commonButtonsInnerWeight.coerceAtLeast(0.001f))
                        .fillMaxHeight()
                ) {
                    val buttonRows = listOf(
                        listOf("AC", "( )", "%", "÷"),
                        listOf("7", "8", "9", "×"),
                        listOf("4", "5", "6", "-"),
                        listOf("1", "2", "3", "+"),
                        listOf(".", "0", "⌫", "=")
                    )

                    buttonRows.forEach { rowData ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 0.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowData.forEach { buttonText ->
                                val isNumberButton = buttonText in listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".")
                                CalculatorButton(
                                    text = buttonText,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    isOperator = buttonText in listOf("÷", "×", "-", "+", "%", "( )"),
                                    isSpecial = buttonText == "=",
                                    isClear = buttonText == "AC",
                                    isScientificButton = false,
                                    isNumber = isNumberButton || buttonText == "⌫",
                                    isGlobalScientificModeActive = viewModel.isScientificMode,
                                    // Pass the custom font family for the backspace button
                                    fontFamily = if (buttonText == "⌫") firaSansFamily else null,
                                    onClick = {
                                        if (buttonText == "( )") {
                                            viewModel.onParenthesesClick()
                                        } else {
                                            viewModel.onButtonClick(buttonText)
                                        }
                                    }
                                )
                            }
                        }
                        if (rowData != buttonRows.last()) {
                            val spacerInnerHeight by animateDpAsState(
                                targetValue = if (viewModel.isScientificMode) 4.dp else 8.dp,
                                label = "SpacerHeightAnimation"
                            )
                            Spacer(Modifier.height(spacerInnerHeight))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScientificButtonsRow1(viewModel: CalculatorViewModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(horizontal = 1.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val firstButtonText = if (viewModel.isInverseMode) "x²" else "√"
        val scientificButtons1 = listOf(firstButtonText, "π", "^")

        scientificButtons1.forEach { text ->
            CalculatorButton(
                text = text,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                isOperator = true,
                isScientificButton = true,
                isNumber = false,
                isGlobalScientificModeActive = viewModel.isScientificMode,
                onClick = { viewModel.onButtonClick(text) }
            )
        }
        CalculatorButton(
            text = "!",
            modifier = Modifier.weight(1f).fillMaxHeight(),
            isOperator = true,
            isScientificButton = true,
            isNumber = false,
            isGlobalScientificModeActive = viewModel.isScientificMode,
            onClick = { viewModel.onButtonClick("!") }
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { viewModel.toggleScientificMode() },
                modifier = Modifier.matchParentSize(),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                val rotationAngle by animateFloatAsState(
                    targetValue = if (viewModel.isScientificMode) 0f else 180f,
                    animationSpec = tween(durationMillis = 300),
                    label = "IconRotation"
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Toggle Scientific Panel",
                    modifier = Modifier.rotate(rotationAngle)
                )
            }
        }
    }
}

@Composable
fun ScientificButtonsRow2(viewModel: CalculatorViewModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 1.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CalculatorButton(
            text = viewModel.angleUnit.name,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            isOperator = true,
            isScientificButton = true,
            isNumber = false,
            isGlobalScientificModeActive = viewModel.isScientificMode,
            onClick = { viewModel.toggleAngleUnit() }
        )

        val trigButtons = listOf("sin", "cos", "tan")
        trigButtons.forEach { text ->
            val buttonDisplayText = if (viewModel.isInverseMode) "$text⁻¹" else text
            CalculatorButton(
                text = buttonDisplayText,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                isOperator = true,
                isScientificButton = true,
                isNumber = false,
                isGlobalScientificModeActive = viewModel.isScientificMode,
                onClick = { viewModel.onButtonClick(text) }
            )
        }
        Spacer(Modifier.width(40.dp))
    }
}

@Composable
fun ScientificButtonsRow3(
    viewModel: CalculatorViewModel,
    isInverseMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 1.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val lnButtonText = if (isInverseMode) "eˣ" else "ln"
        val logButtonText = if (isInverseMode) "10ˣ" else "log"

        val scientificButtons3Order = listOf("INV", "e", lnButtonText, logButtonText)

        scientificButtons3Order.forEach { text ->
            CalculatorButton(
                text = text,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                isOperator = true,
                isScientificButton = true,
                isNumber = false,
                isGlobalScientificModeActive = viewModel.isScientificMode,
                isInverseActive = text == "INV" && isInverseMode,
                onClick = {
                    if (text == "INV") {
                        viewModel.toggleInverseMode()
                    } else {
                        viewModel.onButtonClick(text)
                    }
                }
            )
        }
        Spacer(Modifier.width(40.dp))
    }
}


@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    isOperator: Boolean = false,
    isSpecial: Boolean = false,
    isClear: Boolean = false,
    isScientificButton: Boolean,
    isNumber: Boolean,
    isGlobalScientificModeActive: Boolean,
    isInverseActive: Boolean = false,
    fontFamily: FontFamily? = null,
    onClick: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val targetFontSize = when {
        isScientificButton -> when {
            isLandscape -> if (text.length > 2 || text.contains("⁻¹") || text.contains("ˣ") || text.contains("²")) 20.sp else 20.sp
            else -> if (text.length > 2 || text.contains("⁻¹") || text.contains("ˣ") || text.contains("²")) 20.sp else 20.sp
        }
        isNumber -> when {
            isLandscape -> if (isGlobalScientificModeActive) 28.sp else 28.sp
            else -> if (isGlobalScientificModeActive) 28.sp else 35.sp
        }
        else -> when { // This covers operators like +, -, etc., and AC, ()
            isLandscape -> if (isGlobalScientificModeActive) 28.sp else 28.sp
            else -> if (isGlobalScientificModeActive) 28.sp else 35.sp
        }
    }


    val animatedFontSize by animateDpAsState(
        targetValue = targetFontSize.value.dp,
        animationSpec = tween(durationMillis = 300),
        label = "FontSizeAnimation"
    )

    val containerColor = when {
        isClear -> MaterialTheme.colorScheme.tertiary
        isSpecial -> MaterialTheme.colorScheme.inversePrimary
        text == "INV" && isScientificButton -> if (isInverseActive) MaterialTheme.colorScheme.errorContainer else Color.Transparent
        isScientificButton -> Color.Transparent
        isOperator -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    val contentColor = when {
        isClear -> MaterialTheme.colorScheme.onPrimary
        isSpecial -> MaterialTheme.colorScheme.onSurface
        text == "INV" && isScientificButton -> if (isInverseActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        isScientificButton -> MaterialTheme.colorScheme.onSurfaceVariant
        isOperator -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val cornerRadiusPercent by animateIntAsState(
        targetValue = if (isPressed && !isScientificButton) 30 else 100,
        animationSpec = tween(durationMillis = if (isScientificButton) 0 else 350),
        label = "cornerRadiusAnimation"
    )

    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 48.dp, minWidth = 40.dp),
        shape = RoundedCornerShape(percent = cornerRadiusPercent),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor, contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
        interactionSource = interactionSource
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily,
            fontSize = animatedFontSize.value.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "Portrait - Common Mode")
@Composable
fun PreviewPortraitCommon() {
    CalculatorTheme(darkTheme = false) {
        Surface {
            val sampleViewModel = CalculatorViewModel()
            CompactButtonLayout(viewModel = sampleViewModel)
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "Portrait - Scientific Mode")
@Composable
fun PreviewPortraitScientific() {
    CalculatorTheme(darkTheme = false) {
        Surface {
            val sampleViewModel = CalculatorViewModel()
            sampleViewModel.toggleScientificMode()
            CompactButtonLayout(viewModel = sampleViewModel)
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "Landscape - Common Mode", widthDp = 800, heightDp = 360)
@Composable
fun PreviewLandscapeCommon() {
    CalculatorTheme(darkTheme = false) {
        Surface {
            val sampleViewModel = CalculatorViewModel()
            CompactButtonLayout(viewModel = sampleViewModel)
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "Landscape - Scientific Mode", widthDp = 800, heightDp = 360)
@Composable
fun PreviewLandscapeScientific() {
    CalculatorTheme(darkTheme = false) {
        Surface {
            val sampleViewModel = CalculatorViewModel()
            sampleViewModel.toggleScientificMode()
            CompactButtonLayout(viewModel = sampleViewModel)
        }
    }
}

@Preview(showBackground = true, name = "Num Btn (Port, SciOff)")
@Composable
fun NumBtnPortSciOff() {
    CalculatorTheme { Surface { CalculatorButton(text = "7", isScientificButton = false, isNumber = true, isGlobalScientificModeActive = false, onClick = {}) } }
}
@Preview(showBackground = true, name = "Num Btn (Port, SciOn)")
@Composable
fun NumBtnPortSciOn() {
    CalculatorTheme { Surface { CalculatorButton(text = "7", isScientificButton = false, isNumber = true, isGlobalScientificModeActive = true, onClick = {}) } }
}
@Preview(showBackground = true, name = "Num Btn (Land, SciOff)", widthDp = 100, heightDp=60)
@Composable
fun NumBtnLandSciOff() {
    CalculatorTheme { Surface { CalculatorButton(text = "7", isScientificButton = false, isNumber = true, isGlobalScientificModeActive = false, onClick = {}) } }
}
@Preview(showBackground = true, name = "Num Btn (Land, SciOn)", widthDp = 100, heightDp=60)
@Composable
fun NumBtnLandSciOn() {
    CalculatorTheme { Surface { CalculatorButton(text = "7", isScientificButton = false, isNumber = true, isGlobalScientificModeActive = true, onClick = {}) } }
}

@Preview(showBackground = true, name = "Sci Panel Btn (Port)")
@Composable
fun SciPanelBtnPort() {
    CalculatorTheme { Surface { CalculatorButton(text = "sin", isScientificButton = true, isNumber = false, isGlobalScientificModeActive = true, isOperator = true, onClick = {}) } }
}
@Preview(showBackground = true, name = "Sci Panel Btn (Land)", widthDp = 100, heightDp=60)
@Composable
fun SciPanelBtnLand() {
    CalculatorTheme { Surface { CalculatorButton(text = "sin", isScientificButton = true, isNumber = false, isGlobalScientificModeActive = true, isOperator = true, onClick = {}) } }
}
@Preview(showBackground = true, name = "Sci Panel Btn Long (Land)", widthDp = 100, heightDp=60)
@Composable
fun SciPanelBtnLongLand() {
    CalculatorTheme { Surface { CalculatorButton(text = "sin⁻¹", isScientificButton = true, isNumber = false, isGlobalScientificModeActive = true, isOperator = true, onClick = {}) } }
}

@Preview(showBackground = true, name = "Operator Btn (Port, SciOff)")
@Composable
fun OperatorBtnPortSciOff() {
    CalculatorTheme { Surface { CalculatorButton(text = "+", isScientificButton = false, isNumber = false, isGlobalScientificModeActive = false, isOperator = true, onClick = {}) } }
}

@Preview(showBackground = true, name = "Operator Btn (Port, SciOn)")
@Composable
fun OperatorBtnPortSciOn() {
    CalculatorTheme { Surface { CalculatorButton(text = "+", isScientificButton = false, isNumber = false, isGlobalScientificModeActive = true, isOperator = true, onClick = {}) } }
}

// Preview for the backspace button with Fira Sans
@Preview(showBackground = true, name = "Backspace Button with Fira Sans")
@Composable
fun BackspaceButtonPreview() {
    CalculatorTheme {
        Surface {
            CalculatorButton(
                text = "⌫",
                isScientificButton = false,
                isNumber = true, // Treat as number for styling consistency in this context
                isGlobalScientificModeActive = false,
                fontFamily = firaSansFamily, // Apply Fira Sans
                onClick = {}
            )
        }
    }
}