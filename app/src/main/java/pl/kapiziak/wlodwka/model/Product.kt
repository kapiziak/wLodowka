package pl.kapiziak.wlodwka.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName(value = "status")
    val status: Int,
    @SerializedName(value = "code")
    val ean: String?,
    @SerializedName(value = "product_name")
    val name: String,
    @SerializedName(value = "product")
    val product: ProductDetails?,
    val validity: String?
)