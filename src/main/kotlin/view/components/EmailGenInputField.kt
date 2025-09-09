package ca.uwaterloo.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EmailGenInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .width(310.dp)
            .clip(RoundedCornerShape(5.dp)) // Rounded corners
            //.width(0.45f)

//        colors = TextFieldDefaults.outlinedTextFieldColors(
//            //focusedLabelColor = Color.Transparent,
//            //unfocusedLabelColor = Color.Transparent,
//            //focusedBorderColor = Color.Transparent,
//            //unfocusedBorderColor = Color.Transparent
//        )
    )
}