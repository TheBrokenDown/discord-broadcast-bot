# Discord Broadcast Bot
This is a Discord Bot which enables you to inform server members about new broadcasts (Mixer, Twitch) or videos (YouTube) on channels which was specified by you. You can assign any mentionable role to every channel, and when the channel starts a broadcast or uploads any video, this role will be mentioned. 

Это Discord-бот, позволяющий информировать участников Discord-сервера о новых прямых трансляциях (Mixer, Twitch) или видео (YouTube) на заданных вами каналах. При этом каждому каналу можно присвоить упоминаему роль, которую бот будет пинговать, когда на канале начнётся прямая трансляция или выйдет новое видео.

## How it works
### EN
First of all you need to add channels which you want to track to the tracking list. You can specify a role that will be mentioned when channel starts a broadcast or releases a new video. When a broadcast (if we talk about Twitch channel or Mixer channel) or new video (if we talk about YouTube channel) releases, the bot sends the notification message with the link to the video/livestream to the Discord text channel, which was specified in the configuration file. If you specify a role when you add the channel, this role will be mentioned, so everybody on the server who has this role will receive the notification.
### RU
Вы указываете каналы, которые бот будет проверять на наличие новых видео или прямых трансляций. С каждым каналом можно указать роль, которая будет упомянута, когда на этом канале начнется прямая трансляция или выйдет новое видео. Когда на каком-то из каналов начинается стрим (если это Mixer или Twitch) или выходит новое видео (если это YouTube), бот отправляет сообщение со ссылкой на стрим или видео в заранее указанный в конфигурационном файле канал. Если каналу была привязана роль, бот упоминает эту роль в уведомительном сообщении, тем самым члены сервера, которые имеют эту роль, получат уведомление.

## Features
### EN
- Supports YouTube (videos), Mixer (broadcasts) and Twitch (broadcasts). LiveStreams on YouTube are not currently supported because Google provides the best API in the world (no, it doesn't).
- Has the flexible configuration (you are able to change commands, status, API URLs, messages and some other things).
- User-friendly (I hope).
- Supports Docker: you can deploy the bot using docker-compose file, which is located in root directory of the project.
### RU
- Поддерживает YouTube (видео), Mixer (прямые трансляции) и Twitch (прямые трансляции). Прямые трансляции на YouTube в данный момент не поддерживаются из-за потрясающего Google API.
- Имеет гибкую конфигурацию (вы можете менять команды, статус бота, API URLs, сообщения и некоторые другие вещи).
- Простой в использовании (я надеюсь, я старался).
- Поддерживает Docker: вы можете развернуть приложение, используя docker-compose файл, расположенный в корне проекта.

## Disadvantages
### EN
- Works only with one Discord server per application. You are not able to use one running application with multiple Discord servers. One app = one Discord server. (You can run a few instances of the application if you need to use the bot on multiple servers)
- Sends notification messages only to one text channel, you are not able to specify multiple text channels.
- Can mention only one role per channel. You cannot specify more than one role to mention per channel.
- Doesn't support LiveStreams on YouTube, but videos are supported.
### RU
- Может работать только на одном Discord-сервере. Если есть необходимость использовать бота на нескольких серверах, необходимо поднимать ещё один инстанс приложения.
- Способен отправлять уведомление только в один текстовый канал, несколько указать не выйдет.
- Возможно упоминание только одной роли в сообщении. Нет возможности упоминать несколько ролей в одном уведомлении.
- Не поддерживает прямые трансляции на YouTube, но поддерживает видео.

## Setup
### Using Docker Compose
#### EN
1. Download `docker-compose.yml` and `src/main/resources/application.properties`.
2. Rename `application.properties` to `broadcastbot.properties`.
3. Edit `broadcastbot.properties`. First of all you have to edit these params: 
    - **discord.bot.token** - put here token of the bot. You can create the bot [here](https://discord.com/developers/applications/).
    - **discord.channel.id** - put here ID of the text channel on your server where notification messages will be sent to.
    - **youtube.api.token** - put here your YouTube Data API v3 API key. You can get it [here](https://console.developers.google.com).
    - **twitch.api.client.id** and **twitch.api.client.secret** - you have to register your application [here](https://dev.twitch.tv/console/apps), after that you will be able to get these tokens.
    - **discord.message.pattern.twitch.stream** and **discord.message.pattern.mixer.stream** - put here patterns of the notification messages. Don't forget to put placeholders `{mention}`, `{channelName}` and `{streamLink}`.
    - **discord.message.pattern.youtube.video** - put here a pattern of the notification messages as well. Supported placeholders: `{mention}`, `{channelTitle}` and `{videoLink}`.
4. Edit `docker-compose.yml`.  
All that you have to do here is to specify a path to the folder where `broadcastbot.properties` is located. If your `broadcastbot.properties` has following path: `C:\Users\Nintendo\broadcastbot.properties` you have to specify this one: `C:\Users\Nintendo\ `. In this way the line number 20 in your `docker-compose.yml` must be like that: `- C:\Users\Nintendo\:/app/conf`.  
5. Open the Terminal (or PowerShell), go to the directory where `docker-compose.yml` is located and start the application using the command `docker-compose up -d`.
#### RU
1. Загрузите `docker-compose.yml` и `src/main/resources/application.properties`.
2. Переименуйте `application.properties` в `broadcastbot.properties`.
3. Отредактируйте `broadcastbot.properties`. В первую очередь измените эти параметры:
    - **discord.bot.token** - укажите здесь токен Discord-бота. Бота можно создать [здесь](https://discord.com/developers/applications/).
    - **discord.channel.id** - укажите здесь ID текстового канала на сервере, в который бот будет отправлять сообщения о начале прямых трансляций или о выходе новых видео.
    - **youtube.api.token** - укажите здесь ваш YouTube Data API v3 токен. Его необходимо получить [здесь](https://console.developers.google.com).
    - **twitch.api.client.id** и **twitch.api.client.secret** - вам необходимо зарегистрировать приложение [здесь](https://dev.twitch.tv/console/apps), после чего вы сможете получить два этих токена.
    - **discord.message.pattern.twitch.stream** и **discord.message.pattern.mixer.stream** - укажите здесь шаблоны уведомительных сообщений. Не забудьте указать плейсхолдеры `{mention}`, `{channelName}` и `{streamLink}`.
    - **discord.message.pattern.youtube.video** - здесь тоже укажите шаблон уведомительного сообщения. Поддерживаемые плейсхолдеры: `{mention}`, `{channelTitle}` и `{videoLink}`.
4. Отредактируйте `docker-compose.yml`.  
Все, что вам необходимо сделать здесь, - это указать путь к папке, где лежит `broadcastbot.properties`. Если `broadcastbot.properties` находится по пути `C:\Users\Nintendo\broadcastbot.properties`, вам необходимо указать `C:\Users\Nintendo\ `. В таком случае, 20 строка файла `docker-compose.yml` будет следующего содержания: `- C:\Users\Nintendo\:/app/conf`.
5. Откройте терминал (или PowerShell), перейдите в директорию с файлом `docker-compose.yml` и запустите приложение с помощью команды `docker-compose up -d`.

## Usage
### EN
  - First of all you have to invite the bot to your server. Click [here](https://discordpy.readthedocs.io/en/latest/discord.html) to find out how.
  - Grant administrator permission to the bot, it is needed by some functionality.
  - Make sure the bot has access to:
    - the text channel which you have specified in the configuration file.
    - the text channel where you are going to type the commands.
  - Type `%help` in any text channel. The bot will send you a message with all the commands.
  - Enjoy 😏
### RU
  - Для начала пригласите бота на свой сервер. Кликните [здесь](https://discordpy.readthedocs.io/en/latest/discord.html), чтобы узнать, как это сделать.
  - Выдайте боту права администратора, это необходимо для работоспособности некоторой функциональности.
  - Убедитесь, что бот имеет доступ к:
    - текстовому каналу, который вы указали в конфигурационном файле.
    - текстовому каналу, в который вы собираетесь отправлять команды.
  - Отправьте команду `%help` в любой текстовый канал. Бот отправит вам сообщение, содержащее список доступных команд.
  - Пользуйтесь с удовольствием 😁

## Contacts
Feel free to contact me: https://t.me/TheDelusive
