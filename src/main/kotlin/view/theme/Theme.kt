package ca.uwaterloo.view.theme


import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = Color(0xFF487896), // Light Blue
    primaryVariant = Color(0xFF005B9F),
    secondary = Color(0xA61E1E1E), // Dark Grey Text
    background = Color(0xFFF8FAFC),
    onPrimary = Color(0xFFA1D4EA), // Darker Blue
    onSecondary = Color(0xFFA9A9A9), // Light Grey Text
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColorPalette,
        content = content
    )
}
