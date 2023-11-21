package com.olascoaga.stores

interface MainAux {
    fun changeFABVisibility(isVisible: Boolean = false)
    fun addStore(storeEntity: StoreEntity)
    fun updateStore(storeEntity: StoreEntity)
}