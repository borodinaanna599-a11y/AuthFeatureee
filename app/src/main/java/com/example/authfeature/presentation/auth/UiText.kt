package com.example.authfeature.presentation.auth

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/** Текст, который ViewModel может отдать UI, не зная про Context. */
sealed interface UiText {
    data class Dynamic(val value: String) : UiText
    class Resource(@StringRes val resId: Int, vararg val args: Any) : UiText

    @Composable
    fun asString(): String = when (this) {
        is Dynamic -> value
        is Resource -> stringResource(resId, *args)
    }
}
