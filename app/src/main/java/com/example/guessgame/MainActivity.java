package com.example.guessgame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String[] LEVEL_NAMES = {"Easy", "Medium", "Hard", "Expert"};
    private static final int[] LEVEL_MAX = {50, 100, 500, 1000};
    private static final int[] LEVEL_ATTEMPTS = {10, 7, 9, 12};
    private static final int[] LEVEL_POINTS = {1, 2, 3, 5};

    private static final String[] QUESTIONS = {
            "What comes next?\n2, 4, 8, 16, ?",
            "I have keys but no locks, and space but no room. What am I?",
            "What is 15 x 4 - 10?",
            "What comes next?\n1, 1, 2, 3, 5, 8, ?",
            "The more you take, the more you leave behind. What am I?",
            "I have hands but cannot clap. What am I?"
    };
    private static final String[] ANSWERS = {
            "32", "keyboard", "50", "13", "footsteps|footstep|steps|step", "clock"
    };

    private final Random random = new Random();
    private SharedPreferences prefs;

    private View homeScreen, levelScreen, gameScreen, puzzleScreen;
    private TextView bestText, levelLabel, titleText, resultText;
    private TextView puzzleQuestion, puzzleResult;
    private EditText guessInput, puzzleInput;
    private Button guessButton, puzzleAnswerButton, puzzleSkipButton;

    private int level, secret, attempts;
    private boolean gameOver;
    private int puzzleIndex, puzzleScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("mindmaze", MODE_PRIVATE);

        homeScreen = findViewById(R.id.homeScreen);
        levelScreen = findViewById(R.id.levelScreen);
        gameScreen = findViewById(R.id.gameScreen);
        puzzleScreen = findViewById(R.id.puzzleScreen);
        bestText = findViewById(R.id.bestText);
        levelLabel = findViewById(R.id.levelLabel);
        titleText = findViewById(R.id.titleText);
        resultText = findViewById(R.id.resultText);
        guessInput = findViewById(R.id.guessInput);
        guessButton = findViewById(R.id.guessButton);
        puzzleQuestion = findViewById(R.id.puzzleQuestion);
        puzzleResult = findViewById(R.id.puzzleResult);
        puzzleInput = findViewById(R.id.puzzleInput);
        puzzleAnswerButton = findViewById(R.id.puzzleAnswerButton);
        puzzleSkipButton = findViewById(R.id.puzzleSkipButton);

        click(R.id.playButton, () -> showScreen(levelScreen));
        click(R.id.puzzleButton, this::startPuzzles);
        click(R.id.levelBackButton, this::showHome);
        click(R.id.easyButton, () -> startGame(0));
        click(R.id.mediumButton, () -> startGame(1));
        click(R.id.hardButton, () -> startGame(2));
        click(R.id.expertButton, () -> startGame(3));
        click(R.id.guessButton, this::checkGuess);
        click(R.id.restartButton, () -> startGame(level));
        click(R.id.gameMenuButton, this::showHome);
        click(R.id.puzzleAnswerButton, this::checkPuzzle);
        click(R.id.puzzleSkipButton, () -> {
            puzzleIndex++;
            showQuestion();
        });
        click(R.id.puzzleBackButton, this::showHome);

        showHome();
    }

    private void click(int id, Runnable action) {
        findViewById(id).setOnClickListener(v -> action.run());
    }

    private void showScreen(View screen) {
        homeScreen.setVisibility(View.GONE);
        levelScreen.setVisibility(View.GONE);
        gameScreen.setVisibility(View.GONE);
        puzzleScreen.setVisibility(View.GONE);
        screen.setVisibility(View.VISIBLE);
    }

    private void showHome() {
        bestText.setText("Best score: " + prefs.getInt("best", 0));
        showScreen(homeScreen);
    }

    private void startGame(int newLevel) {
        level = newLevel;
        secret = random.nextInt(LEVEL_MAX[level]) + 1;
        attempts = 0;
        gameOver = false;
        guessInput.setText("");
        guessButton.setEnabled(true);
        levelLabel.setText(LEVEL_NAMES[level]);
        titleText.setText("Guess a number (1-" + LEVEL_MAX[level] + ")");
        resultText.setText("You have " + LEVEL_ATTEMPTS[level] + " attempts");
        showScreen(gameScreen);
    }

    private void checkGuess() {
        if (gameOver) return;

        String text = guessInput.getText().toString().trim();
        int max = LEVEL_MAX[level];
        int guess;
        try {
            guess = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            resultText.setText("Enter a number between 1 and " + max);
            return;
        }
        if (guess < 1 || guess > max) {
            resultText.setText("Enter a number between 1 and " + max);
            return;
        }

        attempts++;
        int left = LEVEL_ATTEMPTS[level] - attempts;
        guessInput.setText("");

        if (guess == secret) {
            int points = (left + 1) * LEVEL_POINTS[level] * 10;
            if (points > prefs.getInt("best", 0)) {
                prefs.edit().putInt("best", points).apply();
            }
            resultText.setText("Correct! You win +" + points + " points");
            endGame();
        } else if (left == 0) {
            resultText.setText("Game over. The number was " + secret);
            endGame();
        } else {
            StringBuilder msg = new StringBuilder();
            if (level <= 2) {
                msg.append(guess < secret ? "Too low! " : "Too high! ");
            }
            if (level >= 2) {
                int diff = Math.abs(guess - secret);
                if (diff <= max / 20) {
                    msg.append("Burning hot! ");
                } else if (diff <= max / 8) {
                    msg.append("Warm. ");
                } else {
                    msg.append("Cold. ");
                }
            }
            msg.append("Attempts left: ").append(left);
            resultText.setText(msg.toString());
        }
    }

    private void endGame() {
        gameOver = true;
        guessButton.setEnabled(false);
    }

    private void startPuzzles() {
        puzzleIndex = 0;
        puzzleScore = 0;
        showQuestion();
        showScreen(puzzleScreen);
    }

    private void showQuestion() {
        puzzleInput.setText("");
        puzzleResult.setText("");
        if (puzzleIndex >= QUESTIONS.length) {
            puzzleQuestion.setText("All done! Score: " + puzzleScore + "/" + QUESTIONS.length);
            puzzleAnswerButton.setEnabled(false);
            puzzleSkipButton.setEnabled(false);
        } else {
            puzzleQuestion.setText("Puzzle " + (puzzleIndex + 1) + "/" + QUESTIONS.length
                    + "\n\n" + QUESTIONS[puzzleIndex]);
            puzzleAnswerButton.setEnabled(true);
            puzzleSkipButton.setEnabled(true);
        }
    }

    private void checkPuzzle() {
        if (puzzleIndex >= QUESTIONS.length) return;
        String answer = puzzleInput.getText().toString().trim().toLowerCase();
        for (String option : ANSWERS[puzzleIndex].split("\\|")) {
            if (answer.equals(option)) {
                puzzleScore++;
                puzzleIndex++;
                showQuestion();
                puzzleResult.setText("Correct!");
                return;
            }
        }
        puzzleResult.setText("Wrong, try again");
    }
}