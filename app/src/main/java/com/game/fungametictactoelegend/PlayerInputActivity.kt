package com.game.fungametictactoelegend

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PlayerInputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_input)

        val player1NameEditText = findViewById<EditText>(R.id.player1_name)
        val player2NameEditText = findViewById<EditText>(R.id.player2_name)
        val startGameButton = findViewById<Button>(R.id.start_button)

        // Radio buttons for game mode selection
        val radioVsFriend = findViewById<RadioButton>(R.id.radio_vs_friend)
        val radioVsComputer = findViewById<RadioButton>(R.id.radio_vs_computer)
        val gameModeGroup = findViewById<RadioGroup>(R.id.game_mode_group)

        // ImageViews for social media icons
        val helpIcon =findViewById<ImageView>(R.id.help_icon)

        // Hide second player's name initially (for vs computer mode)
        player1NameEditText.visibility = EditText.GONE
        player2NameEditText.visibility = EditText.GONE
        startGameButton.visibility = Button.GONE

        // Toggle visibility based on selected game mode
        gameModeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_vs_friend -> {
                    player1NameEditText.visibility = EditText.VISIBLE
                    player2NameEditText.visibility = EditText.VISIBLE
                    player2NameEditText.text.clear()
                    player2NameEditText.hint = getString(R.string.player_2_name)
                    startGameButton.visibility = Button.VISIBLE
                }
                R.id.radio_vs_computer -> {
                    player1NameEditText.visibility = EditText.VISIBLE
                    player2NameEditText.visibility = EditText.GONE
                    startGameButton.visibility = Button.VISIBLE
                }
            }
        }

        // Start game button listener
        startGameButton.setOnClickListener {
            val player1Name = player1NameEditText.text.toString()
            val player2Name = player2NameEditText.text.toString()
            val selectedGameModeId = gameModeGroup.checkedRadioButtonId

            // Check if any game mode is selected
            if (selectedGameModeId == -1) {
                Toast.makeText(this, "Please select a game mode", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate input: Ensure player1 name is provided
            if (player1Name.isEmpty() || player1Name.length > 10) {
                player1NameEditText.error = "Please enter Player 1's name (max 10 characters)"
                return@setOnClickListener
            }

            // If playing with a friend, validate Player 2's name
            if (radioVsFriend.isChecked && (player2Name.isEmpty() || player2Name.length > 10)) {
                player2NameEditText.error = "Please enter Player 2's name (max 10 characters)"
                return@setOnClickListener
            }


            // Proceed based on selected game mode
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("PLAYER_1_NAME", player1Name)
            intent.putExtra("PLAYER_2_NAME", if (radioVsComputer.isChecked) "CPU" else player2Name)
            intent.putExtra("GAME_MODE", if(radioVsComputer.isChecked) "vsComputer" else "vsFriend")
            startActivity(intent)
        }

        helpIcon.setOnClickListener{
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:helpassistant.tictactoegame@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Help with Tic-Tac-Toe Game")
            }
            startActivity(emailIntent)
        }
    }

}
