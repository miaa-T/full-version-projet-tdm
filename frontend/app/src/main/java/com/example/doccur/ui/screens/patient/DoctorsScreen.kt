package com.example.doccur.ui.screens.patient

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.doccur.viewmodels.UsersViewModel
import com.example.doccur.api.RetrofitClient.BASE_URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorsScreen(viewModel: UsersViewModel, navController: NavController) {
    val allDoctors by viewModel.doctorList.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isMapView by remember { mutableStateOf(false) }

    // Filter states
    var selectedSpecialty by remember { mutableStateOf<String?>(null) }
    var selectedLocation by remember { mutableStateOf<String?>(null) }
    var showSpecialtyDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showFiltersExpanded by remember { mutableStateOf(false) }

    // Get unique specialties and locations
    val specialties = remember(allDoctors) {
        allDoctors.map { it.specialty }.distinct().sorted()
    }
    val locations = remember(allDoctors) {
        allDoctors.mapNotNull { it.clinic?.location }.distinct().sorted()
    }

    val filteredDoctors = remember(allDoctors, searchQuery, selectedSpecialty, selectedLocation) {
        allDoctors.filter { doctor ->
            // Search filter
            val matchesSearch = searchQuery.isBlank() ||
                    doctor.first_name.lowercase().contains(searchQuery.lowercase()) ||
                    doctor.last_name.lowercase().contains(searchQuery.lowercase()) ||
                    doctor.specialty.lowercase().contains(searchQuery.lowercase()) ||
                    doctor.clinic?.name?.lowercase()?.contains(searchQuery.lowercase()) == true

            // Specialty filter
            val matchesSpecialty = selectedSpecialty == null ||
                    doctor.specialty == selectedSpecialty

            // Location filter
            val matchesLocation = selectedLocation == null ||
                    doctor.clinic?.location == selectedLocation

            matchesSearch && matchesSpecialty && matchesLocation
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadDoctors()
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Find Doctors") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Search header with gradient background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "Search doctors, specialties...",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showFiltersExpanded = !showFiltersExpanded }) {
                                Icon(
                                    if (showFiltersExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.FilterList,
                                    contentDescription = "Filters",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }

                // Animated Filters Section
                AnimatedVisibility(
                    visible = showFiltersExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = (-8).dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Filter Options",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Specialty Filter
                            OutlinedButton(
                                onClick = { showSpecialtyDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = ButtonDefaults.outlinedButtonBorder,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (selectedSpecialty != null)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Icon(Icons.Default.HealthAndSafety, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = selectedSpecialty ?: "Select Specialty"
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                if (selectedSpecialty != null) {
                                    IconButton(
                                        onClick = { selectedSpecialty = null },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Location Filter
                            OutlinedButton(
                                onClick = { showLocationDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = ButtonDefaults.outlinedButtonBorder,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (selectedLocation != null)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = selectedLocation ?: "Select Location"
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                if (selectedLocation != null) {
                                    IconButton(
                                        onClick = { selectedLocation = null },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Results header with view toggle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${filteredDoctors.size} doctors found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.List,
                                    contentDescription = "List view",
                                    tint = if (!isMapView)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { isMapView = false }
                                        .background(
                                            if (!isMapView)
                                                MaterialTheme.colorScheme.primaryContainer
                                            else
                                                Color.Transparent
                                        )
                                        .padding(8.dp)
                                        .size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Icon(
                                    Icons.Default.GridView,
                                    contentDescription = "Grid view",
                                    tint = if (isMapView)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { isMapView = true }
                                        .background(
                                            if (isMapView)
                                                MaterialTheme.colorScheme.primaryContainer
                                            else
                                                Color.Transparent
                                        )
                                        .padding(8.dp)
                                        .size(24.dp)
                                )
                            }
                        }
                    }
                }

                if (!isMapView) {
                    // List view
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredDoctors) { doctor ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clickable {
                                        navController.navigate("doctorDetails/${doctor.id}")
                                    },
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Doctor profile image
                                    Surface(
                                        modifier = Modifier.size(80.dp),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surface,
                                        shadowElevation = 2.dp
                                    ) {
                                        AsyncImage(
                                            model = BASE_URL + doctor.photo_url,
                                            contentDescription = "Doctor profile photo",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    // Doctor information
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Dr. ${doctor.first_name} ${doctor.last_name}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Spacer(modifier = Modifier.width(8.dp))

                                            Surface(
                                                shape = CircleShape,
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(20.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = "Verified",
                                                    modifier = Modifier.padding(3.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = doctor.specialty,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        doctor.clinic?.let { clinic ->
                                            // Clinic name with icon
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(vertical = 2.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.LocalHospital,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )

                                                Spacer(modifier = Modifier.width(4.dp))

                                                Text(
                                                    text = clinic.name ?: "No clinic information",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            // Location with icon
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(vertical = 2.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.LocationOn,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )

                                                Spacer(modifier = Modifier.width(4.dp))

                                                Text(
                                                    text = if (!clinic.address.isNullOrEmpty() && !clinic.location.isNullOrEmpty()) {
                                                        "${clinic.address}, ${clinic.location}"
                                                    } else {
                                                        clinic.address ?: clinic.location ?: "No address"
                                                    },
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }

                                    // Right arrow icon
                                    IconButton(onClick = { navController.navigate("doctorDetails/${doctor.id}") }) {
                                        Icon(
                                            Icons.Default.ArrowForward,
                                            contentDescription = "View doctor details",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Grid view
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredDoctors) { doctor ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("doctorDetails/${doctor.id}")
                                    },
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Doctor profile image
                                    Surface(
                                        modifier = Modifier.size(90.dp),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surface,
                                        shadowElevation = 2.dp
                                    ) {
                                        AsyncImage(
                                            model = BASE_URL + doctor.photo_url,
                                            contentDescription = "Doctor profile photo",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Doctor name with verification badge
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Dr. ${doctor.first_name}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.width(4.dp))

                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(16.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Verified",
                                                modifier = Modifier.padding(2.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = doctor.specialty,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Divider(
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                        thickness = 1.dp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    doctor.clinic?.let { clinic ->
                                        // Clinic name with icon
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                Icons.Default.LocalHospital,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(12.dp)
                                            )

                                            Spacer(modifier = Modifier.width(4.dp))

                                            Text(
                                                text = clinic.name ?: "No clinic",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        // UPDATED: Location with icon (matching list view)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(12.dp)
                                            )

                                            Spacer(modifier = Modifier.width(4.dp))

                                            Text(
                                                text = if (!clinic.address.isNullOrEmpty() && !clinic.location.isNullOrEmpty()) {
                                                    "${clinic.address}, ${clinic.location}"
                                                } else {
                                                    clinic.address ?: clinic.location ?: "No address"
                                                },
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
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

        // Display specialty selection dialog
        if (showSpecialtyDialog) {
            AlertDialog(
                onDismissRequest = { showSpecialtyDialog = false },
                title = { Text("Select Specialty") },
                text = {
                    LazyColumn {
                        items(specialties) { specialty ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedSpecialty = specialty
                                        showSpecialtyDialog = false
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(specialty)
                                Spacer(modifier = Modifier.weight(1f))
                                if (selectedSpecialty == specialty) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSpecialtyDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Display location selection dialog
        if (showLocationDialog) {
            AlertDialog(
                onDismissRequest = { showLocationDialog = false },
                title = { Text("Select Location") },
                text = {
                    LazyColumn {
                        items(locations) { location ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedLocation = location
                                        showLocationDialog = false
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(location)
                                Spacer(modifier = Modifier.weight(1f))
                                if (selectedLocation == location) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLocationDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}