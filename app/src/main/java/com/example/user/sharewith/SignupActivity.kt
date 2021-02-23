package com.example.user.sharewith

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast

class SignupActivity : Activity() {
    var pswdEnc = PasswordEncryption()

    lateinit var dbHelper : DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        actionBar.setDisplayHomeAsUpEnabled(true)
        var mContainerView : ScrollView = findViewById<ScrollView>(R.id.signup_container) as ScrollView
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.background2)
        val blurredBitmap = BlurBuilder.blur(this, originalBitmap)
        mContainerView.setBackground(BitmapDrawable(resources, blurredBitmap))
        dbHelper = DBHelper(this,null)

        val signup_button = findViewById<Button>(R.id.signup_button) as Button

        signup_button.setOnClickListener{
            getCredentials()
        }

    }
    fun getCredentials()
    {
        val input_name = findViewById<EditText>(R.id.signup_user) as EditText
        val input_mobile = findViewById<EditText>(R.id.signup_mobile_numebr) as EditText
        val input_emailId  = findViewById<EditText>(R.id.signup_email) as EditText
        val input_password = findViewById<EditText>(R.id.signup_password) as EditText
        val input_cnfmPassword = findViewById<EditText>(R.id.signup_conform_password) as EditText

        var name = input_name.text.toString()
        var mobile = input_mobile.text.toString()
        var emailId = input_emailId.text.toString()
        var password = input_password.text.toString()
        var cnfmpassword = input_cnfmPassword.text.toString()


        if(password.equals(cnfmpassword) && emailId != "" && name != "" && mobile != ""  ) {
            // var name = firstname + " " + lastname
            if (isEmailValid(emailId) == false) {
                val t = Toast.makeText(this, "Give Valid email Id", Toast.LENGTH_LONG)
                t.show()
                input_emailId.text.clear()
                input_emailId.requestFocus()
                return
            }
            if (isPasswordValid(password) == false) {
                val t = Toast.makeText(this, "Give Valid Password", Toast.LENGTH_LONG)
                t.show()
                input_password.text.clear()
                input_cnfmPassword.text.clear()
                input_password.requestFocus()
                input_cnfmPassword.requestFocus()
                return

            }
            if(isMobileNumber(mobile) == false)
            {
                val t = Toast.makeText(this, "Give Valid Mobile Number", Toast.LENGTH_LONG)
                t.show()
                input_password.text.clear()
                input_cnfmPassword.text.clear()
                input_password.requestFocus()
                input_cnfmPassword.requestFocus()
                return
            }
            if(dbHelper.checkIfUserAvailable(mobile) == 0)
            {
                //store to table signup
                Users.userMap.put(mobile,name)
                Toast.makeText(this,"user not available", Toast.LENGTH_LONG).show()
                var encryptedPassword = pswdEnc.encodeData(password)
                dbHelper.insertUser(emailId,encryptedPassword, name, mobile)

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            else
            {

                Toast.makeText(this,"user available", Toast.LENGTH_LONG).show()
            }
        }

    }
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun isPasswordValid(password : String ) : Boolean{
        val PASSWORD_REGEX = """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!\-_?&])(?=\S+$).{8,}""".toRegex()
        return PASSWORD_REGEX.matches(password)

    }
    fun isMobileNumber(mobile : String) : Boolean{
        return android.util.Patterns.PHONE.matcher(mobile).matches()
    }
    override fun onNavigateUp(): Boolean {

        onBackPressed()
        return true
    }

}
