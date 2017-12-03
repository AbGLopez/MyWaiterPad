package com.abgl.MyWaiterPad.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.abgl.MyWaiterPad.R
import com.abgl.MyWaiterPad.fragment.ProductListFragment
import com.abgl.MyWaiterPad.models.Product
import com.abgl.MyWaiterPad.models.Table
import com.abgl.MyWaiterPad.models.Tables

class ProductListActivity : AppCompatActivity(),
        ProductListFragment.OnAddProductToTable,
        ProductListFragment.OnDeviceRotate,
        ProductListFragment.OnShowBill,
        ProductListFragment.OnShowProductDetail {

    companion object {
        private val TABLE_EXTRA = "TABLE_EXTRA"
        private val TABLEINDEX_EXTRA = "TABLEINDEX_EXTRA"

        val REQUEST_PRODUCT_AVAILABLE = 1
        val REQUEST_BILL = 2
        val REQUEST_PRODUCT_SHEET = 3

        private var table: Table? = null
        var tableIndex: Int = 0

        fun intent(context: Context, table: Table?, tableIndex: Int): Intent {
            val intent = Intent(context, ProductListActivity::class.java)
            intent.putExtra(TABLE_EXTRA, table)
            intent.putExtra(TABLEINDEX_EXTRA, tableIndex)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val productListFragment = fragmentManager.findFragmentById(R.id.product_list_fragment) as? ProductListFragment

        if (productListFragment == null) {
            val newTable = intent.getSerializableExtra(TABLE_EXTRA) as? Table
            val newTableIndex = intent.getIntExtra(TABLEINDEX_EXTRA, 0)

            newTable?.let { table = newTable }
            tableIndex = newTableIndex

            val newProductListFragment = ProductListFragment.newInstance(newTable, newTableIndex)
            fragmentManager.beginTransaction()
                    .add(R.id.product_list_fragment, newProductListFragment)
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val productListFragment = fragmentManager.findFragmentById(R.id.product_list_fragment) as? ProductListFragment

        if (requestCode == REQUEST_PRODUCT_AVAILABLE) {
            if (resultCode == Activity.RESULT_OK) {

                val newTable = data?.getSerializableExtra(AddProductSheetDetailActivity.TABLE_TO_ADD_PRODUCT) as? Table
                val newTableIndex = data?.getIntExtra(AddProductSheetDetailActivity.TABLE_INDEX_TO_SEND, 0)

                productListFragment?.let {
                    if (newTable != null) {
                        if (newTableIndex != null) {
                            tableIndex = newTableIndex
                            table = Tables.get(tableIndex)

                            it.showTable(newTable, newTableIndex)

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

                updateProductListActivityTable(newTable, "Product deleted!")
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
                Tables.get(tableIndex).replaceProduct(newTable.products)
                table = newTable
                it.showTable(newTable, tableIndex)

                Snackbar.make(
                        productListFragment.view,
                        textForSnackbar,
                        Snackbar.LENGTH_LONG)
                        .show()
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
        startActivityForResult(ProductSheetActivity.newIntent(this, product, table), REQUEST_PRODUCT_SHEET)
    }

    override fun updateTableToShow() {
        val productListFragment = fragmentManager.findFragmentById(R.id.product_list_fragment) as? ProductListFragment

        productListFragment?.showTable(table, tableIndex)
    }

    override fun recordMovingTable(newTable: Table, newTableIndex: Int) {
        table = newTable
        tableIndex = newTableIndex
    }
}
