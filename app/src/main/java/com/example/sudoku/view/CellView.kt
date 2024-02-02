package com.example.sudoku.view

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import com.example.sudoku.R

@RequiresApi(Build.VERSION_CODES.O)
class CellView(context: Context) : ConstraintLayout(context) {
    companion object {
        const val dim = 3
    }

    val color1 = resources.getColor(android.R.color.holo_red_light)
    val color2 = resources.getColor(android.R.color.holo_blue_light)
    private var id = 0
    private var idArray = IntArray(dim * dim)
    private val cs = ConstraintSet()
    private lateinit var lp: ConstraintLayout.LayoutParams
    private lateinit var textView: TextView
    private val space = View(context)

    init {
        space.id = View.generateViewId()
        space.layoutParams = ViewGroup.LayoutParams(0,0 )
        space.background = ResourcesCompat.getDrawable(resources, R.drawable.cell_border, null)
        this.addView(space)
        cs.clone(this)
        cs.connect(space.id, ConstraintSet.TOP, this.id, ConstraintSet.TOP)
        cs.connect(space.id, ConstraintSet.LEFT, this.id, ConstraintSet.LEFT)
        cs.connect(space.id, ConstraintSet.RIGHT, this.id, ConstraintSet.RIGHT)
        cs.connect(space.id, ConstraintSet.BOTTOM, this.id, ConstraintSet.BOTTOM)
        cs.setDimensionRatio(space.id, String.format("%d:%d", dim, dim))
        //makeGrid()
        cs.applyTo(this)

    }

    private fun makeGrid() {
        for (i in 0 until dim) {
            for (j in 0 until dim) {
                textView = TextView(context)
                id = View.generateViewId()
                idArray[i * dim + j] = id
                textView.id = id
                textView.text = (i * dim + j + 1).toString()
                textView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
                lp = LayoutParams(ConstraintSet.MATCH_CONSTRAINT, ConstraintSet.MATCH_CONSTRAINT)
                this.addView(textView, lp)

            }
        }
        for (iRow in 0 until dim) {
            for (iCol in 0 until dim) {
                id = idArray[iRow * dim + iCol]
                cs.setDimensionRatio(id, "1:1")
                when (iRow) {
                    0 -> cs.connect(id, ConstraintSet.TOP, space.id, ConstraintSet.TOP)
                    else -> cs.connect(
                        id,
                        ConstraintSet.TOP,
                        idArray[(iRow - 1) * dim ],
                        ConstraintSet.BOTTOM
                    )
                }
                val chainArray = idArray.copyOfRange(iRow * dim, (iRow + 1) * dim)
                cs.createHorizontalChain(
                    space.id, ConstraintSet.LEFT,
                    space.id, ConstraintSet.RIGHT,
                    chainArray, null,
                    ConstraintSet.CHAIN_PACKED
                )

            }
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        this.setBackgroundColor(color1)
        Log.i("TAG", "setOnClickListener from cell: ")
        super.setOnClickListener(l)
    }
}