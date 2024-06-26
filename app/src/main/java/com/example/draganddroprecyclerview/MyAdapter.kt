package com.example.draganddroprecyclerview

import android.annotation.SuppressLint
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
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

class MyAdapter(
    private val mDataList: MutableList<DataModel>,
    private val mRecyclerView: RecyclerView?
) :
    RecyclerView.Adapter<MyAdapter.MyItemViewHolder>(), MyItemTouchHelperAdapter {
    private val mLayoutManager: LinearLayoutManager =
        mRecyclerView?.layoutManager as LinearLayoutManager

    private val TAG get() = MyAdapter::class.java.simpleName

    private var itemTouchHelper: ItemTouchHelper? = null

    private var mOriginPosition: Int = -1
    private var mIsDrop = true
    private var mLastSwipedPosition = RecyclerView.NO_POSITION  //紀錄上次滑動的位置是誰

    fun setItemTouchHelper(itemTouchHelper: ItemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper
    }

    fun upDateDataSort(mCurrentSortType: SortOrder) {
        val iNewDataList = mutableListOf<DataModel>()
        val iDisableList = mDataList.filter { it.isDisable }
        val iNormalSortedList = sortList(mDataList.filter { !it.isDisable }, mCurrentSortType)
        iNewDataList.addAll(iNormalSortedList)
        iNewDataList.addAll(iDisableList)
        notifyAdapterDataChange(iNewDataList)
    }

    /** DiffUtil 更新List Data*/
    private fun notifyAdapterDataChange(pNewDataList: List<DataModel>) {
        val firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition()
        val diffResult = DiffUtil.calculateDiff(DataModelDiffCallback(mDataList, pNewDataList))
        mDataList.clear()
        mDataList.addAll(pNewDataList)
        diffResult.dispatchUpdatesTo(this)

        /** 確保RecyclerView顯示位置是排序前的顯示位置*/
        mRecyclerView?.scrollToPosition(firstVisibleItemPosition)
        mLayoutManager.scrollToPositionWithOffset(firstVisibleItemPosition, 0)
    }

    private fun sortList(sortList: List<DataModel>, order: SortOrder): List<DataModel> {
        return when (order) {
            SortOrder.ASCENDING -> sortList.sortedBy { it.orderId }
            SortOrder.DESCENDING -> sortList.sortedByDescending { it.orderId }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyItemViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyItemViewHolder, position: Int) {
        holder.bindData(mDataList[position])
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

        private var gestureDetector: GestureDetector? = null

        init {
            gestureDetector = GestureDetector(mBinding.root.context, MyGestureListener())
            mBinding.root.setOnTouchListener(this)

            mBinding.llDelayed.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition != itemCount - 1) {
                    val newList = mDataList.toMutableList()
                    val item = newList.removeAt(currentPosition)
                    item.isDisable = true
                    item.translationX = 0f
                    newList.add(item)
                    notifyAdapterDataChange(newList)
                    notifyItemChanged(currentPosition)  // 通知項目已更改以重新綁定視圖
                    notifyItemChanged(itemCount - 1)    // 通知最後一個項目以更新其狀態
                }
            }
        }


        fun bindData(pDataModel: DataModel) {
            updateUI(pDataModel)
            /**  根據滑動狀態設置 初始化 translationX  */
            mBinding.clMainContent.translationX = pDataModel.translationX
        }

        @SuppressLint("ClickableViewAccessibility")
        private fun updateUI(pDataModel: DataModel) {
            mBinding.apply {
                updateUIDelayedState(pDataModel)
                tvOrderId.text = pDataModel.orderId
                tvAddress.text = pDataModel.address
                Log.d(
                    TAG,
                    "updateUI: tvOrderId = ${tvOrderId.text}, tvAddress = ${tvAddress.text}"
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
        private fun ItemViewBinding.updateUIDelayedState(pDataModel: DataModel) {
            if (pDataModel.isDisable) {
                tvStartDelivery.setViewGone()
                ivDelayed.setViewVisible()
                clMainContent.setBackgroundResource(R.drawable.bg_radius_8_solid_card_disable)
            } else {
                ivDelayed.setViewGone()
                tvStartDelivery.setViewVisible()
                clMainContent.setBackgroundResource(R.drawable.bg_radius_8_solid_card)
            }
        }

        /** 長按顏色 */
        fun onItemTouchLongAndDrag() {
            mBinding.clMainContent.setBackgroundResource(R.drawable.bg_radius_8_solid_card_selected)
        }

        /** 放入後變回原始顏色 */
        fun onItemDrop() {
            mBinding.clMainContent.setBackgroundResource(R.drawable.bg_radius_8_solid_card)
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
                        /** 重置之前滑動的ItemView */
                        resetSwipedItem(currentPosition)
                        /** 向左滑動，最大不能超過 llDelayed 的寬度 */
                        -maxScroll
                    } else {
                        /**向右滑動，回到0的位置，且不超過0 */
                        0f
                    }
                    Log.d("SwipeAction", "newTranslationX: $newTranslationX")


                    animateTranslationXProcess(newTranslationX)

                    /** 更新Data滑動狀態 */
                    mDataList[currentPosition].translationX = newTranslationX

                    return true
                }
                return super.onScroll(e1, e2, distanceX, distanceY)
            }

            /** 輕點擊回彈到原始位置*/
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val currentPosition = adapterPosition
                /** 如果當前項目為禁用狀態 or Adapter 沒有這個位置，讓它回去 */
                if (currentPosition == RecyclerView.NO_POSITION || mDataList[currentPosition].isDisable) {
                    return false
                }
                dataList[mLastSwipedPosition].translationX = 0f
                animateTranslationXProcess(0f)
                return super.onSingleTapUp(e)
            }
        }

        /** 滑動與動畫處理*/
        private fun animateTranslationXProcess(pNewTranslationX: Float) {
            mBinding.clMainContent.animate()
                .translationX(pNewTranslationX)
                .setDuration(300)
                .start()
        }

        override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
            return event?.let { gestureDetector?.onTouchEvent(it) } ?: true
        }

        /**更新上一個已滑動的ItemView*/
        private fun resetSwipedItem(pCurrentPosition: Int) {
            if (mLastSwipedPosition != pCurrentPosition) {
                if (mLastSwipedPosition != RecyclerView.NO_POSITION) {
                    dataList[mLastSwipedPosition].translationX = 0f
                    notifyItemChanged(mLastSwipedPosition)
                }
                mLastSwipedPosition = pCurrentPosition
            }
        }
    }
}
