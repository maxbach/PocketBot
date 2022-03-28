# PocketBot - easy way to send articles from Telegram to Pocket

**Technologies**: Kotlin, Ktor, [kotlin-telegram-bot](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot)

### What can the bot do

- auth to pocket
- find https links in text and hidden urls
- choose url, if there are a few links in article
- auto delete used articles and buttons

<img width="600" alt="image" src="https://user-images.githubusercontent.com/5735956/160408925-a9560c48-fb1d-4d2f-b20f-cd549140304f.png">

### Problems

- no database. when bot restarts, user need to login
- telegram bot can't work on 24/7, because bot works on free version of 
- some phrases is written on Russian language
