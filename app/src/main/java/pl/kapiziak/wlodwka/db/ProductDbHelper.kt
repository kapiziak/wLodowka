package pl.kapiziak.wlodwka.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import pl.kapiziak.wlodwka.ProductContract


class ProductDbHelper(context: Context) : SQLiteOpenHelper(context, ProductContract.DB_NAME, null, ProductContract.DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE " + ProductContract.ProductEntry.TABLE + " ( " +
                ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ProductContract.ProductEntry.COL_PRODUCT_VALIDITY + " DATE NOT NULL, " +
                ProductContract.ProductEntry.COL_PRODUCT_TITLE + " TEXT NOT NULL);"

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE)
        onCreate(db)
    }
}