Необязательные файлы для платного аккаунта Google Play ($25).
Бесплатный вариант практики их не использует.

promote.yml  — ручной перевод Internal -> Production (rollout 1%).
               Чтобы включить: скопировать в .github/workflows/ и добавить секрет PLAY_STORE_JSON_KEY.
fastlane/Fastfile содержит lanes internal и promote_prod для Google Play; в бесплатном варианте они не вызываются.
