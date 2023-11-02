package com.olascoaga.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.olascoaga.stores.databinding.ActivityMainBinding
import java.util.concurrent.LinkedBlockingQueue

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

            Thread {
                StoreApplication.database.storeDao().addStore(storeEntity)
            }.start()

            mAdapter.add(storeEntity)
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, 2)
        getStores()
        mBinding.recyclerView.apply {
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getStores() {
        val queue = LinkedBlockingQueue<MutableList<StoreEntity>>()

        Thread {
            val stores = StoreApplication.database.storeDao().getStores()
            queue.add(stores)
        }.start()

        mAdapter.setStores(queue.take())
    }

    override fun onClick(storeEntity: StoreEntity) {

    }

    override fun onFavoriteClicked(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite

        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)
        }.start()

        mAdapter.update(queue.take())
    }

    override fun onDeleteClicked(storeEntity: StoreEntity) {
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            queue.add(storeEntity)
        }.start()

        mAdapter.delete(queue.take())
    }
}