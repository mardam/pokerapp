package de.markusdamm.pokerapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import kotlin.math.floor

class Chips : AppCompatActivity() {

    private lateinit var playerCount: TextView
    private lateinit var tvReds: TextView
    private lateinit var tvBlues: TextView
    private lateinit var tvGreens: TextView
    private lateinit var tvWhites: TextView
    private lateinit var tvBlacks: TextView
    private lateinit var tvChipCount: TextView

    private val blacks = 8 * 9 + 4
    private val greens = 8 * 9 + 7
    private val blues = 11 * 9 + 5
    private val reds = 11 * 9 + 4
    private val whites = 16 * 9 + 6

    private var seekBarChangeListener: SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            // updated continuously as the user slides the thumb
            updateTextLabels(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            // called when the user first touches the SeekBar
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            // called after the user finishes moving the SeekBar
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chips)

        val seekBar = findViewById<View>(R.id.sbPlayerCount) as SeekBar
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener)

        val progress = seekBar.progress
        playerCount = findViewById<View>(R.id.tvPlayerCount) as TextView

        tvReds = findViewById<View>(R.id.countRed) as TextView
        tvBlues = findViewById<View>(R.id.countBlue) as TextView
        tvGreens = findViewById<View>(R.id.countGreen) as TextView
        tvWhites = findViewById<View>(R.id.countWhite) as TextView
        tvBlacks = findViewById<View>(R.id.countBlack) as TextView
        tvChipCount = findViewById<View>(R.id.chipCount) as TextView

        updateTextLabels(progress)
    }


    private fun updateTextLabels(progressParam: Int) {
        var progress = progressParam
        progress += 2
        val redCount = floor(reds.toDouble() / progress).toInt()
        val blueCount = floor(blues.toDouble() / progress).toInt()
        val greenCount = floor(greens.toDouble() / progress).toInt()
        val whiteCount = floor(whites.toDouble() / progress).toInt()
        val blackCount = floor(blacks.toDouble() / progress).toInt()

        val chipCount = redCount * 5 + blueCount * 10 + greenCount * 25 + whiteCount * 50 + blackCount * 100
        val totalCount = chipCount * progress

        tvReds.text = redCount.toString()
        tvBlues.text = blueCount.toString()
        tvGreens.text = greenCount.toString()
        tvWhites.text = whiteCount.toString()
        tvBlacks.text = blackCount.toString()
        val tvChipText = "Gesamtwert an Chips pro Spieler: " + String.format("%,d", chipCount) + "\n" + "Wert an Chips im Spiel: " + String.format("%,d", totalCount)
        tvChipCount.text = tvChipText
        val plCountText = "Anzahl an Teilnehmern: $progress"
        playerCount.text = plCountText
    }

}
