package com.olascoaga.stores

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.olascoaga.stores.databinding.ActivityMainBinding
import java.util.concurrent.LinkedBlockingQueue

class MainActivity: AppCompatActivity(), OnClickListener, MainAux   {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.fabAdd.setOnClickListener {
            showStoreFragment()
        }

        setupRecyclerView()
    }

    private fun showStoreFragment(args: Bundle? = null) {
        val fragment = FormStoreFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragment.arguments = args
        fragmentTransaction.add(R.id.cl_main, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        changeFABVisibility(false)
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

    override fun onClick(storeId: Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id), storeId)

        showStoreFragment(args)
    }

    override fun onFavoriteClicked(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite

        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)
        }.start()

        updateStore(queue.take())
    }

    override fun onDeleteClicked(storeEntity: StoreEntity) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_dialog_title)
            .setPositiveButton(R.string.delete_dialog_confirm) { _, _ ->
                val queue = LinkedBlockingQueue<StoreEntity>()
                Thread {
                    StoreApplication.database.storeDao().deleteStore(storeEntity)
                    queue.add(storeEntity)
                }.start()

                mAdapter.delete(queue.take())
            }
            .setNegativeButton(R.string.delete_dialog_cancel, null)
            .show()
    }

    /*
     * MainAux
     */

    override fun changeFABVisibility(isVisible: Boolean) {
        if (isVisible) {
            mBinding.fabAdd.show()
        } else {
            mBinding.fabAdd.hide()
        }
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)
    }
}