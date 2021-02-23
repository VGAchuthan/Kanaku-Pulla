package com.example.user.sharewith

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_new.*
import java.time.LocalDate
import java.time.LocalDateTime

class CreateNewActivity : Activity() {

    lateinit var basic_info : TransactionInfo
    lateinit var contact_info : TransactionContactInfo
    lateinit var transaction_share : TranscationShare
    lateinit var dbhelper : DBHelper
    lateinit var sharedPref : SharedPreferences
    //val input_settle_flag : EditText = findViewById(R.id.share_)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_new)
        var userid : String
        sharedPref = getSharedPreferences(LoginActivity.sharedPreFile, Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor
        userid = sharedPref.getString("MOBILE_KEY","DEFAULT")
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        Toast.makeText(this,"welcome $userid", Toast.LENGTH_LONG).show()
        dbhelper = DBHelper(this,null)

        var transid : Int = dbhelper.getRowCount() + 1

        Toast.makeText(this, "Transid $transid",Toast.LENGTH_SHORT).show()
        val create_button :Button = findViewById<Button>(R.id.create_button) as Button
        val contactButton : EditText = findViewById<EditText>(R.id.share_with) as EditText

        create_button.setOnClickListener()
        {
            getValues(userid)
        }
        contactButton.setOnClickListener(){
            getContact()
        }



    }

    fun getValues(userid : String)
    {
        val input_amount : EditText = findViewById<EditText>(R.id.share_amount) as EditText
        val input_title : EditText = findViewById<EditText>(R.id.share_title)as EditText
        val input_desc : EditText = findViewById<EditText>(R.id.share_desc)as EditText
        val input_place : EditText = findViewById<EditText>(R.id.share_place)as EditText
        val input_date : EditText = findViewById<EditText>(R.id.share_date)as EditText
        val input_share_with : EditText = findViewById<EditText>(R.id.share_with) as EditText


        var ownerid : String = userid
        var amount : String = input_amount.text.toString()
        var title : String = input_title.text.toString()
        var place : String = input_place.text.toString()
        var desc : String = input_desc.text.toString()
        var is_group : Int = 0
        var is_settled : Int = 0
        var paid : Double = 0.0
        var balance : Double = amount.toDouble()
        var date : String = input_date.text.toString()
        var sharewith : String = input_share_with.text.toString()
        var shareNum : String= sharewith.substring(sharewith.indexOf('(')+1,sharewith.indexOf(')'))
        dbhelper = DBHelper(this,null)
        var transid : Int = dbhelper.getRowCount() + 1

        //Toast.makeText(this, "Transid $transid :: $shareNum",Toast.LENGTH_SHORT).show()
       if(dbhelper.insertIntoTransactTable(ownerid,amount,paid.toString(),balance.toString(), is_settled,is_group))
        {
            Toast.makeText(this,"Inserted into Transact table", Toast.LENGTH_SHORT).show()
        }
        if(dbhelper.insertIntoTransactBasicTable(transid,title, desc, date, place))
        {
            Toast.makeText(this,"Inserted into Transact basic table", Toast.LENGTH_SHORT).show()
        }
        if(dbhelper.insertIntoTransactContactTable(transid,ownerid,1,shareNum ))
        {
            Toast.makeText(this,"Inserted into Transact contact table", Toast.LENGTH_SHORT).show()
        }







    }
    fun getContact()
    {
        var i = Intent(Intent.ACTION_PICK)
        i.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(i,111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 111 && resultCode == Activity.RESULT_OK){
            var contactUri : Uri = data?.data?:return
            var cols : Array<String> = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER)
            var rs : Cursor? = contentResolver.query(contactUri, cols, null,null, null)
            if(rs!!.moveToFirst()){
                share_with.setText(rs.getString(0) +"("+ rs.getString(1) + ")")
                //Users.userMap.put()
                if(!Users.userMap.contains(rs.getString(1)))
                {
                    Users.userMap.put(rs.getString(1),rs.getString(0))
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()


        if(id == R.id.action_logout)
        {
            // insertHelper.truncateLoginTable()
            var editor = sharedPref.edit()
            editor.remove("EMAIL_KEY")
            editor.remove("NAME_KEY")
            editor.remove("MOBILE_KEY")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigateUp(): Boolean {

        onBackPressed()
        return true
    }
}
