package com.example.guessgame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int MIN = 1;
    private static final int MAX = 100;
    private static final int MAX_ATTEMPTS = 7;

    private final Random random = new Random();
    private int secret;
    private int attempts;
    private boolean gameOver;

    private EditText guessInput;
    private TextView resultText;
    private Button guessButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        guessInput = findViewById(R.id.guessInput);
        resultText = findViewById(R.id.resultText);
        guessButton = findViewById(R.id.guessButton);
        Button restartButton = findViewById(R.id.restartButton);

        guessButton.setOnClickListener(v -> checkGuess());
        restartButton.setOnClickListener(v -> startGame());

        startGame();
    }

    private void startGame() {
        secret = random.nextInt(MAX - MIN + 1) + MIN;
        attempts = 0;
        gameOver = false;
        guessInput.setText("");
        guessButton.setEnabled(true);
        resultText.setText("You have " + MAX_ATTEMPTS + " attempts");
    }

    private void checkGuess() {
        if (gameOver) return;

        String text = guessInput.getText().toString().trim();
        if (text.isEmpty()) {
            resultText.setText("Please enter a number");
            return;
        }

        int guess = Integer.parseInt(text);
        attempts++;
        int left = MAX_ATTEMPTS - attempts;

        if (guess == secret) {
            resultText.setText("Correct! You won in " + attempts + " attempts");
            endGame();
        } else if (left == 0) {
            resultText.setText("Game over. The number was " + secret);
            endGame();
        } else if (guess < secret) {
            resultText.setText("Too low! Attempts left: " + left);
        } else {
            resultText.setText("Too high! Attempts left: " + left);
        }
        guessInput.setText("");
    }

    private void endGame() {
        gameOver = true;
        guessButton.setEnabled(false);
    }
}