package com.example.myapplication.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.model.Medicament
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Medicament::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun itemDao(): MedicamentDao

    companion object {

        @Volatile
        private var INSTANCE: TodoDatabase? = null

        //        @kotlinx.coroutines.InternalCoroutinesApi()
        fun getDatabase(context: Context, scope: CoroutineScope): TodoDatabase {
            val inst = INSTANCE
            if (inst != null) {
                return inst
            }
            val MIGRATION_1_2 = object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE medicamente ADD COLUMN longitudine REAL")
                    database.execSQL("ALTER TABLE medicamente ADD COLUMN latitudine REAL")
                }
            }
            val instance =
                Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_db"
                )
                    .addCallback(WordDatabaseCallback(scope)).build() //addMigrations(MIGRATION_1_2).build()


            INSTANCE = instance
            return instance
        }

        private class WordDatabaseCallback(private val scope: CoroutineScope) :
            RoomDatabase.Callback() {

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)

                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.itemDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(itemDao: MedicamentDao) {
            itemDao.deleteAll()
//            val item = Medicament(1, "For headache","Algocalmin")
//            itemDao.insert(item)
        }
    }

}