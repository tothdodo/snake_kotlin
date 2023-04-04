package com.example

class Scoreboard {
    private var scoreBoard : MutableList<Player> = mutableListOf()

    fun getScoreBoard(): MutableList<Player>{
        return scoreBoard
    }


    fun saveScore(player: Player){
        scoreBoard.add(player)
        scoreBoard.sortByDescending{
            it.getPoints()
        }
    }

    fun isInTop5(earnedPoints: Int): Boolean{
        if(scoreBoard.size < 5 || scoreBoard[4].getPoints() < earnedPoints) return true
        return false
    }


    internal class CustomComparator<T : Comparable<T>> : Comparator<T> {
        override fun compare(x: T, y: T): Int {
            return x.compareTo(y)
        }
    }
}