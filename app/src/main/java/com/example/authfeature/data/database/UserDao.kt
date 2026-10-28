package com.example.authfeature.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM session LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Query("DELETE FROM session")
    suspend fun clear()
}
