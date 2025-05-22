package com.example.doccur.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.doccur.navigation.DoctorScreen
import com.example.doccur.ui.theme.AppColors
import com.example.doccur.ui.theme.Inter

@Composable
fun DocBottomBar(navController: NavController) {
    val items = listOf(
        DoctorScreen.Home,
        DoctorScreen.Appointements,
        DoctorScreen.Notifications,
        DoctorScreen.Profile,
    )

    Column {

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            BottomNavigation(
                backgroundColor = Color.White,
                elevation = 8.dp,
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = if (selected) AppColors.Blue else Color(0xFF9CA3AF),
                                modifier = Modifier.padding(top = 15.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontFamily = Inter,
                                fontSize = 11.sp,
                                color = if (selected) AppColors.Blue else Color(0xFF9CA3AF),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 15.dp)
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    }
}

