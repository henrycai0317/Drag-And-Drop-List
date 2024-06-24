package com.example.draganddroprecyclerview

import android.annotation.SuppressLint
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.draganddroprecyclerview.databinding.ItemViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.abs

class MyAdapter(private val mDataList: MutableList<DataModel>) :
    RecyclerView.Adapter<MyAdapter.MyItemViewHolder>(), MyItemTouchHelperAdapter {
    private val TAG get() = MyAdapter::class.java.simpleName

    private var itemTouchHelper: ItemTouchHelper? = null

    private var mOriginPosition: Int = -1
    private var mIsDrop = true

    fun setItemTouchHelper(itemTouchHelper: ItemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper
    }

    @SuppressLint("NotifyDataSetChanged")
    fun upDateData() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyItemViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyItemViewHolder, position: Int) {
        holder.bindData(mDataList[position], position)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (mIsDrop) {
            mOriginPosition = fromPosition
        }
        mIsDrop = false
        Log.d(TAG, "onItemMove: fromPosition $fromPosition , toPosition $toPosition")
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mDataList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mDataList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)   // 更新項目的移動位置
    }

    override fun isItemDisabled(position: Int): Boolean {
        return mDataList[position].isDisable
    }

    @SuppressLint("ClickableViewAccessibility", "NotifyDataSetChanged")
    inner class MyItemViewHolder(private val mBinding: ItemViewBinding) :
        RecyclerView.ViewHolder(mBinding.root), View.OnTouchListener {

        private val viewHolderScope = CoroutineScope(Dispatchers.Main + Job())
        private var gestureDetector: GestureDetector? = null

        init {
            gestureDetector = GestureDetector(mBinding.root.context, MyGestureListener())
            mBinding.root.setOnTouchListener(this)

            mBinding.llDelayed.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition != itemCount - 1) {
                    val item = mDataList.removeAt(currentPosition)
                    item.isDisable = true
                    item.translationX = 0f
                    mDataList.add(item)
                    notifyDataSetChanged()
                }
            }
        }


        fun bindData(pDataModel: DataModel, pPosition: Int) {
            viewHolderScope.coroutineContext.cancelChildren() // 取消之前的協程
            viewHolderScope.launch {
                val tagNum = withContext(Dispatchers.Default) {
                    countEnabledItems(pPosition)
                }
                Log.d(TAG, "bindData: tagNum = $tagNum")
                updateUI(pDataModel, tagNum)
            }
            /**  根據滑動狀態設置 初始化 translationX  */
            mBinding.clMainContent.translationX = pDataModel.translationX
        }

        @SuppressLint("ClickableViewAccessibility")
        private fun updateUI(pDataModel: DataModel, tagNum: Int) {
            mBinding.apply {
                updateUIDelayedState(pDataModel, tagNum)
                tvOrderId.text = pDataModel.orderId
                tvAddress.text = pDataModel.address
                Log.d(
                    TAG,
                    "updateUI: tvTag = ${tvTag.text}, tvOrderId = ${tvOrderId.text}, tvAddress = ${tvAddress.text}"
                )
                llDragHandle.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        itemTouchHelper?.startDrag(this@MyItemViewHolder)
                    }
                    true
                }
            }
        }

        /** 更新Disable UI */
        private fun ItemViewBinding.updateUIDelayedState(
            pDataModel: DataModel,
            tagNum: Int
        ) {
            if (pDataModel.isDisable) {
                tvTag.setViewGone()
                ivDelayed.setViewVisible()
                clMainContent.setBackgroundResource(R.drawable.bg_radius_8_solid_card_disable)
            } else {
                ivDelayed.setViewGone()
                tvTag.setViewVisible()
                clMainContent.setBackgroundResource(R.drawable.bg_radius_8_solid_card)
                tvTag.text = (tagNum).toString()
            }
        }

        private fun countEnabledItems(pPosition: Int): Int {
            var tagNum = 0
            for (index in 0..pPosition) {
                Log.d(TAG, "countEnabledItems: index: $index")
                if (!mDataList[index].isDisable) {
                    tagNum++
                }
            }
            return tagNum
        }

        /** 長按顏色 */
        fun onItemTouchLongAndDrag() {
            mBinding.clMainContent.setBackgroundResource(R.drawable.bg_radius_8_solid_card_selected)
        }

        /** 放入後變回原始顏色 */
        fun onItemDrop() {
            mBinding.clMainContent.setBackgroundResource(R.drawable.bg_radius_8_solid_card)
        }

        fun onItemChangeUpdateUI(pFromPosition: Int) {
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
                    for (i in pFromPosition..mOriginPosition) {
                        Log.d(TAG, "onItemChangeTag: Update $i")
                        notifyItemChanged(i, "TAG_UPDATE")
                    }
                }
            }
        }

        inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
            /** 必須複寫onDown 通知GestureDetector使用者手指已碰到螢幕，準備開始做後面的邏輯處理
             * 不然會罷工不做後續處理 */
            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            /** 滑動效果 */
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                val currentPosition = adapterPosition
                /** 如果當前項目為禁用狀態 or Adapter 沒有這個位置，讓它回去 */
                if (currentPosition == RecyclerView.NO_POSITION || mDataList[currentPosition].isDisable) {
                    return false
                }

                val deltaX = e2.x - (e1?.x ?: 0f)
                val deltaY = e2.y - (e1?.y ?: 0f)

                if (abs(deltaX) > abs(deltaY)) {
                    // 最大往左滑動值
                    val maxScroll = mBinding.llDelayed.width.toFloat()
                    Log.d("MyGestureListener", " onScroll deltaX:$deltaX maxScroll: $maxScroll")

                    // 計算 translationX 值
                    val newTranslationX = if (deltaX < 0) {
                        /** 向左滑動，最大不能超過 llDelayed 的寬度 */
                        -maxScroll
                    } else {
                        /**向右滑動，回到0的位置，且不超過0 */
                        0f
                    }
                    Log.d("SwipeAction", "newTranslationX: $newTranslationX")


                    mBinding.clMainContent.animate()
                        .translationX(newTranslationX)
                        .setDuration(300)
                        .start()

                    /** 更新Data滑動狀態 */
                    mDataList[currentPosition].translationX = newTranslationX

                    return true
                }
                return super.onScroll(e1, e2, distanceX, distanceY)
            }

            /** 輕點擊回彈到原始位置*/
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                mBinding.clMainContent.animate()
                    .translationX(0f)
                    .setDuration(300)
                    .start()
                return super.onSingleTapUp(e)
            }
        }

        override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
            return event?.let { gestureDetector?.onTouchEvent(it) } ?: true
        }
    }

}
