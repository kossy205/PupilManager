package com.kosiso.pupilmanager.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.kosiso.pupilmanager.data.models.Pupil
import com.kosiso.pupilmanager.ui.screen_states.GetPupilByIdState
import com.kosiso.pupilmanager.ui.theme.BackgroundColor
import com.kosiso.pupilmanager.ui.theme.Black
import com.kosiso.pupilmanager.ui.theme.Pink
import com.kosiso.pupilmanager.ui.theme.White
import com.kosiso.pupilmanager.ui.theme.onest
import com.kosiso.pupilmanager.ui.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.compareTo
import kotlin.text.isBlank

@Composable
fun EditPupilScreen(
    pupilId: Int,
    mainViewModel: MainViewModel,
    onBackClick: () -> Unit){

    mainViewModel.getPupilById(pupilId)
    val pupil = mainViewModel.pupilById.collectAsState().value
    Log.i("edit pupil details", "pupil: $pupil")


    var pupilToEdit by remember { mutableStateOf<Pupil?>(null) }
    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 15.dp)
            .padding(bottom = 65.dp)
    ) {

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Create Pupil",
            style = TextStyle(
                color = Black,
                fontFamily = onest,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 25.sp
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

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
                        Log.i("edit pupil details", "success: ${pupil.data}")
                        EditPupilSection(
                            pupil = pupil.data,
                            editPupil = {
                                pupilToEdit = it
                            }
                        )
                    }
                    is GetPupilByIdState.Error -> {
                        Log.i("edit pupil details", "error: ${pupil.message}")
                    }
                    else -> {}
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Button(
                        onClick = {
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
                            text = "Cancel",
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
                            pupilToEdit?.let { pupilToEdit->
                                validateForm(
                                    updatedPupil = pupilToEdit,
                                    onValid = {
                                        mainViewModel.updatePupil(pupilToEdit.pupilId, pupilToEdit)
                                        onBackClick()
                                    },
                                    context = context
                                )
                                Log.i("update pupil image", pupilToEdit.image)
                            }
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

        Spacer(modifier = Modifier.height(20.dp))


    }
}

@Composable
private fun EditPupilSection(
    pupil: Pupil,
    editPupil:(Pupil) -> Unit,
){
    var pupilName by remember { mutableStateOf("") }
    var pupilCountry by remember { mutableStateOf("") }
//    var pupilImage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(White)

    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())

        ) {
            MyTextField(
                fieldTitle = "Name",
                textInput = pupilName,
                onTextInputChange = { pupilName = it },
                placeholder = pupil.name
            )

            Spacer(modifier = Modifier.height(20.dp))

            MyTextField(
                fieldTitle = "Country",
                textInput = pupilCountry,
                onTextInputChange = { pupilCountry = it },
                placeholder = pupil.country
            )

            ImagePickerWithBase64(
                onImageSelected = { base64String ->
//                    pupilImage = base64String
                    Log.i("Base64 length", "${base64String.length}")
//                    Log.i("Pupil Image", pupilImage)
                }
            )

            LaunchedEffect(pupilName, pupilCountry) {

                val pupilToBeEdited = Pupil(
                    name = pupilName,
                    image = "demo image url to display",
                    pupilId = pupil.pupilId,
                    country = pupilCountry,
                    pageNumber = pupil.pageNumber,
                    latitude = 0.0,
                    longitude = 0.0,
                )
                editPupil(pupilToBeEdited)
            }
        }
    }
}

private fun validateForm(updatedPupil: Pupil, onValid:()-> Unit, context: Context){
    updatedPupil?.let { pupil ->
        when {
            pupil.image.isBlank() -> {
                Toast.makeText(context, "please select image", Toast.LENGTH_LONG).show()
            }
            pupil.name.isBlank() -> {
                Toast.makeText(context, "Pupil name cannot be empty", Toast.LENGTH_LONG).show()
            }
            pupil.country.isBlank() -> {
                Toast.makeText(context, "Quantity cannot be negative", Toast.LENGTH_LONG).show()
            }
//            pupil.latitude == 0.0 && pupil.longitude == 0.0 -> {
//                Toast.makeText(context, "Please select location", Toast.LENGTH_LONG).show()
//            }
            else -> {
                onValid()
            }
        }
    } ?: run {
        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun MyTextField(
    fieldTitle: String,
    textInput: String,
    onTextInputChange: (String) -> Unit,
    placeholder: String
){
    Text(
        text = fieldTitle,
        style = TextStyle(
            color = Black.copy(alpha = 0.4f),
            fontFamily = onest,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp
        )
    )
    OutlinedTextField(
        value = textInput,
        onValueChange = onTextInputChange,
        placeholder = {
            Text(
                text = placeholder,
                style = TextStyle(
                    color = Black.copy(alpha = 0.4f),
                    fontFamily = onest,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp
                )
            )
        },
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 15.sp,
            fontFamily = onest,
            fontWeight = FontWeight.Normal
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Black.copy(alpha = 0.2f),
            focusedBorderColor = Pink,
        ),
        shape = RoundedCornerShape(12.dp)
    )
}


@Composable
private fun ImagePickerWithBase64(
    onImageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBase64 by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var shouldLaunchPicker by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Activity result launcher for picking images
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            isLoading = true

            // Convert image to Base64
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val base64String = convertImageToBase64(context, it)
                    withContext(Dispatchers.Main) {
                        imageBase64 = base64String
                        onImageSelected(base64String)
                        isLoading = false
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        isLoading = false

                    }
                }
            }
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, trigger image picker launch
            shouldLaunchPicker = true
            showPermissionDialog = false
        } else {
            // Permission denied, show dialog
            showPermissionDialog = true
        }
    }

    // Launch picker when permission is granted
    LaunchedEffect(shouldLaunchPicker) {
        if (shouldLaunchPicker) {
            imagePickerLauncher.launch("image/*")
            shouldLaunchPicker = false
        }
    }

    // Function to handle image picker launch
    fun launchImagePicker() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // Android 10+ (API 29+): GetContent() works without explicit permission
                imagePickerLauncher.launch("image/*")
            }
            else -> {
                // Android 9 and below: Check and request permission first
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    imagePickerLauncher.launch("image/*")
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // user would click here to select image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { launchImagePicker() },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedImageUri != null) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator()
                    }
                    selectedImageUri != null -> {
                        // Show image preview
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        // Show placeholder when no image is selected
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit image",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap to select image",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Permission dialog
        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog = false },
                title = { Text("Permission Required") },
                text = {
                    Text("This app needs access to your images to select and convert them to Base64. Please grant the permission in settings.")
                },
                confirmButton = {
                    TextButton(onClick = { showPermissionDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

private suspend fun convertImageToBase64(context: Context, uri: Uri): String {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            bytes?.let {
                Base64.encodeToString(it, Base64.DEFAULT)
            } ?: throw Exception("Failed to read image data")
        } catch (e: Exception) {
            throw Exception("Error converting image to Base64: ${e.message}")
        }
    }
}
