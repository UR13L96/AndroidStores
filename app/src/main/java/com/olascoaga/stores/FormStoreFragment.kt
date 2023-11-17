package com.olascoaga.stores

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.olascoaga.stores.databinding.FragmentFormStoreBinding
import java.util.concurrent.LinkedBlockingQueue

class FormStoreFragment : Fragment() {
    private lateinit var mBinding: FragmentFormStoreBinding
    private var mActivity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentFormStoreBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.form_store_title_add)

        setHasOptionsMenu(true)

        mBinding.btnSave.setOnClickListener {
            val store = StoreEntity(
                name =  mBinding.etName.text.toString().trim(),
                phone = mBinding.etPhone.text.toString().trim(),
                website = mBinding.etWebsite.text.toString().trim()
            )

            val queue = LinkedBlockingQueue<Long?>()
            Thread {
                val storeId = StoreApplication.database.storeDao().addStore(store)
                queue.add(storeId)
            }.start()

            queue.take()?.let {
                hideKeyboard()
                Snackbar.make(
                    mBinding.root,
                    getString(R.string.form_store_message_success),
                    Snackbar.LENGTH_SHORT
                ).show()

                mActivity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save -> {
                val store = StoreEntity(
                    name =  mBinding.etName.text.toString().trim(),
                    phone = mBinding.etPhone.text.toString().trim(),
                    website = mBinding.etWebsite.text.toString().trim()
                )

                val queue = LinkedBlockingQueue<Long?>()
                Thread {
                    val storeId = StoreApplication.database.storeDao().addStore(store)
                    queue.add(storeId)
                }.start()

                queue.take()?.let {
                    hideKeyboard()
                    Snackbar.make(
                        mBinding.root,
                        getString(R.string.form_store_message_success),
                        Snackbar.LENGTH_SHORT
                    ).show()

                    mActivity?.onBackPressedDispatcher?.onBackPressed()
                }
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethod = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethod.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.changeFABVisibility(true)

        setHasOptionsMenu(false)
        super.onDestroy()
    }
}