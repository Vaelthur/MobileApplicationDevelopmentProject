package com.example.myapplication.main

class ItemCategories() {
    private val mainCat = arrayOf("Arts", "Sports", "Baby", "Woman", "Man", "Electronics", "Games", "Automotive")

    fun getMainCategories(): Array<String>{
        return this.mainCat
    }

    fun getPosFromValue(cat: String):Int{
        return this.mainCat.indexOf(cat)
    }

    fun getValueFromNum(pos: Int):String{
        return this.mainCat[pos]
    }


}