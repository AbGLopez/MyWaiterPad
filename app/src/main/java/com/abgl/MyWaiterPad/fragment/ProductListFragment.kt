package com.abgl.MyWaiterPad.fragment

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.abgl.MyWaiterPad.R
import com.abgl.MyWaiterPad.adapter.ProductRecyclerViewAdapter
import com.abgl.MyWaiterPad.models.Product
import com.abgl.MyWaiterPad.models.Table
import com.abgl.MyWaiterPad.models.Tables


class ProductListFragment : Fragment() {

    private lateinit var root: View
    private lateinit var productRecyclerView: RecyclerView
    private var menu: Menu? = null

    private var tableIndex: Int = 0

    private var onAddProductToTable: OnAddProductToTable? = null
    private var onDeviceRotate: OnDeviceRotate? = null
    private var onShowBill: OnShowBill? = null
    private var onShowProductDetail: OnShowProductDetail? = null

    companion object {
        private val TABLE_ARG = "TABLE_ARG"
        private val TABLEINDEX_ARG = "TABLEINDEX_ARG"

        fun newInstance(table: Table?, tableIndex: Int): ProductListFragment {
            val argument = Bundle()
            argument.putSerializable(TABLE_ARG, table)
            argument.putInt(TABLEINDEX_ARG, tableIndex)

            val productListFragment = ProductListFragment()
            productListFragment.arguments = argument

            return productListFragment
        }
    }

    private var table: Table? = null
        set(value) {
            field = value

            value?.let {
                products = value.products
            }
        }

    private var products: MutableList<Product>? = null
        set(value) {
            field = value

            if (value != null) {
                table?.products = value
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        inflater?.let {
            root = it.inflate(R.layout.fragment_product_list, container, false)

            productRecyclerView = root.findViewById(R.id.product_recycler_view)
            productRecyclerView.layoutManager = GridLayoutManager(activity, resources.getInteger(R.integer.recycler_columns))
            productRecyclerView.itemAnimator = DefaultItemAnimator()

            if (arguments != null) {
                table = arguments.getSerializable(TABLE_ARG) as? Table
                tableIndex = arguments.getInt(TABLEINDEX_ARG)
            }

            val adapter = ProductRecyclerViewAdapter(products)
            productRecyclerView.adapter = adapter

            setListenerToAdapter(adapter)

            root.findViewById<FloatingActionButton>(R.id.show_product_available)?.setOnClickListener { v: View? ->
                onAddProductToTable?.showProductAvailable(tableIndex)
            }

            updateToolbarProductName()
        }

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        this.menu = menu

        inflater?.inflate(R.menu.pager, menu)
        onPrepareShowMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.next -> {
                tableIndex = tableIndex + 1
                val newTable = Tables.get(tableIndex)
                showTable(newTable, tableIndex)

                onDeviceRotate?.recordMovingTable(newTable, tableIndex)

                return true
            }
            R.id.previous -> {
                tableIndex = tableIndex - 1
                val newTable = Tables.get(tableIndex)
                showTable(newTable, tableIndex)

                onDeviceRotate?.recordMovingTable(newTable, tableIndex)

                return true
            }
            R.id.bill -> {
                onShowBill?.showBill(table)

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun onPrepareShowMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)

        val menuPrev = menu?.findItem(R.id.previous)
        val menuNext = menu?.findItem(R.id.next)

        menuPrev?.setEnabled(tableIndex > 0)
        menuNext?.setEnabled(tableIndex < Tables.count - 1)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnAddProductToTable) {
            onAddProductToTable = context
        }

        if (context is OnDeviceRotate) {
            onDeviceRotate = context
        }

        if (context is OnShowBill) {
            onShowBill = context
        }

        if (context is OnShowProductDetail) {
            onShowProductDetail = context
        }
    }

    override fun onDetach() {
        super.onDetach()

        onAddProductToTable = null
        onDeviceRotate = null
        onShowBill = null
        onShowProductDetail = null
    }

    override fun onResume() {
        super.onResume()

        onDeviceRotate?.updateTableToShow()
    }

    fun showTable(newTable: Table?, newTableIndex: Int) {
        newTable?.let {
            table = newTable
            tableIndex = newTableIndex

            val adapter = ProductRecyclerViewAdapter(newTable.products)
            productRecyclerView.adapter = adapter

            setListenerToAdapter(adapter)

            updateToolbarProductName()
            onPrepareShowMenu(menu)
        }
    }

    private fun setListenerToAdapter(adapter: ProductRecyclerViewAdapter) {
        adapter.onClickListener = View.OnClickListener { v: View? ->
            val position = productRecyclerView.getChildAdapterPosition(v)
            val product = products?.get(position)

            onShowProductDetail?.showProductDetail(product)
        }
    }

    private fun updateToolbarProductName() {
        if (activity is AppCompatActivity) {
            val supportActionBar = (activity as? AppCompatActivity)?.supportActionBar
            supportActionBar?.title = table?.name
        }
    }

    interface OnAddProductToTable {
        fun showProductAvailable(tableIndex: Int)
    }

    interface OnDeviceRotate {
        fun updateTableToShow()
        fun recordMovingTable(newTable: Table, newTableIndex: Int)
    }

    interface OnShowBill {
        fun showBill(table: Table?)
    }

    interface OnShowProductDetail {
        fun showProductDetail(product: Product?)
    }
}
