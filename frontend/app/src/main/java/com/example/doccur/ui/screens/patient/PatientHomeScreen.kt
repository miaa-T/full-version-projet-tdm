import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.Composable
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import com.example.doccur.ui.screens.doctor.StatCardWithIcon
import com.example.doccur.ui.screens.doctor.calculateTimeUntilAppointment
import com.example.doccur.ui.screens.doctor.darken
import com.example.doccur.ui.screens.doctor.getCurrentFormattedDate
import com.example.doccur.ui.theme.AppColors
import com.example.doccur.ui.theme.Inter
import com.example.doccur.viewmodels.HomeViewModel



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PatientHomeScreen(
    viewModel: HomeViewModel = viewModel(),
    patientId: Int
) {
    val patientStats by viewModel.patientStats.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentDate = remember { getCurrentFormattedDate() }

    LaunchedEffect(Unit) {
        viewModel.fetchPatientStatistics(patientId)
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
                                append("Imene Louni!")
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
                patientStats != null -> {
                    // Upcoming Appointment Section
                    patientStats!!.upcomingAppointment?.let { appointment ->

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
                                            modifier = Modifier.padding(end=6.dp),
                                            )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.doctor),
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
                                                text = appointment.doctorFirstName + " " + appointment.doctorLastName,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.subtitle1,
                                                fontFamily = Inter
                                            )

                                            Text(
                                                text = appointment.doctorSpecialty + " - " + appointment.clinicName,
                                                fontWeight = FontWeight.SemiBold,
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

                    Spacer(modifier = Modifier.height(24.dp))

                    patientStats?.doctorsConsulted?.let { doctors ->
                        Column {
                            Text(
                                text = "Doctors Consulted",
                                fontFamily = Inter,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                            )

                            doctors.forEach { doctor ->
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
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.doctor),
                                                    contentDescription = "Profile Image",
                                                    modifier = Modifier
                                                        .size(70.dp)
                                                        .clip(CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )


                                                Column(
                                                    modifier = Modifier
                                                        .padding(start = 12.dp),
                                                ) {
                                                    Text(
                                                        text = doctor.firstName + " " + doctor.lastName,
                                                        fontWeight = FontWeight.Bold,
                                                        style = MaterialTheme.typography.subtitle1,
                                                        fontFamily = Inter
                                                    )

                                                    Text(
                                                        text = doctor.specialty + " - " + doctor.clinicName,
                                                        fontWeight = FontWeight.SemiBold,
                                                        style = MaterialTheme.typography.subtitle2,
                                                        fontFamily = Inter
                                                    )

                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Mail,
                                                            contentDescription = null,
                                                            tint = AppColors.TextSecondary,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(5.dp))
                                                        Text(
                                                            text = doctor.email,
                                                            fontWeight = FontWeight.Medium,
                                                            style = MaterialTheme.typography.subtitle2,
                                                            fontFamily = Inter,
                                                            color = AppColors.TextSecondary,
                                                        )
                                                    }

                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ){
                                                        Icon(
                                                            imageVector = Icons.Default.Phone,
                                                            contentDescription = null,
                                                            tint = AppColors.TextSecondary,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(5.dp))
                                                        Text(
                                                            text = doctor.phoneNumber,
                                                            fontWeight = FontWeight.Medium,
                                                            style = MaterialTheme.typography.subtitle2,
                                                            fontFamily = Inter,
                                                            color = AppColors.TextSecondary,
                                                        )
                                                    }

                                                }
                                            }
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
                                    horizontalAlignment = Alignment.CenterHorizontally, // Center horizontally
                                    verticalArrangement = Arrangement.Center // Center vertically
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
                                        text = patientStats!!.totalAppointmentsToday.toString(),
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
                                        .padding(vertical = 12.dp, horizontal = 4.dp), // Take all available space
                                    horizontalAlignment = Alignment.CenterHorizontally, // Center horizontally
                                    verticalArrangement = Arrangement.Center // Center vertically
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
                                        text = patientStats!!.completedAppointments.toString(),
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
                                        painter = painterResource(id = R.drawable.c4),
                                        contentDescription = "Profile Image",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )

                                    Text(
                                        text = patientStats!!.pendingAppointments.toString(),
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