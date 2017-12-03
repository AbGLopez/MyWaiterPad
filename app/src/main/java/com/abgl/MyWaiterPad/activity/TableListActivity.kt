package com.abgl.MyWaiterPad.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ViewSwitcher
import com.abgl.MyWaiterPad.productsURL
import com.abgl.MyWaiterPad.*
import com.abgl.MyWaiterPad.Allergen.drawableProduct
import com.abgl.MyWaiterPad.Allergen.toAllergen
import com.abgl.MyWaiterPad.fragment.ProductListFragment
import com.abgl.MyWaiterPad.fragment.TableListFragment
import com.abgl.MyWaiterPad.models.Product
import com.abgl.MyWaiterPad.models.ProductsAvailable
import com.abgl.MyWaiterPad.models.Table
import com.abgl.MyWaiterPad.models.Tables
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.json.JSONObject
import java.net.URL
import java.util.*

class TableListActivity :
        AppCompatActivity(),
        TableListFragment.OnTableSelectedListener,
        ProductListFragment.OnAddProductToTable,
        ProductListFragment.OnDeviceRotate,
        ProductListFragment.OnShowBill,
        ProductListFragment.OnShowProductDetail {

    private lateinit var viewSwitcher: ViewSwitcher

    companion object {
        val REQUEST_PRODUCT_AVAILABLE = 1
        val REQUEST_BILL = 2
        val REQUEST_PRODUCT_SHEET = 3

        private var tableSelected: Table? = null
        private var tableSelectedIndex: Int = 0
    }

    enum class VIEW_INDEX(val index: Int) {
        LOADING(0),
        VIEW(1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_list)

        viewSwitcher = findViewById(R.id.view_switcher)
        viewSwitcher.setInAnimation(this, android.R.anim.fade_in)
        viewSwitcher.setOutAnimation(this, android.R.anim.fade_out)

        if (ProductsAvailable.getProductsAvailable().isEmpty()) {
            updateMenuAvailable()
        }
        else {
            viewSwitcher.displayedChild = VIEW_INDEX.VIEW.index
        }

        if (findViewById<View>(R.id.table_list_fragment) != null) {
            if (fragmentManager.findFragmentById(R.id.table_list_fragment) == null) {
                val tableListFragment = TableListFragment.newInstance()
                fragmentManager.beginTransaction()
                        .add(R.id.table_list_fragment, tableListFragment)
                        .commit()
            }
        }

        if (findViewById<View>(R.id.product_list_fragment) != null) {
            val productListFragment = fragmentManager.findFragmentById(R.id.product_list_fragment) as? ProductListFragment

            tableSelected = Tables.get(tableSelectedIndex)

            if (productListFragment == null) {
                val newProductListFragment = ProductListFragment.newInstance(tableSelected, tableSelectedIndex)
                fragmentManager.beginTransaction()
                        .add(R.id.product_list_fragment, newProductListFragment)
                        .commit()
            }
        }
    }

    private fun updateMenuAvailable() {

        viewSwitcher.displayedChild = VIEW_INDEX.LOADING.index

        async(UI) {
            val productsAvailable: Deferred<List<Product>?> = bg {
                downloadProductsAvailable()
            }

            val downloadedProducts = productsAvailable.await()

            if (downloadedProducts != null) {
                viewSwitcher.displayedChild = VIEW_INDEX.VIEW.index

                val productListFragment = fragmentManager.findFragmentById(R.id.product_list_fragment) as? ProductListFragment
                val viewToSnackbar: View

                if (productListFragment != null) {
                    viewToSnackbar = productListFragment.view
                }
                else {
                    viewToSnackbar = findViewById<View>(android.R.id.content)
                }

                Snackbar.make(
                        viewToSnackbar,
                        "Today's menu downloaded!",
                        Snackbar.LENGTH_LONG)
                        .show()

            }
            else {
                AlertDialog.Builder(this@TableListActivity)
                        .setTitle("Error")
                        .setMessage("Error downloading Today's menu.")
                        .setPositiveButton("Try Again", { dialog, which ->
                            dialog.dismiss()
                            updateMenuAvailable()
                        })
                        .setNegativeButton("Exit", { dialog, which ->
                            finish()
                        })
                        .show()
            }
        }
    }

    private fun downloadProductsAvailable(): List<Product>? {
        try {
            // show progress bar
            Thread.sleep(1100)

            val url = URL(productsURL)
            val jsonString = Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next()

            val jsonRoot = JSONObject(jsonString)
            val productList = jsonRoot.getJSONArray("products")

            for (productIndex in 0..productList.length() - 1 ) {

                val jsonProduct = productList.getJSONObject(productIndex)

                val name = jsonProduct.getString("name")
                val jsonImage = jsonProduct.getInt("image")
                val price = jsonProduct.getDouble("price").toFloat()
                val description = jsonProduct.getString("description")

                val jsonAllergen = jsonProduct.getJSONArray("allergen")
                val allergenList: MutableList<Allergen.AllergensList> = mutableListOf()

                for (allergenIndex in 0 until jsonAllergen.length()) {
                    val allergen = toAllergen(jsonAllergen.getInt(allergenIndex))
                    allergen?.let { allergenList.add(it) }
                }

                val image = drawableProduct(jsonImage)

                val newProduct = Product(
                        name,
                        image,
                        allergenList,
                        price,
                        description,
                        null
                )

                ProductsAvailable.addProduct(newProduct)
            }

            return ProductsAvailable.getProductsAvailable()
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val productListFragment = fragmentManager.findFragmentById(R.id.product_list_fragment) as? ProductListFragment

        if (requestCode == REQUEST_PRODUCT_AVAILABLE) {
            if (resultCode == Activity.RESULT_OK) {

                val newTable = data?.getSerializableExtra(AddProductSheetDetailActivity.TABLE_TO_ADD_PRODUCT) as? Table
                val tableIndex = data?.getIntExtra(AddProductSheetDetailActivity.TABLE_INDEX_TO_SEND, 0)

                productListFragment?.let {
                    if (newTable != null) {
                        if (tableIndex != null) {
                            tableSelected = newTable
                            tableSelectedIndex = tableIndex

                            it.showTable(newTable, tableIndex)

                            Snackbar.make(
                                    productListFragment.view,
                                    "New Product added!",
                                    Snackbar.LENGTH_LONG)
                                    .show()
                        }
                    }
                }
            }
        }
        else if (requestCode == REQUEST_BILL) {
            if (resultCode == Activity.RESULT_OK) {

                val newTable = data?.getSerializableExtra(TableBillActivity.NEW_TABLE_DELETED) as? Table

                updateProductListActivityTable(newTable, "Products deleted!")
            }
        }
        else if (requestCode == REQUEST_PRODUCT_SHEET) {
            if (resultCode == Activity.RESULT_OK) {

                val newTable = data?.getSerializableExtra(ProductSheetActivity.TABLE_FOR_DETAIL) as? Table

                updateProductListActivityTable(newTable, "Product deleted!")
            }
        }
    }

    private fun updateProductListActivityTable(newTable: Table?, textForSnackbar: String) {
        val productListFragment = fragmentManager.findFragmentById(R.id.product_list_fragment) as? ProductListFragment

        productListFragment?.let {
            if (newTable != null) {
                Tables.get(tableSelectedIndex).replaceProduct(newTable.products)
                tableSelected = newTable

                it.showTable(newTable, tableSelectedIndex)

                Snackbar.make(
                        productListFragment.view,
                        textForSnackbar,
                        Snackbar.LENGTH_LONG)
                        .show()
            }
        }
    }

    override fun onTableSelected(table: Table?, position: Int) {
        val productListFragment = fragmentManager.findFragmentById(R.id.product_list_fragment) as? ProductListFragment

        if (productListFragment == null) {
            startActivity(ProductListActivity.intent(this, table, position))
        }
        else {
            table?.let {
                tableSelectedIndex = position
                tableSelected = Tables.get(tableSelectedIndex)
                productListFragment.showTable(it, position)
            }

        }
    }

    override fun showProductAvailable(tableIndex: Int) {
        val intent = ProductsAvailableActivity.intent(this, tableIndex)

        startActivityForResult(intent, REQUEST_PRODUCT_AVAILABLE)
    }

    override fun showBill(table: Table?) {
        table?.let {
            startActivityForResult(TableBillActivity.newIntent(this, table), REQUEST_BILL)
        }
    }

    override fun showProductDetail(product: Product?) {
        startActivityForResult(ProductSheetActivity.newIntent(this, product, tableSelected), REQUEST_PRODUCT_SHEET)
    }

    override fun updateTableToShow() {
        val productListFragment = fragmentManager.findFragmentById(R.id.product_list_fragment) as? ProductListFragment

        productListFragment?.showTable(tableSelected, tableSelectedIndex)
    }

    override fun recordMovingTable(newTable: Table, newTableIndex: Int) {
        tableSelected = newTable
        tableSelectedIndex = newTableIndex
    }



}
