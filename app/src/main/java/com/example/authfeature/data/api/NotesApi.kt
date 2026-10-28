package com.example.authfeature.data.api

import com.example.authfeature.data.dto.NoteDto
import retrofit2.http.GET

interface NotesApi {
    @GET("notes")
    suspend fun getNotes(): List<NoteDto>
}
