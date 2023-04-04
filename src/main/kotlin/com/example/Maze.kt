package com.example

import javafx.scene.control.Alert

class Maze(var initX: Int, var initY: Int, var game: Game) {
    private lateinit var fields: Array<Array<Field>>

    fun build(){
        fields = Array<Array<Field>>(initX) { Array<Field>(initY) { Field(0,0) } }//
        for(i in 0 until initX){
            for(j in 0 until initY){
                fields[i][j] = Field(i, j)
            }
        }
    }

    fun updateSnake(body: MutableList<Field>){
        for(i in 0 until initX){
            for(j in 0 until initY){
                fields[i][j] = Field(i, j)
            }
        }
    }

    fun contains(field : Field) : Boolean {
        if(field.getX() < 0 || field.getY() < 0 || field.getX() > initX - 1 || field.getY() > initY - 1) return false
        return true
    }

    fun spawnApple(x: Int, y: Int){
        fields[x][y] = Apple(x, y)
    }

    fun getNextField(direction: Direction, head: Field): Field {
        var possibleNewField : Field = when (direction) {
            Direction.UP -> Field(head.getX(),head.getY() - 1)
            Direction.DOWN -> Field(head.getX(),head.getY() + 1)
            Direction.RIGHT -> Field(head.getX() + 1,head.getY())
            Direction.LEFT -> Field(head.getX() - 1,head.getY())
        }
        if(!contains(possibleNewField)){
            game.endGame("You crashed the wall!")
            return Field(-1,-1)
        }
        return  when (direction) {
            Direction.UP -> fields[head.getX()][head.getY() - 1]
            Direction.DOWN -> fields[head.getX()][head.getY() + 1]
            Direction.RIGHT -> fields[head.getX() + 1][head.getY()]
            Direction.LEFT -> fields[head.getX() - 1][head.getY()]
        }
    }
}