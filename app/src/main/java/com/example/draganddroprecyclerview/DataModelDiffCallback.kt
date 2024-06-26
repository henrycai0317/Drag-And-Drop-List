package com.example.draganddroprecyclerview

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil

class DataModelDiffCallback(
    private val oldList: List<DataModel>,
    private val newList: List<DataModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].orderId == newList[newItemPosition].orderId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }


    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        val diffBundle = Bundle()

        if (oldItem.isDisable != newItem.isDisable) {
            diffBundle.putBoolean("isDisable", newItem.isDisable)
        }

        return if (diffBundle.size() == 0) null else diffBundle
    }
}
