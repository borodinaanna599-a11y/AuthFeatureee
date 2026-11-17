package com.example.authfeature.data.remote

import com.example.authfeature.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Текст приветствия на главном экране, управляемый через Firebase Remote Config.
 * Если Firebase не настроен (нет google-services.json) или сеть недоступна —
 * возвращается значение по умолчанию.
 */
@Singleton
class WelcomeConfig @Inject constructor() {

    private val remoteConfig: FirebaseRemoteConfig? = runCatching {
        FirebaseRemoteConfig.getInstance().apply {
            setConfigSettingsAsync(remoteConfigSettings {
                // в debug обновляем сразу, в release — не чаще раза в час
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
            })
            setDefaultsAsync(mapOf(KEY_WELCOME_TEXT to DEFAULT_WELCOME_TEXT))
        }
    }.getOrNull()

    /** Текущее значение: последнее активированное или дефолтное. */
    fun current(): String =
        remoteConfig?.getString(KEY_WELCOME_TEXT)?.takeIf { it.isNotBlank() }
            ?: DEFAULT_WELCOME_TEXT

    /** Загружает свежие значения с сервера и возвращает актуальный текст. */
    suspend fun refresh(): String = suspendCancellableCoroutine { cont ->
        val config = remoteConfig
        if (config == null) {
            cont.resume(DEFAULT_WELCOME_TEXT)
        } else {
            config.fetchAndActivate().addOnCompleteListener {
                if (cont.isActive) cont.resume(current())
            }
        }
    }

    companion object {
        const val KEY_WELCOME_TEXT = "welcome_text"
        const val DEFAULT_WELCOME_TEXT = "Добро пожаловать в Auth Feature!"
    }
}
