package com.example.sudoku.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sudoku.core.Cell
import com.example.sudoku.core.LEVEL
import com.example.sudoku.core.Sudoku
import kotlinx.coroutines.launch

class MainViewModel : ViewModel(), SudokuViewModel {
    companion object {
        const val dim = 9
    }

    var grid = MutableLiveData<MutableList<Cell>>()
    private lateinit var solvedMatrix: Array<IntArray>
    private val sudoku = Sudoku()

    init {
        viewModelScope.launch {
            grid.value = sudoku.getSolvedMatrix() as MutableList<Cell>
            grid.value?.let { solvedMatrix = it.convertGridToMatrix() }
        }
    }

    override fun getStartMatrix(level: LEVEL): Array<IntArray>  =
        sudoku.makeStartGrid(solvedMatrix, level)
    override fun getSolvedMatrix(level: Int): Array<IntArray> =
        solvedMatrix

    override fun isCorectDigitButtonClicked(digit: Int, activeCellIndex: Int): Boolean =
        getClue(activeCellIndex) == digit

    override fun getClue(activeCellIndex: Int): Int =
        solvedMatrix[activeCellIndex / dim][activeCellIndex % dim]

    private fun MutableList<Cell>.convertGridToMatrix(): Array<IntArray> {
        val matrix = Array(dim) { IntArray(dim) }
        this?.let {
            for (i in 0 until it.size) {
                it.get(i)?.let { cell ->
                    val iRow = i / dim
                    val iCol = i % dim
                    matrix[iRow][iCol] = cell.head
                }
            }
        }
        return matrix
    }

}