package com.example.authfeature.domain.model

sealed class NoteError : Exception() {
    object EmptyTitle : NoteError()
    object EmptyContent : NoteError()
}
