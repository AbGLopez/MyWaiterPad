package com.abgl.MyWaiterPad.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.abgl.MyWaiterPad.R
import com.abgl.MyWaiterPad.Allergen
import com.abgl.MyWaiterPad.models.Product
import com.abgl.MyWaiterPad.models.Tables

class AddProductSheetDetailActivity : AppCompatActivity() {

    private val options by lazy { findViewById<EditText>(R.id.food_options) }

    companion object {
        val TABLE_TO_ADD_PRODUCT = "TABLE_TO_ADD_PRODUCT"
        val TABLE_INDEX_TO_SEND = "TABLE_INDEX_TO_SEND"
        private val PRODUCT_TO_SHOW = "PRODUCT_TO_SHOW"

        fun intent(context: Context, tableIndex: Int, product: Product): Intent {
            val intent = Intent(context, AddProductSheetDetailActivity::class.java)
            intent.putExtra(TABLE_TO_ADD_PRODUCT, tableIndex)
            intent.putExtra(PRODUCT_TO_SHOW, product)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product_detail)

        val product = intent.getSerializableExtra(PRODUCT_TO_SHOW) as? Product

        product?.let {
            val image = findViewById<ImageView>(R.id.product_image)
            val name = findViewById<TextView>(R.id.product_name)
            val price = findViewById<TextView>(R.id.product_price)
            val description = findViewById<TextView>(R.id.food_description)

            image.setImageResource(it.image)
            name.text = it.name
            price.text = getString(R.string.product_price, it.price)
            description.text = it.description
            options.text.clear()

            updateAllergen(it)
        }

        findViewById<Button>(R.id.add_product_button).setOnClickListener { addProduct(product) }
        findViewById<Button>(R.id.cancel_product_button)?.setOnClickListener { cancelProduct() }
    }

    private fun updateAllergen(product: Product) {
        val allergenImageList = mutableListOf<ImageView>()

        allergenImageList.add(findViewById(R.id.celery_image))
        allergenImageList.add(findViewById(R.id.crustacean_image))
        allergenImageList.add(findViewById(R.id.egg_image))
        allergenImageList.add(findViewById(R.id.fish_image))
        allergenImageList.add(findViewById(R.id.gluten_image))
        allergenImageList.add(findViewById(R.id.lupin_image))
        allergenImageList.add(findViewById(R.id.milk_image))
        allergenImageList.add(findViewById(R.id.mollusc_image))
        allergenImageList.add(findViewById(R.id.mustard_image))
        allergenImageList.add(findViewById(R.id.peanut_image))
        allergenImageList.add(findViewById(R.id.sesamo_image))
        allergenImageList.add(findViewById(R.id.soya_image))
        allergenImageList.add(findViewById(R.id.sulphite_image))
        allergenImageList.add(findViewById(R.id.treenuts_image))

        val allergenListSize = product.allergen?.size

        for (i in 0 until allergenImageList.size) {
            setDisableAllergen(allergenImageList[i])

            if (allergenListSize != null) {
                for (allergenIndex in 0 until allergenListSize) {
                    val image = Allergen.imageToAllergen(allergenImageList[i].id)
                    val allergen = product.allergen?.get(allergenIndex)

                    if (image == allergen) {
                        setEnableAllergen(allergenImageList[i])
                    }
                }
            }
        }
    }

    // pinto solo los alergenos que tiene
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


    private fun cancelProduct() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun addProduct(product: Product?) {
        if (product != null) {
            if (options.text.length > 0) {
                product.options = options.text.toString()
            }

            val tableIndex = intent.getIntExtra(TABLE_TO_ADD_PRODUCT, 0)
            val table = Tables.get(tableIndex)
            table.addProduct(product)

            val returnIntent = Intent()
            returnIntent.putExtra(TABLE_TO_ADD_PRODUCT, table)
            returnIntent.putExtra(TABLE_INDEX_TO_SEND, tableIndex)

            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
        else {
            cancelProduct()
        }
    }
}
