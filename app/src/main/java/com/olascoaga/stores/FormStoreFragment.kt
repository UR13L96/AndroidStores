package com.olascoaga.stores

import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.olascoaga.stores.databinding.FragmentFormStoreBinding
import java.util.concurrent.LinkedBlockingQueue

class FormStoreFragment : Fragment() {
    private lateinit var mBinding: FragmentFormStoreBinding
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private var mStoreEntity: StoreEntity? = null

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

        mBinding.etPhotoUrl.addTextChangedListener {
            Glide.with(this)
                .load(mBinding.etPhotoUrl.text.toString().trim())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.ivPhoto)
        }

        mBinding.btnSave.setOnClickListener {
            if (mStoreEntity != null && validateFields()) {
                with(mStoreEntity!!) {
                    name =  mBinding.etName.text.toString().trim()
                    phone = mBinding.etPhone.text.toString().trim()
                    website = mBinding.etWebsite.text.toString().trim()
                    imageURL = mBinding.etPhotoUrl.text.toString().trim()
                }

                val queue = LinkedBlockingQueue<StoreEntity>()
                Thread {
                    if (mIsEditMode) {
                        StoreApplication.database.storeDao().updateStore(mStoreEntity!!)
                    } else {
                        mStoreEntity!!.id = StoreApplication.database.storeDao().addStore(mStoreEntity!!)
                    }
                    queue.add(mStoreEntity)
                }.start()

                with(queue.take()) {
                    hideKeyboard()
                    if (mIsEditMode) {
                        mActivity?.updateStore(mStoreEntity!!)

                        Snackbar.make(mBinding.root, R.string.form_store_message_success, Snackbar.LENGTH_SHORT).show()
                    } else {
                        mActivity?.addStore(this)
                        Toast.makeText(mActivity, R.string.form_store_message_success, Toast.LENGTH_SHORT).show()

                        mActivity?.onBackPressedDispatcher?.onBackPressed()
                    }
                }
            }
        }

        val id = arguments?.getLong(getString(R.string.arg_id), 0)
        if (id != null && id != 0L) {
            mIsEditMode = true
            getStore(id)
        } else {
            mIsEditMode = false
            mStoreEntity = StoreEntity(
                name = "",
                phone = "",
                imageURL =  ""
            )
        }
    }

    private fun validateFields(): Boolean {
        var isValid: Boolean = true

        if (mBinding.etPhotoUrl.text.toString().isBlank()) {
            mBinding.tilPhotoUrl.error = getString(R.string.helper_required)
            mBinding.etPhotoUrl.requestFocus()
            isValid = false
        }
        if (mBinding.etPhone.text.toString().isBlank()) {
            mBinding.tilPhone.error = getString(R.string.helper_required)
            mBinding.etPhone.requestFocus()
            isValid = false
        }
        if (mBinding.etName.text.toString().isBlank()) {
            mBinding.tilName.error = getString(R.string.helper_required)
            mBinding.etName.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun getStore(id: Long) {
        val queue = LinkedBlockingQueue<StoreEntity?>()
        Thread {
            mStoreEntity = StoreApplication.database.storeDao().getStore(id)
            queue.add(mStoreEntity)
        }.start()
        queue.take()?.let {
            setUIStore(it)
        }
    }

    private fun setUIStore(store: StoreEntity) {
        with(mBinding) {
            etName.text = store.name.editable()
            etPhone.text = store.phone.editable()
            etWebsite.text = store.website.editable()
            etPhotoUrl.text = store.imageURL.editable()
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

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
                    website = mBinding.etWebsite.text.toString().trim(),
                    imageURL = mBinding.etPhotoUrl.text.toString().trim()
                )

                val queue = LinkedBlockingQueue<Long?>()
                Thread {
                    val storeId = StoreApplication.database.storeDao().addStore(store)
                    queue.add(storeId)
                }.start()

                queue.take()?.let {
                    store.id = it
                    mActivity?.addStore(store)
                    hideKeyboard()
                    Toast.makeText(mActivity, R.string.form_store_message_success, Toast.LENGTH_SHORT).show()

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