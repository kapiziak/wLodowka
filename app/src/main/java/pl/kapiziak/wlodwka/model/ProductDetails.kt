package pl.kapiziak.wlodwka.model

import com.google.gson.annotations.SerializedName

data class ProductDetails (

    @SerializedName("product_name")
    val name: String,
    @SerializedName("product_name_pl")
    val name_pl: String?

)