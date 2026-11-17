# AuthFeature — практическая работа №11

Android-приложение на Kotlin + Jetpack Compose. В проекте есть авторизация/регистрация (учебный FakeServerInterceptor), заметки (Room), Retrofit, Hilt, Firebase Remote Config и Crashlytics.

## Открытие и запуск в Android Studio

1. Распакуйте архив в отдельную папку.
2. В Android Studio выберите **File → Open** и укажите папку `AuthFeature` (в ней находятся `settings.gradle.kts` и `build.gradle.kts`).
3. Дождитесь Gradle Sync. Для проекта требуется JDK 17 и Gradle 8.9; Android Studio обычно использует JDK, выбранный в **Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK**.
4. Создайте/выберите эмулятор Android или подключите телефон с USB debugging.
5. Нажмите **Run**.

> В исходном архиве отсутствуют `gradlew`, `gradlew.bat` и `gradle-wrapper.jar`. Поэтому для командной сборки используйте установленный Gradle 8.9 (`gradle ...`) либо восстановите Wrapper через Android Studio/локальную установку Gradle. CI GitHub Actions устанавливает Gradle 8.9 самостоятельно.

## Проверка

В терминале Android Studio из корня проекта:

```bash
gradle testDebugUnitTest
gradle assembleDebug
gradle assembleRelease
```

Если команда `gradle` не найдена, сначала настройте Gradle 8.9 или используйте Android Studio для Gradle Sync и запуска. Успешная сборка создаёт APK в `app/build/outputs/apk/`.

## Тестовый вход

- Любой корректный email + пароль `123456` — вход.
- Другой пароль — ошибка авторизации.
- `offline@test.com` — имитация отсутствия сети.
- Регистрация: корректный email, пароль от 6 символов и совпадающее подтверждение.
- `taken@test.com` — имитация уже занятого email.

Сервер авторизации в этом учебном проекте имитируется `FakeServerInterceptor`; это не production-аутентификация.

## GitHub Actions и бесплатная публикация

- `.github/workflows/android.yml`: на push/PR в `main` выполняются unit-тесты и собирается debug APK.
- `.github/workflows/release.yml`: по тегу `v*` выполняются тесты, собираются release APK и AAB, APK прикрепляется к GitHub Release, AAB сохраняется как artifact.

Чтобы опубликовать релиз:

```bash
git add .
git commit -m "Prepare release"
git tag v1.0.0
git push origin main --tags
```

В GitHub откройте **Actions**, дождитесь зелёного статуса, затем **Releases** — там будет APK для скачивания. В настройках репозитория должно быть разрешено создание релизов GitHub Actions (Settings → Actions → General → Workflow permissions; `contents: write` задан в workflow).

### Подпись APK

Для постоянной подписи создайте собственный keystore один раз и добавьте GitHub Actions secrets:

- `ANDROID_KEYSTORE_BASE64`
- `KEYSTORE_PASSWORD`
- `KEY_PASSWORD`
- `KEY_ALIAS`

Workflow использует их, если все четыре заданы. Если их нет, Gradle подписывает учебный APK debug-ключом. Такой APK подходит для демонстрации/проверки, но не для распространения как стабильное production-приложение и не для обновления установок, подписанных другим ключом. Не теряйте keystore и пароли.

### Firebase (необязательно для локального запуска)

Поместите `google-services.json` в `app/` и настройте Firebase Android app с package name `com.example.authfeature`. Для CI можно добавить содержимое файла в GitHub secret `GOOGLE_SERVICES_JSON`. В Firebase Remote Config создайте параметр `welcome_text` — именно это имя читает приложение. Без конфигурации Firebase используется приветствие по умолчанию.

Crashlytics требует корректно подключённого Firebase-проекта и `google-services.json`; наличие зависимости само по себе не подтверждает отправку crash-отчётов.

## Секреты и файлы

`.gitignore` исключает `key.properties`, `*.jks`, `*.keystore`, `google-services.json`, `local.properties` и собранные APK/AAB. Не добавляйте секреты в Git.

## Ограничения бесплатной альтернативы

GitHub Releases — бесплатное размещение файлов релиза, а не полноценный магазин приложений. Пользователь скачивает APK и устанавливает его вручную. Google Play Console, публикация в магазине и Pre-launch Report здесь не используются.
