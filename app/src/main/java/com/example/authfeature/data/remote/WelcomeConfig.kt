
package com.example.authfeature.data.remote

import com.example.authfeature.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class WelcomeConfig @Inject constructor() {

    private val remoteConfig: FirebaseRemoteConfig? =
        runCatching {
            FirebaseRemoteConfig.getInstance().apply {
                setConfigSettingsAsync(
                    remoteConfigSettings {
                        minimumFetchIntervalInSeconds =
                            if (BuildConfig.DEBUG) 0 else 3600
                    }
                )

                setDefaultsAsync(
                    mapOf(
                        KEY_WELCOME_TEXT to DEFAULT_WELCOME_TEXT,
                        KEY_WELCOME_BANNER_ENABLED to true,
                        KEY_ADD_NOTE_ENABLED to true
                    )
                )
            }
        }.getOrNull()

    // Текст приветствия
    fun current(): String =
        remoteConfig?.getString(KEY_WELCOME_TEXT)
            ?.takeIf { it.isNotBlank() }
            ?: DEFAULT_WELCOME_TEXT

    // Показывать ли приветственный баннер
    fun isWelcomeBannerEnabled(): Boolean =
        remoteConfig?.getBoolean(KEY_WELCOME_BANNER_ENABLED) ?: true

    // Показывать ли кнопку добавления заметки
    fun isAddNoteEnabled(): Boolean =
        remoteConfig?.getBoolean(KEY_ADD_NOTE_ENABLED) ?: true

    // Получение свежих настроек Firebase
    suspend fun refresh(): String =
        suspendCancellableCoroutine { cont ->
            val config = remoteConfig

            if (config == null) {
                cont.resume(DEFAULT_WELCOME_TEXT)
            } else {
                config.fetchAndActivate()
                    .addOnCompleteListener {
                        if (cont.isActive) {
                            cont.resume(current())
                        }
                    }
            }
        }

    companion object {
        const val KEY_WELCOME_TEXT = "welcome_text"
        const val KEY_WELCOME_BANNER_ENABLED = "welcome_banner_enabled"
        const val KEY_ADD_NOTE_ENABLED = "add_note_enabled"

        const val DEFAULT_WELCOME_TEXT =
            "Добро пожаловать в Auth Feature!"
    }
}
