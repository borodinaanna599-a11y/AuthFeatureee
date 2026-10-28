package com.example.authfeature.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.authfeature.domain.model.Note

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long
)

fun NoteEntity.toDomain() = Note(id = id, title = title, content = content, timestamp = timestamp)
fun Note.toEntity() = NoteEntity(id = id, title = title, content = content, timestamp = timestamp)
