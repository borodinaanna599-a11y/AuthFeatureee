package com.example.authfeature.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class, NoteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun notesDao(): NotesDao
}
