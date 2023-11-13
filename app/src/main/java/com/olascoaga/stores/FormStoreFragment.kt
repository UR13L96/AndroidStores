package com.olascoaga.stores

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.olascoaga.stores.databinding.FragmentFormStoreBinding

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
            Snackbar.make(
                mBinding.root,
                getString(R.string.form_store_message_success),
                Snackbar.LENGTH_SHORT
            ).show()
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
                Snackbar.make(
                    mBinding.root,
                    getString(R.string.form_store_message_success),
                    Snackbar.LENGTH_SHORT
                ).show()
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}