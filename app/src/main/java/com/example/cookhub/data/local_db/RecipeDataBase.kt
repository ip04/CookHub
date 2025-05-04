package com.example.cookhub.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cookhub.models.Direction
import com.example.cookhub.models.Ingredient
import com.example.cookhub.models.Recipe

@Database(
    entities = [Recipe::class, Ingredient::class, Direction::class],
    version = 2,
    exportSchema = false
)
abstract class RecipeDataBase: RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object{
        @Volatile
        private var instance: RecipeDataBase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. עדכון טבלת recipes
                // יצירת טבלה זמנית עם הסכמה הישנה של recipes
                database.execSQL("""
                    CREATE TABLE recipes_temp (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        photo TEXT,
                        tag TEXT
                    )
                """)

                // העתקת נתונים לטבלה הזמנית
                database.execSQL("""
                    INSERT INTO recipes_temp (id, title, photo, tag)
                    SELECT id, title, photo, tag FROM recipes
                """)

                // מחיקת הטבלה הישנה
                database.execSQL("DROP TABLE recipes")

                // יצירת טבלה חדשה עם הסכמה של גרסה 2
                database.execSQL("""
                    CREATE TABLE recipes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        author TEXT,
                        difficulty TEXT,
                        time TEXT,
                        photo TEXT
                    )
                """)

                // העתקת נתונים מהטבלה הזמנית
                database.execSQL("""
                    INSERT INTO recipes (id, title, photo)
                    SELECT id, title, photo FROM recipes_temp
                """)

                // מחיקת הטבלה הזמנית
                database.execSQL("DROP TABLE recipes_temp")

                // 2. עדכון טבלת ingredients
                // יצירת טבלה זמנית עם הסכמה הישנה של ingredients (ללא ForeignKey)
                database.execSQL("""
                    CREATE TABLE ingredients_temp (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        recipeId INTEGER NOT NULL
                    )
                """)

                // העתקת נתונים לטבלה הזמנית
                database.execSQL("""
                    INSERT INTO ingredients_temp (id, name, recipeId)
                    SELECT id, name, recipeId FROM ingredients
                """)

                // מחיקת הטבלה הישנה
                database.execSQL("DROP TABLE ingredients")

                // יצירת טבלה חדשה עם ForeignKey
                database.execSQL("""
                    CREATE TABLE ingredients (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        recipeId INTEGER NOT NULL,
                        FOREIGN KEY (recipeId) REFERENCES recipes(id) ON DELETE CASCADE
                    )
                """)

                // העתקת נתונים מהטבלה הזמנית
                database.execSQL("""
                    INSERT INTO ingredients (id, name, recipeId)
                    SELECT id, name, recipeId FROM ingredients_temp
                """)

                // מחיקת הטבלה הזמנית
                database.execSQL("DROP TABLE ingredients_temp")

                // 3. עדכון טבלת directions
                // יצירת טבלה זמנית עם הסכמה הישנה של directions (ללא ForeignKey)
                database.execSQL("""
                    CREATE TABLE directions_temp (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        description TEXT NOT NULL,
                        recipeId INTEGER NOT NULL
                    )
                """)

                // העתקת נתונים לטבלה הזמנית
                database.execSQL("""
                    INSERT INTO directions_temp (id, description, recipeId)
                    SELECT id, description, recipeId FROM directions
                """)

                // מחיקת הטבלה הישנה
                database.execSQL("DROP TABLE directions")

                // יצירת טבלה חדשה עם ForeignKey
                database.execSQL("""
                    CREATE TABLE directions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        description TEXT NOT NULL,
                        recipeId INTEGER NOT NULL,
                        FOREIGN KEY (recipeId) REFERENCES recipes(id) ON DELETE CASCADE
                    )
                """)

                // העתקת נתונים מהטבלה הזמנית
                database.execSQL("""
                    INSERT INTO directions (id, description, recipeId)
                    SELECT id, description, recipeId FROM directions_temp
                """)

                // מחיקת הטבלה הזמנית
                database.execSQL("DROP TABLE directions_temp")
            }
        }

        fun getDatabase(context: Context) = instance ?: synchronized(this){
            Room.databaseBuilder(context.applicationContext, RecipeDataBase::class.java,"recipes_db")
                .addMigrations(MIGRATION_1_2)
                .allowMainThreadQueries().build()
        }
    }
}