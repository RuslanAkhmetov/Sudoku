package com.example.sudoku.viewModel

import com.example.sudoku.core.LEVEL

interface SudokuViewModel {
    fun getSolvedMatrix(level: Int): Array<IntArray>
    fun getStartMatrix(level: LEVEL): Array<IntArray>
    fun isCorectDigitButtonClicked(digit: Int, activeCellIndex: Int) : Boolean
    fun getClue(activeCellIndex: Int) : Int
}