package com.example.greenconnect

import com.google.firebase.firestore.Exclude

data class Products(
    @get:Exclude var productID: String? = "",
    var productName: String? = "",
    var productDescription: String? = "",
    var productPhoto: String? = "",
    var productPrice: String? = "",
    var sellerName: String? = "",
    var sellerAddress: String? = "",
    var sellerPhone: String? = "",
    var sellerLatitude: String? = "",
    var sellerLongitute: String? = "",
)
