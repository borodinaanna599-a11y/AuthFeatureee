package com.example.authfeature.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.authfeature.domain.model.User

/** Хранится одна запись — текущая сессия (id всегда 0). */
@Entity(tableName = "session")
data class UserEntity(
    @PrimaryKey val id: Int = 0,
    val name: String,
    val email: String,
    val token: String
)

fun UserEntity.toDomain() = User(name = name, email = email, token = token)
fun User.toEntity() = UserEntity(name = name, email = email, token = token)
