package com.example.draganddroprecyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.draganddroprecyclerview.databinding.ItemViewBinding
import java.util.*

class MyAdapter(private val mData: MutableList<DataModel>) :
    RecyclerView.Adapter<MyAdapter.MyItemViewHolder>(), MyItemTouchHelperAdapter {

    private var itemTouchHelper: ItemTouchHelper? = null

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
                    clMainContent.setBackgroundResource(R.drawable.bg_radius_8_solid_card_disable)
                } else {
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

        fun onItemTochLongAndDrag() {
            // 選擇顏色
            mBinding.clMainContent.setBackgroundResource(R.drawable.bg_radius_8_solid_card_selected)
        }

        fun onItemDrop() {
            // 恢復原顏色
            mBinding.clMainContent.setBackgroundResource(R.drawable.bg_radius_8_solid_card)

        }

        fun onItemChangeTag(startPosition: Int) {
            notifyItemChanged(startPosition)
            for (i in 0 until itemCount) {
                notifyItemChanged(i, "TAG_UPDATE")
            }
        }
    }


}
