package com.abgl.MyWaiterPad.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.abgl.MyWaiterPad.Allergen
import com.abgl.MyWaiterPad.R
import com.abgl.MyWaiterPad.models.Product
import com.abgl.MyWaiterPad.models.Table

class ProductSheetActivity : AppCompatActivity() {

    companion object {
        val PRODUCT_DETAIL = "PRODUCT_FOR_DETAIL"
        val TABLE_FOR_DETAIL = "TABLE_FOR_DETAIL"

        fun newIntent(context: Context, product: Product?, table: Table?): Intent {
            val intent = Intent(context, ProductSheetActivity::class.java)
            intent.putExtra(PRODUCT_DETAIL, product)
            intent.putExtra(TABLE_FOR_DETAIL, table)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_sheet)

        val product = intent.getSerializableExtra(PRODUCT_DETAIL) as? Product
        val table = intent.getSerializableExtra(TABLE_FOR_DETAIL) as? Table

        product?.let {
            val image = findViewById<ImageView>(R.id.product_image)
            val name = findViewById<TextView>(R.id.product_name)
            val price = findViewById<TextView>(R.id.product_price)
            val description = findViewById<TextView>(R.id.food_description)
            val options = findViewById<TextView>(R.id.food_options)

            image.setImageResource(it.image)
            name.text = it.name
            price.text = getString(R.string.product_price, it.price)
            description.text = it.description
            options.text = it.options

            updateAllergen(it)
        }

        findViewById<Button>(R.id.ok_food_button).setOnClickListener { productOk() }
        findViewById<Button>(R.id.delete_food_button)?.setOnClickListener { deleteProduct(product, table) }
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

    private fun productOk() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun deleteProduct(product: Product?, table: Table?) {
        if (product != null && table != null) {
            AlertDialog.Builder(this)
                    .setTitle("Caution!!")
                    .setMessage("Product will be delete!! Are you sure?")
                    .setPositiveButton("YES", { dialog, which ->
                        dialog.dismiss()

                        table.deleteProduct(product)

                        val returnIntent = Intent()
                        returnIntent.putExtra(TABLE_FOR_DETAIL, table)

                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    })
                    .setNegativeButton("CANCEL", { dialog, which ->
                        dialog.dismiss()
                    })
                    .show()
        }
        else {
            productOk()
        }
    }
}
