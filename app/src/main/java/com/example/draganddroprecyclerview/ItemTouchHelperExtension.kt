package com.example.draganddroprecyclerview

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

object ItemTouchHelperExtension {

    fun attachToRecyclerView(
        recyclerView: RecyclerView,
        adapter: MyItemTouchHelperAdapter
    ): ItemTouchHelper {
        val callback = MyItemTouchHelperCallBack(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)
        return touchHelper
    }
}