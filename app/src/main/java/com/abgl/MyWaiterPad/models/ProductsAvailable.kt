package com.abgl.MyWaiterPad.models

import java.io.Serializable


object ProductsAvailable : Serializable {

    private var productsAvailable: MutableList<Product> = mutableListOf()

    val count: Int
        get() = productsAvailable.size

    fun getProduct(index: Int): Product = productsAvailable[index]
    fun addProduct(dish: Product) = productsAvailable.add(dish)

    fun getProductsAvailable(): List<Product> = productsAvailable
}