package com.example

class Apple(positionX: Int, positionY: Int) : Field(positionX, positionY) {
    override fun eatenBy(snake: Snake) {
        snake.increase(this)
    }
}