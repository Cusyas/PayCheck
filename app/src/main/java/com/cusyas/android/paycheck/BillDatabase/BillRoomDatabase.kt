package com.cusyas.android.paycheck.BillDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = arrayOf(Bill::class), version = 3, exportSchema = false)
public abstract class BillRoomDatabase : RoomDatabase() {

    abstract fun billDao(): BillDao

    companion object {
        @Volatile
        private var INSTANCE: BillRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BillRoomDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BillRoomDatabase::class.java,
                    "bill_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

/*    private class BillDatabaseCallback(
        private val scope: CoroutineScope
    ): RoomDatabase.Callback(){
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                }
            }
        }
    }*/
}