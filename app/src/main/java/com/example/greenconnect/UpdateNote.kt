package com.example.greenconnect

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.greenconnect.ui.theme.GreenConnectTheme
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore

class UpdateNote : ComponentActivity() {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreenConnectTheme() {
                // A surface container using the
                // 'background' color from the theme
                Surface(
                    // on below line we are specifying
                    // modifier and color for our app
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
                                // along with that we are specifying
                                // title for our top bar.
                                title = {
                                    // in the top bar we are specifying
                                    // title as a text
                                    Text(
                                        // on below line we are specifying
                                        // text to display in top app bar
                                        text = "Update Note",
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

                        // on below line getting data from our database
                        // on below line we are calling method to display UI
                        firebaseUI(
                            LocalContext.current,
                            intent.getStringExtra("noteTitle"),
                            intent.getStringExtra("noteBody"),
                            intent.getStringExtra("noteID")

                        )
                    }
                }
            }
        }
    }

    @Composable
    fun firebaseUI(
        context: Context,
        title: String?,
        body: String?,
        noteID: String?,

    ) {

        // on below line creating variable for course name,
        // course duration and course description.
        val noteTitle = remember {
            mutableStateOf(title)
        }

        val noteBody = remember {
            mutableStateOf(body)
        }


        // on below line creating a column
        Column(
            // adding modifier for our column
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color.White),
            // on below line adding vertical and
            // horizontal alignment for column.
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            TextField(
                // on below line we are specifying
                // value for our course name text field.
                value = noteTitle.value.toString(),

                // on below line we are adding on
                // value change for text field.
                onValueChange = { noteTitle.value = it },

                // on below line we are adding place holder
                // as text as "Enter your course name"
                placeholder = { Text(text = "Enter your course name") },

                // on below line we are adding modifier to it
                // and adding padding to it and filling max width
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),

                // on below line we are adding text style
                // specifying color and font size to it.
                textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),

                // on below line we are adding
                // single line to it.
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                // on below line we are specifying
                // value for our course duration text field.
                value = noteBody.value.toString(),

                // on below line we are adding on
                // value change for text field.
                onValueChange = { noteBody.value = it },

                // on below line we are adding place holder
                // as text as "Enter your course duration"
                placeholder = { Text(text = "Enter your course duration") },

                // on below line we are adding modifier to it
                // and adding padding to it and filling max width
                modifier = Modifier
                    .padding(16.dp)
                    .height(120.dp)
                    .fillMaxWidth(),

                // on below line we are adding text style
                // specifying color and font size to it.
                textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),

                // on below line we are adding
                // single line to it.
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(10.dp))

            // on below line creating button to add data
            // to firebase firestore database.
            Button(
                onClick = {
                    // on below line we are validating user input parameters.
                    if (TextUtils.isEmpty(noteTitle.value.toString())) {
                        Toast.makeText(context, "Please enter the title", Toast.LENGTH_SHORT)
                            .show()
                    } else if (TextUtils.isEmpty(noteBody.value.toString())) {
                        Toast.makeText(
                            context,
                            "Please enter the body",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }  else {
                        // on below line adding data to
                        // firebase firestore database.
                        updateDataToFirebase(
                            noteID,
                            noteTitle.value,
                            noteBody.value,
                            context
                        )
                    }
                },
                // on below line we are
                // adding modifier to our button.
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // on below line we are adding text for our button
                Text(text = "Update Data", modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { deleteDataFromFirebase(noteID, context)},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
        ) {
                Text(text = "Delete Note",
                    modifier = Modifier.padding(8.dp))
            }
        }
    }
    private fun deleteDataFromFirebase(noteID: String?, context: Context) {

        // getting our instance from Firebase Firestore.
        val db = FirebaseFirestore.getInstance();

        // below line is for getting the collection
        // where we are storing our courses.
        db.collection("Notes").document(noteID.toString()).delete().addOnSuccessListener {
            // displaying toast message when our course is deleted.
            Toast.makeText(context, "Course Deleted successfully..", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, NotesDetailsActivity::class.java))
        }.addOnFailureListener {
            // on below line displaying toast message when
            // we are not able to delete the course
            Toast.makeText(context, "Fail to delete course..", Toast.LENGTH_SHORT).show()
        }

    }
    private fun updateDataToFirebase(
        courseID: String?,
        title: String?,
        body: String?,
        context: Context
    ) {
        // inside this method we are passing our updated values
        // inside our object class and later on we
        // will pass our whole object tofirebase Firestore.
        val updatedCourse = Notes(courseID, title, body)

        // getting our instance from Firebase Firestore.
        val db = FirebaseFirestore.getInstance();
        db.collection("Notes").document(courseID.toString()).set(updatedCourse)
            .addOnSuccessListener {
                // on below line displaying toast message and opening
                // new activity to view courses.
                Toast.makeText(context, "Course Updated successfully..", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, NotesDetailsActivity::class.java))
                finish()

            }.addOnFailureListener {
                Toast.makeText(context, "Fail to update note : " + it.message, Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
