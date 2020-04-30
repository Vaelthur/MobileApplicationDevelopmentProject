package com.example.myapplication.main

class ItemCategories() {
    private val mainCat = arrayOf("Arts & Crafts", "Sports & Hobby", "Baby", "Women's fashion", "Men's fashion", "Electronics", "Games & Videogames", "Automotive")
    private val artsSubCat = arrayOf("Painting, Drawing & Art Supplies", "Sewing", "Scrapbooking & Stamping", "Party Decorations & Supplies")
    private val sportsSubCat = arrayOf("Sports & Outdoors", "Outdoor Recreation", "Sports & Fitness", "Pet Supplies")
    private val babySubCat = arrayOf("Apparel & Accessories", "Baby & Toddler Toys", "Car Seats & Accessories", "Pregnancy & Maternity", "Strollers & Accessories")
    private val womanSubCat = arrayOf("Clothing", "Shoes", "Watches", "Handbags", "Accessories")
    private val menSubCat = arrayOf("Clothing", "Shoes", "Watches", "Accessories")
    private val electSubCat = arrayOf("Computers", "Monitors", "Printers & Scanners", "Camera & Photo", "Smartphone & Tablet", "Audio", "Television & Video", "Video Game Consoles", "Wearable Technology", "Accessories & Supplies", "Irons & Steamers", "Vacuums & FLoor Care")
    private val gameSubCat = arrayOf("Action Figures & Statues", "Arts & Crafts", "Building Toys", "Dolls & Accessories", "Kids' Electronics", "Learning & Education", "Tricycles, Scooters & Wagons", "Videogames")
    private val autoSubCat = arrayOf("Car Electronics & Accessories", "Accessories", "Motorcycle & Powersports", "Replacement Parts", "RV Parts & Accessories", "Tools & Equipment")
    private val nullArr = arrayOf("Error", "Something", "Went", "Wrong")

    fun getMainCategories(): Array<String>{
        return this.mainCat
    }

    fun getPosFromValue(cat: String):Int{
        return this.mainCat.indexOf(cat)
    }

    fun getValueFromNum(pos: Int):String{
        return this.mainCat[pos]
    }

    fun getSubCategoriesFromMain(mCat: String): Array<String> {

        when (mainCat.indexOf(mCat)) {
            0 -> return artsSubCat
            1 -> return sportsSubCat
            2 -> return babySubCat
            3 -> return womanSubCat
            4 -> return menSubCat
            5 -> return electSubCat
            6 -> return gameSubCat
            7 -> return autoSubCat
            else -> return nullArr
        }
    }

    fun getSubPosFrom(subCat: String, mainCat:String) :Int{
        return when (getPosFromValue(mainCat)){
            0 -> artsSubCat.indexOf(subCat)
            1 -> sportsSubCat.indexOf(subCat)
            2 -> babySubCat.indexOf(subCat)
            3 -> womanSubCat.indexOf(subCat)
            4 -> menSubCat.indexOf(subCat)
            5 -> electSubCat.indexOf(subCat)
            6 -> gameSubCat.indexOf(subCat)
            7 -> autoSubCat.indexOf(subCat)
            else -> nullArr.indexOf("Error")
        }
    }

}