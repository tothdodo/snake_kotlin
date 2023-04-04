package com.example

open class Field(var positionX: Int, var positionY: Int) {
    fun getX(): Int{
        return positionX;
    }

    fun getY(): Int{
        return positionY;
    }

    open fun eatenBy(snake: Snake){}
}