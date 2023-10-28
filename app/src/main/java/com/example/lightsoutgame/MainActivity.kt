package com.example.lightsoutgame

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import com.example.lightsoutgame.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {



    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.btnStart?.setOnClickListener {
            genPickSizeDialog()

        }
    }

    private fun genPickSizeDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.size_pick_dialog)
        val tvButtonPick3x3 :TextView = dialog.findViewById(R.id.tvButtonPick3x3)
        val tvButtonPick5x5 :TextView = dialog.findViewById(R.id.tvButtonPick5x5)
        val tvButtonPick9x9 :TextView = dialog.findViewById(R.id.tvButtonPick9x9)
        val clButtonPickCustom :ConstraintLayout = dialog.findViewById(R.id.clButtonPickCustom)
        val tietNumber: TextInputEditText = dialog.findViewById(R.id.tietNumber)


        tvButtonPick3x3.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra(BOARD_SIZE_KEY, 3)
            startActivity(intent) }

        tvButtonPick5x5.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra(BOARD_SIZE_KEY, 5)
            startActivity(intent) }

        tvButtonPick9x9.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra(BOARD_SIZE_KEY, 9)
            startActivity(intent)
        }

        clButtonPickCustom.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            var value: String = tietNumber.text.toString()
            if (value.isEmpty() || value.toInt() < 3){
                value = "3"
            }
            intent.putExtra(BOARD_SIZE_KEY, value.toInt())
            startActivity(intent)
        }

        tietNumber.doOnTextChanged { text, start, before, count ->
            if (count!=0){
                if (text.toString().toInt() > 100){
                    tietNumber.setText("100")
                }
                if (text.toString().toInt() < 3){
                    tietNumber.setText("3")
                }
                tietNumber.setSelection(tietNumber.text?.length!!)
            }
        }


        dialog.show()
    }

    companion object {
        val BOARD_SIZE_KEY: String? = "BSK"
    }
}