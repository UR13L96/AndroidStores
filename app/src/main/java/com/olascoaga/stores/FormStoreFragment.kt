package com.olascoaga.stores

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.olascoaga.stores.databinding.FragmentFormStoreBinding

class FormStoreFragment : Fragment() {
    private lateinit var mBinding: FragmentFormStoreBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentFormStoreBinding.inflate(inflater, container, false)
        return mBinding.root
    }
}