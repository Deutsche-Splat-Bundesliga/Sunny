# Installation

Zum Installieren von Sunny und Luna benötigt ihr:

- Gradle
- JDK 21
- eine [Discord-Bot Applikation(en)](https://discord.com/developers/applications/) für Sunny und/oder Luna
- eine [Twitch-Applikation](https://dev.twitch.tv/console)
- eine [Challonge-Applikation](https://connect.challonge.com/challonge/apps)

## Bauen des Bots

Das Bauen des Bots setzt ein vollständig aufgesetztes Projekt **mit Gradle** voraus.
Dabei werden alle Dateien in der ZIP-Datei mitsamt Datenbank **in das Wurzelverzeichnis des Projektes** kopiert.

Danach muss das Gradle-Projekt synchronisiert werden, damit alle Dependencies geladen werden können.

### Unter Gradle:

Das Bauen der `.jar`-Datei erfolgt mit der Gradle-Task `shadowJar`. Das Erzeugnis nach erfolgreichem Durchlauf
befindet sich in `./build/libs/sunny.jar`.

### bot.properties

In ``bot.properties`` werden alle sensiblen Variablen des Bots gespeichert.

**Sunny:**

| Schlüssel          | Beschreibung                                                                                                      |
|--------------------|-------------------------------------------------------------------------------------------------------------------|
| `token`            | Der Token des Discord-Bots, den man unter `https://discord.com/developers/applications/DISCORD_BOT_ID/bot` erhält |
| `key_challonge`    | Token der Challonge-v1-API, die man unter `https://connect.challonge.com/challonge/apps` erhält.                  |
| `guildId`          | Die ID des Discord-Servers                                                                                        |
| `clientId`         | Die ID der Twitch-Applikation die man unter `https://dev.twitch.tv/console` erhält.                               |
| `clientSecret`     | Der Geheimschlüssel der Twitch-Applikation die man unter `https://dev.twitch.tv/console` erhält.                  |
| `youtubechannelid` | Die Channel-ID vom Youtube-Kanal, für welches man Updates (neu hochgeladenes Video) erhalten möchte.              |
| `restapi_port`     | Der Port der RESTful-API (Standard: 9882)                                                                         |

**Luna:**

| Schlüssel | Beschreibung                                                                                                      |
|-----------|-------------------------------------------------------------------------------------------------------------------|
| `token`   | Der Token des Discord-Bots, den man unter `https://discord.com/developers/applications/DISCORD_BOT_ID/bot` erhält |
| `guild`   | Die ID des Discord-Servers                                                                                        |

> [WICHTIG]
>
> Ohne `bot.properties` kann der Bot nicht gestartet werden oder nicht richtig funktionieren!

## Starten des Bots

### Shell:

Der Bot kann unter JDK 21 mit dem Command

````shell
java -Dterminal.jline=false -Dterminal.ansi=true -jar {{JARFILE}}
````

gestartet werden, wobei ``{{JARFILE}}`` der Name der JAR-Datei ist (Standard: ``sunny.jar`` oder ``luna.jar``).