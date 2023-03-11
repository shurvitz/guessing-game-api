# Guessing Game API
This application demonstrates the principles of GaaS (Guessing-as-a-Service) using Spring Boot.

The service allows a user to play a guessing game via REST APIs. Once the application chooses a number within the range
specified by the user, any guess made by the user will prompt a response of "Lower" or "Higher" until the exact number
is guessed. The game can be played using ***Postman***, or any other app/cli that can send REST API requests.

## Initiating the game
1. Clone this repo and pull into your local machine.
2. `GuessingGameApplication` (IntelliJ IDEA), or
3. `mvn clean install` followed by `java -jar target/guessing-game-0.0.1-SNAPSHOT.jar` (terminal). 

The Spring Boot application will start running and will be listening on **localhost:8082** (port 8082).

## Starting a new game
Send a ***POST*** request to `http://localhost:8082/guess` (with the following body):
```json
{
    "from": 1,
    "to": 100
}
```
* `from` - The game's start range.
* `to` - The game's end range.

***Note:*** the example above will start a guessing game from 1-100.

The reply from the API is as follows:
```json
{
  "gameId": "a3f85307-22a5-4942-9782-d611d344368a",
  "guessingAttempts": 0,
  "guessStatus": 0,
  "replyMessage": "Guess a number from 1 to 100?"
}
```

* `gameId` - The ID used to reference the game for subsequent guessing requests.
* `guessingAttempts` - The application keeps track of the number of guessing attempts made (initial value is 0).
* `guessStatus` - Indicates whether the number picked by the application is lower (-1), higher (1), or matches the guess (0).
* `replyMessage` - Message from the application.

## Guessing the number
Send a ***GET*** request to `http://localhost:8082/guess/{id}?guess={guess}` (see example below).

```text
http://localhost:8082/guess/a3f85307-22a5-4942-9782-d611d344368a?guess=50
```
***Note:*** if no guess is made withing ~ 60 seconds, the game will be purged from the system.

Assuming the game id is valid, the reply from the API will have the same format as the ***POST*** reply:
```json
{
  "gameId": "a3f85307-22a5-4942-9782-d611d344368a",
  "guessingAttempts": 1,
  "guessStatus": 1,
  "replyMessage": "Higher..."
}
```
***Note:*** when the game is finally won, the response will contain `"guessStatus": 0` with a congratulatory replyMessage.

## Listing all games
The following API request can be used to get a list of all games actively being played.
Sending a ***GET*** request to `http://localhost:8082/guess/games` results in the following reply:
```json
[
  {
    "id": "a3f85307-22a5-4942-9782-d611d344368a",
    "guessingAttempts": 1,
    "fromRange": 1,
    "toRange": 100,
    "lastModified": "2023-03-09T23:52:58.706155Z"
  }
]
```
