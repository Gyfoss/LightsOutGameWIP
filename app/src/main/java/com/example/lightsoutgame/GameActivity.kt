package com.example.lightsoutgame

import android.app.ActionBar.LayoutParams
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.lightsoutgame.databinding.ActivityGameBinding
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private var binding: ActivityGameBinding? = null
    private var board: ArrayList<ArrayList<Boolean>> = ArrayList()
    private var squares: ArrayList<ArrayList<TextView>> = ArrayList()
    private var boardSize = 0
    private var boardWidth = 0
    private var dX = 0.0f
    private var dY = 0.0f
    private var oldD = 0.0
    private var scale = 1.0f
    private var a = 0
    private var scaleMultiplier = 0.0
    private var isScalableEnabled = true
    private var isMovingEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (!intent.extras!!.isEmpty){
            boardSize = intent.getIntExtra(MainActivity.BOARD_SIZE_KEY, 3)
        }
        binding?.swMoving?.isChecked = !isMovingEnabled
        binding?.swZoom?.isChecked = !isMovingEnabled



        for (i in 0 until boardSize){
            var temp: ArrayList<Boolean> = ArrayList()
            for (j in 0 until boardSize){
                temp.add(false)
            }
            board.add(temp)
        }


        val change  =  {x:Int, y:Int ->
            board[x][y] = !board[x][y]
        }

        for (i in 0 until board.size){
            for (j in 0 until board.size){
                if (Random.nextInt(boardSize*boardSize)%2 == 1) {
                    change(i, j)
                    if (i-1 >=0) {
                        change(i-1, j)
                    }
                    if (j-1 >=0) {
                        change(i, j-1)
                    }
                    if (i+1 < boardSize) {
                        change(i+1, j)
                    }
                    if (j+1 < boardSize) {
                        change(i, j+1)
                    }
                }
            }
        }


        boardWidth = Resources.getSystem().displayMetrics.widthPixels -3 - (50*3*2)

        a = (boardWidth/boardSize)


        for (i in 0 until boardSize){
            binding?.llBoard?.addView(genRow(i))
        }


        binding?.swMoving?.setOnCheckedChangeListener { _, isChecked ->
            isMovingEnabled = !isChecked
        }

        binding?.swZoom?.setOnCheckedChangeListener { _, isChecked ->
            isScalableEnabled = !isChecked
        }
    }

    private fun genRow(i: Int): View? {
        var temp: ArrayList<TextView> = ArrayList()
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        row.layoutParams = params

        for (j in 0 until boardSize){
            temp.add(TextView(this))
            val params = ViewGroup.MarginLayoutParams(a, a);
            params.setMargins(a/5, a/5, a/5, a/5)
            temp[j].layoutParams = params
            if (board[i][j]){
                temp[j].setBackgroundColor(Color.CYAN)
            } else {
                temp[j].setBackgroundColor(Color.BLACK)
            }
            temp[j].text = "${i}_${j}"
            temp[j].setTextColor(Color.TRANSPARENT)
            row.addView(temp[j])
            temp[j].setOnClickListener {
                val text = temp[j].text
                val (a, b) = text.split("_")

                val check  =  {x:Int, y:Int ->
                    board[x][y] = !board[x][y]
                    if (board[x][y]){
                        squares[x][y].setBackgroundColor(Color.CYAN)
                    } else {
                        squares[x][y].setBackgroundColor(Color.BLACK)
                    }
                }

                check(a.toInt(), b.toInt())
                if (a.toInt()-1 >=0) {
                    check(a.toInt()-1, b.toInt())
                }
                if (b.toInt()-1 >=0) {
                    check(a.toInt(), b.toInt()-1)
                }
                if (a.toInt()+1 < boardSize) {
                    check(a.toInt()+1, b.toInt())
                }
                if (b.toInt()+1 < boardSize) {
                    check(a.toInt(), b.toInt()+1)
                }

                var ifHaveTrue = ifContainsTrue()


                if (!ifHaveTrue!!) {
                    Toast.makeText(this@GameActivity, "Koniec Gry!", Toast.LENGTH_SHORT).show()
                }

            }
        }
        squares.add(temp)
        return row
    }

    fun ifContainsTrue(): Boolean? {
        var ifHaveTrue: Boolean? = null
        for(i in board) {
            if (i.contains(true)) {
                ifHaveTrue = true
                break
            } else {
                ifHaveTrue = false
            }
        }

        return ifHaveTrue
    }

    fun coutTrues(): Int {
        var trueCount: Int = 0
        for(i in board) {
            if (i.contains(true)) {
                trueCount++
                break
            }
        }
        return trueCount
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.getAction()) {

                ACTION_DOWN -> {

                    if (event.pointerCount==1 && isMovingEnabled) {
                        dX = binding?.llBoard?.x?.minus(event.getRawX()) ?: 0f;
                        dY = binding?.llBoard?.y?.minus(event.getRawY()) ?: 0f;
                    }

                    if (event.pointerCount == 2 && isScalableEnabled) {
                        val p1x = event.getX(0).toDouble()
                        val p1y = event.getY(0).toDouble()
                        val p2x = event.getX(1).toDouble()
                        val p2y = event.getY(1).toDouble()

                        Log.d("MY_TAG", "x1: " + p1x + ", y1: " + p1y + "\nx2: " + p2x + ", y2: " + p2y)
                        oldD = Math.sqrt(Math.pow(p2x-p1x, 2.0) + Math.pow(p2y-p1y, 2.0))
                    }
                }

                ACTION_MOVE -> {
                    if (event.pointerCount == 1 && isMovingEnabled){
                        binding?.llBoard?.animate()
                            ?.x(event.getRawX() + dX)
                            ?.y(event.getRawY() + dY)
                            ?.setDuration(0)
                            ?.start()
                    }

                    if (event.pointerCount == 2 && isScalableEnabled) {
                        val p1x = event.getX(0).toDouble()
                        val p1y = event.getY(0).toDouble()
                        val p2x = event.getX(1).toDouble()
                        val p2y = event.getY(1).toDouble()

                        val d = Math.sqrt(Math.pow(p2x-p1x, 2.0) + Math.pow(p2y-p1y, 2.0))

                        if (d > oldD){
                            if (scale < 3.0f * ((4*boardSize)/ boardSize)) {
                                scale += 0.06f
                            }
                            oldD = d
                        }
                        if (d < oldD){
                            if(scale > 0.1f * ((4*boardSize)/ boardSize)) {
                                scale -= 0.06f
                            }
                            oldD = d
                        }

                        binding?.llBoard?.scaleX = scale
                        binding?.llBoard?.scaleY = scale
                    }
                }

                else -> {
                    return false
                }
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}