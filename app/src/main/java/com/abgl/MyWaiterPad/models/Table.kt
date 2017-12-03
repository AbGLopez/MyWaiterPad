package com.abgl.MyWaiterPad.models

import java.io.Serializable


data class Table(
        var name: String,
        var products: MutableList<Product>?) : Serializable {

    constructor(name: String) : this(name, null)

    override fun toString() = name

    val count: Int
        get() = products?.size ?: 0

    fun addProduct(dish: Product) {
        if (products != null) {
            products?.add(dish)
        }
        else {
            products = mutableListOf(dish)
        }
    }

    fun deleteProduct(dish: Product) {
        products?.remove(dish)
    }

    fun replaceProduct(newDishes: MutableList<Product>?) {
        products = newDishes
    }
}