package com.abgl.MyWaiterPad.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.abgl.MyWaiterPad.R
import com.abgl.MyWaiterPad.adapter.ProductsAvailableRecyclerViewAdapter
import com.abgl.MyWaiterPad.models.ProductsAvailable

class ProductsAvailableActivity : AppCompatActivity() {

    private lateinit var productsAvailableRecyclerView: RecyclerView

    companion object {
        private val REQUEST_PRODUCT_FOR_ADD = 1
        private val TABLE_FOR_PRODUCT_AVAILABLE_LIST_EXTRA = "TABLE_FOR_PRODUCTS_AVAILABLE_LIST_EXTRA"

        fun intent(context: Context, tableIndex: Int): Intent {
            val intent = Intent(context, ProductsAvailableActivity::class.java)
            intent.putExtra(TABLE_FOR_PRODUCT_AVAILABLE_LIST_EXTRA, tableIndex)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products_available)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        productsAvailableRecyclerView = findViewById(R.id.products_available_recycler_view)
        productsAvailableRecyclerView.layoutManager = GridLayoutManager(this, resources.getInteger(R.integer.products_available_recycler_colums))
        productsAvailableRecyclerView.itemAnimator = DefaultItemAnimator()

        val adapter = ProductsAvailableRecyclerViewAdapter()
        productsAvailableRecyclerView.adapter = adapter

        adapter.onClickListener = View.OnClickListener { v: View? ->
            val tableIndex = intent.getIntExtra(TABLE_FOR_PRODUCT_AVAILABLE_LIST_EXTRA, 0)
            val position = productsAvailableRecyclerView.getChildAdapterPosition(v)

            val intent = AddProductSheetDetailActivity.intent(this, tableIndex, ProductsAvailable.getProduct(position))

            startActivityForResult(intent, REQUEST_PRODUCT_FOR_ADD)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PRODUCT_FOR_ADD) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    setResult(Activity.RESULT_OK, data)
                }
                else {
                    setResult(Activity.RESULT_CANCELED)
                }

                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
