package com.kosiso.pupilmanager.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kosiso.pupilmanager.data.models.Pupil
import com.kosiso.pupilmanager.ui.theme.BackgroundColor
import com.kosiso.pupilmanager.ui.theme.Black
import com.kosiso.pupilmanager.ui.theme.Pink
import com.kosiso.pupilmanager.ui.theme.White
import com.kosiso.pupilmanager.ui.theme.onest
import com.kosiso.pupilmanager.ui.viewmodels.MainViewModel
import java.util.UUID
import kotlin.text.isBlank

@Composable
fun AddPupilScreen(
    mainViewModel: MainViewModel,
    onBackClick: () -> Unit){

    var pupilToAdd by remember { mutableStateOf<Pupil?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 15.dp)
            .padding(bottom = 65.dp)
            .verticalScroll(rememberScrollState())
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

                AddPupilSection(
                    addPupil = {
                        pupilToAdd = it
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween){
                    Button(
                        onClick = {
                            Log.i("cancel edit pupil btn", "pressed")
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
                            Log.i("save pupil btn", "pressed")
                            pupilToAdd?.let { pupilToAdd->
                                validateForm(
                                    updatedPupil = pupilToAdd,
                                    onValid = {
                                        mainViewModel.createPupil(pupilToAdd)
                                        onBackClick()
                                    },
                                    context = context
                                )
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
                            text = "Add",
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
}

@Composable
private fun AddPupilSection(
    addPupil:(Pupil) -> Unit,
){

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(White)

    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())

        ) {

            var pupilName by remember { mutableStateOf("") }
            MyTextField(
                fieldTitle = "Name",
                textInput = pupilName,
                onTextInputChange = { pupilName = it },
                placeholder = "pupil name"
            )

            Spacer(modifier = Modifier.height(20.dp))

            var pupilCountry by remember { mutableStateOf("") }
            MyTextField(
                fieldTitle = "Country",
                textInput = pupilCountry,
                onTextInputChange = { pupilCountry = it },
                placeholder = "pupil description"
            )

            LaunchedEffect(pupilName, pupilCountry) {

                val pupilToBeAdded = Pupil(
                    name = pupilName,
                    image = "",
                    pupilId = TODO(),
                    country = TODO(),
                    pageNumber = TODO(),
                    latitude = TODO(),
                    longitude = TODO(),
                )
                addPupil(pupilToBeAdded)
            }
        }
    }
}

private fun validateForm(updatedPupil: Pupil, onValid:()-> Unit, context: Context){
    updatedPupil?.let { pupil ->
        // Validate fields
        when {
            pupil.name.isBlank() -> {
                Toast.makeText(context, "Pupil name cannot be empty", Toast.LENGTH_SHORT).show()
            }
            pupil.country.isBlank() -> {
                Toast.makeText(context, "Quantity cannot be negative", Toast.LENGTH_SHORT).show()
            }
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

