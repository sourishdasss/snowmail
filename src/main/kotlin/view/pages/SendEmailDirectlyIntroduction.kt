package ca.uwaterloo.view.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource

@Composable
fun SendEmailsDirectlyIntroduction(
    NavigateToSignup: () -> Unit,
    NavigateToLogin: () -> Unit,
    NavigateToIntroductionPage: () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(50.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Button(
                onClick = NavigateToIntroductionPage,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = MaterialTheme.colors.primary
                )
            ) {
                Text("Back")
            }

            Row {
                Button(
                    onClick = NavigateToLogin,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = MaterialTheme.colors.primary
                    )
                ) {
                    Text("Log in")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = NavigateToSignup,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Sign Up")
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxHeight * 0.7f)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Why Choose Snowmail?",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h4,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.secondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = NavigateToIntroductionPage)
                    .background(color = Color(240 / 255f, 232 / 255f, 232 / 255f, 0.2f))
                    .border(BorderStroke(2.dp, MaterialTheme.colors.primary), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                    ) {
                        Text(text = "Send Cold Emails Directly",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.h4,
                            color = MaterialTheme.colors.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No need for external tools, manage your emails in one place!")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Image(
                        painter = painterResource("esendss.png"),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}
