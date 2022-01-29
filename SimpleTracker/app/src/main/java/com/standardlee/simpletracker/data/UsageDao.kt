package com.standardlee.simpletracker.data

import androidx.room.*

@Dao
interface UsageDao {
    @Query("SELECT * FROM usage")
    fun getAll(): List<Usage>

    @Query("SELECT * FROM usage WHERE label LIKE :label")
    fun findByLabel(label: String): Usage

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insert(usage: Usage)
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg usages: Usage)

    @Delete
    fun delete(usage: Usage)

    // Table DELETE QUERY
    @Query("DELETE FROM Usage")
    fun deleteAll()
}