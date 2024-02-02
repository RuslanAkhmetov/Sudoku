package com.example.sudoku.view

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import com.example.sudoku.R

@RequiresApi(Build.VERSION_CODES.O)
class GameView(context: Context, val parent: ConstraintLayout) : View(context) {
    companion object {
        const val dim = 9
    }

    val color1 = resources.getColor(android.R.color.holo_red_light)
    val color2 = resources.getColor(android.R.color.holo_blue_light)
    private var id = 0
    private val idArray = IntArray(dim * dim)
    private val cs = ConstraintSet()
    private lateinit var lp: ConstraintLayout.LayoutParams
    private lateinit var cell: CellView


    init {
       // this.id = View.generateViewId()
        this.layoutParams = ViewGroup.LayoutParams(0,0)
        this.background = ResourcesCompat.getDrawable(resources, R.drawable.cell_border, null)
        parent.addView(this)
        Log.i("TAG", "thisId: ${this.id}")
        Log.i("TAG", "childrens: ${parent.childCount}")
        for (child in parent.children){
            child.id = View.generateViewId()
            Log.i("TAG", "childID: ${child.id} ${this.id}")
        }
        cs.clone(parent)
        cs.connect(this.id, ConstraintSet.TOP, parent.id, ConstraintSet.TOP)
        cs.connect(this.id, ConstraintSet.LEFT, parent.id, ConstraintSet.LEFT)
        cs.connect(this.id, ConstraintSet.BOTTOM, parent.id, ConstraintSet.BOTTOM)
        cs.connect(this.id, ConstraintSet.RIGHT, parent.id, ConstraintSet.RIGHT)
        cs.setDimensionRatio(this.id, dim.toString() + ":" + dim.toString())
        for (i in 0 until dim) {
            for (j in 0 until dim) {
                cell = CellView(context)
                id = View.generateViewId()
                idArray[i * dim + j] = id
                cell.id = id
                lp = ConstraintLayout.LayoutParams(
                    ConstraintSet.MATCH_CONSTRAINT,
                    ConstraintSet.MATCH_CONSTRAINT
                )
                // cell.setBackgroundColor(if ((i*dim+j) % 2 == 0) color1 else color2)
                parent.addView(cell, lp)
            }
        }
        for (iRow in 0 until dim) {
            for (iCol in 0 until dim) {
                id = idArray[iRow * dim + iCol]
                cs.setDimensionRatio(id, "1:1")
                when (iRow) {
                    0 -> cs.connect(id, ConstraintSet.TOP, this.id, ConstraintSet.TOP)
                    else -> cs.connect(
                        id, ConstraintSet.TOP,
                        idArray[(iRow - 1) * dim], ConstraintSet.BOTTOM
                    )
                }

                val chainArray = idArray.copyOfRange(iRow * dim, (iRow + 1) * dim)
                cs.createHorizontalChain(
                    this.id, ConstraintSet.LEFT,
                    this.id, ConstraintSet.RIGHT,
                    chainArray, null,
                    ConstraintSet.CHAIN_PACKED
                )
            }
        }
        cs.applyTo(parent)
    }


}