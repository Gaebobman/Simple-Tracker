package com.standardlee.simpletracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Usage::class], version = 1)
abstract class UsageDatabase : RoomDatabase() {
    abstract fun UsageDao(): UsageDao

    // 싱글톤 패턴으로 구현하기 위함임
    companion object {
        @Volatile
        private var INSTANCE: UsageDatabase? = null

        fun getDatabase(context: Context): UsageDatabase {
            // INSTANCE 가 null 이 아니면 INSTANCE return,
            // null 이면 아래 코드를 실행
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UsageDatabase::class.java,
                    "usage_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}