# Tic-Tac-Toe Android Game

This is an Android application that lets users play the classic Tic-Tac-Toe game either against the computer or with a friend. The game features a simple user interface with options to select game mode, input player names, and track scores.

## Features
- **Game Modes**: Play against a friend or against the computer.
- **Player Names**: Input custom names for Player 1 and Player 2. In the case of playing against the computer, the computer's name defaults to "Computer".
- **Score Tracking**: Scores are maintained for both players and ties.
- **Dynamic Turn Display**: The game shows whose turn it is at any given moment.
- **Restart Functionality**: Players can restart the game at any point without closing the app.
- **Randomized Computer Moves**: When playing against the computer, the AI makes random moves to simulate a basic opponent.
- **Responsive UI**: Buttons and layout adapt to the game state, improving user experience.

## Screenshots

<table>
  <tr>
    <td><img src="/screenshots/main_screen.jpg" alt="Player Input Screen" width="300"/></td>
    <td><img src="/screenshots/vs_friend.jpg" alt="Gameplay Screen" width="300"/></td>
        <td><img src="/screenshots/vs_computer.jpg" alt="Player Input Screen" width="300"/></td>
  </tr>
  <tr>
    <td><img src="/screenshots/game_vs_friend.jpg" alt="Gameplay Screen" width="300"/></td>
    <td><img src="/screenshots/game_vs_friendwin.jpg" alt="Game Over Screen" width="300"/></td>
    <td><img src="/screenshots/game_vsCwin.jpg" alt="Score Display" width="300"/></td>
  </tr>
</table>

## How to Play
1. **Select Game Mode**: Choose to play against the computer or a friend by selecting the appropriate radio button.
2. **Input Player Names**:
   - When playing against a friend, input both Player 1 and Player 2's names.
   - When playing against the computer, input Player 1's name. The computer's name will be automatically set to "Computer".
3. **Start the Game**: Once player names are provided, tap the "Start Game" button.
4. **Gameplay**:
   - Tap on an empty cell in the 3x3 grid to place your symbol (X or O).
   - Players take turns until one player wins or the game ends in a draw.
5. **Score**: The score is updated for wins and ties, and the game continues until manually restarted.

## Tech Stack
- **Kotlin**: Primary language for app development.
- **XML**: Used for UI design and layout.
- **Android Studio**: IDE used for development.

## Project Structure
- `MainActivity.kt`: Handles the game logic, including move validation, winner detection, and score tracking.
- `PlayerInputActivity.kt`: Manages the player input screen, including game mode selection and player name validation.
- `activity_main.xml`: Layout for the gameplay screen, including the 3x3 grid and score display.
- `activity_player_input.xml`: Layout for the player input screen, including fields for player names and social media links.

## How to Run the Project
1. Clone this repository.
2. Open the project in **Android Studio**.
3. Sync the Gradle files.
4. Build and run the project on an Android device or emulator.

## License
This project is open-source and free to use. Feel free to fork and modify it as per your requirements.

## Social Links
Connect with me on:
- [LinkedIn](https://www.linkedin.com/in/mohit-dwivedi13/)
- [Instagram](https://www.instagram.com/dmohit13/)
- [Twitter](https://twitter.com/dmohit013)
