package com.example.user.sharewith

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.app.TabActivity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TabHost
import android.widget.Toast
import android.widget.Toolbar

/**
 * Created by User on 09-02-2021.
 */
class TabbedMainActivity : TabActivity()   {
    lateinit var sharedPref : SharedPreferences
    lateinit var dbhelper : DBHelper
    lateinit var  userid : String
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        sharedPref = getSharedPreferences(LoginActivity.sharedPreFile, Context.MODE_PRIVATE)

        userid = sharedPref.getString("MOBILE_KEY","DEFAULT")




        actionBar.setDisplayHomeAsUpEnabled(true)

        Toast.makeText(this,"welcome $userid", Toast.LENGTH_LONG).show()
        //val recyclerView : RecyclerView = findViewById<RecyclerView>(R.id.recycler_view_share) as RecyclerView
        //recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL,false)
        dbhelper = DBHelper(this, null)
        getAllUsers()

        var host : TabHost =getTabHost()
        host.addTab(host.newTabSpec("Pending").setIndicator("Pending").setContent(Intent(this,ShareActivity::class.java)))
        host.addTab(host.newTabSpec("History").setIndicator("History").setContent(Intent(this,HistoryTransactionActivity::class.java)))
         /*val toolbar : Toolbar = Toolbar(this)//findViewById(R.id.toolbar)
        val param : LinearLayout.LayoutParams =  LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150)
        toolbar.layoutParams = param
        toolbar.popupTheme = R.style.AppTheme
        toolbar.setBackgroundColor(resources.getColor(R.color.design_default_color_primary))
        toolbar.visibility = View.VISIBLE
         setActionBar(toolbar)*/

    }
    fun getAllUsers() : HashMap<String, String>{

        val cursor : Cursor = dbhelper.getAllUser()
        try {
            if (cursor!!.moveToFirst()) {

                while(cursor.isAfterLast == false){
                    if (cursor == null || cursor.count == -1) {
                        /*Log.e("Bye", "User not found by email")
                        Toast.makeText(this,"Check Username and password",Toast.LENGTH_SHORT).show()
                        input.text.clear()
                        password.text.clear()*/
                    } else {

                        val userMobile = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_MOBILE))
                        val userName = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME))

                        Users.userMap.put(userMobile,userName)


                    }
                    cursor.moveToNext()

                }



            }



        }
        catch(e : Exception) {
            Log.e("exception in login  ", "  in catch $e")
        }
        return Users.userMap
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_activity_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {

        R.id.action_new ->
        {
            // insertHelper.truncateLoginTable()
            val intent = Intent(this, CreateNewActivity::class.java)
            startActivity(intent)
            true
        }
         R.id.action_new_group ->
        {
            // insertHelper.truncateLoginTable()
            val intent = Intent(this, CreateNewGroupActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.action_logout ->
        {
            // insertHelper.truncateLoginTable()
            var editor = sharedPref.edit()
            editor.remove("EMAIL_KEY")
            editor.remove("NAME_KEY")
            editor.remove("MOBILE_KEY")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            true
        }

        R.id.logout_icon ->
        {
            // insertHelper.truncateLoginTable()
            var editor = sharedPref.edit()
            editor.remove("EMAIL_KEY")
            editor.remove("NAME_KEY")
            editor.remove("MOBILE_KEY")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        else ->
        super.onOptionsItemSelected(item)
    }
    override fun onNavigateUp(): Boolean {

        onBackPressed()
        return true
    }
}