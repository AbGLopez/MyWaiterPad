package com.abgl.MyWaiterPad.fragment

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.abgl.MyWaiterPad.R
import com.abgl.MyWaiterPad.models.Table
import com.abgl.MyWaiterPad.models.Tables


class TableListFragment : Fragment() {

    lateinit var root: View
    private var onTableSelectedListener: OnTableSelectedListener? = null

    companion object {
        fun newInstance(): TableListFragment {
            return TableListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        inflater?.let {
            root = inflater.inflate(R.layout.fragment_table_list, container, false)

            val tableList = root.findViewById<ListView>(R.id.table_list)
            val adapter = ArrayAdapter<Table>(
                    activity,
                    android.R.layout.simple_list_item_1,
                    Tables.toArray())

            tableList.adapter = adapter

            tableList.setOnItemClickListener { parent, view, position, id ->
                onTableSelectedListener?.onTableSelected(Tables.get(position), position)
            }
        }

        return root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnTableSelectedListener) {
            onTableSelectedListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()

        onTableSelectedListener = null
    }


    interface OnTableSelectedListener {
        fun onTableSelected(table: Table?, position: Int)
    }
}