package com.neelkanth.hangman

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.neelkanth.hangman.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var guessesLeft = 10
    private var hintsLeft = 5
    private var wordLen = 0
    private var randomWord = ""
    private var randomWordIndex = 0
    private var guessedLetter = ""
    private var word: String = ""
    private var visibleLetterLen: Int = 0
    private var alreadyCheckedIndex = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Resize screen on keyboard appear
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // Reset game
        resetGame()
        // On Try button clicked
        binding.tryBtn.setOnClickListener {
            checkGame()
        }

        // On hint button clicked
        binding.hintTV.setOnClickListener {
            hintUse()
        }

        // On restart clicked
        binding.restartBtn.setOnClickListener {
            resetGame()
        }

    }

    @SuppressLint("SetTextI18n")
    fun resetGame() {
        hintsLeft = 5
        guessesLeft = 10
        randomWord = ""
        randomWordIndex = 0
        wordLen = 0
        guessedLetter = ""
        word = ""
        visibleLetterLen = 0
        binding.secretWordTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 46f)
        binding.tryBtn.visibility = View.VISIBLE
        binding.hangmanImg.setImageResource(R.drawable.hangman0)
        // Get random words from the list
        var wordsList = WordList().words
        randomWordIndex = Random.nextInt(wordsList.size - 1)

        randomWord = wordsList[randomWordIndex]
        wordLen = randomWord.length - 1
        visibleLetterLen = wordLen / 3
        // Setting (_) according word length
        for (i in 0..wordLen) {
            alreadyCheckedIndex.add(i, -1)
            word += "_"
        }

        // Giving some letters in the beginning for hint
        for (i in 0..visibleLetterLen) {
            var visibleLetterIndex = Random.nextInt(wordLen)
            var newWord = StringBuilder(word)
            alreadyCheckedIndex[visibleLetterIndex] = visibleLetterIndex
            newWord[visibleLetterIndex] = randomWord[visibleLetterIndex]
            word = StringBuilder(newWord).toString()

        }
        binding.secretWordTV.text = word
        binding.wordLenTV.text = "Word length: ${wordLen + 1}"
        binding.guessesLeftTV.text = "Guesses : $guessesLeft"
        binding.hintTV.text = "Hint: $hintsLeft"
        Log.d("word", randomWord)

    }


    private fun checkGame() {
        guessedLetter = binding.guessLetterET.text.toString()
        // Checking if user enter any letter or not
        if (guessedLetter.equals("")) {
            binding.guessLetterET.error = "Guess a letter please!"
        } else {
            for (i in 0..wordLen) {
                // Checking that guessed letter is equal to random word
                if (guessedLetter[0].equals(randomWord[i], ignoreCase = true) && guessesLeft >= 1) {

                    // Checking if guessed word is already present or not
                    if (alreadyCheckedIndex[i] != i) {
                        alreadyCheckedIndex[i] = i
                        var newWord = StringBuilder(word)
                        newWord.setCharAt(i, guessedLetter[0])
                        binding.secretWordTV.text = newWord
                        word = StringBuilder(newWord).toString()
                        binding.guessLetterET.setText("")
                        // Checking if user win or not
                        if (word.equals(randomWord, ignoreCase = true)) {
                            Toast.makeText(this, "You Win", Toast.LENGTH_SHORT).show()
                            val mp = MediaPlayer.create(this, R.raw.victory_sfx)
                            mp.start()
                            binding.hangmanImg.setImageResource(R.drawable.hangman_win)
                        }
                    }
                    // if guessed letter is already present
                    else if (alreadyCheckedIndex[i] == i) {
                        continue
                    } else {
                        Toast.makeText(this, "Already Guessed Fool!", Toast.LENGTH_SHORT).show()
                    }
                    break
                } else if (i == wordLen && !guessedLetter[0].equals(
                        randomWord[i],
                        ignoreCase = true
                    )
                ) {
                    Toast.makeText(this, "Wrong Guessed Fool!", Toast.LENGTH_SHORT).show()
                    guessesLeft -= 1
                    binding.guessesLeftTV.text = "Guesses : $guessesLeft"
                    binding.guessLetterET.setText("")
                    changeHangmanImg(guessesLeft)
                    // Checking the left guesses and if guesses are less than 1 and guessed word is not equal to real word
                    if (guessesLeft < 1) {
                        Toast.makeText(this, "You loose", Toast.LENGTH_SHORT).show()
                        val mp = MediaPlayer.create(this, R.raw.death_sfx)
                        mp.start()
                        binding.secretWordTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
                        binding.secretWordTV.text = "Word is ${randomWord}"
                        binding.tryBtn.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    // Setting word according to left guesses
    private fun changeHangmanImg(guessesLeft: Int) {
        when (guessesLeft) {
            10 -> binding.hangmanImg.setImageResource(R.drawable.hangman0)
            9 -> binding.hangmanImg.setImageResource(R.drawable.hangman1)
            8 -> binding.hangmanImg.setImageResource(R.drawable.hangman2)
            7 -> binding.hangmanImg.setImageResource(R.drawable.hangman3)
            6 -> binding.hangmanImg.setImageResource(R.drawable.hangman4)
            5 -> binding.hangmanImg.setImageResource(R.drawable.hangman5)
            4 -> binding.hangmanImg.setImageResource(R.drawable.hangman6)
            3 -> binding.hangmanImg.setImageResource(R.drawable.hangman7)
            2 -> binding.hangmanImg.setImageResource(R.drawable.hangman8)
            1 -> binding.hangmanImg.setImageResource(R.drawable.hangman9)
            0 -> binding.hangmanImg.setImageResource(R.drawable.hangman10)
        }
    }

    /*private fun hintUse() {
        if (hintsLeft < 1) {
            Toast.makeText(this, "You don't have hints", Toast.LENGTH_SHORT).show()
        }
        for (i in 0..wordLen) {
            var hintLetterIndex = Random.nextInt(wordLen)
            if (hintsLeft > 0) {
                if (alreadyCheckedIndex[hintLetterIndex] == hintLetterIndex){
                    continue
                }
                else {
                    var hintWord = StringBuilder(word)
                    hintWord[hintLetterIndex] = randomWord[hintLetterIndex]
                    alreadyCheckedIndex[hintLetterIndex] = hintLetterIndex
                    word = StringBuilder(hintWord).toString()
                    hintsLeft -= 1
                    binding.secretWordTV.text = word
                    binding.hintTV.text = "Hint: $hintsLeft"
                    Log.d("hint", "Loop $i and hint index $hintLetterIndex")
                    break
                }

            }

        }
    }*/

    private fun hintUse() {
        if (hintsLeft < 1) {
            Toast.makeText(this, "You don't have hints", Toast.LENGTH_SHORT).show()
            return
        } else {
            val hintLetterIndex = Random.nextInt(wordLen)
            if (alreadyCheckedIndex[hintLetterIndex] != hintLetterIndex) {
                val hintWord = StringBuilder(word)
                hintWord[hintLetterIndex] = randomWord[hintLetterIndex]
                alreadyCheckedIndex[hintLetterIndex] = hintLetterIndex
                word = hintWord.toString()
                hintsLeft -= 1
                binding.secretWordTV.text = word
                binding.hintTV.text = "Hint: $hintsLeft"

            }
        }
    }


}

