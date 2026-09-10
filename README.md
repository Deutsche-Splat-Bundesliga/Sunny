# Sunny

The Discord bot for the **Deutsche Splatoon Bundesliga (DSB)** server.

Originally created by CyCeption Productions, now maintained by the Deutsche Splatoon Bundesliga team. This repository also includes **Luna**, a companion bot built alongside Sunny.

## Features

Sunny integrates with several external services to support the DSB Discord community:

- **Discord** bot functionality for Sunny and Luna
- **Twitch** integration for stream-related features
- **Challonge** integration for tournament management
- **YouTube** update notifications for newly uploaded videos
- A built-in **RESTful API** (default port: `9882`)

## Requirements

To build and run Sunny and/or Luna, you'll need:

- [Gradle](https://gradle.org/)
- JDK 21
- One or more [Discord bot applications](https://discord.com/developers/applications/) for Sunny and/or Luna
- A [Twitch application](https://dev.twitch.tv/console)
- A [Challonge application](https://connect.challonge.com/challonge/apps)

## Building

Building the bot requires a fully set up Gradle project. Any required data files (including the database) should be copied into the project's root directory before building.

Once set up, sync the Gradle project to resolve all dependencies, then build the JAR using the `shadowJar` task:

```bash
./gradlew shadowJar
```

The resulting JAR file will be located at `./build/libs/sunny.jar`.

## Configuration

All sensitive configuration values are stored in a `bot.properties` file in the project root. **The bot cannot start (or will not function correctly) without this file.**

### Sunny

| Key | Description |
| --- | --- |
| `token` | The Discord bot token, available at `https://discord.com/developers/applications/DISCORD_BOT_ID/bot` |
| `key_challonge` | Challonge v1 API token, available at `https://connect.challonge.com/challonge/apps` |
| `guildId` | The ID of the Discord server |
| `clientId` | The Twitch application client ID, available at `https://dev.twitch.tv/console` |
| `clientSecret` | The Twitch application client secret, available at `https://dev.twitch.tv/console` |
| `youtubechannelid` | The YouTube channel ID to receive upload notifications for |
| `restapi_port` | The port for the RESTful API (default: `9882`) |

Example `bot.properties`:

```properties
token= [SECRET]
key_challonge= [SECRET]
guildId= 707547801265111071
clientId= 0glwotecdsnuy6s51gznuyhckxu7o0
clientSecret= [SECRET]
youtubechannelid= DeutscheSplatoonBundesliga
restapi_port: 9882
```

### Luna

| Key | Description |
| --- | --- |
| `token` | The Discord bot token, available at `https://discord.com/developers/applications/DISCORD_BOT_ID/bot` |
| `guild` | The ID of the Discord server |

## Running

With JDK 21 installed, run the built JAR with:

```bash
java -Dterminal.jline=false -Dterminal.ansi=true -jar {{JARFILE}}
```

Replace `{{JARFILE}}` with the name of your JAR file (default: `sunny.jar` or `luna.jar`).

## Credits

- Original creation: **CyCeption Productions**
- Maintenance: **Deutsche Splatoon Bundesliga**

For detailed installation instructions, see [INSTALL.md](INSTALL.md).
