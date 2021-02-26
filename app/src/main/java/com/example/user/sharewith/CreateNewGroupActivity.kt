package com.example.user.sharewith

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_new.*

/**
 * Created by User on 05-02-2021.
 */
class CreateNewGroupActivity : Activity() {
    lateinit var basic_info : TransactionInfo
    lateinit var contact_info : TransactionContactInfo
    lateinit var transaction_share : TranscationShare
    lateinit var dbhelper : DBHelper
    lateinit var sharedPref : SharedPreferences
    lateinit var currentEdittextView : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        var userid : String
        sharedPref = getSharedPreferences(LoginActivity.sharedPreFile, Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor
        userid = sharedPref.getString("MOBILE_KEY","DEFAULT")
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        Toast.makeText(this,"welcome $userid", Toast.LENGTH_LONG).show()
        dbhelper = DBHelper(this,null)

        var transid : Int = dbhelper.getRowCount() + 1
        CreateNewGroupActivity.contactCount = 0


        Toast.makeText(this, "Transid $transid", Toast.LENGTH_SHORT).show()
       /* val create_button : Button = findViewById<Button>(R.id.create_group_count_button) as Button
       // val contactButton : EditText = findViewById<EditText>(R.id.share_with) as EditText

        create_button.setOnClickListener()
        {
            getValues(userid)
        }*/
        val create_group_button: Button = findViewById<Button>(R.id.create_group_count_button) as Button
        // val contactButton : EditText = findViewById<EditText>(R.id.share_with) as EditText
        var contact = ArrayList<String>()
        create_group_button.setOnClickListener()
        {
            Log.e("check", "clicked")
            addContacts(5)
        }
        val save_button : Button = findViewById<Button>(R.id.create_save) as Button
        save_button.setOnClickListener(){
            var contactList : ArrayList<String> = getContacts()
            getValues(userid, contactList)
        }

    }
    fun getContacts() : ArrayList<String>{
        var listOfContacts = ArrayList<String>()
        var shareNum : String
        val ll : LinearLayout = findViewById(R.id.contact_layout)
        var count = ll.childCount
        for(i in 1..count){
            var view : View = ll.getChildAt(i - 1)
            if(view is EditText){
                shareNum = view.text.substring(view.text.indexOf('(')+1,view.text.indexOf(')'))

                listOfContacts.add(shareNum)
                Log.e("contatcs: ", "${view.text.toString()}")
            }
        }

        return listOfContacts
    }
    fun getValues(userid : String, contactList : ArrayList<String>) {
        var input_amount: EditText = findViewById<EditText>(R.id.share_group_amount) as EditText
        val input_title: EditText = findViewById<EditText>(R.id.share_group_title) as EditText
        val input_desc: EditText = findViewById<EditText>(R.id.share_group_desc) as EditText
        val input_place: EditText = findViewById<EditText>(R.id.share_group_place) as EditText
        val input_date: EditText = findViewById<EditText>(R.id.share_group_date) as EditText
        //val input_share_count: EditText = findViewById<EditText>(R.id.share_group_count) as EditText
        //if(input_amount != null && input_title != null && input_desc != null && input_place != null && input_share_count != null)
        //{
        var ownerid: String = userid
        var amount: String = input_amount.text.toString()
        var title: String = input_title.text.toString()
        var place: String = input_place.text.toString()
        var desc: String = input_desc.text.toString()
        //var shareCount: String = input_share_count.text.toString()
        var is_group: Int = 1
        var is_settled: Int = 0
        var paid: Double = 0.0
        var balance: Double = amount.toDouble()/*/shareCount.toDouble()*/
        var date: String = input_date.text.toString()
        var transid: Int = dbhelper.getRowCount() + 1
        /*Log.e("contact variable","COLUMN_TRANSACT_CONTACT_LIST_0"+contactList.size)
        for(i in 1..contactList.size){
        Log.e("contact variable","COLUMN_TRANSACT_CONTACT_LIST_0"+"$i")}*/
        if(dbhelper.insertIntoTransactTable(ownerid,amount,paid.toString(),balance.toString(), is_settled,is_group))
        {
            Toast.makeText(this,"Inserted into Transact table", Toast.LENGTH_SHORT).show()
        }
        if(dbhelper.insertIntoTransactBasicTable(transid,title, desc, date, place))
        {
            Toast.makeText(this,"Inserted into Transact basic table", Toast.LENGTH_SHORT).show()
        }
        if(dbhelper.insertIntoTransactContactTableForGroup(transid,ownerid,contactList.size,contactList))
        {
            Toast.makeText(this,"Inserted into Transact Contact for group table", Toast.LENGTH_SHORT).show()
        }

   // }


    }
    fun addContacts(shareCount : Int) {
        val dialog = Dialog(this)
        var count : Int = 0
        Log.w("in get","$shareCount")

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)

        /*val ll  : LinearLayout = LinearLayout(this)
        ll.orientation = LinearLayout.VERTICAL
        ll.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)*/
        val param: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        param.setMargins(0,8,0,8)
        val ll : LinearLayout = findViewById<LinearLayout>(R.id.contact_layout) as LinearLayout
       // for(i in 1..shareCount)
        //{
            //Log.e("index: ","$i")
            var etv : EditText = EditText(this)
           //tv.height = ViewGroup.LayoutParams.WRAP_CONTENT
           //tv.width = ViewGroup.LayoutParams.MATCH_PARENT
        etv.layoutParams=param
         //   tv.textSize = 15.0f
        etv.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.contact_img,0)
        etv.setTextColor(this.resources.getColor(R.color.text_white))
            CreateNewGroupActivity.contactCount +=1
        etv.hint = "Contact "+"${CreateNewGroupActivity.contactCount}"
        etv.id = CreateNewGroupActivity.contactCount
        etv.setOnClickListener(){
            getContact(etv)
        }


            //tv.setPadding(15,0,15,15)
            //tv.layoutParams = param

            // tv.id = count
            //count += 1
            ll.addView(etv)
        //}



    }
    fun getContact(view : EditText)
    {
        this.currentEdittextView = view
        var i = Intent(Intent.ACTION_PICK)
        i.putExtra("id",view.id)
        i.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(i,111)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val id = intent.getStringArrayExtra("id")
        if(requestCode == 111 && resultCode == Activity.RESULT_OK){
            var contactUri : Uri = data?.data?:return
            var cols : Array<String> = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER)
            var rs : Cursor? = contentResolver.query(contactUri, cols, null,null, null)
            if(rs!!.moveToFirst()){
                this.currentEdittextView.setText(rs.getString(0) +"("+ rs.getString(1) + ")")
                //Users.userMap.put()
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

    companion object{

        var contactCount : Int = 0;

    }
}