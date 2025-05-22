package com.example.doccur.navigation



import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppsOutage
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.doccur.repositories.AppointmentRepository
import com.example.doccur.repositories.HomeRepository
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.repositories.ProfileRepository
import com.example.doccur.ui.screens.NotificationsScreen
import com.example.doccur.ui.screens.doctor.AppointmentDetailsScreen
import com.example.doccur.ui.screens.doctor.AppointmentsScreen
import com.example.doccur.ui.screens.doctor.DoctorHomeScreen
import com.example.doccur.ui.screens.doctor.ProfileScreen
import com.example.doccur.viewmodels.AppointmentViewModel
import com.example.doccur.viewmodels.AppointmentViewModelFactory
import com.example.doccur.viewmodels.HomeViewModel
import com.example.doccur.viewmodels.HomeViewModelFactory
import com.example.doccur.viewmodels.NotificationViewModel
import com.example.doccur.viewmodels.NotificationViewModelFactory
import com.example.doccur.viewmodels.ProfileViewModel
import com.example.doccur.viewmodels.ProfileViewModelFactory

// Screen objects for navigation
sealed class DoctorScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : DoctorScreen("home", "Home", Icons.Filled.Home)
    object Appointements : DoctorScreen("appointements", "Appointements", Icons.Filled.CalendarToday)
    object AppointmentDetails : DoctorScreen(
        "appointmentDetails/{appointmentId}",
        "Appointment Details",
        Icons.Filled.CalendarToday
    ) {
        fun createRoute(appointmentId: Int) = "appointmentDetails/$appointmentId"
    }
    object Notifications : DoctorScreen("notifications", "Notifications", Icons.Filled.Notifications)
    object Profile : DoctorScreen("profile", "Profile", Icons.Filled.MedicalServices)

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DocNavGraph(
    navController: NavHostController,
    notificationRepository: NotificationRepository,
    homeRepository: HomeRepository,
    appointmentRepository: AppointmentRepository,
    profileRepository: ProfileRepository
) {

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(homeRepository)
    )

    val notificationViewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(
            notificationRepository,
            context = LocalContext.current,
            wsBaseUrl = "ws://172.20.10.4:8000")
    )

    val appointmentViewModel: AppointmentViewModel = viewModel(
        factory = AppointmentViewModelFactory(appointmentRepository)
    )

    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(profileRepository)
    )

    NavHost(navController, startDestination = DoctorScreen.Home.route) {
        composable(DoctorScreen.Home.route) {
            val doctorId = 1
            DoctorHomeScreen(
                viewModel = homeViewModel,
                userId = doctorId)
        }

        composable(DoctorScreen.Appointements.route) {
            val doctorId = 1
            AppointmentsScreen(
                navController = navController,
                appointmentViewModel,doctorId
            )
        }

        composable(
            route = DoctorScreen.AppointmentDetails.route,
            arguments = listOf(navArgument("appointmentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getInt("appointmentId") ?: return@composable
            AppointmentDetailsScreen(
                appointmentId = appointmentId,
                viewModel = appointmentViewModel,
            )
        }

        composable(DoctorScreen.Notifications.route) {
            val doctorId = 1

            NotificationsScreen(
                viewModel = notificationViewModel,
                userId = doctorId,
                userType = "doctor"
            )
        }

        composable(DoctorScreen.Profile.route) {
            val doctorId = 1
            ProfileScreen(viewModel = profileViewModel, doctorId=doctorId,onBackClick={})
        }
    }
}