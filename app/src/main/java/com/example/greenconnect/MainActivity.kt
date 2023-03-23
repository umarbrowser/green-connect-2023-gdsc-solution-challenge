package com.example.greenconnect
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.greenconnect.ui.theme.GreenConnectTheme
import com.google.accompanist.web.WebViewState
import com.google.accompanist.web.rememberWebViewState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class MainActivity : ComponentActivity() {
    @SuppressLint("SetJavaScriptEnabled")

    companion object {
        const val RC_SIGN_IN = 100
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private var locationCallback: LocationCallback? = null
    var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequired = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // firebase auth instance
        mAuth = FirebaseAuth.getInstance()

        // configure Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            GreenConnectTheme() {

                if (mAuth.currentUser == null) {
                    GoogleSignInButton {
                        signIn()
                    }
                } else {
                    val user: FirebaseUser = mAuth.currentUser!!
                    ProfileScreen(
                        profileImage = user.photoUrl!!,
                        name = user.displayName!!,
                        email = user.email!!,
                        signOutClicked = {
                            signOut()
                        }
                    )
                }

            }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // result returned from launching the intent from GoogleSignInApi.getSignInIntent()
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // Google SignIn was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: Exception) {
                    // Google SignIn Failed
                    Log.d("SignIn", "Google SignIn Failed")
                }
            } else {
                Log.d("SignIn", exception.toString())
            }
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // SignIn Successful
                    Toast.makeText(this, "SignIn Successful", Toast.LENGTH_SHORT).show()
                    setContent {
                        GreenConnectTheme() {
                            val user: FirebaseUser = mAuth.currentUser!!
                            ProfileScreen(
                                profileImage = user.photoUrl!!,
                                name = user.displayName!!,
                                email = user.email!!,
                                signOutClicked = {
                                    signOut()
                                }
                            )
                        }
                    }
                } else {
                    // SignIn Failed
                    Toast.makeText(this, "SignIn Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signOut() {
        // get the google account
        val googleSignInClient: GoogleSignInClient

        // configure Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Sign Out of all accounts
        mAuth.signOut()
        googleSignInClient.signOut()
            .addOnSuccessListener {
                Toast.makeText(this, "Sign Out Successful", Toast.LENGTH_SHORT).show()
                setContent {
                    GreenConnectTheme() {
                        GoogleSignInButton {
                            signIn()
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Sign Out Failed", Toast.LENGTH_SHORT).show()
            }
    }

}

@Composable
fun GoogleSignInButton(
    signInClicked: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.splash), contentDescription = null,
            modifier = Modifier
                .padding(start = 30.dp, bottom = 15.dp, end = 30.dp)
                .size(150.dp)
                .fillMaxWidth())
        Text(text = "Welcome Back",
            modifier = Modifier.padding(start = 30.dp, bottom = 10.dp, end = 30.dp),
            style = MaterialTheme.typography.h1)
        Text(text = "You've been Missed",
            modifier = Modifier.padding(start = 30.dp, bottom = 100.dp, end = 30.dp),
            style = MaterialTheme.typography.subtitle2)
        Card(
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp)
                .height(55.dp)
                .fillMaxWidth()
                .clickable {
                    signInClicked()
                },
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(width = 1.5.dp, color = Color.Black),
            elevation = 5.dp
        ) {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .size(32.dp)
                        .padding(0.dp)
                        .align(Alignment.CenterVertically),
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = "google_logo"
                )
                Text(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .align(Alignment.CenterVertically),
                    text = "Sign In With Google",
                    fontSize = MaterialTheme.typography.h6.fontSize,
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(
    profileImage: Uri,
    name: String,
    email: String,
    signOutClicked: () -> Unit
) {
    Column() {
        Navigation(profileImage, name, email, signOutClicked)
    }
}

@Composable
fun Navigation(profileImage: Uri,
               name: String,
               email: String,
               signOutClicked: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = "main_screen") {

        // Main Screen
        composable("main_screen") {
            GreenConnectHome(navController = navController,
                profileImage, name,email, signOutClicked
            )
        }
        // Market Screen
        composable("market_screen") {
            GreenConnectMarketPlace(navController = navController
            )
        }
        // Training Screen
        composable("training_screen") {
            GreenConnectTraining(navController = navController
            )
        }
        // Loans Screen
        composable("loans_screen") {
            GreenConnectLoans(navController = navController
            )
        }
        // Notes Screen
        composable("notes_screen") {
            GreenConnectNotes(
                navController = navController,
                LocalContext.current
            )
        }
        // Weather Screen
        composable("weather_screen") {
            GreenConnectWeather( navController = navController,
                LocalContext.current)
        }

        // Weather Screen
        composable("forum_screen") {
            GreenConnectForum( navController = navController,
                LocalContext.current)
        }

    }
}

@Composable
fun GreenConnectForum(navController: NavController, context: Context) {
    Column(modifier = Modifier.padding(8.dp),

        ) {
        Row(modifier = Modifier) {
            Text(text = "FORUM", style = MaterialTheme.typography.h2,
                modifier = Modifier
            )
        }
        LazyColumn(modifier = Modifier

            .fillMaxWidth()
        ){
            items(1){
                Training(R.drawable.forum_page,
                    R.string.forum_page,
                    R.string.forum_page_desc,
                    "https://agfuse.com",
                    navController)



            }

        }

    }
}

@Composable
fun GreenConnectWeather(navController: NavController, context: Context) {
    Column() {
        WebViewPage("https://weather.com/")

    }
}

@Composable
fun GreenConnectLoans(navController: NavController) {
    Column(modifier = Modifier.padding(8.dp),

        ) {
        Row(modifier = Modifier) {
            Text(text = "Access and Apply for those Loans and Grants", style = MaterialTheme.typography.h2,
                modifier = Modifier
            )
        }
        LazyColumn(modifier = Modifier

            .fillMaxWidth()
        ){
            items(1){
                Training(R.drawable.agmis,
                    R.string.loan_1,
                    R.string.loan_1_desc,
                    "https://agsmeisapp.nmfb.com.ng/",
                    navController)


                Training(R.drawable.bank_of_agric,
                    R.string.loan_2,
                    R.string.loan_2_desc,
                    "https://boanig.com/",
                    navController)

                Training(R.drawable.central_bank_of_nigeria,
                    R.string.loan_3,
                    R.string.loan_3_desc,
                    "https://www.cbn.gov.ng/devfin/acgsf.asp",
                    navController)
                Training(R.drawable.first,
                    R.string.loan_4,
                    R.string.loan_4_desc,
                    "https://www.firstbanknigeria.com/business/agriculture/commercial-agriculture-credit-scheme/",
                    navController)

            }

        }

    }
}

@Composable
fun GreenConnectTraining(navController: NavController) {
    Column(modifier = Modifier.padding(8.dp),

        ) {
        Row(modifier = Modifier) {
            Text(text = "Check out those learning materials and explore them for free", style = MaterialTheme.typography.h2,
                modifier = Modifier
            )
        }
        LazyColumn(modifier = Modifier

            .fillMaxWidth()
        ){
            items(1){
                //https://www.edx.org/course/irrigation-efficiency-more-food-with-less-water
                Training(R.drawable.irri,
                    R.string.category_1,
                    R.string.category_1_desc,
                    "https://www.edx.org/course/irrigation-efficiency-more-food-with-less-water",
                    navController)
//650118
                //https://alison.com/course/how-to-manage-a-poultry-farming-business
                Training(R.drawable.kaji,
                    R.string.category_2,
                    R.string.category_2_desc,
                    "https://alison.com/course/how-to-manage-a-poultry-farming-business",
                    navController)

                //https://alison.com/course/growing-organic-food-sustainably
                Training(R.drawable.organic,
                    R.string.category_3,
                    R.string.category_3_desc,
                    "https://alison.com/course/growing-organic-food-sustainably",
                    navController)
                Training(image = R.drawable.category_4,
                    title = R.string.category_4,
                    body = R.string.category_4_desc, link = "https://www.coursera.org/learn/agribusiness-management-challenges",
                    navController = navController )
                Training(image = R.drawable.category_5,
                    title = R.string.category_5,
                    body = R.string.category_5_desc,
                    link = "https://www.classcentral.com/course/climate-change-and-resilience-in-food-systems-33497/visit",
                    navController = navController)

            }

        }

    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Training(@DrawableRes image: Int,
             @StringRes title: Int,
             @StringRes body: Int,
             link: String,
             navController: NavController) {
    val context = LocalContext.current
    Column(modifier = Modifier
        .padding(15.dp)
        .fillMaxSize()) {
        Card(modifier = Modifier, elevation = 4.dp, onClick = {
            val urlIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(link)
            )
            context.startActivity(urlIntent)
        }) {
            Column {
                Image(
                    painter = painterResource(image),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(194.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = stringResource(id = title),
                    modifier = Modifier
                        .padding(16.dp),
                    style = MaterialTheme.typography.h5
                )
                Text(
                    text = stringResource(id = body),
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 5.dp, bottom = 16.dp),
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

@Composable
fun GreenConnectNotes(navController: NavController, context: Context) {
    Column() {
        WebViewPage("https://www.rapidtables.com/tools/notepad.html")

    }
}

fun addImageToStorage(imageUri: Uri,
                      image_name: String,
                      context: Context) {
    val storage:FirebaseStorage = FirebaseStorage.getInstance()
    storage.reference.child("images")
            .child("${image_name}.jpg")
            .putFile(imageUri)
            .addOnSuccessListener {
                // after the data addition is successful

                // we are displaying a success toast message.
                Toast.makeText(
                    context,
                    "Your Image has been added.",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener { e ->
                // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(context, "Fail to add Image \n$e", Toast.LENGTH_SHORT).show()
            }


   // val downloadUrl = storage.downloadUrl
       // storage.downloadUrl
}

fun addDataToFirebase(
    product_name: String,
    product_description: String,
    product_price: String,
    image_uri: String,
    seller_name: String,
    seller_address: String,
    seller_phone: String,
    seller_latt: String,
    seller_long: String,
    context: Context
) {
    // on below line creating an instance of firebase firestore.
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    //creating a collection reference for our Firebase Firestore database.
    val dbProducts: CollectionReference = db.collection("Products")
    //adding our data to our courses object class.
    val products = Products(
        productName = product_name,
        productDescription = product_description,
        productPrice = product_price,
        productPhoto = image_uri,
        sellerName = seller_name,
        sellerAddress = seller_address,
        sellerPhone = seller_phone,
        sellerLatitude = seller_latt,
        sellerLongitute = seller_long
    )
    //below method is use to add data to Firebase Firestore.
    dbProducts.add(products).addOnSuccessListener {
        // after the data addition is successful
        // we are displaying a success toast message.
        Toast.makeText(
            context,
            "Your Products has been added.",
            Toast.LENGTH_LONG
        ).show()
    }.addOnFailureListener { e ->
        // this method is called when the data addition process is failed.
        // displaying a toast message when data addition is failed.
        Toast.makeText(context, "Fail to add products \n$e", Toast.LENGTH_LONG).show()
    }
    context.startActivity(Intent(context, MainActivity::class.java))
}
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GreenConnectHome(navController: NavController,
                     profileImage: Uri,
                     name: String,
                     email: String,
                     signOutClicked: () -> Unit
){
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }
    var uiSettings by remember { mutableStateOf(MapUiSettings()) }
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    Column(modifier = Modifier,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Card(elevation = 4.dp){
            Row(modifier = Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)) {
            Card(
                modifier = Modifier
                    .size(50.dp)
                    .fillMaxHeight(0.4f),
                shape = RoundedCornerShape(125.dp),
                elevation = 10.dp
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = profileImage,
                    contentDescription = "profile_photo",
                    contentScale = ContentScale.FillBounds
                )
            }


            Column(modifier = Modifier
                .weight(5f)
                .padding(start = 5.dp)) {
                Text(
                    text = "Hi, $name",
                    modifier = Modifier,
                    style = MaterialTheme.typography.h2
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.body2
                )

            }
            IconButton(onClick = { signOutClicked() }) {
                Icon(
                    imageVector = Icons.Default.ExitToApp, contentDescription = "Location",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(3.dp)
                        .weight(1f)


                )

            }

        }
    }


        LazyColumn(modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
        ){
            items(1){
                CategoryList(R.drawable.kasuwa, R.string.home_1, R.drawable.koyarwa, R.string.home_2,
                    R.drawable.majalisa, R.string.home_3,
                    R.drawable.bashi, R.string.home_4,
                    R.drawable.rubutu, R.string.home_5,
                    R.drawable.yanayi, R.string.home_6,
                    navController)
                Card(elevation = 4.dp, modifier = Modifier.height(300.dp), shape = RoundedCornerShape(20.dp)) {
                    Column {
                       // WebViewPage("https://www.google.com/maps")
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                          //  cameraPositionState = cameraPositionState,
                            properties = properties,
                            uiSettings = uiSettings
                        ) {
                            Marker(
                                state = MarkerState(position = singapore),
                                title = "Singapore",
                                snippet = "Marker in Singapore"
                            )
                        }

                    }

                }

            }

        }




    }

}


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewPage(url: String) {
    val infoDialog = remember { mutableStateOf(false) }
    AndroidView(factory = {

        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            // to play video on a web view
            settings.javaScriptEnabled = true
            // Bind JavaScript code to Android code
            addJavascriptInterface(WebAppInterface(context,infoDialog), "Android")
            loadUrl(url)


        }
    }, update = {
        it.loadUrl(url)
    })
}

/** Instantiate the interface and set the context  */
class WebAppInterface(private val mContext: Context, var infoDialog: MutableState<Boolean>) {

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        infoDialog.value=true
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun GreenConnectMarketPlace(navController: NavController) {
        GreenConnectCategory(navController)
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategoryList(@DrawableRes firstImage: Int, @StringRes firstName: Int,
                 @DrawableRes secondImage:Int, @StringRes secondName: Int,
                 @DrawableRes thirdImage: Int, @StringRes thirdName: Int,
                 @DrawableRes forthImage:Int, @StringRes forthName: Int,
                 @DrawableRes fifthImage: Int, @StringRes fifthName: Int,
                 @DrawableRes sixthImage:Int, @StringRes sixthName: Int,
                 navController: NavController
) {

    Row(modifier = Modifier) {
        Card(
            onClick = {
                navController.navigate("market_screen")
                      }
            ,elevation = 4.dp, modifier = Modifier
                .padding(8.dp)
                .weight(1f), shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier
                .aspectRatio(1f)
                , horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Image(painter = painterResource(id = firstImage),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )

                Text(modifier = Modifier.padding(top = 8.dp),
                    text = stringResource(firstName ), style = MaterialTheme.typography.h3)

            }

        }

        Card(onClick = {
            navController.navigate("training_screen")
        },elevation = 4.dp, modifier = Modifier
            .padding(8.dp)
            .weight(1f), shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier

                .aspectRatio(1f)
                , horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Image(painter = painterResource(id = secondImage),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )

                Text(modifier = Modifier.padding(top = 8.dp),
                    text = stringResource(secondName ), style = MaterialTheme.typography.h3)

            }

        }



    }
    Row(modifier = Modifier) {
        Card(
            onClick = {
                navController.navigate("forum_screen")
            }
            ,elevation = 4.dp, modifier = Modifier
                .padding(8.dp)
                .weight(1f), shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier

                .aspectRatio(1f)
                , horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Image(painter = painterResource(id = thirdImage),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )

                Text(modifier = Modifier.padding(top = 8.dp),
                    text = stringResource(thirdName ), style = MaterialTheme.typography.h3)

            }

        }

        Card(onClick = {
            navController.navigate("loans_screen")
                       //loans_screen
        },elevation = 4.dp, modifier = Modifier
            .padding(8.dp)
            .weight(1f), shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier

                .aspectRatio(1f)
                , horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Image(painter = painterResource(id = forthImage),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )

                Text(modifier = Modifier.padding(top = 8.dp),
                    text = stringResource(forthName ), style = MaterialTheme.typography.h3)

            }

        }

    }
    Row(modifier = Modifier) {
        Card(
            onClick = {
               navController.navigate("notes_screen")
                      }
            ,elevation = 4.dp, modifier = Modifier
                .padding(8.dp)
                .weight(1f), shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier

                .aspectRatio(1f)
                , horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Image(painter = painterResource(id = fifthImage),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )

                Text(modifier = Modifier.padding(top = 8.dp),
                    text = stringResource(fifthName ), style = MaterialTheme.typography.h3)

            }

        }

        Card(onClick = {
            navController.navigate("weather_screen")
        },elevation = 4.dp, modifier = Modifier
            .padding(8.dp)
            .weight(1f), shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier

                .aspectRatio(1f)
                , horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Image(painter = painterResource(id = sixthImage),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )

                Text(modifier = Modifier.padding(top = 8.dp),
                    text = stringResource(sixthName ), style = MaterialTheme.typography.h3)

            }

        }

    }


}

@SuppressLint("UnrememberedMutableState")
@Composable
fun GreenConnectCategory(navController: NavController) {
    var context = LocalContext.current
    var productList = mutableStateListOf<Products?>()
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    // on below line getting data from our database
    db.collection("Products").get()
        .addOnSuccessListener { queryDocumentSnapshots ->
            // after getting the data we are calling
            // on success method
            // and inside this method we are checking
            // if the received query snapshot is empty or not.
            if (!queryDocumentSnapshots.isEmpty) {
                // if the snapshot is not empty we are
                // hiding our progress bar and adding
                // our data in a list.
                // loadingPB.setVisibility(View.GONE)
                val list = queryDocumentSnapshots.documents
                for (d in list) {
                    // after getting this list we are passing that
                    // list to our object class.
                    val c: Products? = d.toObject(Products::class.java)
                    c?.productID = d.id
                    // and we will pass this object class inside
                    // our arraylist which we have created for list view.
                    productList.add(c)

                }
            } else {
                // if the snapshot is empty we are displaying
                // a toast message.
                Toast.makeText(
                    context,
                    "No data found in Database",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        // if we don't get any data or any error
        // we are displaying a toast message
        // that we donot get any data
        .addOnFailureListener {
            Toast.makeText(
                context,
                "Fail to get the data.",
                Toast.LENGTH_SHORT
            ).show()
        }
    // on below line we are calling method to display UI
    ProductUI(context = LocalContext.current, product_list = productList)

}

@Composable
fun ProductUI(context: Context, product_list: SnapshotStateList<Products?>) {
    var lat = ""
    var lon = ""
    var prod_address = ""
    var  prod_image = ""
    Column(modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //  BackToHome(navController =
        OutlinedButton(onClick = {  context.startActivity(Intent(context, SellerActivity::class.java))  }, modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null )
            Spacer(Modifier.size(ButtonDefaults.IconSize))
            Text(text = "SELL A PRODUCT",style= MaterialTheme.typography.h2)
        }


        LazyColumn(modifier = Modifier
            .padding(8.dp)

            .fillMaxWidth()
        ){
            itemsIndexed(product_list){ index, item ->
                Column(modifier = Modifier
                    .padding(15.dp)
                    // .weight(1f)
                ) {
                    Card(modifier = Modifier, elevation = 4.dp, shape = RoundedCornerShape(20.dp)) {
                        Column {
                            product_list[index]?.productPhoto?.let {
                                prod_image = it
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(it)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(194.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            product_list[index]?.productName?.let {
                                Text(
                                    text = it,
                                    modifier = Modifier
                                        .padding(16.dp),
                                    style = MaterialTheme.typography.h5
                                )
                            }
                            product_list[index]?.productDescription?.let {
                                Text(
                                    text = it,
                                    modifier = Modifier
                                        .padding(16.dp),
                                    style = MaterialTheme.typography.body1
                                )
                            }
                            product_list[index]?.productPrice?.let {
                                Text(
                                    text = "₦" + it,
                                    modifier = Modifier
                                        .padding(16.dp),
                                    style = MaterialTheme.typography.h5
                                )
                            }
                        }
                    }
                    var popup by remember { mutableStateOf(false) }
                    OutlinedButton(onClick = { popup = true }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = null )
                        Spacer(Modifier.size(ButtonDefaults.IconSize))
                        Text(text = "Contact The Seller",style= MaterialTheme.typography.h6)
                    }
                    if (popup){
                        Popup(onDismissRequest = {popup = true},
                            alignment = Alignment.Center,
                            properties = PopupProperties(focusable = true),
                        ) {
                            Surface(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.align(Alignment.CenterHorizontally)

                            ) {
                                Column(
                                    modifier = Modifier.padding(0.dp),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally)
                                {
                                    Row(modifier = Modifier.padding(10.dp)) {
                                        Icon(imageVector = Icons.Default.Person, contentDescription = null,
                                            modifier = Modifier)
                                        product_list[index]?.sellerName?.let {
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.h2
                                            )
                                        }
                                    }
                                    Row(modifier = Modifier.padding(10.dp)) {
                                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null,
                                            modifier = Modifier)
                                        product_list[index]?.sellerAddress?.let {
                                            prod_address = it
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.body1
                                            )
                                        }
                                    }

                                    Card(elevation = 4.dp, modifier = Modifier
                                        .height(300.dp)
                                        .padding(15.dp), shape = RoundedCornerShape(20.dp)){
                                        product_list[index]?.sellerLatitude?.let {
                                            lat = it
                                        }
                                        product_list[index]?.sellerLongitute?.let {
                                            lon = it
                                        }
                                        val seller_loc = LatLng(lat.toDouble(), lon.toDouble())
                                        val cameraPositionState = rememberCameraPositionState {
                                            position = CameraPosition.fromLatLngZoom(seller_loc, +10f)
                                        }
                                        var uiSettings by remember { mutableStateOf(MapUiSettings()) }
                                        var properties by remember {
                                            mutableStateOf(MapProperties(mapType = MapType.NORMAL))
                                        }
                                        GoogleMap(
                                            modifier = Modifier.fillMaxSize(),
                                            cameraPositionState = cameraPositionState,
                                            properties = properties,
                                            uiSettings = uiSettings
                                        ) {
                                            Marker(
                                                state = MarkerState(position = seller_loc),
                                                title = "Product Address",
                                                snippet = prod_address,
                                           //   icon = BitmapDescriptorFactory.fromBitmap(Icons.Default.Person)

                                            )
                                        }

                                    }
                                    Spacer(modifier = Modifier.height(32.dp))
                                    Row(modifier = Modifier.padding(8.dp)) {
                                        OutlinedButton(onClick = { popup = false },
                                            modifier = Modifier.weight(1f)) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowBack,
                                                contentDescription = null
                                            )
                                            Spacer(Modifier.size(ButtonDefaults.IconSize))
                                            Text(
                                                text = "Go Back",
                                                style = MaterialTheme.typography.button
                                            )
                                        }
                                        val ctx = LocalContext.current
                                        product_list[index]?.sellerPhone?.let {
                                            OutlinedButton(
                                                modifier = Modifier.weight(1f), onClick = {

                                                    // on below line we are opening the dialer of our
                                                    // phone and passing phone number.
                                                    // Use format with "tel:" and phoneNumber created is
                                                    // stored in u.
                                                    val u = Uri.parse("tel:" + it)

                                                    // Create the intent and set the data for the
                                                    // intent as the phone number.
                                                    val i = Intent(Intent.ACTION_DIAL, u)
                                                    try {

                                                        // Launch the Phone app's dialer with a phone
                                                        // number to dial a call.
                                                        ctx.startActivity(i)
                                                    } catch (s: SecurityException) {

                                                        // show() method display the toast with
                                                        // exception message.
                                                        Toast.makeText(
                                                            ctx,
                                                            "An error occurred",
                                                            Toast.LENGTH_LONG
                                                        )
                                                            .show()
                                                    }
                                                }) {
                                                Icon(
                                                    imageVector = Icons.Default.Call,
                                                    contentDescription = null
                                                )
                                                Spacer(Modifier.size(ButtonDefaults.IconSize))
                                                Text(
                                                    text = "Call Seller",
                                                    style = MaterialTheme.typography.button
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

        }


    }
}



@Composable
@Preview(showSystemUi = true)
fun ShowPrev(){
    GreenConnectTheme() {
        val navController = rememberNavController()
       GreenConnectLoans(navController = navController )

    }
}
