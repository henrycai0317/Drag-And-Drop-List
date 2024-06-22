package com.example.draganddroprecyclerview

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

interface MyItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun isItemDisabled(position: Int): Boolean
}

class MyItemTouchHelper(
    private val mAdapter: MyItemTouchHelperAdapter,
    dataList: MutableList<DataModel>
) :
    ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // 如果項目是disable狀態，禁用拖動
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
        mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 不處理滑動
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        // 拖移時滑動到畫面頂部或底部時，RecyclerView才滑動的邏輯
        val scrollThreshold = 30
        val topThreshold = recyclerView.paddingTop + scrollThreshold
        val bottomThreshold = recyclerView.height - recyclerView.paddingBottom - scrollThreshold

        // 當向下拖動且到達頂部時，RecyclerView 向上滾動
        if (dY > 0 && viewHolder.adapterPosition == 0 && viewHolder.itemView.top < topThreshold) {
            recyclerView.scrollBy(0, dY.toInt())
        }
        // 當向上拖動且到達底部時，RecyclerView 向下滾動
        else if (dY < 0 && viewHolder.adapterPosition == dataList.size - 1 && viewHolder.itemView.bottom > bottomThreshold) {
            recyclerView.scrollBy(0, dY.toInt())
        }
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            if (viewHolder is MyAdapter.MyItemViewHolder) {
                viewHolder.onItemTochLongAndDrag()
            }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is MyAdapter.MyItemViewHolder) {
            viewHolder.onItemDrop()
            viewHolder.itemView.post {
                val startPosition = viewHolder.adapterPosition
                viewHolder.onItemChangeTag(startPosition)
            }
        }


    }
}