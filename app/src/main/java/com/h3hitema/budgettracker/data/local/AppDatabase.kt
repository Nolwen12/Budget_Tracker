package com.h3hitema.budgettracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ExpenseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Room génère l'implémentation concrète du DAO au moment de la compilation.
    abstract fun taskDao(): ExpenseDao

    companion object {
        private const val DATABASE_NAME = "todo_database"

        // @Volatile garantit que tous les threads voient la même valeur de INSTANCE.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // On utilise applicationContext pour éviter de garder une Activity en mémoire.
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}