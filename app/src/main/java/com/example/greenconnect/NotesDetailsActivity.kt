package com.example.greenconnect

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.greenconnect.ui.theme.GreenConnectTheme
import com.google.firebase.firestore.FirebaseFirestore

class NotesDetailsActivity : ComponentActivity() {

    @SuppressLint("UnrememberedMutableState", "UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            GreenConnectTheme() {
                // A surface container using the 'background' color from the theme
                Surface(
                    // on below line we are specifying modifier and color for our app
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    // on the below line we are specifying
                    // the theme as the scaffold.
                    Scaffold(
                        // in scaffold we are specifying the top bar.
                        topBar = {
                            // inside top bar we are specifying
                            // background color.
                            TopAppBar(backgroundColor = MaterialTheme.colors.primary,
                                // along with that we are
                                // specifying title for our top bar.
                                title = {
                                    // in the top bar we are specifying
                                    // tile as a text
                                    Text(
                                        // on below line we are specifying
                                        // text to display in top app bar
                                        text = "View Notes",
                                        // on below line we are specifying
                                        // modifier to fill max width
                                        modifier = Modifier.fillMaxWidth(),
                                        // on below line we are specifying
                                        // text alignment
                                        textAlign = TextAlign.Center,
                                        // on below line we are specifying
                                        // color for our text.
                                        color = Color.White
                                    )
                                })
                        }) {

                        // on below line creating variable for list of data.
                        var noteList = mutableStateListOf<Notes?>()
                        // on below line creating variable for freebase database
                        // and database reference.
                        var db: FirebaseFirestore = FirebaseFirestore.getInstance()

                        // on below line getting data from our database
                        db.collection("Notes").get()
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
                                        val c: Notes? = d.toObject(Notes::class.java)
                                        c?.noteID = d.id
                                        // and we will pass this object class inside
                                        // our arraylist which we have created for list view.
                                        noteList.add(c)

                                    }
                                } else {
                                    // if the snapshot is empty we are displaying
                                    // a toast message.
                                    Toast.makeText(
                                        this@NotesDetailsActivity,
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
                                    this@NotesDetailsActivity,
                                    "Fail to get the data.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        // on below line we are calling method to display UI
                        firebaseUI(LocalContext.current, noteList)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun firebaseUI(context: Context, noteList: SnapshotStateList<Notes?>) {

        // on below line creating a column
        // to display our retrieved list.
        Column(
            // adding modifier for our column
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color.White),
            // on below line adding vertical and
            // horizontal alignment for column.
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // on below line we are
            // calling lazy column
            // for displaying listview.
            LazyColumn {
                // on below line we are setting data
                // for each item of our listview.
                itemsIndexed(noteList) { index, item ->
                    // on below line we are creating
                    // a card for our list view item.
                    Card(
                        onClick = {
                            // on below line opening new activity and passing data.
                            val i = Intent(context, UpdateNote::class.java)
                            i.putExtra("noteID", item?.noteID)
                            i.putExtra("noteTitle", item?.title)
                            i.putExtra("noteBody", item?.body)

                            // inside on click we are opening
                            // new activity to update course details.
                            context.startActivity(i)
                        },
                        // on below line we are adding
                        // padding from our all sides.
                        modifier = Modifier.padding(8.dp),

                        // on below line we are adding
                        // elevation for the card.
                        elevation = 6.dp
                    ) {
                        // on below line we are creating
                        // a row for our list view item.
                        Column(
                            // for our row we are adding modifier
                            // to set padding from all sides.
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            // on below line inside row we are adding spacer
                            Spacer(modifier = Modifier.width(5.dp))
                            // on below line we are displaying course name.
                            noteList[index]?.title?.let {
                                Text(
                                    // inside the text on below line we are
                                    // setting text as the language name
                                    // from our modal class.
                                    text = it,

                                    // on below line we are adding padding
                                    // for our text from all sides.
                                    modifier = Modifier.padding(4.dp),

                                    // on below line we are adding
                                    // color for our text
                                    //   color = greenColor,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.subtitle2
                                )
                            }
                            // adding spacer on below line.
                            Spacer(modifier = Modifier.height(5.dp))

                            // on below line displaying text for course duration
                            noteList[index]?.body?.let {
                                Text(
                                    // inside the text on below line we are
                                    // setting text as the language name
                                    // from our modal class.
                                    text = it,

                                    // on below line we are adding padding
                                    // for our text from all sides.
                                    modifier = Modifier.padding(4.dp),

                                    // on below line we are
                                    // adding color for our text
                                    //color = Color.Black,
                                    textAlign = TextAlign.Justify,
                                    style = MaterialTheme.typography.body1

                                )
                                Spacer(modifier = Modifier.height(5.dp))

                                // on below line displaying text for course duration
                                noteList[index]?.productPhoto?.let {
                                    AsyncImage(
                                        // inside the text on below line we are
                                        // setting text as the language name
                                        // from our modal class.
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(it)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = null,

                                        // on below line we are adding padding
                                        // for our text from all sides.
                                        modifier = Modifier.padding(4.dp),
                                        contentScale = ContentScale.Crop

                                        // on below line we are
                                        // adding color for our text
                                        //color = Color.Black,
                                        //  textAlign = TextAlign.Justify,
                                        //   style = MaterialTheme.typography.body1
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
