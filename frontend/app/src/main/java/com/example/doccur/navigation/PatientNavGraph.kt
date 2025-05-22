package com.example.doccur.navigation


import PatientHomeScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.doccur.repositories.AppointmentRepository
import com.example.doccur.repositories.HomeRepository
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.repositories.UsersRepository
import com.example.doccur.ui.screens.NotificationsScreen
import com.example.doccur.ui.screens.patient.AppointmentsScreen
import com.example.doccur.ui.screens.patient.DoctorDetailsScreen
import com.example.doccur.ui.screens.patient.DoctorsScreen
import com.example.doccur.viewmodels.AppointmentViewModel
import com.example.doccur.viewmodels.AppointmentViewModelFactory
import com.example.doccur.viewmodels.HomeViewModel
import com.example.doccur.viewmodels.HomeViewModelFactory
import com.example.doccur.viewmodels.NotificationViewModel
import com.example.doccur.viewmodels.NotificationViewModelFactory
import com.example.doccur.viewmodels.UsersViewModel
import com.example.doccur.viewmodels.UsersViewModelFactory

// Screen objects for navigation
sealed class PatientScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : PatientScreen("home", "Home", Icons.Filled.Home)
    object DoctorList : PatientScreen("doctorList", "Doctors", Icons.Filled.Person)
    object DoctorDetails {
        const val route = "doctorDetails/{doctorId}"
        fun createRoute(doctorId: Int) = "doctorDetails/$doctorId"
    }
    object Notifications : PatientScreen("notifications", "Notifications", Icons.Filled.Notifications)
    object Appointments : PatientScreen("appointments", "Appointments", Icons.Filled.CalendarToday)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PatientNavGraph(
    navController: NavHostController,
    notificationRepository: NotificationRepository,
    homeRepository: HomeRepository,
    usersRepository: UsersRepository,
    appointmentRepository: AppointmentRepository
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

    val usersViewModel: UsersViewModel = viewModel(
        factory = UsersViewModelFactory(usersRepository)
    )

    val appointmentViewModel: AppointmentViewModel = viewModel(
        factory = AppointmentViewModelFactory(appointmentRepository)
    )

    NavHost(navController, startDestination = PatientScreen.Home.route) {
        composable(PatientScreen.Home.route) {
            val userId = 2
            PatientHomeScreen(
                viewModel = homeViewModel,
                patientId = userId
            )
        }
        composable(PatientScreen.Notifications.route) {
            val userId = 2
            NotificationsScreen(
                viewModel = notificationViewModel,
                userId = userId,
                userType = "patient"
            )
        }

        composable(PatientScreen.DoctorList.route) {
            DoctorsScreen(usersViewModel,navController)
        }

        composable(PatientScreen.Appointments.route) {
            AppointmentsScreen(
                viewModel= appointmentViewModel,
                patientId = 2
            )
        }

        composable(PatientScreen.DoctorDetails.route) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId")?.toIntOrNull()
            doctorId?.let {
                DoctorDetailsScreen(
                    viewModel = usersViewModel,
                    doctorId = it,
                    patientId = 2,
                    appointmentViewModel = appointmentViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}