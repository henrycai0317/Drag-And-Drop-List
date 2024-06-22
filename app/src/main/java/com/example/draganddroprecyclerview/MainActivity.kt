package com.example.draganddroprecyclerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.draganddroprecyclerview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mBinding:ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding?.root)

        initView()
    }

    private fun initView() {
        mBinding?.apply {
            val adapter = MyAdapter(dataList)
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = adapter

            val myItemTouchCallback = MyItemTouchHelper(adapter,dataList)
            val touchHelper = ItemTouchHelper(myItemTouchCallback)
            touchHelper.attachToRecyclerView(recyclerView)
        }
    }
}