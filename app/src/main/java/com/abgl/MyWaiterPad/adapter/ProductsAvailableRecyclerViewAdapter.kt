package com.abgl.MyWaiterPad.adapter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.abgl.MyWaiterPad.Allergen
import com.abgl.MyWaiterPad.R
import com.abgl.MyWaiterPad.models.Product
import com.abgl.MyWaiterPad.models.ProductsAvailable


class ProductsAvailableRecyclerViewAdapter :
        RecyclerView.Adapter<ProductsAvailableRecyclerViewAdapter.DishesAvailableViewHolder>() {

    var onClickListener: View.OnClickListener? = null

    override fun onBindViewHolder(holder: DishesAvailableViewHolder?, position: Int) {
        holder?.bindDishAvailable(ProductsAvailable.getProduct(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DishesAvailableViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.content_products_available, parent, false)
        view.setOnClickListener(onClickListener)

        return DishesAvailableViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ProductsAvailable.count
    }


    inner class DishesAvailableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val dishName = itemView.findViewById<TextView>(R.id.product_available_name)
        private val dishImage = itemView.findViewById<ImageView>(R.id.product_image)
        private val dishPrice = itemView.findViewById<TextView>(R.id.product_price)

        private val allergenImageList = mutableListOf<ImageView>()


        fun bindDishAvailable(dish: Product) {

            dishName.text = dish.name
            dishImage.setImageResource(dish.image)
            dishPrice.text = itemView.context.getString(R.string.product_price, dish.price)

            allergenImageList.add(itemView.findViewById(R.id.celery_image))
            allergenImageList.add(itemView.findViewById(R.id.crustacean_image))
            allergenImageList.add(itemView.findViewById(R.id.egg_image))
            allergenImageList.add(itemView.findViewById(R.id.fish_image))
            allergenImageList.add(itemView.findViewById(R.id.gluten_image))
            allergenImageList.add(itemView.findViewById(R.id.lupin_image))
            allergenImageList.add(itemView.findViewById(R.id.milk_image))
            allergenImageList.add(itemView.findViewById(R.id.mollusc_image))
            allergenImageList.add(itemView.findViewById(R.id.mustard_image))
            allergenImageList.add(itemView.findViewById(R.id.peanut_image))
            allergenImageList.add(itemView.findViewById(R.id.sesamo_image))
            allergenImageList.add(itemView.findViewById(R.id.soya_image))
            allergenImageList.add(itemView.findViewById(R.id.sulphite_image))
            allergenImageList.add(itemView.findViewById(R.id.treenuts_image))

            val allergenListSize = dish.allergen?.size

            for (i in 0 until allergenImageList.size) {
                setDisableAllergen(allergenImageList[i])

                if (allergenListSize != null) {
                    for (allergenIndex in 0 until allergenListSize) {
                        val image = Allergen.imageToAllergen(allergenImageList[i].id)
                        val allergen = dish.allergen?.get(allergenIndex)

                        if (image == allergen) {
                            setEnableAllergen(allergenImageList[i])
                        }
                    }
                }
            }
        }

        private fun setDisableAllergen(allergen: ImageView) {
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f)
            val colorMatrixFilter = ColorMatrixColorFilter(colorMatrix)

            allergen.setColorFilter(colorMatrixFilter)
            allergen.alpha = 0.25f
        }

        private fun setEnableAllergen(allergen: ImageView) {
            allergen.clearColorFilter()
            allergen.alpha = 1f
        }
    }
}