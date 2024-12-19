package com.game.fungametictactoelegend


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
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

    private var difficultyLevel: String = "Easy"
    private lateinit var difficultySelector: RadioGroup
    private lateinit var levelEasy: RadioButton
    private lateinit var levelMedium: RadioButton
    private lateinit var levelHard: RadioButton
    private var isLevelSelected = false

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

        val gameMode = intent.getStringExtra("GAME_MODE")
        difficultySelector = findViewById<RadioGroup>(R.id.levelSelector)
        levelEasy = findViewById<RadioButton>(R.id.levelEasy)
        levelMedium  = findViewById<RadioButton>(R.id.levelMedium)
        levelHard = findViewById<RadioButton>(R.id.levelHard)


        if(gameMode == "vsComputer"){
            isVsComputer = true
            difficultySelector.visibility = RadioGroup.VISIBLE
        }else{
            isVsComputer = false
            difficultySelector.visibility = RadioGroup.GONE
        }
        val player1TextView = findViewById<TextView>(R.id.textView3)
        val player2TextView = findViewById<TextView>(R.id.textView7)

        player1Name = toTitleCase(player1Name)
        player2Name = toTitleCase(player2Name)
        player1TextView.text = player1Name
        player2TextView.text = player2Name

        difficultySelector.setOnCheckedChangeListener { _, checkedId ->
            difficultyLevel = when (checkedId) {
                R.id.levelEasy -> {
                    levelMedium.isChecked = false
                    levelHard.isChecked = false
                    difficultySelector.visibility = RadioGroup.GONE
                    isLevelSelected = true
                    cpuClickAfterRestart()
                    "Easy"
                }
                R.id.levelMedium -> {
                    levelEasy.isChecked = false
                    levelHard.isChecked = false
                    isLevelSelected = true
                    difficultySelector.visibility = RadioGroup.GONE
                    cpuClickAfterRestart()
                    "Medium"
                }
                R.id.levelHard -> {
                    levelEasy.isChecked = false
                    levelMedium.isChecked = false
                    isLevelSelected = true
                    difficultySelector.visibility = RadioGroup.GONE
                    cpuClickAfterRestart()
                    "Hard"
                }
                else -> "Easy"
            }
        }


        updatePlayerTurn()
        initializeBoard()

    }

    private fun initializeBoard() {
        for (i in buttons.indices) {
            buttons[i].setOnClickListener {
                if(isVsComputer){
                    if(isLevelSelected){
                        if (gameActive && board[i].isNullOrEmpty()) {
                            makeMove(buttons[i], i)
                        }
                    }else{
                        Toast.makeText(this, "Please select a difficulty level", Toast.LENGTH_SHORT).show()
                    }

                }else{
                    if (gameActive && board[i].isNullOrEmpty()) {
                        makeMove(buttons[i], i)
                    }
                }

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun makeMove(button: Button, position: Int) {
        button.text = currentPlayer
        board[position] = currentPlayer

        val winningPositions = checkWinner()
        // If won the game
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
        }
        // when match is draw
        else if (board.all { it != null }) {
            gameActive = false
            tiesScore++
            tiesScoreTextView.text = tiesScore.toString()
            Toast.makeText(this, "It's a draw!", Toast.LENGTH_SHORT).show()
        }
        // when game is continue
        else {
            currentPlayer = if (currentPlayer == "X") "O" else "X"
            updatePlayerTurn()

            // If it's vs Computer and it's the computer's turn
//            if (isVsComputer && currentPlayer == "O") {
//                disableButtons()
//                buttons[position].postDelayed({
//                    val bestMove = findBestMove(board)
//                    buttons[bestMove].performClick() // Simulate button click for computer's move
//                    enableButtons()
//                }, 500)
//            }

            if (isVsComputer && currentPlayer == "O") {
                disableButtons()
                buttons[position].postDelayed({
                    val bestMove = when (difficultyLevel) {
                        "Easy" -> findRandomMove()
                        "Medium" -> findMediumMove()
                        "Hard" -> findBestMove(board)
                        else -> findBestMove(board)
                    }
                    buttons[bestMove].performClick() // Simulate button click for computer's move
                    enableButtons()
                }, 500)
            }

        }
    }

    private fun findRandomMove(): Int {
        val emptyPositions = board.indices.filter { board[it].isNullOrEmpty() }
        return emptyPositions.random()
    }

    private fun findMediumMove(): Int {
        // Check if AI can win in one move
        for (i in board.indices) {
            if (board[i].isNullOrEmpty()) {
                board[i] = "O"
                if (evaluateBoard() == 10) {
                    board[i] = null
                    return i
                }
                board[i] = null
            }
        }

        // Check if Player can win in one move, block it
        for (i in board.indices) {
            if (board[i].isNullOrEmpty()) {
                board[i] = "X"
                if (evaluateBoard() == -10) {
                    board[i] = null
                    return i
                }
                board[i] = null
            }
        }

        // Otherwise, make a random move
        return findRandomMove()
    }


    private fun disableButtons() {
        buttons.forEach { it.isEnabled = false }
    }

    private fun enableButtons() {
        buttons.forEachIndexed { index, button ->
            if (board[index].isNullOrEmpty()) button.isEnabled = true
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
            buttons[position].setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    fun restartGame(view: View) {
        for (i in buttons.indices) {
            buttons[i].text = ""
            buttons[i].setBackgroundResource(R.drawable.button_background) // Set background to drawable
            board[i] = null
        }
        currentPlayer = if (currentPlayer == "X") "O" else "X"

        gameActive = true
        updatePlayerTurn()
        enableButtons()

        if (!isVsComputer) {
            difficultySelector.visibility = RadioGroup.GONE
        }
        else {
        levelEasy.isChecked = false
        levelMedium.isChecked = false
        levelHard.isChecked = false
        difficultySelector.visibility = RadioGroup.VISIBLE
            isLevelSelected = false
            Toast.makeText(this, "selection prev", Toast.LENGTH_SHORT).show()
        }

//        cpuClickAfterRestart()


    }

    private fun cpuClickAfterRestart(){
        if (isVsComputer && currentPlayer == "O") {
            disableButtons()
            buttons[findBestMove(board)].postDelayed({
                buttons[findBestMove(board)].performClick()
                enableButtons()
            }, 500) // Delay the move by 1 second
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updatePlayerTurn() {
        if (currentPlayer == "X") {
            playerTurnTextView.text = "$player1Name's turn (X)"
            playerTurnTextView.setTextColor(ContextCompat.getColor(this, R.color.player1))
        } else {
            playerTurnTextView.text = "$player2Name's turn (O)"
            playerTurnTextView.setTextColor(ContextCompat.getColor(this, R.color.yellow))
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