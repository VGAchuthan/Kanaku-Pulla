package com.example.user.sharewith

import android.app.Activity
import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text


class TransactionInfoActivity : Activity() {
    lateinit var transId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_info)
        var bundle: Bundle? = intent.extras
         transId = bundle!!.getString("transId")
        actionBar.setDisplayHomeAsUpEnabled(true)
        Toast.makeText(this, " TRansid ===== ${transId}", Toast.LENGTH_SHORT).show()

        val transact : TranscationShare? = TranscationShare.shareIdMap.get(transId.toInt())

        if(transact != null)
        {
            Toast.makeText(this,"in info activity ${transact.transId}",Toast.LENGTH_SHORT).show()
        }
        var desc : TextView =  findViewById<TextView>(R.id.info_desc)
        var amount : TextView = findViewById<TextView>(R.id.info_amount)
        var owner : TextView = findViewById<TextView>(R.id.info_to)

        desc.text = transact?.basic_info?.title
        amount.text = transact?.balance.toString()
        owner.text = Users.userMap.get(transact?.owner)

        var settleButton : Button =  findViewById<Button>(R.id.info_settle_button)

        settleButton.setOnClickListener(){
                showSettleDialog(transact)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("In infoactivity","vanduruchu")
        val transact : TranscationShare? = TranscationShare.shareIdMap.get(transId.toInt())
        var desc : TextView =  findViewById<TextView>(R.id.info_desc)
        var amount : TextView = findViewById<TextView>(R.id.info_amount)
        var owner : TextView = findViewById<TextView>(R.id.info_to)

        desc.text = transact?.basic_info?.title
        amount.text = transact?.balance.toString()
        owner.text = Users.userMap.get(transact!!.owner)

        var settleButton : Button =  findViewById<Button>(R.id.info_settle_button)
        if(amount.text.equals(0.0))
        {
            settleButton.text = "Settled"
            settleButton.isEnabled = false
        }
        else
        {
            settleButton.setOnClickListener(){
                showSettleDialog(transact)
            }

        }



    }
    fun showSettleDialog(transact : TranscationShare?){
        val dialog = Dialog(this)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.settle_dialog)
         var sendto = dialog.findViewById<TextView>(R.id.settle_to) as TextView
         var amount = dialog.findViewById<EditText>(R.id.settle_amount) as EditText

        sendto.text = Users.userMap.get(transact?.owner)

        val saveBtn = dialog.findViewById<Button>(R.id.settle_save_button) as Button
        var dbhelpher= DBHelper(this,null)
        var settleFlag : Int  = 0
        saveBtn.setOnClickListener {
            var settleAmount: String = amount.text.toString()
            if(settleAmount != "")
            {
                if (transact?.balance?.compareTo(settleAmount.toDouble()) == -1) {
                    Toast.makeText(this, " settled is greater than amount ", Toast.LENGTH_SHORT).show()
                    //onBackPressed()

                }
                if (transact?.balance?.compareTo(settleAmount.toDouble()) == 0) {
                    transact?.balance = 0.0
                    if(transact?.balance == 0.0) {
                        settleFlag = 1
                        transact?.settle_flag = 1
                        transact?.balance = 0.0
                        TranscationShare.shareIdMap.put(transact?.transId.toInt(), transact)
                    }
                    Toast.makeText(this, " amount equals $settleAmount", Toast.LENGTH_SHORT).show()
                    if(dbhelpher.updateTable(transact?.transId.toInt(),settleAmount.toDouble(),0.0 ,1) == true)
                    {
                        Log.e("update", "Share Settled")
                        transact?.settle_flag = 1
                        transact?.balance = 0.0
                        onBackPressed()
                    }
                    //  dbhelpher.updateTable(transact?.transId.toInt(),settleAmount.toDouble(),0.0 ,1)
                }
                if (transact?.balance?.compareTo(settleAmount.toDouble()) == 1) {
                    var balance = (transact!!.balance - settleAmount.toDouble()).toDouble()
                    transact?.balance = balance
                    transact?.paid = transact?.paid + settleAmount.toDouble()
                    TranscationShare.shareIdMap.put(transact?.transId.toInt(), transact)
                    Toast.makeText(this, "save clicked ${settleAmount} balance : $balance ", Toast.LENGTH_SHORT).show()
                    if(dbhelpher.updateTable(transact?.transId.toInt(),settleAmount.toDouble(),balance ,0) == true)
                    {
                        Log.e("Upadte" , "Partial Update")
                    }
                    // dbhelpher.updateTable(transact?.transId.toInt(),settleAmount.toDouble(),balance ,0)
                }
             }
            else
            {
                Toast.makeText(this, "Fill the amount  ", Toast.LENGTH_SHORT).show()
            }
          //  transact?.paid = settleAmount.toDouble()
           // var balance = (transact!!.amount - settleAmount.toDouble()).toDouble()



            //transact?.amount = settleAmount.toDouble()
            dialog.dismiss()
            onResume()



        }
        dialog.show()

    }
    override fun onNavigateUp(): Boolean {

        onBackPressed()
        return true
    }
}
