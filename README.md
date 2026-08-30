# Anime366

Неофициальный клиент [Anime365](https://anime-365.ru/) для Android TV и смартфонов.

Форк [YummyTV](https://github.com/Helandy/YummyTV) (Helandy, CC BY-NC 4.0), переведённый на [API Anime365](https://anime-365.ru/api-docs).

[Скачать APK](https://github.com/AlexeyLCP/Anime366/releases/latest)

## Возможности

- отдельные UI для Android TV (пульт) и телефона
- каталог, поиск, топ, онгоинги
- лента новых серий
- страница тайтла: описание, серии, озвучки и субтитры
- плеер Media3 / ExoPlayer: качество, озвучка / субтитры, софтсаб
- вход в аккаунт Anime365 (нужна подписка сайта для видео)
- переключение зеркала в настройках

Комментарии, блогеры и ЛС сайта в публичном API нет — в приложении отключены.

## Сборка

Нужны JDK 21 и Android SDK. Подпись релиза: `release.jks` + поля `anime366.*` в `local.properties` (не коммитить).

```
./gradlew :app:assembleRelease
```

Релиз в GitHub: тег `v*` (например `v1.22-1`).

## Лицензия

[PolyForm Noncommercial 1.0.0](LICENSE) — только некоммерческое использование.

Исходный код YummyTV остаётся под [CC BY-NC 4.0](https://creativecommons.org/licenses/by-nc/4.0/).

Приложение не хранит и не распространяет контент. Все права на аниме — у правообладателей.
