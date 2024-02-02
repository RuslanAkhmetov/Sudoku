package com.example.sudoku

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.sudoku.view.CellView

class MainActivity : AppCompatActivity() {
    private val dim = 9
    val idArray = IntArray(dim * dim)
    val cs = ConstraintSet()
    lateinit var lp: ConstraintLayout.LayoutParams


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layout = findViewById<ConstraintLayout>(R.id.layout)
        val grid = findViewById<View>(R.id.grid)
        fillGameGrid(grid, layout)

        grid.setOnClickListener{
            val color1 = resources.getColor(android.R.color.holo_red_light)
            val color2 = resources.getColor(android.R.color.holo_blue_light)
            Log.i("TAG", "onCreate: setOnClickListener from MainActivity ")
            it.setBackgroundColor(color2)
        }

    }





    @RequiresApi(Build.VERSION_CODES.O)
    private fun fillGameGrid(gridView: View, parent: ConstraintLayout){
        var id = 0
        val color1 = resources.getColor(android.R.color.holo_red_light)
        lateinit var cell: CellView
            cs.clone(parent)
            cs.setDimensionRatio(gridView.id, dim.toString() + ":" + dim.toString())
            for (i in 0 until dim) {
                for (j in 0 until dim) {
                    cell = CellView(this)
                    id = View.generateViewId()
                    idArray[i * dim + j] = id
                    cell.tag = i * dim + j
                    cell.id = id
                    lp = ConstraintLayout.LayoutParams(
                        ConstraintSet.MATCH_CONSTRAINT,
                        ConstraintSet.MATCH_CONSTRAINT
                    )
           /*         cell.setOnClickListener{
                        Log.i("TAG", "fillGameGrid: clicked ${it.id}")
                        it.setBackgroundColor(color1)

                    }*/
                    parent.addView(cell, lp)
                }
            }
            for (iRow in 0 until dim) {
                for (iCol in 0 until dim) {
                    id = idArray[iRow * dim + iCol]
                    cs.setDimensionRatio(id, "1:1")
                    when (iRow) {
                        0 -> cs.connect(id, ConstraintSet.TOP, gridView.id   , ConstraintSet.TOP)
                        else -> cs.connect(
                            id, ConstraintSet.TOP,
                            idArray[(iRow - 1) * dim], ConstraintSet.BOTTOM
                        )
                    }

                    val chainArray = idArray.copyOfRange(iRow * dim, (iRow + 1) * dim)
                    cs.createHorizontalChain(
                        gridView.id, ConstraintSet.LEFT,
                        gridView.id, ConstraintSet.RIGHT,
                        chainArray, null,
                        ConstraintSet.CHAIN_PACKED
                    )
                }
            }

            cs.applyTo(parent)
    }


}