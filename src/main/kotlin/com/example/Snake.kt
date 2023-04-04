package com.example

import javafx.animation.Timeline
import javafx.scene.Group
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle


class Snake() {
    private lateinit var direction: Direction

    private lateinit var body : MutableList<Field>

    private lateinit var startingPosition : Field

    private lateinit var maze : Maze

    private lateinit var game: Game

    constructor(headPosition : Field, maze : Maze, game: Game) : this() {
        this.startingPosition = headPosition
        this.game = game
        this.maze = maze
        body = mutableListOf<Field>()
        body.add(startingPosition)
    }

    fun setDirection(newDirection: Direction){
        direction = newDirection
    }

    fun move(){
        unDraw()

        for(i in body.size - 1 downTo  1){
            body[i] = body[i - 1]
        }

        var nextField = maze.getNextField(direction, body[0] )

        for(snakeBody in body){
            if(snakeBody.getY() == nextField.getY() && snakeBody.getX() == nextField.getX()){
                maze.game.endGame("You ate yourself!")
            }
        }
        nextField.eatenBy(this)
        body[0] = nextField
        draw()
    }

    fun increase(apple: Apple) {
        body.add((Field(apple.getX(), apple.getY())))
        maze.game.increaseAppleCounter()
        maze.updateSnake(body)
    }

    private fun draw(){
        for(field in body){
            val rectangle = Rectangle((field.getX() * 50).toDouble(), (field.getY() * 50).toDouble(), 50.0, 50.0)
            rectangle.fill = Color.GREEN
            rectangle.stroke = Color.BLACK
            rectangle.strokeWidth = 2.0
            game.getRoot().children.add(rectangle)
        }
    }

    private fun unDraw(){
        for(field in body){
            val rectangle = Rectangle((field.getX() * 50).toDouble(), (field.getY() * 50).toDouble(), 50.0, 50.0)
            rectangle.fill = Color.WHITE
            rectangle.stroke = Color.BLACK
            rectangle.strokeWidth = 2.0
            game.getRoot().children.add(rectangle)
        }
    }

    fun getDirection(): Direction {
        return direction
    }
}