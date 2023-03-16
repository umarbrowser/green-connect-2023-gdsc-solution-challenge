package com.example.greenconnect

import com.google.firebase.firestore.Exclude
import java.time.Duration

data class Notes(
    @get:Exclude var noteID: String? = "",
    var title: String? = "",
    var body: String? = "",
    var productPhoto: String? = "",
    var sellerName: String? = "",
    var sellerAddress: String? = "",
    var sellerPhone: String? = "",
    var sellerLattitude: String? = "",
    var sellerLongitute: String? = "",
)




