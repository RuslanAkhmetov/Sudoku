package com.example.sudoku.core

data class Cell(
    var head: Int,
    var tail: MutableSet<Int>?
)
