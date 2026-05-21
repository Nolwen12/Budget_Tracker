package com.h3hitema.budgettracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CategoryDao{
    @Query("SELECT * FROM category ORDER BY id DESC")
    suspend fun getAllCategory(): List<CategoryEntity>

    @Query("SELECT * FROM category WHERE id = :categoryId LIMIT 1")
    suspend fun getCategoryById(categoryId: Int): CategoryEntity?

    @Insert
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("DELETE FROM category WHERE id = :categoryId")
    suspend fun deleteCategoryId(categoryId: Int)

    @Query("DELETE FROM category")
    suspend fun deleteAllCategory()

    @Query("SELECT COUNT(*) FROM category")
    suspend fun countCategory(): Int
}