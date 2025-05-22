package com.example.doccur.ui.screens.patient

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.doccur.R
import com.example.doccur.api.RetrofitClient.BASE_URL
import com.example.doccur.entities.Timeslot
import com.example.doccur.ui.theme.AppColors
import com.example.doccur.viewmodels.AppointmentViewModel
import com.example.doccur.viewmodels.UsersViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailsScreen(
    appointmentViewModel: AppointmentViewModel,
    viewModel: UsersViewModel,
    doctorId: Int,
    patientId: Int,
    onNavigateBack: () -> Unit = {},
) {
    val doctor by viewModel.selectedDoctor.collectAsState()
    val context = LocalContext.current
    var showSocialMedia by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Add these state variables for appointment booking
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var selectedTimeslotId by remember { mutableStateOf<Int?>(null) }

    // Observe appointment booking result
    val appointmentBookingResult by appointmentViewModel.appointmentBookingResult.collectAsState()
    val isLoading by appointmentViewModel.loading.collectAsState()
    val error by appointmentViewModel.error.collectAsState()

    LaunchedEffect(doctorId) {
        viewModel.loadDoctorDetails(doctorId)
    }

    // Handle booking result
    LaunchedEffect(appointmentBookingResult) {
        appointmentBookingResult?.let { result ->
            if (result.appointmentId > 0) {
                // Show success message and navigate back or refresh
                // You can show a snackbar or dialog here
                onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Doctor Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            doctor?.let { doc ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Doctor Header with gradient background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        // Gradient background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                )
                        )

                        // Profile content
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Profile image with border
                            Surface(
                                modifier = Modifier
                                    .size(120.dp)
                                    .padding(4.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 4.dp
                            ) {
                                AsyncImage(
                                    model = BASE_URL + doc.photo_url,
                                    contentDescription = "Doctor profile photo",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Name with verification badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Text(
                                    text = "Dr. ${doc.first_name} ${doc.last_name}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Verified",
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }

                            // Specialty
                            Text(
                                text = doc.specialty,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Contact actions
                    ContactActionsRow(
                        onCallClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${doc.phone_number}")
                            }
                            context.startActivity(intent)
                        },
                        onEmailClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${doc.email}")
                            }
                            context.startActivity(intent)
                        },
                        onSocialClick = {
                            showSocialMedia = !showSocialMedia
                        }
                    )

                    // Social media links
                    AnimatedVisibility(
                        visible = showSocialMedia,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        SocialMediaLinks(
                            facebookLink = doc.facebook_link,
                            instagramLink = doc.instagram_link,
                            twitterLink = doc.twitter_link,
                            linkedinLink = doc.linkedin_link,
                            context = context
                        )
                    }

                    // Clinic information card
                    doc.clinic?.let { clinic ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Clinic Information",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Business,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = clinic.name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }

                                Divider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val mapQuery = "${clinic.address}, ${clinic.location}"
                                            val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("geo:0,0?q=${Uri.encode(mapQuery)}")
                                            )
                                            intent.setPackage("com.google.android.apps.maps")
                                            context.startActivity(intent)
                                        }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = clinic.address,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = clinic.location,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Available appointment slots
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Available Appointments",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Group timeslots by date
                            val timeslotsByDate = doc.timeslots
                                .filter { !it.is_booked } // Only show available slots
                                .groupBy { it.date }

                            // Show dates with available slots
                            if (timeslotsByDate.isNotEmpty()) {
                                // Display dates as tabs or buttons
                                val selectedDateState = remember { mutableStateOf(timeslotsByDate.keys.first()) }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    timeslotsByDate.keys.forEach { date ->
                                        val isSelected = date == selectedDateState.value
                                        val displayDate = formatDateForDisplay(date)

                                        if (isSelected) {
                                            Button(
                                                onClick = {
                                                    selectedDateState.value = date
                                                    selectedDate = date
                                                    selectedTime = null // Reset time selection when date changes
                                                    selectedTimeslotId = null
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(displayDate)
                                            }
                                        } else {
                                            OutlinedButton(
                                                onClick = {
                                                    selectedDateState.value = date
                                                    selectedDate = date
                                                    selectedTime = null // Reset time selection when date changes
                                                    selectedTimeslotId = null
                                                },
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(displayDate)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Display timeslots for selected date
                                timeslotsByDate[selectedDateState.value]?.let { slots ->
                                    // Group by morning/afternoon
                                    val morningSlots = slots.filter { slot ->
                                        val time = LocalTime.parse(slot.start_time)
                                        time.isBefore(LocalTime.NOON)
                                    }

                                    val afternoonSlots = slots.filter { slot ->
                                        val time = LocalTime.parse(slot.start_time)
                                        !time.isBefore(LocalTime.NOON)
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        if (morningSlots.isNotEmpty()) {
                                            Text(
                                                text = "Morning",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                            TimeSlotRow(
                                                slots = morningSlots,
                                                selectedTime = selectedTime,
                                                onSlotSelected = { slot ->
                                                    selectedTime = slot.start_time
                                                    selectedTimeslotId = slot.id
                                                }
                                            )
                                        }

                                        if (afternoonSlots.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = "Afternoon",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                            TimeSlotRow(
                                                slots = afternoonSlots,
                                                selectedTime = selectedTime,
                                                onSlotSelected = { slot ->
                                                    selectedTime = slot.start_time
                                                    selectedTimeslotId = slot.id
                                                }
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "No available timeslots",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Modified Book Appointment Button
                            Button(
                                onClick = {
                                    if (selectedDate != null && selectedTime != null) {
                                        appointmentViewModel.bookAppointment(
                                            patientId = patientId,
                                            doctorId = doctorId,
                                            date = selectedDate!!,
                                            time = selectedTime!!
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = selectedDate != null && selectedTime != null && !isLoading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isLoading) {
                                    androidx.compose.material3.CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Booking...")
                                } else {
                                    Text("Book Appointment")
                                }
                            }

                            // Show error if any
                            error?.let { errorMessage ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = errorMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// Modified TimeSlotRow to handle actual slot selection with timeslot objects
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSlotRow(
    slots: List<Timeslot>, // Change this to accept Timeslot objects instead of strings
    selectedTime: String? = null,
    onSlotSelected: (Timeslot) -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        slots.forEach { slot ->
            val timeDisplay = formatTimeForDisplay(slot.start_time)
            val isSelected = slot.start_time == selectedTime

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected)
                    AppColors.Blue
                else
                    AppColors.Blue,
                contentColor = if (isSelected)
                    AppColors.Blue
                else
                    AppColors.Blue
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            onSlotSelected(slot)
                        }
                ) {
                    Text(
                        text = timeDisplay,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ContactActionsRow(
    onCallClick: () -> Unit,
    onEmailClick: () -> Unit,
    onSocialClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-20).dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ContactActionButton(
                icon = Icons.Default.Phone,
                text = "Call",
                onClick = onCallClick
            )

            ContactActionButton(
                icon = Icons.Default.Email,
                text = "Email",
                onClick = onEmailClick
            )

            ContactActionButton(
                icon = Icons.Default.Public,
                text = "Social",
                onClick = onSocialClick
            )
        }
    }
}

@Composable
fun ContactActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SocialMediaLinks(
    facebookLink: String?,
    instagramLink: String?,
    twitterLink: String?,
    linkedinLink: String?,
    context: android.content.Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            facebookLink?.let {
                SocialMediaIcon(
                    iconRes = R.drawable.facebook,
                    contentDescription = "Facebook",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        context.startActivity(intent)
                    }
                )
            }

            instagramLink?.let {
                SocialMediaIcon(
                    iconRes = R.drawable.instagram,
                    contentDescription = "Instagram",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        context.startActivity(intent)
                    }
                )
            }

            twitterLink?.let {
                SocialMediaIcon(
                    iconRes = R.drawable.twitter,
                    contentDescription = "Twitter",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        context.startActivity(intent)
                    }
                )
            }

            linkedinLink?.let {
                SocialMediaIcon(
                    iconRes = R.drawable.linkedin,
                    contentDescription = "LinkedIn",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun SocialMediaIcon(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateForDisplay(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        when (date) {
            today -> "Today"
            tomorrow -> "Tomorrow"
            else -> {
                val formatter = DateTimeFormatter.ofPattern("MMM d")
                date.format(formatter)
            }
        }
    } catch (e: Exception) {
        dateString // Fallback to raw string if parsing fails
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTimeForDisplay(timeString: String): String {
    return try {
        val time = LocalTime.parse(timeString)
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        time.format(formatter)
    } catch (e: Exception) {
        timeString // Fallback to raw string if parsing fails
    }
}

// Modified TimeSlotRow to handle actual slot selection
@Composable
fun TimeSlotRow(
    slots: List<String>,
    onSlotSelected: (String) -> Unit = {}
) {
    val selectedSlot = remember { mutableStateOf<String?>(null) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        slots.forEach { time ->
            val isSelected = time == selectedSlot.value

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            selectedSlot.value = time
                            onSlotSelected(time)
                        }
                ) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}