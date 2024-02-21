package com.example.sudoku

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.example.sudoku.view.CellView
import com.example.sudoku.viewModel.MainViewModel
import com.example.sudoku.core.LEVEL
import com.example.sudoku.core.MODE

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val dim = 9
    private val cellsIdArray = IntArray(dim * dim)
    private val buttonsIdArray = IntArray(dim)
    private val cs = ConstraintSet()
    private lateinit var lp: ConstraintLayout.LayoutParams
    private var colorbackground = 0
    private var colorButtonClicked = 0
    private var colorClue = 0
    private var colorWrong = 0
    private var colorCorrect = 0
    private var activeCellId = 0
    private var mode = MODE.EDIT
    private var errorsValue = 0
    private lateinit var  errorsTextView:TextView
    private val activityViewModel: MainViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        colorbackground = ContextCompat.getColor(this, android.R.color.background_light)
        colorButtonClicked = ContextCompat.getColor(this, android.R.color.system_accent1_100)
        colorClue = ContextCompat.getColor(this, android.R.color.holo_green_dark)
        colorWrong = ContextCompat.getColor(this, android.R.color.holo_red_dark)
        colorCorrect = ContextCompat.getColor(this, android.R.color.black)
        val layout = findViewById<ConstraintLayout>(R.id.layout)
        val grid = findViewById<View>(R.id.grid)
        val digits = findViewById<View>(R.id.digits)
        val editButton = findViewById<ImageView>(R.id.edit_button)
        val clueButton = findViewById<ImageView>(R.id.clue_button)
        val clearButton = findViewById<ImageView>(R.id.clear_button)

        clearButton.setOnClickListener {
            val activeCell = findViewById<CellView>(activeCellId)
            activeCell.fillMain( 0 ,colorWrong)
        }

        errorsTextView = findViewById<TextView>(R.id.error_value)
        errorsTextView.text = errorsValue.toString()

        editButton.setOnClickListener {
            if (mode == MODE.EDIT){
                it.setBackgroundColor(colorButtonClicked)
                mode = MODE.ASSUMPTION}
            else {
                it.setBackgroundColor(colorbackground)
                mode = MODE.EDIT}
        }

        clueButton.setOnClickListener {
            val activeCell = findViewById<CellView>(activeCellId)
            val activeCellIndex = cellsIdArray.indexOf(activeCellId)
            val digit = activityViewModel.getClue(activeCellIndex)
            if (activeCell.getMainDigitTextValue() == 0) {
                activeCell.fillMain(digit, colorClue)
                hideAssumption(digit, activeCellIndex)
            }
        }

        initDigits(digits, layout)

        fillGameGrid(grid, layout, activityViewModel.getStartMatrix(LEVEL.EASY))

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initDigits(container: View, parent: ConstraintLayout) {
        var id: Int
        lateinit var button: Button
        val color1 = resources.getColor(android.R.color.holo_blue_light)
        val cs = ConstraintSet()
        cs.clone(parent)
        for (i in 0 until dim) {
            button = Button(this)
            id = View.generateViewId()
            button.id = id
            buttonsIdArray[i] = id
            button.text = (i + 1).toString()
            button.setBackgroundColor(color1)
            button.setOnClickListener { it -> digitButtonClick(it as Button) }

            lp = ConstraintLayout.LayoutParams(
                ConstraintSet.MATCH_CONSTRAINT, ConstraintSet.MATCH_CONSTRAINT
            )
            parent.addView(button, lp)
        }
        for (i in 0 until dim) {
            id = buttonsIdArray[i]
            cs.setDimensionRatio(id, "1:2")
            cs.connect(id, ConstraintSet.TOP, container.id, ConstraintSet.TOP)
            cs.connect(id, ConstraintSet.BOTTOM, container.id, ConstraintSet.BOTTOM)
        }
        cs.createHorizontalChain(
            container.id, ConstraintSet.LEFT, container.id, ConstraintSet.RIGHT,
            buttonsIdArray, null,  ConstraintSet.CHAIN_SPREAD
        )
        cs.applyTo(parent)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun digitButtonClick(view: Button) {
        val activeCell: CellView = findViewById(activeCellId)
        val digit = view.text.toString().toInt()
        val activeCellIndex = cellsIdArray.indexOf(activeCell.id)
        if (mode == MODE.EDIT) {
            var color = 0
            if (activityViewModel.isCorectDigitButtonClicked( digit, activeCellIndex )){
                color = colorCorrect
            }
            else {
                color = colorWrong
                errorsTextView.text = (++errorsValue).toString()
            }
            activeCell.fillMain(digit, color)
            hideAssumption(digit, activeCellIndex)
        } else {
            activeCell.setAssumption(digit)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun hideAssumption(digit: Int, activeCellIndex: Int) {
            val iRow = activeCellIndex / dim
            val iCol = activeCellIndex % dim
            for (i in 0 until dim) {
                val columnCell = findViewById<CellView>(cellsIdArray[i * dim + iCol])
                val rowCell = findViewById<CellView>(cellsIdArray[iRow * dim + i])
                columnCell.setAssumption(digit, false)
                rowCell.setAssumption(digit, false)
            }
            val xStart = (iCol / 3) * 3
            val yStart = (iRow / 3) * 3
            for (x in xStart until xStart + 3) {
                for (y in yStart until yStart + 3) {
                    val cell = findViewById<CellView>(cellsIdArray[y * dim + x])
                    cell.setAssumption(digit, false)
                }
            }

        }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fillGameGrid(gridView: View, parent: ConstraintLayout, matrix: Array<IntArray>) {
        var id: Int
        lateinit var cell: CellView
        cs.clone(parent)
        cs.setDimensionRatio(gridView.id, dim.toString() + ":" + dim.toString())
        for (i in 0 until dim) {
            for (j in 0 until dim) {
                cell = CellView(this)
                id = View.generateViewId()
                cellsIdArray[i * dim + j] = id
                cell.id = id
                if (matrix[i][j] != 0) {
                    cell.fillMain(matrix[i][j], colorCorrect)
                }
                cell.setOnClickListener(this)
                lp = ConstraintLayout.LayoutParams(
                    ConstraintSet.MATCH_CONSTRAINT, ConstraintSet.MATCH_CONSTRAINT
                )
                parent.addView(cell, lp)
            }
        }
        for (iRow in 0 until dim) {
            for (iCol in 0 until dim) {
                id = cellsIdArray[iRow * dim + iCol]
                cs.setDimensionRatio(id, "1:1")
                when (iRow) {
                    0 -> cs.connect(id, ConstraintSet.TOP, gridView.id, ConstraintSet.TOP)
                    else -> cs.connect(
                        id, ConstraintSet.TOP,
                        cellsIdArray[(iRow - 1) * dim], ConstraintSet.BOTTOM
                    )
                }

                val chainArray = cellsIdArray.copyOfRange(iRow * dim, (iRow + 1) * dim)
                cs.createHorizontalChain(
                    gridView.id, ConstraintSet.LEFT,
                    gridView.id, ConstraintSet.RIGHT,
                    chainArray, null,
                    ConstraintSet.CHAIN_PACKED
                )
            }
        }
        activeCellId = cellsIdArray[0]
        cs.applyTo(parent)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(cell: View?) {
        cell?.let { v ->
            crossBackground(activeCellId, colorbackground)
            activeCellId = v.id
            crossBackground(activeCellId, colorButtonClicked)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun crossBackground(cellId: Int, color: Int) {
        val activeCell = findViewById<CellView>(cellId)
        if (activeCell.getMainDigitTextValue() > 0) {
            cellsIdArray.indexOf(cellId)?.let { index ->
                val iRow = index / dim
                val iCol = index % dim
                for (i in 0 until dim) {
                    val columnCell = findViewById<CellView>(cellsIdArray[i * dim + iCol])
                    val rowCell = findViewById<CellView>(cellsIdArray[iRow * dim + i])
                    columnCell.setBackgroundColor(color)
                    rowCell.setBackgroundColor(color)
                }
                val xStart = (iCol / 3) * 3
                val yStart = (iRow / 3) * 3
                for (x in xStart until xStart + 3) {
                    for (y in yStart until yStart + 3) {
                        val cell = findViewById<CellView>(cellsIdArray[y * dim + x])
                        cell.setBackgroundColor(color)
                    }
                }

            }
            for (id in cellsIdArray) {
                val cell = findViewById<CellView>(id)
                if (cell.getMainDigitTextValue() == activeCell.getMainDigitTextValue())
                    cell.setBackgroundColor(color)
            }
        } else {
            activeCell.setBackgroundColor(color)
        }
    }


}