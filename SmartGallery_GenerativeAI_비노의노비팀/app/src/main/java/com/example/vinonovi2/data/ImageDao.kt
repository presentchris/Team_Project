package com.example.vinonovi2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("SELECT * FROM image")
    fun getAll(): Flow<List<Image>>


    @Query("SELECT * FROM image WHERE imageUrl LIKE :imageUri")
    fun findByUri(imageUri: String): Flow<Image>

    @Query("SELECT COUNT(*) FROM image WHERE imageUrl = :imageUri")
    fun getImageCountByUri(imageUri: String): Int

    @Insert
    fun insertAll(vararg images: Image)

    @Delete
    fun delete(image: Image)
//    fun deleteByImageUrI(imageUri: String)
}