package com.example

class Player(name: String, points: Int) : Comparable<Player>{
    private var name : String = name

    private var points : Int = points

    fun getName(): String{
        return name
    }

    fun getPoints(): Int{
        return points
    }

    override fun compareTo(other: Player): Int {
        if(this.points < other.points) return 1
        return 0
    }
}