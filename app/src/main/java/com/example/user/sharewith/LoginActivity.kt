package com.example.user.sharewith

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Allocation
import android.util.Log
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.widget.CardView
import android.widget.*


class LoginActivity : Activity() {
    lateinit var input : EditText
    lateinit  var  password : EditText
    lateinit var dbHelper : DBHelper
    var pswdEnc = PasswordEncryption()
    companion object {
        var sharedPreFile : String = "ShareWithPreference"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //setTheme(R.style.Base_Theme_AppCompat_Light_DarkActionBar)
       /* if(sharedPreFile.isNotEmpty())
        {
            setContentView(R.layout.activity_share)
            finish()
        }*/
        setContentView(R.layout.activity_login)
        //val backgroundImage : Drawable = resources.getDrawable(R.drawable.background)
        //backgroundImage.alpha = 50
        var mContainerView : ScrollView = findViewById<ScrollView>(R.id.container) as ScrollView
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.background2)
        val blurredBitmap = BlurBuilder.blur(this, originalBitmap)
        mContainerView.setBackground(BitmapDrawable(resources, blurredBitmap))
        dbHelper = DBHelper(this,null)
        val signup_link = findViewById<TextView>(R.id.signup_link) as TextView

        signup_link?.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)

        }
       val login_button = findViewById<Button>(R.id.login_button) as Button

        login_button?.setOnClickListener{
            getLoginProcess()
        }



    }


    fun getLoginProcess()
    {
        input = findViewById<EditText>(R.id.input_user) as EditText
        password = findViewById<EditText>(R.id.input_password) as EditText

        if(input.text.toString()!= "" && password.text.toString() != "")
        {
            if(isEmailValid(input.text.toString()))
            {
                val cursor : Cursor = dbHelper.getUserByEmail(input.text.toString(),pswdEnc.encodeData(password.text.toString()))
                try {
                    if (cursor!!.moveToFirst()) {

                        if (cursor == null || cursor.count == -1) {
                            Log.e("Bye", "User not found by email")
                            Toast.makeText(this,"Check Username and password",Toast.LENGTH_SHORT).show()
                            input.text.clear()
                            password.text.clear()
                        } else {
                            val sharedPref: SharedPreferences = this.getSharedPreferences(sharedPreFile, Context.MODE_PRIVATE)
                            val sharedEmail = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_EMAILID))
                            val sharedMobile = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_MOBILE))
                            val sharedName = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME))
                            val editor: SharedPreferences.Editor = sharedPref.edit()
                            editor.putString("EMAIL_KEY", sharedEmail)
                            editor.putString("MOBILE_KEY", sharedMobile)
                            editor.putString("NAME_KEY", sharedName)
                            editor.apply()
                            editor.commit()
                            val intent = Intent(this, TabbedMainActivity::class.java)
                            startActivity(intent)
                            finish()

                        }
                    }


                    }
                    catch(e : Exception) {
                        Log.e("exception in login  ", "  in catch $e")
                    }



            }

            else if(isMobileNumber(input.text.toString()))
            {
                val cursor : Cursor = dbHelper.getUserByMobile(input.text.toString(),pswdEnc.encodeData(password.text.toString()))
                try{
                    if(cursor!!.moveToFirst())
                    {
                        if(cursor == null || cursor.count == -1)
                        {
                            Log.e("Bye","User not found by mobile")
                            //Toast.makeText(this,"")
                        }
                        else{
                            val sharedPref : SharedPreferences = this.getSharedPreferences(sharedPreFile, Context.MODE_PRIVATE)
                            val sharedEmail = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_EMAILID))
                            val sharedMobile = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_MOBILE))
                            val sharedName = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME))
                            val editor:SharedPreferences.Editor =  sharedPref.edit()
                            editor.putString("EMAIL_KEY",sharedEmail)
                            editor.putString("MOBILE_KEY",sharedMobile)
                            editor.putString("NAME_KEY",sharedName)
                            editor.apply()
                            editor.commit()
                            val intent = Intent(this, TabbedMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                }
                catch(e : Exception){
                    Log.e("exception ", " $e")
                }


            }

        }


    }



    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isMobileNumber(mobile : String) : Boolean{
        return android.util.Patterns.PHONE.matcher(mobile).matches()
    }

}
