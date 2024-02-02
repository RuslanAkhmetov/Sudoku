package com.example.sudoku.Utils

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet


    fun makeGrid(dim:Int, idArray: IntArray, cs: ConstraintSet, space: View, parent: ConstraintLayout) {
        var id = 0
        for (iRow in 0 until dim) {
            for (iCol in 0 until dim) {
                id = idArray[iRow * dim + iCol]
                cs.setDimensionRatio(id, "1:1")
                when (iRow) {
                    0 -> cs.connect(id, ConstraintSet.TOP, space.id, ConstraintSet.TOP)
                    else -> cs.connect(
                        id,
                        ConstraintSet.TOP,
                        idArray[(iRow - 1) * dim + iCol],
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
                cs.applyTo(parent)
            }
        }
    }
