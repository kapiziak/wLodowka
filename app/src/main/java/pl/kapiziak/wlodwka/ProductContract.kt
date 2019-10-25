package pl.kapiziak.wlodwka

import android.provider.BaseColumns

class ProductContract {
    companion object {
        val DB_NAME = "pl.kapiziak.wlodwka.db"
        val DB_VERSION = 1
    }

    class ProductEntry : BaseColumns {

        companion object {
            val TABLE = "tasks"
            val COL_PRODUCT_TITLE = "title"
            val COL_PRODUCT_VALIDITY = "validity"
            val _ID = BaseColumns._ID
        }
    }
}