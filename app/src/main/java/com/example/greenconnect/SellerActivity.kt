package com.example.greenconnect

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.greenconnect.ui.theme.GreenConnectTheme
import com.google.android.gms.location.*


class SellerActivity : ComponentActivity() {

    private var locationCallback: LocationCallback? = null
    var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequired = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreenConnectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val product_name = remember {
                        mutableStateOf("")
                    }
                    val product_description = remember {
                        mutableStateOf("")
                    }
                    val product_price = remember {
                        mutableStateOf("")
                    }
                    val seller_name = remember {
                        mutableStateOf("")
                    }
                    val seller_address = remember {
                        mutableStateOf("")
                    }
                    val seller_phone = remember {
                        mutableStateOf("")
                    }
                    var seller_latitude = ""
                    var seller_longitude = ""
                    var currentTimeMillis = System.currentTimeMillis()
                    val context = LocalContext.current
                    val galleryLauncher =  rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
                        imageUri?.let {
                            addImageToStorage(imageUri = imageUri, image_name = "photo${currentTimeMillis}",context = context)
                        }
                    }

                    var currentLocation by remember {
                        mutableStateOf(LocationDetails(0.toDouble(), 0.toDouble()))
                    }
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                    locationCallback = object : LocationCallback() {
                        override fun onLocationResult(p0: LocationResult) {
                            for (lo in p0.locations) {
                                // Update UI with location data
                                currentLocation = LocationDetails(lo.latitude, lo.longitude)
                            }
                        }
                    }

                    val launcherMultiplePermissions = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestMultiplePermissions()
                    ) { permissionsMap ->
                        val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
                        if (areGranted) {
                            locationRequired = true
                            startLocationUpdates()
                            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                        }
                    }

                    Column {
                        Text(text = "Add and Sell a Product", style = MaterialTheme.typography.h2,
                        modifier = Modifier.padding(5.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyColumn() {
                            items(1) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth()
                                        .background(Color.White),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    val permissions = arrayOf(
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )

                                    TextField(
                                        value = product_name.value,
                                        onValueChange = { if (!it.contains(Regex("[1234567890~`!@#$%^&*()-=+{}/?,.<>]"))) product_name.value = it },
                                        placeholder = { Text(text = "Product Name") },
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        textStyle = TextStyle(
                                            color = Color.Black,
                                            fontSize = 15.sp
                                        ),
                                        singleLine = true,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    TextField(
                                        value = product_price.value,
                                        onValueChange = { product_price.value = it },
                                        placeholder = { Text(text = "Product Price") },
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        textStyle = TextStyle(
                                            color = Color.Black,
                                            fontSize = 15.sp
                                        ),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    TextField(
                                        value = product_description.value,
                                        onValueChange = { product_description.value = it },
                                        placeholder = { Text(text = "Description") },
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .height(120.dp)
                                            .fillMaxWidth(),
                                        textStyle = TextStyle(
                                            color = Color.Black,
                                            fontSize = 15.sp
                                        ),
                                        singleLine = false,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            galleryLauncher.launch("image/*")
                                                  },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Select the Image",
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    TextField(
                                        value = seller_name.value,
                                        onValueChange = { seller_name.value = it },
                                        placeholder = { Text(text = "Enter Your Name") },
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        textStyle = TextStyle(
                                            color = Color.Black,
                                            fontSize = 15.sp
                                        ),
                                        singleLine = true,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    TextField(
                                        value = seller_address.value,
                                        onValueChange = { seller_address.value = it },
                                        placeholder = { Text(text = "Enter Your Address") },
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .height(120.dp)
                                            .fillMaxWidth(),
                                        textStyle = TextStyle(
                                            color = Color.Black,
                                            fontSize = 15.sp
                                        ),
                                        singleLine = false,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    TextField(
                                        value = seller_phone.value,
                                        onValueChange = { seller_phone.value = it },
                                        placeholder = { Text(text = "Enter Your Phone Number") },
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        textStyle = TextStyle(
                                            color = Color.Black,
                                            fontSize = 15.sp
                                        ),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(onClick = {
                                        if (permissions.all {
                                                ContextCompat.checkSelfPermission(
                                                    context,
                                                    it
                                                ) == PackageManager.PERMISSION_GRANTED
                                            }) {
                                            // Get the location
                                            startLocationUpdates()
                                        } else {
                                            launcherMultiplePermissions.launch(permissions)
                                        }
                                    }) {
                                        Text(text = "Use my current location")
                                    }
                                    seller_latitude = currentLocation.latitude.toString()
                                    seller_longitude = currentLocation.longitude.toString()
                                    println(seller_latitude+"-"+seller_longitude)
                                    Text(text = "Note: Please wait for Your current location to be shown below and recorded before pressing Add Product",
                                        style = MaterialTheme.typography.h3, modifier = Modifier.padding(10.dp))
                                    Text(text = "Latitude : " + currentLocation.latitude, style = MaterialTheme.typography.caption)
                                    Text(text = "Longitude : " + currentLocation.longitude, style = MaterialTheme.typography.caption)
                                    Button(
                                        onClick = {

                                            // on below line we are validating user input parameters.
                                            if (TextUtils.isEmpty(product_name.value)) {
                                                Toast.makeText(context, "Please enter product name", Toast.LENGTH_SHORT).show()
                                            } else if (TextUtils.isEmpty(product_description.value)) {
                                                Toast.makeText(context, "Please enter product description", Toast.LENGTH_SHORT)
                                                    .show()
                                            }else if (TextUtils.isEmpty(seller_name.value)) {
                                                Toast.makeText(context, "Please enter seller name", Toast.LENGTH_SHORT)
                                                    .show()
                                            }else if (TextUtils.isEmpty(seller_address.value)) {
                                                Toast.makeText(context, "Please enter seller address", Toast.LENGTH_SHORT)
                                                    .show()
                                            }else if (TextUtils.isEmpty(seller_phone.value)) {
                                                Toast.makeText(context, "Please enter seller", Toast.LENGTH_SHORT)
                                                    .show()
                                            }
                                            else {
                                                var uri_formated = "https://firebasestorage.googleapis.com/v0/b/greenconnect-2dc2e.appspot.com/o/images%2Fphoto${currentTimeMillis}.jpg?alt=media&token=35bc2a27-c8e4-4efe-b272-748a0bc38ac6"
                                                // on below line adding data to
                                                // firebase firestore database.
                                                addDataToFirebase(
                                                    product_name = product_name.value,
                                                    product_description = product_description.value,
                                                    product_price = product_price.value,
                                                    image_uri = uri_formated,
                                                    seller_name = seller_name.value,
                                                    seller_address = seller_address.value,
                                                    seller_phone = seller_phone.value,
                                                    seller_latt = currentLocation.latitude.toString(),
                                                    seller_long = currentLocation.longitude.toString(),
                                                    context = context
                                                )

                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Add Product",
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationCallback?.let {
            val locationRequest = LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (locationRequired) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
    }


}