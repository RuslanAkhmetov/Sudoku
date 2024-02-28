package com.example.sudoku.view

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.sudoku.R

@RequiresApi(Build.VERSION_CODES.O)
class CellView(context: Context) : ConstraintLayout(context) {
    companion object {
        const val dim = 3

    }

    private var id = 0
    private var idAsumptionArray = IntArray(dim * dim)
    private val cs = ConstraintSet()
    private lateinit var lp: ConstraintLayout.LayoutParams
    private lateinit var textView: TextView
    private lateinit var mainDigitTextView: TextView
    private val space = View(context)

    init {
        space.id = View.generateViewId()
        space.layoutParams = ViewGroup.LayoutParams(0, 0)
        space.background = ResourcesCompat.getDrawable(resources, R.drawable.cell_border, null)
        this.addView(space)
        cs.clone(this)
        cs.connect(space.id, ConstraintSet.TOP, this.id, ConstraintSet.TOP)
        cs.connect(space.id, ConstraintSet.LEFT, this.id, ConstraintSet.LEFT)
        cs.connect(space.id, ConstraintSet.RIGHT, this.id, ConstraintSet.RIGHT)
        cs.connect(space.id, ConstraintSet.BOTTOM, this.id, ConstraintSet.BOTTOM)
        cs.setDimensionRatio(space.id, String.format("%d:%d", dim, dim))
        mainDigitTextView = TextView(context)
        mainDigitTextView.layoutParams = ViewGroup.LayoutParams(0, 0)
        mainDigitTextView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        mainDigitTextView.id = View.generateViewId()
        mainDigitTextView.gravity = Gravity.CENTER_HORIZONTAL
        mainDigitTextView.visibility = View.GONE
        this.addView(mainDigitTextView)
        cs.connect(mainDigitTextView.id, ConstraintSet.TOP, space.id, ConstraintSet.TOP)
        cs.connect(mainDigitTextView.id, ConstraintSet.LEFT, space.id, ConstraintSet.LEFT)
        cs.connect(mainDigitTextView.id, ConstraintSet.RIGHT, space.id, ConstraintSet.RIGHT)
        cs.connect(mainDigitTextView.id, ConstraintSet.BOTTOM, space.id, ConstraintSet.BOTTOM)
        makeGrid()
        cs.applyTo(this)

    }

    fun fillMain(mainValue: Int, color: Int) {
        mainDigitTextView.visibility = View.VISIBLE
        mainDigitTextView.setTextColor(color)
        if (mainValue in 1..9) {
            mainDigitTextView.text = mainValue.toString()
        } else {
            mainDigitTextView.text = ""
        }
        hideAssumptions()
    }

    fun setAssumption(digit: Int, visible: Boolean) {
        if ((getMainDigitTextValue() == 0) and (digit in 1..9)) {
            findViewById<TextView>(idAsumptionArray[digit - 1]).visibility =
                if (visible) View.VISIBLE
                else View.INVISIBLE
        }
    }

    fun setAssumption(digit: Int) {
        if ((getMainDigitTextValue() == 0) and (digit in 1..9))  {
            val visible = findViewById<TextView>(idAsumptionArray[digit - 1]).isVisible
            findViewById<TextView>(idAsumptionArray[digit - 1]).visibility =
                if (visible) View.INVISIBLE
                else View.VISIBLE
        }
    }

    fun hideAssumptions() {
        idAsumptionArray.forEach {
            findViewById<TextView>(it).visibility = View.INVISIBLE
        }
    }

    fun getMainDigitTextValue() =
        if (mainDigitTextView.text.isEmpty()) 0
        else Character.getNumericValue(mainDigitTextView.text.first())


    private fun makeGrid() {
        for (i in 0 until dim) {
            for (j in 0 until dim) {
                textView = TextView(context)
                id = View.generateViewId()
                idAsumptionArray[i * dim + j] = id
                textView.id = id
                textView.text = (i * dim + j + 1).toString()
                textView.setAutoSizeTextTypeUniformWithConfiguration(6,20,
                    1,TypedValue.COMPLEX_UNIT_SP)
                lp = LayoutParams(ConstraintSet.MATCH_CONSTRAINT, ConstraintSet.MATCH_CONSTRAINT)
                this.addView(textView, lp)

            }
        }
        for (iRow in 0 until dim) {
            for (iCol in 0 until dim) {
                id = idAsumptionArray[iRow * dim + iCol]
                cs.setDimensionRatio(id, "1:1")
                cs.setVisibility(id, View.INVISIBLE)
                when (iRow) {
                    0 -> cs.connect(id, ConstraintSet.TOP, space.id, ConstraintSet.TOP)
                    else -> cs.connect(
                        id, ConstraintSet.TOP,
                        idAsumptionArray[(iRow - 1) * dim], ConstraintSet.BOTTOM
                    )
                }
            }

            val chainArray = idAsumptionArray.copyOfRange(iRow * dim, (iRow + 1) * dim)
            cs.createHorizontalChain(
                space.id, ConstraintSet.LEFT,
                space.id, ConstraintSet.RIGHT,
                chainArray, null,
                ConstraintSet.CHAIN_PACKED
            )
        }

    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
    }
}


