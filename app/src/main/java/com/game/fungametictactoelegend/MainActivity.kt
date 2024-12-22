package com.game.fungametictactoelegend


import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
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

    // initialize board and make move
    private fun initializeBoard() {
        for (i in buttons.indices) {
            buttons[i].setOnClickListener {
                if(isVsComputer){
                    if(isLevelSelected){
                        if (gameActive && board[i].isNullOrEmpty()) {
                            makeMove(buttons[i], i)
                            playClickSound(this) // to play the click sound after making moves
                        }
                    }else{
//                        showCustomToast(this,"Please select a difficulty level")
                        Toast.makeText(this, "Please select a difficulty level", Toast.LENGTH_SHORT).show()
                    }

                }else{
                    if (gameActive && board[i].isNullOrEmpty()) {
                        makeMove(buttons[i], i)
                        playClickSound((this))// To play the click sound after making moves
                    }
                }

            }
        }
    }

    // To make move when it turn
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
                showCustomToast(this,"Congratulation","$player1Name is the Winner!")
//                Toast.makeText(this, "$player1Name wins!", Toast.LENGTH_SHORT).show()
            } else {
                player2Score++
                player2ScoreTextView.text = player2Score.toString()
//                Toast.makeText(this, "$player2Name wins!", Toast.LENGTH_SHORT).show()
                showCustomToast(this,"Congratulation","$player2Name is the Winner!")
            }
        }
        // when match is draw
        else if (board.all { it != null }) {
            gameActive = false
            tiesScore++
            tiesScoreTextView.text = tiesScore.toString()
            showCustomToast(this,"Draw","Choose your move wisely")
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
//                    Toast.makeText(this, "comp click", Toast.LENGTH_SHORT).show()
                    buttons[bestMove].performClick() // Simulate button click for computer's move
                    enableButtons()
                }, 500)
            }

        }
    }

    // CPU move for easy level difficulty
    private fun findRandomMove(): Int {
        val emptyPositions = board.indices.filter { board[it].isNullOrEmpty() }
        return emptyPositions.random()
    }

    // CPU move for medium level difficulty
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

    // To disable the button while CPU is making it's move
    private fun disableButtons() {
        buttons.forEach { it.isEnabled = false }
    }

    // To enable the button after CPU makes it move
    private fun enableButtons() {
        buttons.forEachIndexed { index, button ->
            if (board[index].isNullOrEmpty()) button.isEnabled = true
        }
    }

    // To find the best move for min max algo from the remaining card of board
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

    // min max algorithm for CPU to play at its best level
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

    // before CPU makes it's move, to Evaluate the board and make it's move accordingly
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

    // To highlight winning button after won
    private fun highlightWinningButtons(winningPositions: IntArray) {
        for (position in winningPositions) {
            buttons[position].setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    // To restart the game
    fun restartGame(view: View) {
        for (i in buttons.indices) {
            buttons[i].text = ""
            buttons[i].setBackgroundResource(R.drawable.button_background) // Set background to drawable
            board[i] = null
        }
        currentPlayer = if (currentPlayer == "X") "O" else "X"
//        Toast.makeText(this, "after currentPlayer", Toast.LENGTH_SHORT).show()
        gameActive = true
//        Toast.makeText(this, "after gameActive", Toast.LENGTH_SHORT).show()
        updatePlayerTurn()
//        Toast.makeText(this, "after updatePlayerTurn", Toast.LENGTH_SHORT).show()
        enableButtons()
//        Toast.makeText(this, "after enabledButton", Toast.LENGTH_SHORT).show()

        if (!isVsComputer) {
            difficultySelector.visibility = RadioGroup.GONE
        }else{
            cpuClickAfterRestart()
        }
        // If you want user to select the level after restart
//        else {
//        levelEasy.isChecked = false
//        levelMedium.isChecked = false
//        levelHard.isChecked = false
//        difficultySelector.visibility = RadioGroup.VISIBLE
//            isLevelSelected = false
//        }

    }

    // Make CPU click after restart when it's turn
    private fun cpuClickAfterRestart(){
        if (isVsComputer && currentPlayer == "O") {
            disableButtons()
//            Toast.makeText(this, "cpuClARestart", Toast.LENGTH_SHORT).show()
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

    // To check if we found any winner or not
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

    // To convert string into title case to display user's name
    private fun toTitleCase(text: String): String {
        return text.split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }

    // To display custom toast message
    fun showCustomToast(context: Context,center:String, winner: String) {
        // Inflate the custom toast layout
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_toast, null)

        // Set the text messages
        val winnerMessage: TextView = layout.findViewById(R.id.toast_message)
        val subMessage: TextView = layout.findViewById(R.id.toast_sub_message)

        winnerMessage.text = center
        subMessage.text = winner

        // Create and show the toast
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }


    private fun playClickSound(context: Context) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.click_sound)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release() // Release the media player once the sound is completed
        }
    }

}