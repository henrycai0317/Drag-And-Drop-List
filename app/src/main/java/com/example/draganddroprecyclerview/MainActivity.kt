package com.example.draganddroprecyclerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.draganddroprecyclerview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mBinding: ActivityMainBinding? = null
    private val mAdapter: MyAdapter by lazy { MyAdapter(dataList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding?.root)

        initView()
        initListener()
    }

    private fun initListener() {
        mBinding?.apply {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    viewLine.setViewInVisible()
                    if (recyclerView.canScrollVertically(1)) {
                        viewLine.setViewVisible()
                    }
                }
            })

            btSort.setOnClickListener {
                val iDisableList = dataList.filter { it.isDisable }
                dataList.removeAll(iDisableList)
                dataList.addAll(iDisableList)
                mAdapter.upDateData()
            }
        }
    }

    private fun initView() {
        mBinding?.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = mAdapter
            mAdapter.setItemTouchHelper(
                ItemTouchHelperExtension.attachToRecyclerView(
                    recyclerView,
                    mAdapter
                )
            )
        }
    }
}