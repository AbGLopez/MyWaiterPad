package com.abgl.MyWaiterPad.models

import java.io.Serializable


object Tables : Serializable {

    private var tables: MutableList<Table> = mutableListOf(
            Table("Table 1"),
            Table("Table 2"),
            Table("Table 3"),
            Table("Table 4"),
            Table("Table 5"),
            Table("Table 6"),
            Table("Table 7"),
            Table("Table 8")

    )

    val count: Int
        get() = tables.size

    operator fun get(i: Int): Table = tables[i]

    fun toArray() = tables.toTypedArray()
}