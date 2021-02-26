package com.example.user.sharewith

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import java.util.function.BooleanSupplier

/**
 * Created by User on 04-01-2021.
 */
class DBHelper (context : Context, factory: SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // db.execSQL("DROP TABLE $TABLE_SIGNUP")
        db.execSQL(CREATE_SIGNUP_TABLE)
        db.execSQL(CREATE_TRANSACT_BASIC_TABLE)
        db.execSQL(CREATE_TRANSACT_CONTACT_TABLE)
        db.execSQL(CREATE_TRANSACT_TABLE)
        //db.execSQL(CREATE_ADMIN_TABLE)
        //db.execSQL(CREATE_STOCK_TABLE)
        //db.execSQL(CREATE_ORDER_TABLE)
        //db.execSQL(CREATE_TABLE_LOGIN)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
//        db.execSQL(SQL_DELETE_ENTRIES)
//        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //onUpgrade(db, oldVersion, newVersion)
    }

    fun insertUser(emailid: String, password: String, name: String, mobile: String): Boolean {
        // Gets the data repository in write mode
        val writeValues = this.writableDatabase
        Log.e("hai : ", "insertuser")
        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(COLUMN_EMAILID, emailid)
        values.put(COLUMN_MOBILE, mobile)
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_PASSWORD, password)

        // values.put(COLUMN_GENDER, gender)
        // values.put(COLUMN_USER_TYPE, type)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = writeValues.insert(TABLE_SIGNUP, null, values)
        writeValues.close()
        Log.e("bye : ", "insertuser added")


        return true
    }

    fun insertIntoTransactTable(ownerid : String, amount : String, paid : String, balance : String, isSettle : Int, isGroup : Int) : Boolean{

        val writeValues = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_TRANSACT_OWNERID, ownerid)
        values.put(COLUMN_AMOUNT, amount)
        values.put(COLUMN_PAID, paid)
        values.put(COLUMN_BALANCE, balance)
        values.put(COLUMN_SETTLE_FLAG, isSettle)
        values.put(COLUMN_IS_GROUP, isGroup)

        val newRowId = writeValues.insert(TABLE_TRANSACT, null , values)
        writeValues.close()
        Log.e("hai", "values inserted into transact table")
        return true


    }

    fun insertIntoTransactBasicTable(transid : Int, title : String, desc : String, date : String, place : String):Boolean {
        val writeValues  = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_TRANSACTION_ID, transid)
        values.put(COLUMN_TRANSACT_BASIC_TITLE, title)
        values.put(COLUMN_TRANSACT_BASIC_DESCRIPTION, desc)
        values.put(COLUMN_TRANSACT_BASIC_DATE, date)
        values.put(COLUMN_TRANSACT_BASIC_PLACE, place)
        val newRowid = writeValues.insert(TABLE_TRANSACT_BASIC_INFO, null, values)
        writeValues.close()
        Log.e("hai", "values inserted into transact basic table")
        return true

    }

    fun insertIntoTransactContactTable(transid : Int, ownerid : String, share_count : Int,lender_id : String): Boolean{

        val writeValues = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TRANSACT_OWNERID,ownerid)
        values.put(COLUMN_TRANSACTION_ID, transid)
        values.put(COLUMN_TRANSACT_SHARING_COUNT, share_count)
        values.put(COLUMN_TRANSACT_CONTACT_LIST_01, lender_id)

        val newRowId = writeValues.insert(TABLE_TRANSACT_CONTACT,null, values)
        writeValues.close()
        Log.e("hai", "values inserted into transact contact  table")
        return true


    }
    fun insertIntoTransactContactTableForGroup(transid : Int, ownerid : String, share_count : Int, contact_list : ArrayList<String>): Boolean{
        val writeValues = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TRANSACT_OWNERID,ownerid)
        values.put(COLUMN_TRANSACTION_ID, transid)
        values.put(COLUMN_TRANSACT_SHARING_COUNT, share_count)
        for(i in 1..share_count){
            if(i == 10)
            {
                values.put(COLUMN_TRANSACT_CONTACT_LIST_10,contact_list.get(9))
            }
            values.put(COLUMN_TRANSACT_CONTACT_LIST_0+"$i",contact_list.get(i-1))
        }
        val newRowId = writeValues.insert(TABLE_TRANSACT_CONTACT,null, values)
        writeValues.close()
        Log.e("hai", "values inserted into transact group  table")
        return true
    }

    fun updateTable(transid : Int, paid : Double, balance : Double, settleFlag : Int) : Boolean{
        val writeValues = this.writableDatabase
        val values = ContentValues()
        //values.put(COLUMN_TRANSACTION_ID,transid)
        values.put(COLUMN_PAID, paid.toString())
        values.put(COLUMN_BALANCE, balance.toString())
        values.put(COLUMN_SETTLE_FLAG, settleFlag)

        val newRowId = writeValues.update(TABLE_TRANSACT,values,"$COLUMN_TRANSACTION_ID = ?", arrayOf(transid.toString()))
        writeValues.close()
        return true




    }
    fun getTransact( mobile : String) : Cursor{
        val db = this.readableDatabase
        val c : Cursor  = db.rawQuery("SELECT * FROM $TABLE_TRANSACT WHERE $COLUMN_TRANSACTION_ID IN (SELECT $COLUMN_TRANSACTION_ID FROM $TABLE_TRANSACT_CONTACT WHERE $COLUMN_TRANSACT_CONTACT_LIST_01 = ? )", arrayOf(mobile))
        //db.close()
        return c
    }

    fun getTransactContact(mobile : String ) : Cursor {
        val db = this.readableDatabase
        val c : Cursor = db.rawQuery("SELECT * FROM $TABLE_TRANSACT_CONTACT WHERE $COLUMN_TRANSACT_CONTACT_LIST_01 =? OR $COLUMN_TRANSACT_CONTACT_LIST_02 = ? OR $COLUMN_TRANSACT_CONTACT_LIST_03 = ? OR $COLUMN_TRANSACT_CONTACT_LIST_04 = ? OR $COLUMN_TRANSACT_CONTACT_LIST_05 = ? OR $COLUMN_TRANSACT_CONTACT_LIST_06 = ? OR $COLUMN_TRANSACT_CONTACT_LIST_07 = ? OR $COLUMN_TRANSACT_CONTACT_LIST_08 = ? OR $COLUMN_TRANSACT_CONTACT_LIST_09 = ? OR $COLUMN_TRANSACT_CONTACT_LIST_10 = ?  OR $COLUMN_TRANSACT_OWNERID = ?", arrayOf(mobile,mobile,mobile,mobile,mobile,mobile,mobile,mobile,mobile,mobile, mobile))
      //  db.close()
        return c
    }

    fun getInfoByTransId(transid : Int) : Cursor{
        val db = this.readableDatabase
        val c : Cursor = db.rawQuery("SELECT * FROM $TABLE_TRANSACT_BASIC_INFO WHERE $COLUMN_TRANSACTION_ID = ?", arrayOf(transid.toString()))
      //  db.close()
        return c
    }

    fun getContactByTransId(transid : Int) : Cursor{
        val db = this.readableDatabase
        val c : Cursor = db.rawQuery("SELECT * FROM $TABLE_TRANSACT_CONTACT WHERE $COLUMN_TRANSACTION_ID = ?", arrayOf(transid.toString()))
        //  db.close()
        return c
    }

    fun getShareByTransId(transid : Int) : Cursor{
        val db = this.readableDatabase
        val c : Cursor =  db.rawQuery("SELECT * FROM $TABLE_TRANSACT WHERE $COLUMN_TRANSACTION_ID = ? ", arrayOf(transid.toString()))
        //db.close()
        return c
    }

    fun getUserByMobile(input: String, password: String): Cursor {
        val db = this.readableDatabase
        val c : Cursor = db.rawQuery("SELECT * FROM $TABLE_SIGNUP WHERE $COLUMN_MOBILE = ? AND $COLUMN_PASSWORD = ?", arrayOf(input, password))
      //  db.close()
        return c
    }

    fun getUserByEmail(input: String, password: String): Cursor {
        val db = this.readableDatabase
        val c : Cursor = db.rawQuery("SELECT * FROM $TABLE_SIGNUP WHERE $COLUMN_EMAILID = ? AND $COLUMN_PASSWORD = ?", arrayOf(input, password))
       // db.close()
        return c

    }

   /* fun getUser(input: String, password: String): Cursor {
        var flag: Int = 0

        val db = this.readableDatabase
        var cursor: Cursor = getUserByMobile(input, password)
        try {
            if (cursor!!.moveToFirst()) {
                if (cursor == null && cursor!!.count == -1) {
                    Log.e("hai","inside first try in get user")
                    flag = 0
                } else {

                    flag =1
                }
            }
            if(flag == 1)
                return true
        } catch (e: SQLiteException) {

            Log.e("bye", "terminated from catch")
            return false

        }
        cursor = getUserByEmail(input, password)

        try {
            if (cursor!!.moveToFirst()) {
                if (cursor == null && cursor!!.count == -1) {
                    Log.e("hai","inside second try in get user")
                    flag =0
                } else {
                    flag =1
                }
                if(flag == 1)
                    return true
            }
        } catch (e: SQLiteException)
        {
            Log.e("bye", "terminated from catch")
            return false
        }
        return false
    }*/

    fun getRowCount() : Int{
        val db = this.readableDatabase
        val count : Long = DatabaseUtils.queryNumEntries(db, TABLE_TRANSACT)
        db.close()
        return count.toInt()

    }
    fun getAllUser() : Cursor{
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_SIGNUP", null)
    }





    fun checkIfUserAvailable(mobile : String) : Int{
        val db = this.readableDatabase
        Log.e("dei " , " db la la irundu da maapla")
        val cursor : Cursor = db.rawQuery("SELECT * FROM $TABLE_SIGNUP WHERE $COLUMN_MOBILE = ?", arrayOf(mobile))
        if( cursor.count == 0)
        {
            return 0
        }



        return 1
    }


    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "sharewith.db"

        //val dbObj:DBHelper = DBHelper(DBHelper.class :: class,null)

        /*  SIGNUP TABLE COLUMNS */
        val TABLE_SIGNUP = "signup_table"
        val COLUMN_EMAILID = "emailid"
        val COLUMN_NAME = "name"
        val COLUMN_MOBILE = "mobilenumber"
        //val COLUMN_GENDER = "gender"
        //val COLUMN_PASSWORD = ""
        val COLUMN_PASSWORD = "password"
        // val COLUMN_USER_TYPE = "type"

        val CREATE_SIGNUP_TABLE = ("CREATE TABLE " +
                TABLE_SIGNUP + "("
                + COLUMN_EMAILID + " VARCHAR ," +
                COLUMN_MOBILE
                + " VARCHAR PRIMARY KEY," +
                COLUMN_NAME
                + " VARCHAR," +
                COLUMN_PASSWORD
                + " VARCHAR" +
                ")")

        val COLUMN_TRANSACTION_ID = "transaction_id"
        //Transact Contact table

        val TABLE_TRANSACT_CONTACT = "transact_contact"
        val COLUMN_TRANSACT_OWNERID = "owner_id"
        val COLUMN_TRANSACT_SHARING_COUNT = "sharing_count"
        val COLUMN_TRANSACT_CONTACT_LIST_01 = "contact_01"
        val COLUMN_TRANSACT_CONTACT_LIST_02 = "contact_02"
        val COLUMN_TRANSACT_CONTACT_LIST_03 = "contact_03"
        val COLUMN_TRANSACT_CONTACT_LIST_04 = "contact_04"
        val COLUMN_TRANSACT_CONTACT_LIST_05 = "contact_05"
        val COLUMN_TRANSACT_CONTACT_LIST_06 = "contact_06"
        val COLUMN_TRANSACT_CONTACT_LIST_07 = "contact_07"
        val COLUMN_TRANSACT_CONTACT_LIST_08 = "contact_08"
        val COLUMN_TRANSACT_CONTACT_LIST_09 = "contact_09"
        val COLUMN_TRANSACT_CONTACT_LIST_10 = "contact_10"
        val COLUMN_TRANSACT_CONTACT_LIST_0 = "contact_0"

        val CREATE_TRANSACT_CONTACT_TABLE = ("CREATE TABLE " +
                TABLE_TRANSACT_CONTACT + "("
                + COLUMN_TRANSACT_OWNERID + " VARCHAR ," +
                COLUMN_TRANSACTION_ID
                + " INTEGER PRIMARY KEY , " +
                COLUMN_TRANSACT_SHARING_COUNT
                + " INTEGER," +
                COLUMN_TRANSACT_CONTACT_LIST_01
                + " VARCHAR," +
                COLUMN_TRANSACT_CONTACT_LIST_02
                + " VARCHAR," +
                COLUMN_TRANSACT_CONTACT_LIST_03
                + " VARCHAR," +
                COLUMN_TRANSACT_CONTACT_LIST_04
                + " VARCHAR," +
                COLUMN_TRANSACT_CONTACT_LIST_05
                + " VARCHAR," +
                COLUMN_TRANSACT_CONTACT_LIST_06
                + " VARCHAR," +
                COLUMN_TRANSACT_CONTACT_LIST_07
                + " VARCHAR," +
                COLUMN_TRANSACT_CONTACT_LIST_08
                + " VARCHAR," +
                COLUMN_TRANSACT_CONTACT_LIST_09
                + " VARCHAR," +
                COLUMN_TRANSACT_CONTACT_LIST_10
                + " VARCHAR" +
                ")")


        // Transact Basic Info TAble

        val TABLE_TRANSACT_BASIC_INFO = "transact_basic_info"
        val COLUMN_TRANSACT_BASIC_TITLE = "title"
        val COLUMN_TRANSACT_BASIC_DESCRIPTION = "description"
        val COLUMN_TRANSACT_BASIC_DATE = "date"
        val COLUMN_TRANSACT_BASIC_PLACE = "place"

        val CREATE_TRANSACT_BASIC_TABLE = ("CREATE TABLE " +
                TABLE_TRANSACT_BASIC_INFO + "("
                +  COLUMN_TRANSACTION_ID
                + " INTEGER PRIMARY KEY, " +
                COLUMN_TRANSACT_BASIC_TITLE
                + " VARCHAR," +
                COLUMN_TRANSACT_BASIC_DESCRIPTION
                + " VARCHAR," +
                COLUMN_TRANSACT_BASIC_DATE
                + " VARCHAR," +
                COLUMN_TRANSACT_BASIC_PLACE
                + " VARCHAR" +
                ")")

        //Transact Table

        val TABLE_TRANSACT = "transact"
        val COLUMN_AMOUNT = "amount"
        val COLUMN_PAID = "paid"
        val COLUMN_BALANCE = "balance"
        val COLUMN_SETTLE_FLAG = "settle_flag"
        //val COLUMN_TRANSACT_OWNERID = "owner_id"
        val COLUMN_IS_GROUP = "is_group"

        val CREATE_TRANSACT_TABLE = ("CREATE TABLE " +
                TABLE_TRANSACT + "("
                +  COLUMN_TRANSACTION_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TRANSACT_OWNERID
                + " VARCHAR," +
                COLUMN_AMOUNT
                + " VARCHAR," +
                COLUMN_PAID
                + " VARCHAR," +
                COLUMN_BALANCE
                + " VARCHAR," +
                COLUMN_SETTLE_FLAG
                + " INTEGER," +
                COLUMN_IS_GROUP
                + " INTEGER" +
                ")")


    }

    }