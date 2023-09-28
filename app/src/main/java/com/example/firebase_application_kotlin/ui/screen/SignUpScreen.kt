package com.example.firebase_application_kotlin.ui.screen

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.firebase_application_kotlin.R
import com.example.firebase_application_kotlin.Screen
import com.example.firebase_application_kotlin.data.SignUp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavHostController){

    var imgUrl by remember{ mutableStateOf("") }
    val database = Firebase.database
    var isUploading = remember{ mutableStateOf(false) }
    val emty by remember{ mutableStateOf("") }
    var name by remember{ mutableStateOf("") }
    var password by remember{ mutableStateOf("") }
    var passwordVisibility by remember{ mutableStateOf(false) }


    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showDialog by remember {
        mutableStateOf(false)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ){uri: Uri? ->
        uri?.let {
            bitmap = if (Build.VERSION.SDK_INT < 28){
                MediaStore.Images.Media.getBitmap(context.contentResolver,it)
            }else{
                val source = ImageDecoder.createSource(context.contentResolver,it)
                ImageDecoder.decodeBitmap(source)
            }
        }
    }
    val cLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ){
        bitmap = it
    }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp)
    ){
        if (bitmap!=null){
            Image(
                bitmap=bitmap?.asImageBitmap()!! ,
                contentDescription ="",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Blue)
                    .size(150.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Blue,
                        shape = CircleShape
                    )
                    .clickable { showDialog = true }
            )
        }else{
            Image(
                painter = painterResource(id = R.drawable.person),
                contentDescription = "",
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Blue)
                    .size(150.dp)
                    .clickable { showDialog = true }
            )
        }
        Column (
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp)
        ){
            if (showDialog){
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .width(300.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Blue)
                ){
                    Column (modifier = Modifier.padding(start = 60.dp)){
                        Image(
                            painter = painterResource(id = R.drawable.camera),
                            contentDescription = "",
                            modifier= Modifier
                                .size(50.dp)
                                .clickable {
                                    cLauncher.launch()
                                    showDialog = false
                                }
                        )
                        Text(
                            text = "Camera",
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.padding(30.dp))
                    Column (){
                        Image(
                            painter = painterResource(id = R.drawable.image),
                            contentDescription = "",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    launcher.launch("image/*")
                                    showDialog = false
                                }
                        )
                        Text(
                            text = "Gallery",
                            color = Color.White
                        )
                    }
                    Column (
                        modifier = Modifier
                            .padding(start = 50.dp, bottom = 80.dp)
                    ){
                        Text(
                            text = "X",
                            color = Color.White,
                            modifier = Modifier
                                .clickable {
                                    showDialog = false
                                }
                        )
                    }
                }
            }
        }
    }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier= Modifier
            .fillMaxSize()
            .padding(top = 300.dp)
    ){
        TextField(
            value = name,
            onValueChange ={
                name = it
            },
            label = {
                Text(text = "Username")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.person),
                    contentDescription =""
                )
            },
            trailingIcon = {
                if (name.isNotEmpty()){
                    Icon(
                        painter = painterResource(id = R.drawable.cancel) ,
                        contentDescription = "",
                        Modifier.clickable { name = emty.toString() }
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Blue,
                focusedLabelColor = Color.Blue,
                focusedLeadingIconColor = Color.Blue,
                containerColor = Color.White,
                unfocusedIndicatorColor = Color.Blue,
                unfocusedLabelColor = Color.Blue,
                unfocusedLeadingIconColor = Color.Blue,
            ),
            textStyle = TextStyle(
                color = Color.Blue,
                fontSize = 16.sp,
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = password,
            onValueChange = {
                password=it
            },
            label = {
                Text(text = "Password")
            },
            leadingIcon =  {
                Icon(painter = painterResource(id = R.drawable.lock),contentDescription = null)
            },
            trailingIcon = {
                if (password.isNotEmpty()) {
                    val visibilityIcon = when (passwordVisibility) {
                        true -> painterResource(id = R.drawable.visibility)
                        false -> painterResource(id = R.drawable.visibility_off)
                    }
                    Icon(
                        painter = visibilityIcon,
                        contentDescription = "",
                        Modifier.clickable {
                            passwordVisibility = !passwordVisibility
                        }
                    )
                }
            },
            visualTransformation = if (passwordVisibility){
                VisualTransformation.None
            }else{
                PasswordVisualTransformation()
            },

            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Blue,
                focusedLabelColor = Color.Blue,
                focusedLeadingIconColor = Color.Blue,
                containerColor = Color.White,
                unfocusedIndicatorColor = Color.Blue,
                unfocusedLabelColor = Color.Blue,
                unfocusedLeadingIconColor = Color.Blue,
            ),
            textStyle = TextStyle(
                color = Color.Blue,
                fontSize = 16.sp,
            )
        )
        Spacer(modifier = Modifier.height(70.dp))

        Button(
            onClick = {
                isUploading.value = true
                bitmap.let{bitmap ->
                    if (bitmap!=null){
                        uploadImageToFirebase(bitmap, context as ComponentActivity){success, imageUrl->
                            isUploading.value=false
                            if(success){
                                imageUrl.let {
                                    imgUrl = it
                                }
                                val signupRef = database.reference.child("Username")
                                val userRef = signupRef.child(name)
                                val user= SignUp(password,imageUrl)
                                userRef.setValue(user)
                                Toast.makeText(context,"Save User", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.FormScreen.route)
                            }
                            else{
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(.7f)
        ) {
            Text(
                text = "Sign Up",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier= Modifier
            .fillMaxWidth()
            .height(330.dp)
    ){
        if (isUploading.value){
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp),
                color = Color.White
            )
        }
    }
}


fun uploadImageToFirebase(
    bitmap: Bitmap,
    context : ComponentActivity,
    callback:(Boolean,String) -> Unit
){
    val storageRef = Firebase.storage.reference
    val imageRef = storageRef.child("images/${bitmap}")

    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
    val imageData = baos.toByteArray()

    imageRef.putBytes(imageData).addOnSuccessListener{
        imageRef.downloadUrl.addOnSuccessListener{ uri->
            val imageUrl = uri.toString()
            callback(true,imageUrl)
        }.addOnSuccessListener{
            callback(false,null.toString())
        }
    }.addOnFailureListener{
        callback(false,null.toString())

    }
}