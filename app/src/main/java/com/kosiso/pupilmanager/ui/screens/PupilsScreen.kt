package com.kosiso.pupilmanager.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.kosiso.pupilmanager.R
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kosiso.pupilmanager.data.models.Pupil
import com.kosiso.pupilmanager.ui.theme.BackgroundColor
import com.kosiso.pupilmanager.ui.theme.Black
import com.kosiso.pupilmanager.ui.theme.Pink
import com.kosiso.pupilmanager.ui.theme.White
import com.kosiso.pupilmanager.ui.theme.onest
import com.kosiso.pupilmanager.ui.viewmodels.MainViewModel
import com.kosiso.pupilmanager.utils.NetworkUtils
import com.kosiso.pupilmanager.utils.PaginationState
import com.kosiso.pupilmanager.ui.screen_states.GetPupilState
import com.kosiso.pupilmanager.ui.screen_states.ImageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PupilsScreen(
    mainViewModel: MainViewModel,
    onNavigateToPupilDetailsScreen: (Int) -> Unit,
    onNavigateToAddPupilScreen: () -> Unit){

    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 15.dp)
            .padding(bottom = 65.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
        ){

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()

            ){
                Text(
                    text = "Pupils",
                    style = TextStyle(
                        color = Black,
                        fontFamily = onest,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 25.sp
                    )
                )
                PaginationControls(mainViewModel)
            }


            Spacer(modifier = Modifier.height(15.dp))

            PupilListSection(
                mainViewModel,
                onNavigateToPupilDetailsScreen
            )

        }

        FloatingActionButton(
            onClick = { onNavigateToAddPupilScreen() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Pink,
            contentColor = White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Pupil"
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PupilListSection(
    mainViewModel: MainViewModel,
    onNavigateToPupilDetailsScreen: (Int) -> Unit
){

    var showDeleteDialog by remember { mutableStateOf(false) }
    var pupilToDelete by remember { mutableStateOf<Pupil?>(null) }

    val context = LocalContext.current
    val pupilListState = mainViewModel.pupilsList.collectAsState().value
    val toastEvent = mainViewModel.toastEventPL.collectAsState().value
    if (toastEvent != "") {
        LaunchedEffect(toastEvent) {
            Toast.makeText(context, toastEvent, Toast.LENGTH_SHORT).show()
        }
    }
    Log.i("show pupils list", "$pupilListState")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(White)

    ){
        when(pupilListState){
            is GetPupilState.Idle ->{ }
            is GetPupilState.Loading ->{}
            is GetPupilState.Error -> {}
            is GetPupilState.Success ->{
                val pupilList = pupilListState.data
                if(pupilList.isEmpty()){
                    Log.i("show pupils list", "$pupilListState")
                    Text("No Pupils available, you can always add one.",
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center))
                }else{
                    LazyColumn {
                        items(
                            items = pupilList,
                            key = { it.pupilId }
                        ) { pupil->

                            SwipeToDelete(
                                onDelete = {
                                    pupilToDelete = pupil
                                    showDeleteDialog = true
                                },
                                pupilItem = {
                                    Box(
                                        modifier = Modifier
                                            .clickable{
                                                onNavigateToPupilDetailsScreen(pupil.pupilId)
                                            }
                                    ) {
                                        PupilItem(pupil)
                                    }
                                }
                            )
                        }
                    }
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
fun PaginationControls(mainViewModel: MainViewModel) {
    val mPagination = mainViewModel.pagination.collectAsState()
    val pagination = mPagination.value
    Log.i("pagination", "${pagination}")
    val context = LocalContext.current
    var showProgressNext = remember { mutableStateOf(false) }
    var showProgressPrev = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when(pagination){
            is PaginationState.Loading -> {}

            is PaginationState.Success -> {

                showProgressNext.value = false
                showProgressPrev.value = false
                val itemsPerPage = 5
                val startItem = (pagination.data.pageNumber - 1) * itemsPerPage + 1
                val endItem = (pagination.data.itemCount)*(pagination.data.pageNumber)
                val isPreviousEnabled = pagination.data.pageNumber > 1
                val isNextEnabled = pagination.data.pageNumber < pagination.data.totalPages


                Button(
                    onClick = {
                        showProgressPrev.value = true
                        mainViewModel.getPupils((pagination.data.pageNumber) - 1)
                    },
                    enabled = isPreviousEnabled
                ) {
                    if(NetworkUtils.isInternetAvailable(context)){
                        if(showProgressPrev.value){
                            ShowProgressBar()
                        }else{
                            Text("<")
                        }
                    }else{
                        Text("<")
                    }

                }

                Text(if(startItem < 1) "0" else "$startItem" +
                        " -- " +
                        "$endItem " +
                        "of " +
                        "${(pagination.data.totalPages)*(pagination.data.itemCount)}")

                Button(
                    onClick = {
                        showProgressNext.value = true
                        mainViewModel.getPupils((pagination.data.pageNumber) + 1)
                    },
                    enabled = isNextEnabled
                ) {
                    if(NetworkUtils.isInternetAvailable(context)){
                        if(showProgressNext.value){
                            ShowProgressBar()
                        }else{
                            Text(">")
                        }
                    }else{
                        Text(">")
                    }
                }
            }

            else -> {}
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDelete(
    onDelete:() -> Unit,
    pupilItem: @Composable () -> Unit
){
    val dismissState = rememberSwipeToDismissBoxState()

    if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
        LaunchedEffect(Unit) {
            onDelete()
            dismissState.reset()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true
    ) {
        pupilItem()
    }
}

@Composable
private fun PupilItem(pupil: Pupil){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(White)
    ){
        Box(
            modifier = Modifier
                .weight(0.3f),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(id = R.drawable.ic_pupil),
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

        }

        Box(
            modifier = Modifier
                .weight(0.7f)
                .padding(end = 16.dp)
        ){
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Text(
                            text = if (pupil.name.length > 30) pupil.name.take(29) + "..." else pupil.name,
                            style = TextStyle(
                                color = Black,
                                fontFamily = onest,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        )

                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Text(
                            text = "ttt",
                            style = TextStyle(
                                color = Black.copy(alpha = 0.4f),
                                fontFamily = onest,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = pupil.country,
                            style = TextStyle(
                                color = Black,
                                fontFamily = onest,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 19.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowProgressBar(){
    Box(contentAlignment = Alignment.Center){
        CircularProgressIndicator(
            modifier = Modifier
                .size(10.dp),
            color = White,
            strokeCap = StrokeCap.Round,
            strokeWidth = 1.dp
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
