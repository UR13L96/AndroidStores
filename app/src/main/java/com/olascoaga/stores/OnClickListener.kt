package com.olascoaga.stores

interface OnClickListener {
    fun onClick(storeId: Long)
    fun onFavoriteClicked(storeEntity: StoreEntity)
    fun onLongClicked(storeEntity: StoreEntity)
}