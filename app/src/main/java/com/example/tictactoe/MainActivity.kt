package com.example.tictactoe

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val board = arrayOfNulls<String>(9)
    private var currentPlayer = "X"
    private var gameActive = true

    private lateinit var buttons: Array<Button>
    private lateinit var playerTurnTextView: TextView
    private lateinit var player1ScoreTextView: TextView
    private lateinit var tiesScoreTextView: TextView
    private lateinit var player2ScoreTextView: TextView

    private var player1Score = 0
    private var tiesScore = 0
    private var player2Score = 0

    private lateinit var player1Name: String
    private lateinit var player2Name: String

    // Boolean flag to track if the game is vs Computer
    private var isVsComputer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttons = arrayOf(
            findViewById(R.id.button0),
            findViewById(R.id.button1),
            findViewById(R.id.button2),
            findViewById(R.id.button3),
            findViewById(R.id.button4),
            findViewById(R.id.button5),
            findViewById(R.id.button6),
            findViewById(R.id.button7),
            findViewById(R.id.button8)
        )

        playerTurnTextView = findViewById(R.id.textView2)
        player1ScoreTextView = findViewById(R.id.textView4)
        tiesScoreTextView = findViewById(R.id.textView6)
        player2ScoreTextView = findViewById(R.id.textView8)

        player1Name = intent.getStringExtra("PLAYER_1_NAME") ?: "Player 1"
        player2Name = intent.getStringExtra("PLAYER_2_NAME") ?: "Player 2"

        // Check if the game is vs Computer
        isVsComputer = player2Name == "Computer"

        val player1TextView = findViewById<TextView>(R.id.textView3)
        val player2TextView = findViewById<TextView>(R.id.textView7)

        player1Name = toTitleCase(player1Name)
        player2Name = toTitleCase(player2Name)
        player1TextView.text = player1Name
        player2TextView.text = player2Name

        updatePlayerTurn()
        initializeBoard()
    }

    private fun initializeBoard() {
        for (i in buttons.indices) {
            buttons[i].setOnClickListener {
                if (gameActive && board[i].isNullOrEmpty()) {
                    makeMove(buttons[i], i)
                }
            }
        }
    }

    private fun makeMove(button: Button, position: Int) {
        button.text = currentPlayer
        board[position] = currentPlayer

        val winningPositions = checkWinner()
        if (winningPositions != null) {
            gameActive = false
            highlightWinningButtons(winningPositions)
            if (currentPlayer == "X") {
                player1Score++
                player1ScoreTextView.text = player1Score.toString()
                Toast.makeText(this, "$player1Name wins!", Toast.LENGTH_SHORT).show()

            } else {
                player2Score++
                player2ScoreTextView.text = player2Score.toString()
                Toast.makeText(this, "$player2Name wins!", Toast.LENGTH_SHORT).show()

            }
        } else if (board.all { it != null }) {
            gameActive = false
            tiesScore++
            tiesScoreTextView.text = tiesScore.toString()
            Toast.makeText(this, "It's a draw!", Toast.LENGTH_SHORT).show()
        } else {
            currentPlayer = if (currentPlayer == "X") "O" else "X"
            updatePlayerTurn()

            // If it's vs Computer and it's the computer's turn, make a smart move
            if (isVsComputer && currentPlayer == "O") {
                val bestMove = findBestMove(board)
                buttons[bestMove].performClick() // Simulate button click for computer's move
            }
        }
    }

    private fun findBestMove(board: Array<String?>): Int {
        var bestVal = -1000
        var bestMove = -1

        for (i in board.indices) {
            if (board[i].isNullOrEmpty()) {
                board[i] = "O" // Computer's move
                val moveVal = minimax(board, 0, false)
                board[i] = null // Undo the move

                if (moveVal > bestVal) {
                    bestMove = i
                    bestVal = moveVal
                }
            }
        }
        return bestMove
    }

    private fun minimax(board: Array<String?>, depth: Int, isMax: Boolean): Int {
        val score = evaluateBoard()

        // If computer has won
        if (score == 10) return score - depth

        // If player has won
        if (score == -10) return score + depth

        // If no more moves and no winner, it's a draw
        if (board.all { it != null }) return 0

        return if (isMax) {
            // Maximizer's move (Computer - O)
            var best = -1000
            for (i in board.indices) {
                if (board[i].isNullOrEmpty()) {
                    board[i] = "O"
                    best = maxOf(best, minimax(board, depth + 1, false))
                    board[i] = null
                }
            }
            best
        } else {
            // Minimizer's move (Player - X)
            var best = 1000
            for (i in board.indices) {
                if (board[i].isNullOrEmpty()) {
                    board[i] = "X"
                    best = minOf(best, minimax(board, depth + 1, true))
                    board[i] = null
                }
            }
            best
        }
    }

    private fun evaluateBoard(): Int {
        val winningPositions = arrayOf(
            intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)
        )

        for (positions in winningPositions) {
            if (board[positions[0]] == board[positions[1]] &&
                board[positions[1]] == board[positions[2]] &&
                board[positions[0]] != null
            ) {
                return when (board[positions[0]]) {
                    "O" -> 10  // Computer wins
                    "X" -> -10 // Player wins
                    else -> 0
                }
            }
        }
        return 0 // No winner
    }

    private fun highlightWinningButtons(winningPositions: IntArray) {
        for (position in winningPositions) {
            buttons[position].setBackgroundColor(ContextCompat.getColor(this, R.color.textcolor))
        }
    }

    fun restartGame(view: View) {
        for (i in buttons.indices) {
            buttons[i].text = ""
            buttons[i].setBackgroundColor(ContextCompat.getColor(this, R.color.teal))
            board[i] = null
        }

        currentPlayer = "X"
        gameActive = true
        updatePlayerTurn()
    }
    private fun updatePlayerTurn() {
        if (currentPlayer == "X") {
            playerTurnTextView.text = "$player1Name's turn"
        } else {
            playerTurnTextView.text = "$player2Name's turn"
        }
    }


    private fun checkWinner(): IntArray? {
        val winningPositions = arrayOf(
            intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)
        )

        for (positions in winningPositions) {
            if (board[positions[0]] == board[positions[1]] &&
                board[positions[1]] == board[positions[2]] &&
                board[positions[0]] != null) {
                return positions
            }
        }
        return null
    }


    private fun toTitleCase(text: String): String {
        return text.split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}