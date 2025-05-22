package com.example.doccur.ui.screens.doctor

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doccur.R
import com.example.doccur.entities.AppointmentPatient
import com.example.doccur.ui.theme.AppColors
import com.example.doccur.ui.theme.Inter
import com.example.doccur.viewmodels.AppointmentViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    navController: NavHostController,
    viewModel: AppointmentViewModel,
    doctorId: Int,
) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    val appointments by viewModel.appointments.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val sheetHeight = screenHeight * 0.5f

    var selectedAppointment by remember { mutableStateOf<AppointmentPatient?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(currentDate) {
        viewModel.fetchAppointmentsForDoctor(doctorId)
    }

    Scaffold(
        containerColor = Color(0xFFF9FAFB)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Title
            Text(
                text = "Appointments",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Inter,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date Navigation
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { currentDate = currentDate.minusDays(1) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Previous Day",
                            tint = Color(0xFF4285F4)
                        )
                    }
                    Text(
                        text = currentDate.format(formatter),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    IconButton(onClick = { currentDate = currentDate.plusDays(1) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next Day",
                            tint = Color(0xFF4285F4)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF4285F4))
                    }
                }

                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Error",
                                tint = Color(0xFFFE3B46),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(error ?: "Unknown error occurred", color = Color(0xFFFE3B46))
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.fetchAppointmentsForDoctor(doctorId) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4285F4)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Retry", color = Color.White)
                            }
                        }
                    }
                }

                else -> {
                    val filteredAppointments = appointments
                        .filter { it.date == currentDate.toString() }
                        .sortedBy { it.time }

                    if (filteredAppointments.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No appointments for this day",
                                color = Color.Gray,
                                fontFamily = Inter,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredAppointments) { appointment ->
                                AppointmentCard(appointment = appointment) {
                                    selectedAppointment = appointment
                                    showBottomSheet = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Bottom Sheet
    if (showBottomSheet && selectedAppointment != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Box(modifier = Modifier.height(sheetHeight)) {
                AppointmentDetailsScreen(
                    appointmentId = selectedAppointment!!.id,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: AppointmentPatient,
    onClick: () -> Unit
) {
    val statusColor = when (appointment.status.lowercase()) {
        "confirmed" -> AppColors.Green.copy(alpha = 0.1f)
        "completed" -> Color(0xFF4285F4).copy(alpha = 0.1f)
        "cancelled" -> AppColors.Red.copy(alpha = 0.1f)
        "pending" -> Color(0xFFF57F17).copy(alpha = 0.1f)
        else -> Color(0xFFEEEEEE)
    }

    val statusTextColor = when (appointment.status.lowercase()) {
        "confirmed" -> AppColors.Green
        "completed" -> Color(0xFF4285F4)
        "cancelled" -> AppColors.Red
        "pending" -> Color(0xFFF57F17)
        else -> Color(0xFF424242)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Top row: time and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.time,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (appointment.hasPrescription && appointment.status.equals("completed", true)) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "View Prescription",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { /* open prescription */ }
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = appointment.status,
                        color = if (appointment.status.equals("Confirmed", ignoreCase = true))
                            Color(0xFF2ECC71) else Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Patient row: image and info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.doctor), // Change to actual patient photo if needed
                    contentDescription = "Patient",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = appointment.patient.fullName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontFamily = Inter
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Date: ${appointment.date}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = Inter
                    )
                }
            }
        }
    }}