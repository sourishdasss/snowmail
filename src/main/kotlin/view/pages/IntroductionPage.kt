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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource

@Composable
fun IntroductionPage(
    NavigateToSignup: () -> Unit,
    NavigateToLogin: () -> Unit,
    NavigateToWelcomePage: () -> Unit,
    NavigateToWelcomePage2: () -> Unit,
    NavigateToWelcomePage3: () -> Unit,
    NavigateToWelcomePage4: () -> Unit
) {
    Column(
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
                onClick = NavigateToWelcomePage,
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

        Spacer(modifier = Modifier.padding(25.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth(),
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
            Row (
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(end = 5.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .clickable(onClick = NavigateToWelcomePage2) // Makes the box clickable
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = Color(240 / 255f, 232 / 255f, 232 / 255f, 0.2f), // Background color
                        )
                        .height(250.dp)
                        .width(370.dp)
                        .border(BorderStroke(2.dp, MaterialTheme.colors.primary), RoundedCornerShape(16.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource("egss.png"),
                        contentDescription = null,
                        modifier = Modifier
                            .size(170.dp)
                    )
                        Text("Cold Email Generation", fontSize = 16.sp, color = MaterialTheme.colors.secondary)
                        Text("Easily create professional and personalized emails in minutes", fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.width(75.dp))

                Column(
                    modifier = Modifier
                        .clickable(onClick = NavigateToWelcomePage3)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = Color(240 / 255f, 232 / 255f, 232 / 255f, 0.2f), // Background color
                        )
                        .height(250.dp)
                        .width(370.dp)
                        .border(BorderStroke(2.dp, MaterialTheme.colors.primary), RoundedCornerShape(16.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,// Centers the text inside the box
                ) {
                    Image(
                        painter = painterResource("progss.png"),
                        contentDescription = null,
                        modifier = Modifier
                            .size(170.dp)
                    )
                    Text("Job Application Progress", fontSize = 16.sp, color = MaterialTheme.colors.secondary)
                    Text("Track your job applications, follow-up emails, and responses", fontSize = 10.sp)
                }

            }
           Column(
               modifier = Modifier
                   .clickable(onClick = NavigateToWelcomePage4) // Makes the box clickable
                   .clip(RoundedCornerShape(16.dp))
                   .background(
                       color = Color(240 / 255f, 232 / 255f, 232 / 255f, 0.2f), // Background color
                   )
                   .height(250.dp)
                   .width(370.dp)
                   .border(BorderStroke(2.dp, MaterialTheme.colors.primary), RoundedCornerShape(16.dp)),
               horizontalAlignment = Alignment.CenterHorizontally, // Centers the text inside the box
           ) {
               Image(
                   painter = painterResource("esendss.png"),
                   contentDescription = null,
                   modifier = Modifier
                       .size(170.dp)
               )
               Text("Send Cold Emails Directly", fontSize = 16.sp, color = MaterialTheme.colors.secondary)
               Text("No need for external tools, manage your emails in one place.", fontSize = 10.sp)
           }

        }
    }
}
