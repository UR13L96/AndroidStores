package com.olascoaga.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.olascoaga.stores.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity(), OnClickListener {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnSave.setOnClickListener {
            val storeEntity = StoreEntity(
                name = mBinding.etName.text.toString().trim()
            )
            mAdapter.add(storeEntity)
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, 2)
        mBinding.recyclerView.apply {
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    override fun onClick(storeEntity: StoreEntity) {

    }
}