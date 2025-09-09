package ca.uwaterloo.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.dp
import ca.uwaterloo.controller.SignInController
import integration.SupabaseClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun TopNavigationBar(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    NavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val signInController = SignInController(SupabaseClient().authRepository)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TopAppBar(
            backgroundColor = Color.White,
            elevation = 4.dp,
            modifier = modifier
                .fillMaxWidth(0.9f)
                .shadow(4.dp, shape = RoundedCornerShape(15.dp))
                .clip(RoundedCornerShape(15.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    backgroundColor = Color.White,
                    contentColor = MaterialTheme.colors.secondary,
                    indicator = { Box{} },
                    divider = { Box{} },
                    modifier = Modifier.weight(1f)
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { onTabSelected(0) },
                        text = { Text("Cold Email Generation") })
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { onTabSelected(1) },
                        text = { Text("Job Application Progress") })
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { onTabSelected(2) },
                        text = { Text("Documents") })
                    Tab(
                        selected = selectedTabIndex == 3,
                        onClick = { onTabSelected(3) },
                        text = { Text("Profile") })
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            runBlocking {
                                signInController.logoutUser()
                            }
                            NavigateToLogin()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Text("Sign Out")
                }
            }
        }
    }
}
