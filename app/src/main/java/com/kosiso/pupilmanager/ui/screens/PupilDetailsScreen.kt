package com.kosiso.pupilmanager.ui.screens

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun PupilDetailsScreen(
    pupilId: Int,
    mainViewModel: MainViewModel,
    onBackClick: () -> Unit,
    // the Int here is the pupil Id
    onNavigateToEditPupilScreen: (Int) -> Unit){

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

            PupilDetailsSection(
                mainViewModel,
                pupilId
            )

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
}

@Composable
private fun PupilDetailsSection(mainViewModel: MainViewModel, pupilId: Int){
    mainViewModel.getPupils(pupilId)
    val pupil = mainViewModel.pupilById.collectAsState().value
    Log.i("pupil details", "pupil: $pupil")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(White)

    ){
        Column(
            modifier = Modifier
                .padding(20.dp)

        ){
            when(pupil){
                is GetPupilByIdState.Success -> {
                    Log.i("pupil details", "success: ${pupil.data}")
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Pupil Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = pupil.data.name,
                        style = TextStyle(
                            color = Black,
                            fontFamily = onest,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = pupil.data.country,
                        style = TextStyle(
                            color = Black,
                            fontFamily = onest,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    GoogleMapsBox(
                        lat = pupil.data.latitude,
                        long = pupil.data.longitude
                    )
                }
                is GetPupilByIdState.Error -> {
                    Log.i("pupil details", "error: ${pupil.message}")
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun GoogleMapsBox(lat: Double, long: Double) {
    val markerPosition = LatLng(lat, long)
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
            state = MarkerState(position = markerPosition),
            title = "Location",
            snippet = "Lat: $lat, Long: $long"
        )
    }
}