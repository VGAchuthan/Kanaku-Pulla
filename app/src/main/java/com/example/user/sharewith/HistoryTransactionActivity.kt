package com.example.user.sharewith

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.LinearLayout

class HistoryTransactionActivity : Activity() {
    lateinit var sharedPref : SharedPreferences
    lateinit var dbhelper : DBHelper
    lateinit var  userid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_transaction)
        dbhelper = DBHelper(this,null)
        sharedPref = getSharedPreferences(LoginActivity.sharedPreFile, Context.MODE_PRIVATE)

        userid = sharedPref.getString("MOBILE_KEY","DEFAULT")
        val recyclerView : RecyclerView = findViewById<RecyclerView>(R.id.recycler_view_history_transact) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL,false)
        val adapter = ShareCardAdapter(this,ShareActivity.historyTransaction,userid)
        Log.e("History ","${adapter.itemCount}")
        fillAdapter(userid)

        //recyclerView.adapter= adapter
    }
    override fun onResume() {
        super.onResume()
        fillAdapter(this.userid)
    }
    fun fillAdapter(userId : String)
    {
        val recyclerView : RecyclerView = findViewById<RecyclerView>(R.id.recycler_view_history_transact) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL,false)
        var transact =ArrayList<TranscationShare>()
        transact = getAllTransact(userId)
        var t : TranscationShare = TranscationShare()
        //  t.printAllData(transact)
        for(v in transact)
        {
            Log.e( "VAlues : ", " ${v.transId} :: ${v.amount} :: ${v.balance} :: ${v.paid} :: settleflag ${v.settle_flag} " )
        }

        val adapter = ShareCardAdapter(this,transact,userId)
        recyclerView.adapter= adapter
    }

    fun getAllTransact(userid : String): ArrayList<TranscationShare>{
        var transactionContact: TransactionContactInfo
        var transactionInfo : TransactionInfo
        var transactionShare : TranscationShare
        var title : String
        var desc : String
        var place : String
        var date : String
        var amount  : String
        var paid :String
        var balance : String
        var settleFlag  :Int
        var isGroup : Int
        var transid : Int
        var owner : String
        var lender : String
        var sharing_count : Int
        var contact = ArrayList<String>()
        var share = ArrayList<TranscationShare>()


        try {
            //code to get contact where one of contact is user
            var cursorForContact: Cursor = dbhelper.getTransactContact(userid)
            if(cursorForContact!!.moveToFirst()) {
                while (cursorForContact.isAfterLast == false) {
                    transid = (cursorForContact.getInt(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACTION_ID)))
                    owner  = (cursorForContact.getString(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_OWNERID)))

                    sharing_count  = (cursorForContact.getInt(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_SHARING_COUNT)))
                    // contact = ArrayList<String>()

                    if(sharing_count == 1)
                    {
                        lender   = (cursorForContact.getString(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_CONTACT_LIST_01)))
                        contact.add(lender)
                    }
                    else
                    {
                        for(i in 1..sharing_count){
                            if(sharing_count == 10)
                            {
                                lender   = (cursorForContact.getString(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_CONTACT_LIST_10)))
                                contact.add(lender)
                            }
                            else {
                                lender = (cursorForContact.getString(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_CONTACT_LIST_0 + "$i")))
                                contact.add(lender)
                            }
                        }


                    }


                    // code to get basic info by transid
                    var cursorForInfo : Cursor = dbhelper.getInfoByTransId(transid)
                    if(cursorForInfo!!.moveToFirst()) {
                        while (cursorForInfo.isAfterLast == false) {
                            title  = (cursorForInfo.getString(cursorForInfo.getColumnIndex(DBHelper.COLUMN_TRANSACT_BASIC_TITLE)))
                            desc  = (cursorForInfo.getString(cursorForInfo.getColumnIndex(DBHelper.COLUMN_TRANSACT_BASIC_DESCRIPTION)))
                            place  = (cursorForInfo.getString(cursorForInfo.getColumnIndex(DBHelper.COLUMN_TRANSACT_BASIC_PLACE)))
                            date  = (cursorForInfo.getString(cursorForInfo.getColumnIndex(DBHelper.COLUMN_TRANSACT_BASIC_DATE)))
                            // place to store share details
                            var cursorForShare : Cursor = dbhelper.getShareByTransId(transid)
                            if(cursorForShare!!.moveToFirst()) {
                                while (cursorForShare.isAfterLast == false) {
                                    // var owner : String  = (cursorForShare.getString(cursorForShare.getColumnIndex(DBHelper.COLUMN_TRANSACT_BASIC_TITLE)))
                                    amount   = (cursorForShare.getString(cursorForShare.getColumnIndex(DBHelper.COLUMN_AMOUNT)))
                                    paid   = (cursorForShare.getString(cursorForShare.getColumnIndex(DBHelper.COLUMN_PAID)))
                                    balance   = (cursorForShare.getString(cursorForShare.getColumnIndex(DBHelper.COLUMN_BALANCE)))
                                    settleFlag = (cursorForShare.getInt(cursorForShare.getColumnIndex(DBHelper.COLUMN_SETTLE_FLAG)))
                                    isGroup  = (cursorForShare.getInt(cursorForShare.getColumnIndex(DBHelper.COLUMN_IS_GROUP)))

                                    transactionContact = TransactionContactInfo(owner,sharing_count, contact)
                                    transactionInfo = TransactionInfo(title,desc,date,place)
                                    transactionShare = TranscationShare(transid.toLong(),amount.toDouble(),paid.toDouble(), balance.toDouble(),settleFlag,owner,transactionInfo,transactionContact,isGroup)
                                    if(settleFlag == 1){
                                        share.add(transactionShare)
                                    }


                                    TranscationShare.shareIdMap.put(transid,transactionShare)
                                    cursorForShare.moveToNext()

                                }
                            }

                            cursorForInfo.moveToNext()
                            cursorForShare.close()




                        }
                    }
                    cursorForContact.moveToNext()
                    cursorForInfo.close()





                }
            }
            cursorForContact.close()
        }
        catch(e : Exception)
        {
            Log.e("bye","exception $e ")
        }

        return  share
    }
}
