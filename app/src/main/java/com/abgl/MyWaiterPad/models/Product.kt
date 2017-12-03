package com.abgl.MyWaiterPad.models

import com.abgl.MyWaiterPad.Allergen
import java.io.Serializable


data class Product(
        var name: String,
        var image: Int,
        var allergen: List<Allergen.AllergensList>?,
        var price: Float,
        var description: String? = "Product without description available.",
        var options: String?) : Serializable