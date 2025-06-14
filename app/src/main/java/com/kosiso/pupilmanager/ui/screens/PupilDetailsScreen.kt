package com.kosiso.pupilmanager.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kosiso.pupilmanager.ui.screen_states.GetPupilByIdState
import com.kosiso.pupilmanager.ui.theme.BackgroundColor
import com.kosiso.pupilmanager.ui.theme.Black
import com.kosiso.pupilmanager.ui.theme.Pink
import com.kosiso.pupilmanager.ui.theme.White
import com.kosiso.pupilmanager.ui.theme.onest
import com.kosiso.pupilmanager.ui.viewmodels.MainViewModel
import com.kosiso.pupilmanager.R
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.kosiso.pupilmanager.data.models.Pupil

@Composable
fun PupilDetailsScreen(
    pupilId: Int,
    mainViewModel: MainViewModel,
    onBackClick: () -> Unit,
    // the Int here is the pupil Id
    onNavigateToEditPupilScreen: (Int) -> Unit){

    mainViewModel.getPupilById(pupilId)
    val pupil = mainViewModel.pupilById.collectAsState().value
    Log.i("pupil details", "pupil: $pupil")

    val context = LocalContext.current
    val toastEvent = mainViewModel.toastEventP.collectAsState().value
    if (toastEvent != "") {
        LaunchedEffect(toastEvent) {
            Toast.makeText(context, toastEvent, Toast.LENGTH_SHORT).show()
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var pupilToDelete by remember { mutableStateOf<Pupil?>(null) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 15.dp)
            .padding(bottom = 20.dp)
    ){
        Column{

            Spacer(modifier = Modifier.height(20.dp))

            when(pupil){
                is GetPupilByIdState.Success -> {
                    Log.i("pupil details", "success: ${pupil.data}")
                    PupilDetailsSection(pupil.data)
                }
                is GetPupilByIdState.Error -> {
                    Log.i("pupil details", "error: ${pupil.message}")
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween){
                Button(
                    onClick = {
                        Log.i("delete pupil btn", "pressed")
                        mainViewModel.deletePupil(pupilId)
                        onBackClick()
                    },
                    modifier = Modifier
                        .weight(0.4f)
                        .height(50.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        bottomStart = 12.dp,
                        topEnd = 12.dp,
                        bottomEnd = 12.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Black
                    )
                ) {
                    Text(
                        text = "Delete",
                        style = TextStyle(
                            color = White,
                            fontFamily = onest,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = {
                        Log.i("update pupil btn", "pressed")
                        onNavigateToEditPupilScreen(pupilId)
                    },
                    modifier = Modifier
                        .weight(0.4f)
                        .height(50.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        bottomStart = 12.dp,
                        topEnd = 12.dp,
                        bottomEnd = 12.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Pink
                    )
                ) {
                    Text(
                        text = "Edit",
                        style = TextStyle(
                            color = White,
                            fontFamily = onest,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }

    if(showDeleteDialog && pupilToDelete != null){
        ShowDeleteDialog(
            pupil = pupilToDelete!!,
            onConfirm = {
                mainViewModel.deletePupil(pupilToDelete!!.pupilId)
                showDeleteDialog = false
                pupilToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                pupilToDelete = null
            }
        )
    }
}

@Composable
private fun PupilDetailsSection(pupil: Pupil){
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(White)
            .height(screenHeight * 0.8f)
    ){
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())

        ){

            Image(
                painter = painterResource(id = R.drawable.ic_pupil),
                contentDescription = "Pupil Image",
                modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(80.dp))
                .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = pupil.name,
                style = TextStyle(
                    color = Black,
                    fontFamily = onest,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = pupil.country,
                style = TextStyle(
                    color = Black,
                    fontFamily = onest,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            GoogleMapsBox(
                lat = pupil.latitude,
                long = pupil.longitude
            )

        }
    }
}

@Composable
private fun GoogleMapsBox(lat: Double, long: Double) {
    val markerPosition =LatLng(lat, long)
    val cameraPosition = remember {
        CameraPosition.fromLatLngZoom(markerPosition, 15f)
    }

    // Google Map Composable
    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        cameraPositionState = rememberCameraPositionState {
            position = cameraPosition
        }
    ) {
        Marker(
            state = remember { MarkerState(position = markerPosition) },
            title = "Location",
            snippet = "Lat: $lat, Long: $long"
        )
    }
}

@Composable
private fun ShowDeleteDialog(
    pupil: Pupil,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Confirm Delete")
        },
        text = {
            Text(text = "Are you sure you want to delete ${pupil.name}? This action cannot be undone.")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}