package com.example.sudoku.core

import android.util.Log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import kotlin.random.Random


class Sudoku() {
    companion object {
        private val MINERASEDCELLS = 20
        const val dim = 9
    }
    private val grid = mutableListOf<Cell>()
    private val matrix = Array(dim, {IntArray(dim)})

    private var times:Int = 0
    init {
        for (i in 0..8) {
            for (j in 0..8) {
                grid.add(9 * i + j, Cell(0, filltail(0)))
            }
        }
    }

    constructor(rows: List<String>) : this() {
        require(rows.size == 9 && rows.all { it.corect() }) {
            "Grid must be 9 * 9 lines and columns has uniK digits"
        }
        for (i in 0..8) {
            for (j in 0..8) {
                grid[9 * i + j]= Cell(rows[i][j] - '0', filltail(rows[i][j] - '0'))
            }
        }
    }



    private var solved = false
    var filled: Int = 0

    suspend fun getSolvedMatrix(): List<Cell> {
        coroutineScope {
            launch {
                while (!solved) {
                    makeRandomGrid()
                    machineSolve()
                    times = 0
                }
            }
        }
        return grid
    }

    private fun makeRandomGrid() {
        for (i in 0..8) {
            for (j in 0..8) {
                grid[9 * i + j]= Cell(0, filltail(0))
            }
        }
        var j = Random.nextInt(0, dim)
        var new = Random.nextInt(0, dim)
        for (n in 0..1) {
            do {
                new =Random.nextInt(0, dim)
            } while(new == j)
            j=new

            for (i in 0+j until dim*(dim-1)+j step dim) {
                grid[i].tail?.let {
                    if (it.size > 1) {
                        var e = -1
                        do {
                            val index = Random.nextInt(0, it.size )
                            // ThreadLocalRandom.current().nextInt(list.size)
                            e = it.elementAt(index)
                        } while (!checkValidity(e, i / dim, i % dim))
                        fillCell(i, e)
                    }
                }
            }
        }
    }

    fun makeStartGrid(solvedMatrix : Array<IntArray>, level:LEVEL): Array<IntArray>{
        var pos:Int
        var cells = dim*dim
        print("Difficulty = ${level.difficulty}")
        Log.i("TAG", "Difficulty = ${level.difficulty}")

        for (i in 0 until  solvedMatrix.size){
            for (j in 0 until solvedMatrix[0].size){
                matrix[i][j] = solvedMatrix[i][j]
            }
        }

        var min = 0
        while (times < level.difficulty * dim*dim && cells > 18){
            do {
                pos = Random.nextInt(0, dim*dim)
            } while (matrix[pos/dim][pos%dim] == 0)
            val temp = matrix[pos/dim][pos%dim]
            matrix[pos/dim][pos%dim] = 0
            cells -= 1
            min++

            if (min > MINERASEDCELLS) {
                grid.gridFromMatrix(matrix)
                solved = false
                machineSolve()
                if (!solved ){
                    matrix[pos/dim][pos%dim] = temp
                    return matrix
                }
            }
        }
        return matrix
    }


    fun machineSolve() {
        times = 0
        print("Starting1 grid:\n\n$this")
        placeNumber(0)
        Log.i("TAG","machineSolve : Times = $times Solved = $solved")
        print(if (solved) "Solution\n\n$this" else "Unsolved\n")
    }



    private fun placeNumber(pos: Int) {
        if (solved) return
        times ++
        //print("placeNumber: $times \n")
        if (times > LEVEL.UNREAL.difficulty*dim*dim) return

        if (pos == 81) {
            solved = true
            return
        }
        if (grid[pos].head > 0) {
            placeNumber(pos + 1)
            return
        }
        for (n in 1..9) {
            if (checkValidity(n, pos / 9, pos % 9)) {
                grid[pos].head = n
                placeNumber(pos + 1)
                if (solved) return
                grid[pos].head = 0
            }
        }
    }

    private fun MutableList<Cell>.gridFromMatrix(matrix: Array<IntArray>) : MutableList<Cell>{
        for (pos in 0 until this.size){
            if (matrix[pos/dim][pos%dim] == 0) this[pos].head=0
        }
        return this
    }


    private fun checkValidity(v: Int, y: Int, x: Int): Boolean {
        for (i in 0 until dim) {
            if (grid[y * dim + i].head == v || grid[i * dim + x].head == v) return false
        }
        val startX = (x / 3) * 3
        val startY = (y / 3) * 3
        for (i in startY until startY + 3) {
            for (j in startX until startX + 3) {
                if (grid[i * dim + j].head == v) return false
            }
        }
        return true
    }

    private fun fillCell(pos: Int, value: Int) {
        grid[pos].head = value
        grid[pos].tail = null
        val x = pos / dim
        val y = pos % dim
        for (i in 0 until dim) {
            grid[x * dim + i].tail?.remove(value)
            grid[i * dim + y].tail?.remove(value)
        }
        val xStart = (x / 3) * 3
        val yStart = (y / 3) * 3
        for (i in xStart until xStart + 3) {
            for (j in yStart until yStart + 3) {
                grid[i * dim + j].tail?.remove(value)
            }
        }
        filled++
    }


    private fun filltail(head: Int): MutableSet<Int>? =
        if (head == 0) {
            mutableSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        } else {
            null
        }

    override fun toString(): String {
        val sb = StringBuilder()
        for (i in 0..8) {
            for (j in 0..8) {
                sb.append(grid[i * 9 + j].head)
                sb.append(" ")
                if (j == 2 || j == 5) sb.append("| ")
            }
            sb.append("\n")
            if (i == 2 || i == 5) {
                sb.append("------+-------+------\n")
            }
        }
        return sb.toString()
    }


    fun main(args: Array<String>) {
        val rows = listOf(
            "850002400",
            "720000009",
            "004000000",
            "000107002",
            "305000900",
            "040000000",
            "000080070",
            "017000000",
            "000036040"
        )
        Sudoku(rows).machineSolve()
    }

}

private fun String.corect(): Boolean {
    if (this.length != 9) return false
    return arrayCheck(this.toCharArray())
}

fun arrayCheck(charArray: CharArray): Boolean {
    charArray.sort()
    for (i in 0..8) {
        if (!Character.isDigit(charArray[i])) return false
        if (i < 8 && charArray[i] == charArray[i + 1] && charArray[i] != '0') return false
    }
    return true
}
