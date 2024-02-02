package com.example.sudoku.core

import java.lang.StringBuilder


class Sudoku(rows: List<String>) {
    private val grid = mutableListOf<Cell>()
    private var solved = false

    init {
        require(rows.size == 9 && rows.all { it.corect() }) {
            "Grid must be 9 * 9 lines and columns has uniK digits"
        }
        for (i in 0..8) {
            for (j in 0..8) {
                grid.add(9 * i + j, Cell( rows[i][j] - '0',filltail(rows[i][j] -'0')))
            }
        }

    }

    fun machineSolve() {
        print("Starting1 grid:\n\n$this")
        placeNumber(0)
        print(if (solved) "Solution\n\n$this" else "Unsolved")
    }

    private fun placeNumber(pos: Int) {
        if (solved) return
        if (pos == 81) {
            solved = true
            return
        }
        if (grid[pos].head > 0) {
            placeNumber(pos + 1)
            return
        }
        for (n in 1..9) {
            if (checkValidity(n, pos % 9, pos / 9)) {
                grid[pos].head = n
                placeNumber(pos + 1)
                if (solved) return
                grid[pos].head = 0
            }
        }
    }



      private fun checkValidity(v: Int, x: Int, y: Int): Boolean {
            for (i in 0..8){
                if (grid[ y * 9 + i].head == v || grid[ i * 9 + x].head == v ) return false
            }
            val startX = (x/3)*3
            val startY = (y/3)*3
            for (i in startY until startY+3){
                for (j in startX until  startX+3){
                    if (grid[i * 9 + j].head == v) return false
                }
            }
            return true
        }

    override fun toString(): String{
        val sb = StringBuilder()
        for (i in 0..8){
            for (j in 0..8){
                sb.append(grid[i * 9 + j].head)
                sb.append(" ")
                if (j ==2 || j ==5 ) sb.append("| ")
            }
            sb.append("\n")
            if (i == 2 || i == 5) {
                sb.append("------+-------+------\n")
            }
        }
        return sb.toString()
    }

    private fun filltail(head: Int): MutableSet<Int>? =
        if (head>0) {
            var tail = mutableSetOf<Int>()
            for (i in 1..9) tail?.add(i)
            tail
        } else{ null}


    fun main (args: Array<String>){
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
    if(this.length !=9) return false
    return arrayCheck(this.toCharArray())
}

fun arrayCheck(charArray: CharArray): Boolean {
    charArray.sort()
    for (i in 0..8){
        if (!Character.isDigit(charArray[i])) return false
        if(i<8 && charArray[i]==charArray[i+1] && charArray[i] != '0' ) return false
    }
    return true
}
