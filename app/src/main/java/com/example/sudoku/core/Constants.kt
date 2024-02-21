package com.example.sudoku.core

enum class MODE {
    EDIT,
    ASSUMPTION
}

enum class LEVEL(val difficulty: Int){
    EASY(20),
    MIDDLE(60),
    DIFFICULT(80),
    EXPERT(100),
    UNREAL(200)
}