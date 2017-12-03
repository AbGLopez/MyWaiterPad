package com.abgl.MyWaiterPad.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.abgl.MyWaiterPad.R
import com.abgl.MyWaiterPad.models.Product


class ProductRecyclerViewAdapter(val productList: List<Product>?) :
        RecyclerView.Adapter<ProductRecyclerViewAdapter.productListViewHolder>() {

    var onClickListener: View.OnClickListener? = null

    override fun onBindViewHolder(holder: productListViewHolder?, position: Int) {
        productList?.let {
            holder?.bindProduct(productList.get(position))
        }
    }

    override fun getItemCount(): Int {
        return productList?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): productListViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.content_product_list, parent, false)
        view.setOnClickListener(onClickListener)

        return productListViewHolder(view)
    }


    inner class productListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val productImage = itemView.findViewById<ImageView>(R.id.product_image)
        val productName = itemView.findViewById<TextView>(R.id.product_name)
        val productPrice = itemView.findViewById<TextView>(R.id.product_price)

        fun bindProduct(product: Product) {
            productImage.setImageResource(product.image)
            productName.text = product.name
            productPrice.text = itemView.context.getString(R.string.product_price, product.price)
        }
    }
}
