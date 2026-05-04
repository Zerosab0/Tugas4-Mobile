package com.tradein.app.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.tradein.app.model.DcaStrategy

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tradein.db"
        private const val DATABASE_VERSION = 1

        // Table
        const val TABLE_STRATEGY = "dca_strategy"

        // Columns
        const val COL_ID            = "id"
        const val COL_ASSET_SYMBOL  = "asset_symbol"
        const val COL_AMOUNT        = "investment_amount"
        const val COL_FREQUENCY     = "frequency"
        const val COL_START_DATE    = "start_date"
        const val COL_END_DATE      = "end_date"
        const val COL_NOTES         = "notes"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_STRATEGY (
                $COL_ID           INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ASSET_SYMBOL TEXT    NOT NULL,
                $COL_AMOUNT       REAL    NOT NULL,
                $COL_FREQUENCY    TEXT    NOT NULL,
                $COL_START_DATE   TEXT    NOT NULL,
                $COL_END_DATE     TEXT    NOT NULL,
                $COL_NOTES        TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STRATEGY")
        onCreate(db)
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    fun insertStrategy(strategy: DcaStrategy): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ASSET_SYMBOL, strategy.assetSymbol)
            put(COL_AMOUNT,       strategy.investmentAmount)
            put(COL_FREQUENCY,    strategy.frequency)
            put(COL_START_DATE,   strategy.startDate)
            put(COL_END_DATE,     strategy.endDate)
            put(COL_NOTES,        strategy.notes)
        }
        val id = db.insert(TABLE_STRATEGY, null, cv)
        db.close()
        return id
    }

    // ── READ ──────────────────────────────────────────────────────────────────
    fun getAllStrategies(): List<DcaStrategy> {
        val list = mutableListOf<DcaStrategy>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_STRATEGY, null, null, null, null, null,
            "$COL_ID DESC"
        )
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    DcaStrategy(
                        id             = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                        assetSymbol    = cursor.getString(cursor.getColumnIndexOrThrow(COL_ASSET_SYMBOL)),
                        investmentAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_AMOUNT)),
                        frequency      = cursor.getString(cursor.getColumnIndexOrThrow(COL_FREQUENCY)),
                        startDate      = cursor.getString(cursor.getColumnIndexOrThrow(COL_START_DATE)),
                        endDate        = cursor.getString(cursor.getColumnIndexOrThrow(COL_END_DATE)),
                        notes          = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTES)) ?: ""
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun getStrategyCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_STRATEGY", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()
        return count
    }
}
