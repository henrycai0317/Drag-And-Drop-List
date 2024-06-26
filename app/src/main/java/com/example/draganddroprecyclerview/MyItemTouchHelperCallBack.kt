package com.example.draganddroprecyclerview

import android.graphics.Canvas
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

interface MyItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun isItemDisabled(position: Int): Boolean
}

class MyItemTouchHelperCallBack(private val mAdapter: MyItemTouchHelperAdapter) :
    ItemTouchHelper.Callback() {

    private var mIsTouchDisableItemView = false //是否碰到Disable Flag

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        /*** 如果項目是disable狀態，禁用拖動 ***/
        return if (mAdapter.isItemDisabled(viewHolder.adapterPosition)) {
            0
        } else {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = 0
            makeMovementFlags(dragFlags, swipeFlags)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (mAdapter.isItemDisabled(target.adapterPosition)) {
            mIsTouchDisableItemView = true
            Log.d(
                "MyItemTouchHelperCallBack",
                "onMove: target position ${target.adapterPosition} disable 旗標狀態 $mIsTouchDisableItemView"
            )
        } else {
            Log.d(
                "MyItemTouchHelperCallBack",
                "onMove: target position ${target.adapterPosition} move 旗標狀態 $mIsTouchDisableItemView"
            )
        }

        if (!mIsTouchDisableItemView) {
            mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        }
        return !mIsTouchDisableItemView
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    /*** 處理項目拖動時的透明視覺效果 ***/
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        if (isCurrentlyActive) {
            viewHolder.itemView.alpha = 0.7f
        } else {
            viewHolder.itemView.alpha = 1.0f
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            if (viewHolder is MyAdapter.MyItemViewHolder) {
                viewHolder.onItemTouchLongAndDrag()
            }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        Log.d(
            "MyItemTouchHelperCallBack",
            "clearView: 拖移結束 mIsTouchDisableItemView 旗標清除狀態"
        )
        mIsTouchDisableItemView = false
        viewHolder.itemView.alpha = 1.0f // 確保透明度恢復
        if (viewHolder is MyAdapter.MyItemViewHolder) {
            viewHolder.onItemDrop()
        }
    }
}