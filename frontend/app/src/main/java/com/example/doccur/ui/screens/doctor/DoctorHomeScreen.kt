package com.example.doccur.ui.screens.doctor

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doccur.R
import com.example.doccur.ui.screens.customTextStyle
import com.example.doccur.ui.theme.AppColors
import com.example.doccur.ui.theme.Inter
import com.example.doccur.viewmodels.HomeViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.LocalTime
import java.time.Period
import java.time.temporal.ChronoUnit


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorHomeScreen(
    viewModel: HomeViewModel = viewModel(),
    userId: Int
) {
    val doctorStats by viewModel.doctorStats.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentDate = remember { getCurrentFormattedDate() }

    LaunchedEffect(Unit) {
        viewModel.fetchDoctorStatistics(userId)
    }


    CompositionLocalProvider(
        LocalTextStyle provides com.example.doccur.ui.screens.customTextStyle
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 40.dp, horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        buildAnnotatedString {
                            append("Welcome back ")
                            withStyle(style = SpanStyle(color = AppColors.Blue)) {
                                append("Dr.Sarah Arabi!")
                            }
                        },
                        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                        fontFamily = Inter
                    )

                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentDate,
                style = MaterialTheme.typography.body1,
                color = AppColors.TextSecondary,
                fontFamily = Inter
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                errorMessage != null -> {
                    Text(
                        text = "Error: $errorMessage",
                        color = Color.Red
                    )
                }
                doctorStats != null -> {
                    // Upcoming Appointment Section
                    doctorStats!!.upcoming_appointment?.let { appointment ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 4.dp,
                            backgroundColor = Color.White
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical=16.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ){
                                        Text(
                                            text = "Upcoming Appointment",
                                            fontFamily = Inter,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Text(
                                            text = calculateTimeUntilAppointment(appointment.date, appointment.time),
                                            fontSize = 12.sp,
                                            fontFamily = Inter,
                                            color = AppColors.Blue,
                                            )
                                    }


                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.patient),
                                            contentDescription = "Profile Image",
                                            modifier = Modifier
                                                .size(60.dp) // Size of the image
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )


                                        Column(
                                            modifier = Modifier
                                                .padding(start = 12.dp),
                                        ) {
                                            Text(
                                                text = appointment.patient_last_name + " " + appointment.patient_first_name,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.subtitle1,
                                                fontFamily = Inter
                                            )

                                            Text(
                                                text = "Female," + " " + calculateAge(appointment.patient_date_birth) + " years old",
                                                fontWeight = FontWeight.Medium,
                                                style = MaterialTheme.typography.subtitle2,
                                                fontFamily = Inter
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,

                                        ) {
                                        Row {
                                            Icon(
                                                imageVector = Icons.Default.DateRange,
                                                contentDescription = "Location",
                                                modifier = Modifier.size(16.dp),
                                                tint = AppColors.TextSecondary
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = appointment.date,
                                                color = AppColors.TextSecondary,
                                                fontFamily = Inter
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(start = 20.dp),) {
                                            Icon(
                                                imageVector = Icons.Default.AccessTime,
                                                contentDescription = "Location",
                                                modifier = Modifier.size(16.dp),
                                                tint = AppColors.TextSecondary
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = appointment.time,
                                                color = AppColors.TextSecondary,
                                                fontFamily = Inter
                                            )
                                        }
                                    }
                                }
                            }
                        }

                    }

                    Spacer(modifier = Modifier.height(20.dp))


                    Text(
                        text = "You have:",
                        fontFamily = Inter,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item{
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = 4.dp,
                                backgroundColor = Color.White
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.c1),
                                        contentDescription = "Profile Image",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )

                                    Text(
                                        text = doctorStats!!.total_appointments_today.toString(),
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.subtitle2,
                                        fontFamily = Inter,
                                        color = AppColors.TextSecondary,
                                    )

                                    Text(
                                        text = "Appointments today",
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.subtitle2,
                                        fontFamily = Inter,
                                        color = AppColors.TextSecondary,
                                    )

                                }
                            }
                        }
                        item{
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = 4.dp,
                                backgroundColor = Color.White
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ){
                                    Image(
                                        painter = painterResource(id = R.drawable.c2),
                                        contentDescription = "Profile Image",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )

                                    Text(
                                        text = doctorStats!!.completed_appointments.toString(),
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.subtitle2,
                                        fontFamily = Inter,
                                        color = AppColors.TextSecondary,
                                    )

                                    Text(
                                        text = "complete appointments",
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.subtitle2,
                                        fontFamily = Inter,
                                        color = AppColors.TextSecondary,
                                    )

                                }
                            }
                        }

                        item{
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = 4.dp,
                                backgroundColor = Color.White
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ){
                                    Image(
                                        painter = painterResource(id = R.drawable.c3),
                                        contentDescription = "Profile Image",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )

                                    Text(
                                        text = doctorStats!!.patients_attended.toString(),
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.subtitle2,
                                        fontFamily = Inter,
                                        color = AppColors.TextSecondary,
                                    )

                                    Text(
                                        text = "Patients attended",
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.subtitle2,
                                        fontFamily = Inter,
                                        color = AppColors.TextSecondary,
                                    )
                                }
                            }
                        }
                        item{
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = 4.dp,
                                backgroundColor = Color.White
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ){
                                    Image(
                                        painter = painterResource(id = R.drawable.c4),
                                        contentDescription = "Profile Image",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )

                                    Text(
                                        text = doctorStats!!.pending_appointments.toString(),
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.subtitle2,
                                        fontFamily = Inter,
                                        color = AppColors.TextSecondary,
                                    )

                                    Text(
                                        text = "pending appointments",
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.subtitle2,
                                        fontFamily = Inter,
                                        color = AppColors.TextSecondary,
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

    }
}

@Composable
fun StatCardWithIcon(
    label: String,
    value: Int,
    icon: ImageVector,
    backgroundColor: Color,
) {
    CompositionLocalProvider(
        LocalTextStyle provides customTextStyle
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            elevation = 0.dp,
            backgroundColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(backgroundColor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = backgroundColor.darken(0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter
                )

                Text(
                    text = label,
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontFamily = Inter
                )
            }
        }
    }
}

fun Color.darken(factor: Float): Color {
    return Color(
        red = this.red * (1 - factor),
        green = this.green * (1 - factor),
        blue = this.blue * (1 - factor),
        alpha = this.alpha
    )
}

// Helper function to get formatted date
@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentFormattedDate(): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
    return LocalDate.now().format(formatter)
}


@RequiresApi(Build.VERSION_CODES.O)
fun calculateTimeUntilAppointment(appointmentDate: String, appointmentTime: String): String {
    try {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

        val date = LocalDate.parse(appointmentDate, dateFormatter)
        val time = LocalTime.parse(appointmentTime, timeFormatter)

        // Combine date and time into a LocalDateTime
        val appointmentDateTime = LocalDateTime.of(date, time)
        val now = LocalDateTime.now()

        // Calculate the difference
        val minutes = ChronoUnit.MINUTES.between(now, appointmentDateTime)

        return when {
            minutes < 0 -> "Appointment passed"
            minutes < 60 -> "in $minutes minutes"
            minutes < 24 * 60 -> {
                val hours = minutes / 60
                val remainingMinutes = minutes % 60
                if (remainingMinutes > 0) {
                    "in $hours hours $remainingMinutes minutes"
                } else {
                    "in $hours hours"
                }
            }
            else -> {
                val days = minutes / (24 * 60)
                val remainingHours = (minutes % (24 * 60)) / 60
                if (remainingHours > 0) {
                    "in $days days $remainingHours hours"
                } else {
                    "in $days days"
                }
            }
        }
    } catch (e: Exception) {
        return "Time unavailable"
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun calculateAge(birthDateString: String?): Int {
    if (birthDateString.isNullOrEmpty()) return 0
    val birthDate = LocalDate.parse(birthDateString, DateTimeFormatter.ISO_DATE)
    return Period.between(birthDate, LocalDate.now()).years
}
