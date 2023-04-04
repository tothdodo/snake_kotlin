package com.example

import javafx.animation.Animation
import javafx.animation.AnimationTimer
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import javafx.util.Duration
import java.util.*


class Game : Application() {

    companion object {
        private const val WIDTH = 1200
        private const val HEIGHT = 800
    }

    private lateinit var snake: Snake

    private lateinit var maze: Maze

    private var appleCounter : Int = 0
    private var appleCounterTF: TextField = TextField("Apples eaten: $appleCounter")

    private var restartButton: Button = Button("Restart")

    private lateinit var mainScene: Scene
    private lateinit var graphicsContext: GraphicsContext

    private val currentlyActiveKeys = mutableSetOf<KeyCode>()

    private var scoreboard = Scoreboard()
    private var scoreboardBox = VBox()

    private var timelines: MutableList<Timeline>  = mutableListOf()

    private lateinit var root: Group

    override fun start(mainStage: Stage) {
        mainStage.title = "Event Handling"

        root = Group()
        mainScene = Scene(root)
        mainStage.scene = mainScene

        val canvas = Canvas(WIDTH.toDouble(), HEIGHT.toDouble())
        root.children.add(canvas)

        val holder = StackPane()
        holder.children.add(canvas)
        holder.style = "-fx-background-color: green"
        root.children.add(holder)

        // Scoreboard
        val scoreBoardSign = TextField("ScoreBoard")
        scoreBoardSign.layoutX = 1075.0
        scoreBoardSign.layoutY = 500.0
        scoreBoardSign.isFocusTraversable = false
        scoreBoardSign.isEditable = false
        scoreBoardSign.prefWidth = 75.0
        root.children.add(scoreBoardSign)

        scoreboardBox.alignment = Pos.BOTTOM_RIGHT
        holder.children.add(scoreboardBox)

        // Eaten apples textfield
        appleCounterTF.alignment = Pos.CENTER_RIGHT
        appleCounterTF.isFocusTraversable = false
        appleCounterTF.isEditable = false
        holder.children.add(appleCounterTF)

        // Restart button

        restartButton.layoutX = 1075.0
        restartButton.layoutY = 300.0
        restartButton.isFocusTraversable = false;

        restartButton.setOnAction { restart(mainStage) }

        root.children.add(restartButton)

        prepareActionHandlers()

        graphicsContext = canvas.graphicsContext2D
        startGame(mainStage)
    }

    private fun startGame(stage: Stage){
        cleanup()
        stage.show()
    }

    private fun restart(stage: Stage){
        startGame(stage)
    }

    private fun cleanup() {
        for(timeline in timelines)
            timeline.stop()

        counterSetTo0()

        maze = Maze(WIDTH/50 - 4, HEIGHT/50, this)
        maze.build()

        snake = Snake(Field(10,10), maze, this)

        snake.setDirection(Direction.UP)

        for(i in 0 until WIDTH/50 - 4){
            for(j in 0 until HEIGHT/50){
                val rectangle = Rectangle((i * 50).toDouble(), (j * 50).toDouble(), 50.0, 50.0)
                rectangle.fill = Color.WHITE
                rectangle.stroke = Color.BLACK
                rectangle.strokeWidth = 2.0
                root.children.add(rectangle)
            }
        }
        object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                tickAndRender()
            }
        }.start()

        val snakeTimeline = Timeline(KeyFrame(Duration.seconds(0.5), { _: ActionEvent? ->
            snake.move()
        }))
        snakeTimeline.cycleCount = Animation.INDEFINITE;
        timelines.add(snakeTimeline)
        snakeTimeline.play();

        val appleTimeline = Timeline(KeyFrame(Duration.seconds(5.0), { _: ActionEvent? ->
            val randomX = (0 until WIDTH/50 - 4).random()
            val randomY = (0 until HEIGHT/50).random()
            maze.spawnApple(randomX,randomY)

            val rectangle = Rectangle((randomX * 50).toDouble(), (randomY * 50).toDouble(), 50.0, 50.0)
            rectangle.fill = Color.RED
            rectangle.stroke = Color.BLACK
            rectangle.strokeWidth = 2.0
            root.children.add(rectangle)
        }))
        appleTimeline.cycleCount = Animation.INDEFINITE;
        timelines.add(appleTimeline)
        appleTimeline.play();
    }

    private fun prepareActionHandlers() {
        mainScene.onKeyPressed = EventHandler { event ->
            currentlyActiveKeys.add(event.code)
        }
        mainScene.onKeyReleased = EventHandler { event ->
            currentlyActiveKeys.remove(event.code)
        }
    }

    private fun tickAndRender() {
        updateDirection()
    }

    private fun counterSetTo0(){
        appleCounter = 0
        appleCounterTF.text = "Apples eaten: $appleCounter"
    }

    fun increaseAppleCounter(){
        appleCounter++
        appleCounterTF.text = "Apples eaten: $appleCounter"
    }

    fun getRoot() : Group{
        return root
    }

    fun endGame(message : String){
        for(tl in timelines){
            tl.stop()
        }

        if(scoreboard.isInTop5(appleCounter)){
            var alert = TextInputDialog()
            alert.contentText = "$message\nApples eaten: $appleCounter.\nYou are top 5!\nAdd your name to save!"
            alert.title = "MessageBox"
            alert.headerText = "Game over!"
            alert.show()
            alert.setOnHidden { evt ->
                scoreboard.saveScore(Player(alert.editor.text, appleCounter))
                updateScoreboard() }
        }
        else{
            var alert = Alert(Alert.AlertType.INFORMATION)
            alert.title = "MessageBox"
            alert.headerText = "Game over!"
            alert.contentText = "$message\nApples eaten: $appleCounter."
            alert.show()
        }
    }

    private fun updateScoreboard() {
        scoreboardBox.children.removeAll(scoreboardBox.children)
        var i = 1
        for(player in scoreboard.getScoreBoard()){
            val name = player.getName()
            val score = player.getPoints()
            var playerRow = TextField("$i)  $name  $score")
            playerRow.isFocusTraversable = false
            playerRow.isEditable = false
            playerRow.alignment = Pos.TOP_RIGHT
            scoreboardBox.children.add(playerRow)
            i++
        }
    }

    private fun updateDirection() {
        if (currentlyActiveKeys.contains(KeyCode.RIGHT) && snake.getDirection() == Direction.LEFT) {
            return
        }
        if (currentlyActiveKeys.contains(KeyCode.LEFT) && snake.getDirection() == Direction.RIGHT) {
            return
        }
        if (currentlyActiveKeys.contains(KeyCode.UP) && snake.getDirection() == Direction.DOWN) {
            return
        }
        if (currentlyActiveKeys.contains(KeyCode.DOWN) && snake.getDirection() == Direction.UP) {
            return
        }
        if (currentlyActiveKeys.contains(KeyCode.LEFT)) {
            snake.setDirection(Direction.LEFT)
        }
        if (currentlyActiveKeys.contains(KeyCode.RIGHT)) {
            snake.setDirection(Direction.RIGHT)
        }
        if (currentlyActiveKeys.contains(KeyCode.UP)) {
            snake.setDirection(Direction.UP)
        }
        if (currentlyActiveKeys.contains(KeyCode.DOWN)) {
            snake.setDirection(Direction.DOWN)
        }
    }
}
