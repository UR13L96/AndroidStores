package com.olascoaga.stores

interface OnClickListener {
    fun onClick(storeEntity: StoreEntity)
    fun onFavoriteClicked(storeEntity: StoreEntity)
    fun onDeleteClicked(storeEntity: StoreEntity)
}