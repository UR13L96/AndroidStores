package com.olascoaga.stores

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface StoreDao {
    @Query("SELECT * FROM StoreEntity")
    fun getStores(): MutableList<StoreEntity>

     @Insert
     fun addStore(storeEntity: StoreEntity): Long

     @Update
     fun updateStore(storeEntity: StoreEntity)

     @Delete
     fun deleteStore(storeEntity: StoreEntity)

     @Query("SELECT * FROM StoreEntity where id = :id")
     fun getStore(id: Long): StoreEntity
}