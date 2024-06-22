package com.example.draganddroprecyclerview

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.draganddroprecyclerview.databinding.ItemViewBinding
import java.util.*

class MyAdapter(private val mData: MutableList<DataModel>) :
    RecyclerView.Adapter<MyAdapter.MyItemViewHolder>(), MyItemTouchHelperAdapter {
    private val TAG get() = MyAdapter::class.java.simpleName

    private var itemTouchHelper: ItemTouchHelper? = null

    private var mOriginPosition: Int = -1
    private var mIsDrop = true

    fun setItemTouchHelper(itemTouchHelper: ItemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyItemViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyItemViewHolder, position: Int) {
        holder.bindData(mData[position], position)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (mIsDrop) {
            mOriginPosition = fromPosition
        }
        mIsDrop = false
        Log.d(TAG, "onItemMove: fromPosition $fromPosition , toPosition $toPosition")
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mData, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mData, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)   // 更新項目的移動位置
    }

    override fun isItemDisabled(position: Int): Boolean {
        return mData[position].isDisable
    }

    inner class MyItemViewHolder(private val mBinding: ItemViewBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
        fun bindData(pDataModel: DataModel, pPosition: Int) {
            mBinding.apply {
                if (pDataModel.isDisable) {
//                    tvTag.setViewGone()
                    clMainContent.setBackgroundResource(R.drawable.bg_radius_8_solid_card_disable)
                } else {
//                    tvTag.setViewVisible()
                    clMainContent.setBackgroundResource(R.drawable.bg_radius_8_solid_card)
                }
                tvOrderId.text = pDataModel.orderId
                tvAddress.text = pDataModel.address
                tvTag.text = (pPosition + 1).toString()
                llDragHandle.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        itemTouchHelper?.startDrag(this@MyItemViewHolder)
                    }
                    true
                }
            }
        }

        fun onItemTouchLongAndDrag() {
            // 選擇顏色
            mBinding.clMainContent.setBackgroundResource(R.drawable.bg_radius_8_solid_card_selected)
        }

        fun onItemDrop() {
            // 恢復原顏色
            mBinding.clMainContent.setBackgroundResource(R.drawable.bg_radius_8_solid_card)
        }

        fun onItemChangeTag(pFromPosition: Int) {
            mIsDrop = true
            Log.d(
                TAG,
                "onItemChangeTag: mOriginPosition $mOriginPosition startPosition $pFromPosition "
            )
            if (mOriginPosition != -1) {
                if (mOriginPosition < pFromPosition) {
                    for (i in mOriginPosition..pFromPosition) {
                        Log.d(TAG, "onItemChangeTag: Update $i")
                        notifyItemChanged(i, "TAG_UPDATE")
                    }
                } else {
                    for (i in pFromPosition .. mOriginPosition) {
                        Log.d(TAG, "onItemChangeTag: Update $i")
                        notifyItemChanged(i, "TAG_UPDATE")
                    }
                }
            }
        }
    }

}
