# AuthFeature — ПР №9: MVVM + StateFlow + Clean Architecture + Hilt (Jetpack Compose)

Открыть: Android Studio -> File -> Open -> папка AuthFeature -> Gradle Sync -> Run.
Тесты: ПКМ на app/src/test -> Run Tests.

Структура (по инструкционной карте):
  domain/        model, repository (интерфейсы), usecase
  data/          api (Retrofit), database (Room), dto, repository (реализации)
  presentation/  auth (вход), notes (заметки) — Compose + ViewModel + StateFlow
  di/            Hilt-модули

Вход (сервер имитирует FakeServerInterceptor):
  любой валидный email + пароль 123456 -> вход, открывается список заметок
  другой пароль                         -> Snackbar "Неверный email или пароль"
  email offline@test.com                -> Snackbar "Нет соединения с сетью"

Регистрация (кнопка «Нет аккаунта? Зарегистрироваться» на экране входа):
  валидный email + пароль от 6 символов + совпадающее подтверждение -> аккаунт создан, сразу открывается список заметок
  email taken@test.com (начинается с taken)                         -> Snackbar "Этот email уже зарегистрирован"
  email offline@test.com                                            -> Snackbar "Нет соединения с сетью"
