package com.example.myapplication.data


import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "item_table")
data class Item(
    @ColumnInfo(name = "picture_uri") val pictureURIString: String?,
    @ColumnInfo(name =  "title") val title: String,
    @ColumnInfo(name = "location") val location: String?,
    @ColumnInfo(name = "price") val price: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "sub_category") val subCategory: String,
    @ColumnInfo(name = "expDate") val expDate: String,
    @ColumnInfo(name = "condition") val condition: String,
    @ColumnInfo(name = "description") val description: String
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var itemId: Int = 0
}

@Dao
interface ItemDao {
    @Query("SELECT * FROM item_table")
    fun getAllItems(): LiveData<List<Item>>

    @Query("SELECT * FROM item_table WHERE itemId IN(:itemIds)")
    fun getAllByIds(itemIds: IntArray): LiveData<List<Item>>

    @Insert
    fun insertAll(vararg items: Item)

    @Delete
    fun delete(item: Item)

    @Query("DELETE FROM item_table WHERE 1")
    fun deleteAll()

}

@Database(entities = [Item::class], version = 2)
abstract class ItemRoomDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): ItemRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    "word_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class WordDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.itemDao())
                    }
                }
            }
        }

        fun populateDatabase(itemDao: ItemDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.

            itemDao.deleteAll()

            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

            var item = Item(
                "android.resource://com.example.myapplication/drawable/default__item_image", "TOLO FIGA",
                "Torino", "100", "divernten", "sottocategoria", timeStamp, "makle", "descrivo"
            )

            itemDao.insertAll(item)

            item = Item(
                "android.resource://com.example.myapplication/drawable/default__item_image", "LOOOL",
                "Mustafa", "100", "divernten","sottocategoria", timeStamp, "makle", "descrivo"
            )

            itemDao.insertAll(item)
        }
    }
}
