package com.murach.hangtest

import android.app.ActivityOptions
import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MineActivity : AppCompatActivity() {

    var mineField: Array<Array<String>> = arrayOf(
        arrayOf(".", ".", ".", ".", ".", ".", ".", "."),
        arrayOf(".", ".", ".", ".", ".", ".", ".", "."),
        arrayOf(".", ".", ".", ".", ".", ".", ".", "."),
        arrayOf(".", ".", ".", ".", ".", ".", ".", "."),
        arrayOf(".", ".", ".", ".", ".", ".", ".", "."),
        arrayOf(".", ".", ".", ".", ".", ".", ".", "."),
        arrayOf(".", ".", ".", ".", ".", ".", ".", "."),
        arrayOf(".", ".", ".", ".", ".", ".", ".", "."),
    )

    var mineLocations: Array<Array<Boolean>> = arrayOf(
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
    )

    var markedLocations: Array<Array<Boolean>> = arrayOf(
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
    )

    var visitedLocations: Array<Array<Boolean>> = arrayOf(
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
        arrayOf(false, false, false, false, false, false, false, false),
    )

    var numbers: Array<Array<Int>> = arrayOf(
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
        arrayOf(0, 0, 0, 0, 0, 0, 0, 0))

    var minefieldSet = false
    var marker = false
    lateinit var btnMarker: Button
    lateinit var minesInput: EditText
    lateinit var soundFX: SoundPool
    var winSound: Int = 0
    var boomSound: Int = 0

    fun toggleMarker() {
        if (!marker) {
            btnMarker.text = "*"
            marker = true
        } else {
            btnMarker.text = ""
            marker = false
        }
    }

    fun checkForWin() {
        Log.d("XXX", "Checking for win")
        var won: Boolean = true
        for (i in 0 until 8) {
            //var resultMines: String = ""
            //var resultMarked: String = ""
            for (j in 0 until 8) {
                if (mineLocations[i][j] != markedLocations[i][j]
                    || visitedLocations[i][j] == mineLocations[i][j]) {
                    won = false
                    break
                }
                //resultMines += mineLocations[i][j].toString() + " "
                //resultMarked += markedLocations[i][j].toString() + " "
            }
            //Log.d("Mine Locations", resultMines)
            //Log.d("Marked Locations", resultMarked)
        }
        if (won) {
            var toast: Toast = Toast.makeText(this, "ALL MINES FOUND! YOU WIN", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            soundFX.play(winSound, 1F, 1F, 1, 0, .91875f)
            reset()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mine)

        soundFX = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        winSound = soundFX.load(this, R.raw.applause, 1)
        boomSound = soundFX.load(this, R.raw.boom, 1)

        val btnReset = findViewById<View>(R.id.button5) as Button
        btnMarker = findViewById<View>(R.id.button8) as Button
        minesInput = findViewById<View>(R.id.mines) as EditText
        minesInput.setText("8")

        btnReset.setOnClickListener {
            reset()
        }
        btnMarker.setOnClickListener {
            toggleMarker()
        }
        for (i in 1 until 65) {
            val tvName = "textView$i"
            val resourceID = resources.getIdentifier(
                tvName,
                "id",
                packageName
            )

            val tv = findViewById<View>(resourceID) as TextView
            tv.setBackgroundResource(R.drawable.border1)
            tv.setOnClickListener {
                onClicked(i)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.minesweeper, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var intent = Intent()
        var d = false
        when (item.itemId) {
            R.id.menu_simon -> intent = Intent(
                applicationContext,
                SimonSaysActivity::class.java
            )
            R.id.menu_c4 -> intent = Intent(applicationContext, ConnectFourActivity::class.java)
            R.id.menu_hang -> intent = Intent(applicationContext, HangmanActivity::class.java)
            R.id.menu_settings -> intent = Intent(applicationContext, SettingsActivity::class.java)
            R.id.menu_about -> intent = Intent(applicationContext, AboutActivity::class.java)
            else -> d = true
        }
        if (d) {
            return super.onOptionsItemSelected(item)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
        return true
    }

    fun reset() {
        minefieldSet = false
        for (i in 0 until 8) {
            for (j in 0 until 8) {
                mineField[i][j] = "."
                mineLocations[i][j] = false
                markedLocations[i][j] = false
                visitedLocations[i][j] = false
            }
        }
        for (i in 1 until 65) {
            val tvName = "textView$i"
            val resourceID = resources.getIdentifier(
                tvName,
                "id",
                packageName
            )
            val tv = findViewById<View>(resourceID) as TextView
            tv.text = ""
        }
        refreshField()
    }

    fun onClicked(boxNumber: Int) {
        // get x y coords for box... could be replaced with individual references
        val tempX: Int = boxNumber / 8
        val y: Int = if ((boxNumber % 8 - 1) != -1) {
            boxNumber % 8 - 1
        } else {
            7
        }
        val x: Int = if (y == 7) {
            tempX - 1
        } else {
            tempX
        }

        if (!minefieldSet) {
            setMines(minesInput.text.toString().toInt(), x, y)
            setNumbers()
            refreshField()
            minefieldSet = true
        }

        when {
            marker -> {
                markedLocations[x][y] = !markedLocations[x][y]
                if (!markedLocations[x][y]) {
                    val tvName = "textView$boxNumber"
                    val resourceID = resources.getIdentifier(
                        tvName,
                        "id",
                        packageName
                    )
                    val tv = findViewById<View>(resourceID) as TextView
                    tv.text = ""
                }
            }
            mineLocations[x][y] -> {
                mineField[x][y] = "X"
                var toast: Toast = Toast.makeText(this, "BOOM!!! YOU LOSE", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                soundFX.play(boomSound, 1F, 1F, 1, 0, .91875f)
                reset()
            }
            else -> {
                explore(x, y)
                Log.d("XXX", "Returned from Explore")
            }
        }

        refreshField()
        checkForWin()
    }

    fun refreshField() {
        for (i in 1 until 65) {
            val tvName = "textView$i"
            val resourceID = resources.getIdentifier(
                tvName,
                "id",
                packageName
            )
            val tempX: Int = i / 8
            val y: Int = if ((i % 8 - 1) != -1) {
                i % 8 - 1
            } else {
                7
            }
            val x: Int = if (y == 7) {
                tempX - 1
            } else {
                tempX
            }

            val tv = findViewById<View>(resourceID) as TextView
            if (mineField[x][y] == ".") {
                tv.setBackgroundResource(R.drawable.border1)
            } else {
                tv.setBackgroundResource(R.drawable.border)
                if (mineField[x][y] != "/") {
                    tv.text = mineField[x][y]
                }
            }

            if (markedLocations[x][y]) {
                tv.setBackgroundResource(R.drawable.border1)
                tv.setTextColor(resources.getColor(R.color.colorWhite))
                tv.text = "*"
            } else {
                if (tv.text == "*") {
                    Log.d("XXX", "Trying to remove * from marked square")
                    tv.text = if (numbers[x][y] > 0) {
                        numbers[x][y].toString()
                    } else {
                        ""
                    }
                }
                tv.setTextColor(resources.getColor(R.color.colorBlack))
            }
        }
    }

    fun setMines(mines: Int, startX: Int, startY: Int) {
        var numMines = mines
        var x: Int
        var y : Int
        do {
            for (i in 0 until numMines) {
                do {
                    x = Random.nextInt(0, 8)
                    y = Random.nextInt(0, 8)
                    // ensure first player choice is clear along with surrounding spaces
                } while (x == startX && y == startY
                    || (x == startX && y == startY - 1)
                    || (x == startX && y == startY + 1)
                    || (x == startX + 1 && y == startY)
                    || (x == startX + 1 && y == startY - 1)
                    || (x == startX + 1 && y == startY + 1)
                    || (x == startX - 1 && y == startY)
                    || (x == startX - 1 && y == startY - 1)
                    || (x == startX - 1 && y == startY + 1))

                if (!mineLocations[x][y]) {
                    numMines -= 1
                    mineLocations[x][y] = true
                }
            }
        } while (numMines != 0)
        //minefieldSet = true
    }

    fun setNumbers() {
        for (x in 0 until 8) {
            for (y in 0 until 8) {
                var count = 0
                // check around upper left corner
                if (x == 0 && y == 0 && !mineLocations[x][y]) {
                    if (mineLocations[x][y + 1])
                        count++
                    if (mineLocations[x + 1][y])
                        count++
                    if (mineLocations[x + 1][y + 1])
                        count++

                    // check around upper right corner
                } else if (x == 0 && y == 7 && !mineLocations[x][y]) {
                    if (mineLocations[x][y - 1])
                        count++
                    if (mineLocations[x + 1][y])
                        count++
                    if (mineLocations[x + 1][y - 1])
                        count++

                    // check around non-corner top row
                } else if (x == 0 && y != 0 && y != 7 && !mineLocations[x][y]) {
                    if (mineLocations[x][y - 1])
                        count++
                    if (mineLocations[x][y + 1])
                        count++
                    if (mineLocations[x + 1][y - 1])
                        count++
                    if (mineLocations[x + 1][y])
                        count++
                    if (mineLocations[x + 1][y + 1])
                        count++

                    // check around non-corner left side
                } else if (x != 0 && x != 7 && y == 0 && !mineLocations[x][y]) {
                    if (mineLocations[x - 1][y])
                        count++
                    if (mineLocations[x - 1][y + 1])
                        count++
                    if (mineLocations[x][y + 1])
                        count++
                    if (mineLocations[x + 1][y])
                        count++
                    if (mineLocations[x + 1][y + 1])
                        count++

                    // check around non-corner right side
                } else if (x != 0 && x != 7 && y == 7 && !mineLocations[x][y]) {
                    if (mineLocations[x - 1][y])
                        count++
                    if (mineLocations[x + 1][y])
                        count++
                    if (mineLocations[x - 1][y - 1])
                        count++
                    if (mineLocations[x][y - 1])
                        count++
                    if (mineLocations[x + 1][y - 1])
                        count++

                    // check around non-corner bottom row
                } else if (x == 7 && y != 0 && y != 7 && !mineLocations[x][y]) {
                    if (mineLocations[x][y - 1])
                        count++
                    if (mineLocations[x][y + 1])
                        count++
                    if (mineLocations[x - 1][y - 1])
                        count++
                    if (mineLocations[x - 1][y])
                        count++
                    if (mineLocations[x - 1][y + 1])
                        count++

                    // check around bottom left corner
                } else if (x == 7 && y == 0 && !mineLocations[x][y]) {
                    if (mineLocations[x - 1][y])
                        count++
                    if (mineLocations[x - 1][y + 1])
                        count++
                    if (mineLocations[x][y + 1])
                        count++

                    // check around bottom right corner
                } else if (x == 7 && y == 7 && !mineLocations[x][y]) {
                    if (mineLocations[x - 1][y])
                        count++
                    if (mineLocations[x - 1][y - 1])
                        count++
                    if (mineLocations[x][y - 1])
                        count++

                    // check all middle spaces
                } else if (x in 1..6 && y in 1..6 && !mineLocations[x][y]) {
                    if (mineLocations[x - 1][y - 1])
                        count++
                    if (mineLocations[x - 1][y])
                        count++
                    if (mineLocations[x - 1][y + 1])
                        count++
                    if (mineLocations[x][y - 1])
                        count++
                    if (mineLocations[x][y + 1])
                        count++
                    if (mineLocations[x + 1][y - 1])
                        count++
                    if (mineLocations[x + 1][y])
                        count++
                    if (mineLocations[x + 1][y + 1])
                        count++
                }

                numbers[x][y] = count
            }
        }
    }

    fun explore(x: Int, y: Int) {
        Log.d("XXX", "Explore called")

        if (mineField[x][y] == "." || mineField[x][y] == "*" || mineField[x][y] == "/") {
            visitedLocations[x][y] = true
            if (numbers[x][y] != 0) {
                mineField[x][y] = numbers[x][y].toString()
                return
            } else {
                mineField[x][y] = "/"
            }
        }

        //refreshField()  Maybe add a pause here?

        // upper left corner
        if (x == 0 && y == 0) {
            if (!mineLocations[x][y+1] && !mineLocations[x+1][y] && !mineLocations[x+1][y+1]) {
                if (mineField[x][y+1] == "." || mineField[x][y+1] == "*")
                    explore(x, y + 1)
                if (mineField[x+1][y] == "." || mineField[x+1][y] == "*")
                    explore(x + 1, y)
                if (mineField[x+1][y+1] == "." || mineField[x+1][y+1] == "*")
                    explore(x + 1, y + 1)
            }

            // top row
        } else if (x == 0 && y != 0 && y != 7) {
            if (!mineLocations[x][y - 1] && !mineLocations[x][y + 1] &&
                !mineLocations[x + 1][y - 1] && !mineLocations[x + 1][y] && !mineLocations[x + 1][y + 1]) {
                if (mineField[x][y-1] == "." || mineField[x][y-1] == "*")
                    explore(x, y - 1)
                if (mineField[x][y+1] == "." || mineField[x][y+1] == "*")
                    explore(x, y + 1)
                if (mineField[x+1][y-1] == "." || mineField[x+1][y-1] == "*")
                    explore(x + 1, y - 1)
                if (mineField[x+1][y] == "." || mineField[x+1][y] == "*")
                    explore(x + 1, y)
                if (mineField[x+1][y+1] == "." || mineField[x+1][y+1] == "*")
                    explore(x + 1, y + 1)
            }

            // upper right corner
        } else if (x == 0 && y == 7) {
            if (!mineLocations[x][y - 1] && !mineLocations[x + 1][y - 1] && !mineLocations[x + 1][y]) {
                if (mineField[x][y-1] == "." || mineField[x][y-1] == "*")
                    explore(x, y - 1)
                if (mineField[x+1][y-1] == "." || mineField[x+1][y-1] == "*")
                    explore(x + 1, y - 1)
                if (mineField[x+1][y] == "." || mineField[x+1][y] == "*")
                    explore(x + 1, y)
            }

            // left side
        } else if (x != 0 && x != 7 && y == 0) {
            if (!mineLocations[x - 1][y] && !mineLocations[x - 1][y + 1] && !mineLocations[x][y + 1] &&
                !mineLocations[x + 1][y + 1] && !mineLocations[x + 1][y]) {
                if (mineField[x-1][y] == "." || mineField[x-1][y] == "*")
                    explore(x - 1, y)
                if (mineField[x-1][y+1] == "." || mineField[x-1][y+1] == "*")
                    explore(x - 1, y + 1)
                if (mineField[x][y+1] == "." || mineField[x][y+1] == "*")
                    explore(x, y + 1)
                if (mineField[x+1][y+1] == "." || mineField[x+1][y+1] == "*")
                    explore(x + 1, y + 1)
                if (mineField[x+1][y] == "." || mineField[x+1][y] == "*")
                    explore(x + 1, y)
            }

            // right side
        } else if (x != 0 && x!= 7 && y == 7) {
            if (!mineLocations[x - 1][y] && !mineLocations[x - 1][y - 1] && !mineLocations[x][y - 1] &&
                !mineLocations[x + 1][y - 1] && !mineLocations[x + 1][y]) {
                if (mineField[x-1][y] == "." || mineField[x-1][y] == "*")
                    explore(x - 1, y)
                if (mineField[x-1][y-1] == "." || mineField[x-1][y-1] == "*")
                    explore(x - 1, y - 1)
                if (mineField[x][y-1] == "." || mineField[x][y-1] == "*")
                    explore(x, y - 1)
                if (mineField[x+1][y-1] == "." || mineField[x+1][y-1] == "*")
                    explore(x + 1, y - 1)
                if (mineField[x+1][y] == "." || mineField[x+1][y] == "*")
                    explore(x + 1, y)
            }

            // lower left corner
        } else if (x == 7 && y == 0) {
            if (!mineLocations[x - 1][y] && !mineLocations[x - 1][y + 1] && !mineLocations[x][y + 1]) {
                if (mineField[x-1][y] == "." || mineField[x-1][y] == "*")
                    explore(x - 1, y)
                if (mineField[x-1][y+1] == "." || mineField[x-1][y+1] == "*")
                    explore(x - 1, y + 1)
                if (mineField[x][y+1] == "." || mineField[x][y+1] == "*")
                    explore(x, y + 1)
            }

            // bottom row
        } else if (x == 7 && y != 0 && y != 7) {
            if (!mineLocations[x][y - 1] && !mineLocations[x][y + 1] &&
                !mineLocations[x - 1][y - 1] && !mineLocations[x - 1][y] && !mineLocations[x - 1][y + 1]) {
                if (mineField[x][y-1] == "." || mineField[x][y-1] == "*")
                    explore(x, y - 1)
                if (mineField[x][y+1] == "." || mineField[x][y+1] == "*")
                    explore(x, y + 1)
                if (mineField[x-1][y-1] == "." || mineField[x-1][y-1] == "*")
                    explore(x - 1, y - 1)
                if (mineField[x-1][y] == "." || mineField[x-1][y] == "*")
                    explore(x - 1, y)
                if (mineField[x-1][y+1] == "." || mineField[x-1][y+1] == "*")
                    explore(x - 1, y + 1)
            }

            // lower right corner
        } else if (x == 7 && y == 7) {
            if (!mineLocations[x - 1][y] && !mineLocations[x - 1][y - 1] && !mineLocations[x][y - 1]) {
                if (mineField[x-1][y] == "." || mineField[x-1][y] == "*")
                    explore(x - 1, y)
                if (mineField[x-1][y-1] == "." || mineField[x-1][y-1] == "*")
                    explore(x - 1, y - 1)
                if (mineField[x][y-1] == "." || mineField[x][y-1] == "*")
                    explore(x, y - 1)
            }

            // middle spaces
        } else {
            if (!mineLocations[x-1][y-1] && !mineLocations[x-1][y] && !mineLocations[x-1][y+1] &&
                !mineLocations[x][y-1] && !mineLocations[x][y+1] &&
                !mineLocations[x+1][y-1] && !mineLocations[x+1][y] && !mineLocations[x+1][y+1]) {
                if (mineField[x-1][y-1] == "." || mineField[x-1][y-1] == "*")
                    explore(x-1, y-1)
                if (mineField[x-1][y] == "." || mineField[x-1][y] == "*")
                    explore(x-1, y)
                if (mineField[x-1][y+1] == "." || mineField[x-1][y+1] == "*")
                    explore(x-1, y+1)
                if (mineField[x][y-1] == "." || mineField[x][y-1] == "*")
                    explore(x, y-1)
                if (mineField[x][y+1] == "." || mineField[x][y+1] == "*")
                    explore(x, y+1)
                if (mineField[x+1][y-1] == "." || mineField[x+1][y-1] == "*")
                    explore(x+1, y-1)
                if (mineField[x+1][y] == "." || mineField[x+1][y] == "*")
                    explore(x+1, y)
                if (mineField[x+1][y+1] == "." || mineField[x+1][y+1] == "*")
                    explore(x+1, y+1)
            }
        }
    }
}