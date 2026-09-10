# Installation

To install Sunny and Luna, you will need:

- Gradle
- JDK 21
- A [Discord bot application](https://discord.com/developers/applications/)
- A [Twitch application](https://dev.twitch.tv/console)
- A [Challonge application](https://connect.challonge.com/challonge/apps)

## Building the Bot

Building the bot requires a fully set-up project **with Gradle**.
All files from the ZIP file, including the database, must be copied **into the project's root directory**.

Afterward, the Gradle project must be synced so that all dependencies can be downloaded.

### Under Gradle:

Building the `.jar` file is done using the Gradle task `shadowJar`. Once completed successfully, the resulting file will be located at `./build/libs/sunny.jar`.

### bot.properties

All sensitive variables for the bot are stored in `bot.properties`.

**Sunny:**

| Key | Description |
| --- | --- |
| `token` | The Discord bot's token, obtained at `https://discord.com/developers/applications/DISCORD_BOT_ID/bot` |
| `key_challonge` | Token for the Challonge v1 API, obtained at `https://connect.challonge.com/challonge/apps` |
| `guildId` | The ID of the Discord server |
| `clientId` | The ID of the Twitch application, obtained at `https://dev.twitch.tv/console` |
| `clientSecret` | The secret key of the Twitch application, obtained at `https://dev.twitch.tv/console` |
| `youtubechannelid` | The channel ID of the YouTube channel for which you want to receive updates (newly uploaded video) |
| `restapi_port` | The port of the RESTful API (default: 9882) |

> [IMPORTANT]
>
> Without `bot.properties`, the bot cannot be started or will not function correctly!

## Starting the Bot

### Shell:

The bot can be started under JDK 21 with the command

```
java -Dterminal.jline=false -Dterminal.ansi=true -jar {{JARFILE}}
```

where `{{JARFILE}}` is the name of the JAR file (default: `sunny.jar`).
