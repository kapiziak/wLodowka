package pl.kapiziak.wlodwka

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator

import kotlinx.android.synthetic.main.activity_main.*

import android.content.ContentValues
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.content_main.*
import pl.kapiziak.wlodwka.model.Product
import java.net.URL
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import pl.kapiziak.wlodwka.db.ProductDbHelper
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var mHelper: ProductDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //mProductListView = findViewById(R.id.list_product)

        mHelper = ProductDbHelper(this)
        updateUI()

        fab.setOnClickListener {
            val scanner = IntentIntegrator(this)

            //scanner.setOrientationLocked(false)
            scanner.initiateScan()
        }
    }

    fun clickPajacyk(view: View) {
        //https://www.pajacyk.pl/
        val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.pajacyk.pl/"))
        startActivity(i)
    }

    fun clickZrzutka(view: View) {
        //https://zrzutka.pl/szukaj/?term=g%C5%82%C3%B3d
        val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://zrzutka.pl/szukaj/?term=g%C5%82%C3%B3d"))
        startActivity(i)
    }

    fun deleteProduct(view: View) {
        val parent = view.getParent() as View
        val productTextView = parent.findViewById<TextView>(R.id.product_title)
        val product = productTextView.text.toString()
        val db = mHelper.writableDatabase
        db.delete(ProductContract.ProductEntry.TABLE,
            ProductContract.ProductEntry.COL_PRODUCT_TITLE + " = ?",
            arrayOf(product))
        db.close()

        Snackbar.make(view, "Produkt został odznaczony jako zużyty.", Snackbar.LENGTH_LONG)
            .setAction("OK", null).show()

        updateUI()
    }

    fun updateValidity(view: View) {
        val picker = DatePicker(this@MainActivity)
        picker.minDate = System.currentTimeMillis() - 1000
        val dialog = AlertDialog.Builder(this@MainActivity)
            .setTitle("Aktualizacja produktu")
            .setMessage("Zmień datę ważności: ")
            .setView(picker)
            .setPositiveButton("Zmień", DialogInterface.OnClickListener { _, _ ->

                val parent = view.getParent() as View
                val productTextView = parent.findViewById<TextView>(R.id.product_title)
                val product = productTextView.text.toString()
                val db = mHelper.writableDatabase
                val values = ContentValues()
                val dateVal = "" + picker.year + "-" + picker.month + "-" + picker.dayOfMonth
                values.put(ProductContract.ProductEntry.COL_PRODUCT_VALIDITY, dateVal)
                db.updateWithOnConflict(ProductContract.ProductEntry.TABLE,
                    values,
                    ProductContract.ProductEntry.COL_PRODUCT_TITLE + "='" + product + "'",
                    null,
                    SQLiteDatabase.CONFLICT_REPLACE)
                db.close()

                Snackbar.make(view, "Data ważności została zaktualizowana.", Snackbar.LENGTH_LONG)
                    .setAction("OK", null).show()
                updateUI()
            })
            .setNegativeButton("Anuluj", null)
            .create()

        dialog.show()


    }

    private fun updateUI() {

        val productList = arrayListOf<Product>()
        //val validityList = ArrayList<String>()
        val db = mHelper.readableDatabase
        val cursor = db.query(ProductContract.ProductEntry.TABLE,
            arrayOf(ProductContract.ProductEntry._ID, ProductContract.ProductEntry.COL_PRODUCT_TITLE, ProductContract.ProductEntry.COL_PRODUCT_VALIDITY), null, null, null, null, null)
        while (cursor.moveToNext()) {
            val idx = cursor.getColumnIndex(ProductContract.ProductEntry.COL_PRODUCT_TITLE)
            val idx2 = cursor.getColumnIndex(ProductContract.ProductEntry.COL_PRODUCT_VALIDITY)
            productList.add(Product(1, null, cursor.getString(idx), null, cursor.getString(idx2)))

        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ProductAdapter(productList)
        }

        cursor.close()
        db.close()

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    val progressDialog = AlertDialog.Builder(this@MainActivity)
                    progressDialog.setMessage(getString(R.string.ladowanie))
                    progressDialog.setCancelable(false)
                    progressDialog.create()
                    var adb = progressDialog.show()
                    doAsync {


                        val json =
                            URL("https://world.openfoodfacts.org/api/v0/product/"+ result.contents +".json").readText()
                        uiThread {
                            adb.dismiss()
                            Log.d("testing", json.toString())

                            val product = Gson().fromJson(json, Product::class.java)
                            //Toast.makeText(this, "Scanned: ", Toast.LENGTH_LONG).show()
                            if(product.status == 1) {
                                //val taskEditText = EditText(this@MainActivity)
                                val picker = DatePicker(this@MainActivity)


                                picker.minDate = System.currentTimeMillis() - 1000


                                val dialog = AlertDialog.Builder(this@MainActivity)
                                    .setTitle("Dodawanie nowego produktu")
                                    .setMessage("Data ważności produktu: ")
                                    .setView(picker)
                                    .setPositiveButton("Dodaj", DialogInterface.OnClickListener { _ , _ ->
                                        d("testing", "ean: " + product.ean + " name: " + product.product!!.name)
                                        d("testing", product.toString())
                                        //welcomeSign.text = product.product.name


                                        val db = mHelper.writableDatabase
                                        val values = ContentValues()
                                        val dateval = "" + picker.year + "-" + picker.month + "-" + picker.dayOfMonth
                                        values.put(ProductContract.ProductEntry.COL_PRODUCT_TITLE, product.product?.name)
                                        values.put(ProductContract.ProductEntry.COL_PRODUCT_VALIDITY, dateval)
                                        db.insertWithOnConflict(ProductContract.ProductEntry.TABLE,
                                            null,
                                            values,
                                            SQLiteDatabase.CONFLICT_REPLACE)
                                        db.close()

                                        updateUI()
                                    })
                                    .setNegativeButton("Anuluj", null)
                                    .setNeutralButton("Szybkie dodawanie", DialogInterface.OnClickListener { _ , _ ->
                                        //welcomeSign.text = product.product!!.name


                                        val db = mHelper.writableDatabase
                                        val values = ContentValues()


                                        val date = Calendar.getInstance()
                                        date.add(Calendar.DATE, +3)
                                        val date2: Date = date.time

                                        val dateFormat = SimpleDateFormat("yyyy/MM/dd")

                                        val dateval = dateFormat.format(date2)
                                        values.put(ProductContract.ProductEntry.COL_PRODUCT_TITLE,
                                            product.product?.name
                                        )
                                        values.put(ProductContract.ProductEntry.COL_PRODUCT_VALIDITY, dateval)
                                        db.insertWithOnConflict(ProductContract.ProductEntry.TABLE,
                                            null,
                                            values,
                                            SQLiteDatabase.CONFLICT_REPLACE)
                                        db.close()

                                        updateUI()
                                    })
                                    .create()
                                dialog.show()

                            } else {
                                val taskEditText = EditText(this@MainActivity)






                                val dialog = AlertDialog.Builder(this@MainActivity)
                                    .setTitle("Dodawanie nowego produktu")
                                    .setMessage("Nazwa produktu: ")
                                    .setView(taskEditText)
                                    .setPositiveButton("Dodaj", DialogInterface.OnClickListener { _ , _ ->


                                        val productName = taskEditText.text.toString()
                                        //welcomeSign.text = productName

                                        val db = mHelper.writableDatabase
                                        val values = ContentValues()


                                        val date = Calendar.getInstance()
                                        date.add(Calendar.DATE, +3)
                                        val date2: Date = date.time

                                        val dateFormat = SimpleDateFormat("yyyy/MM/dd")

                                        val dateval = dateFormat.format(date2)
                                        values.put(ProductContract.ProductEntry.COL_PRODUCT_TITLE, productName)
                                        values.put(ProductContract.ProductEntry.COL_PRODUCT_VALIDITY, dateval)
                                        db.insertWithOnConflict(ProductContract.ProductEntry.TABLE,
                                            null,
                                            values,
                                            SQLiteDatabase.CONFLICT_REPLACE)
                                        db.close()


                                        updateUI()
                                    })
                                    .setNegativeButton("Anuluj", null)
                                dialog.show()
                                d("testing", "Product not found!")

                            }
                        }
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}
