package com.example.doccur.ui.screens.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.doccur.R
import com.example.doccur.api.RetrofitClient.BASE_URL
import com.example.doccur.entities.DoctorProfile
import com.example.doccur.viewmodels.ProfileViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.example.doccur.ui.theme.AppColors
import com.example.doccur.ui.theme.Inter

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    doctorId: Int,
    onBackClick: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.getDoctorDetails(doctorId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (viewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (viewModel.error != null) {
            Text(
                "Error: ${viewModel.error}",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center),
                fontFamily = Inter,
                )
        } else if (viewModel.doctor != null) {
            DoctorProfileDetails(
                doctor = viewModel.doctor!!,
                onBackClick = onBackClick
            )
        } else {
            Text(
                "No doctor data available",
                modifier = Modifier.align(Alignment.Center),
                fontFamily = Inter,
                )
        }
    }
}

@Composable
fun DoctorProfileDetails(
    doctor: DoctorProfile,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header with photo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top=36.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 1.dp, bottom = 16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                // Profile photo
                if (doctor.photo_url != null) {
                    Surface(
                        modifier = Modifier
                            .size(130.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(BASE_URL + doctor.photo_url)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Doctor profile photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${doctor.first_name.first()}${doctor.last_name.first()}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Inter,
                            )
                    }
                }

            }

            // Doctor name and specialty
            Text(
                text = "Dr. ${doctor.first_name} ${doctor.last_name}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Inter,
                )

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = doctor.specialty,
                fontSize = 16.sp,
                color = AppColors.Blue,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,


                )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Personal Information Section
        InfoSection(
            title = "Personal Information",
            content = {
                InfoField("Full Name", "${doctor.first_name} ${doctor.last_name}")
                // Using hardcoded values from screenshot since they're not in the doctor entity
                InfoField("Gender", "Male")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Contact Information Section
        InfoSection(
            title = "Contact Information",
            content = {
                InfoField("Phone", doctor.phone_number)
                InfoField("Email", doctor.email)
                doctor.clinic?.let {
                    InfoField("Name", it.name)
                    InfoField("Location", it.location)
                    InfoField("Address", it.address)

                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))


        // Logout Button
        Button(
            onClick = { /* Handle logout */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE74C3C)
            ),
            shape = RoundedCornerShape(6.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = "Logout",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Logout",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                fontFamily = Inter,
                )
        }

    }
}

@Composable
fun InfoSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontFamily = Inter,
                )

            content()
        }
    }
}

@Composable
fun InfoField(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
            fontFamily = Inter,
            )

        Text(
            text = value,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = Color.Black,
            fontFamily = Inter,
            )
    }
}
