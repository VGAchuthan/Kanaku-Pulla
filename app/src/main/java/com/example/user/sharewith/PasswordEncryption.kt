package com.example.user.sharewith

import android.util.Base64

/**
 * Created by User on 04-01-2021.
 */
class PasswordEncryption {

    fun encodeData(name: String): String{
        val encoded = Base64.encode(name.toByteArray(), Base64.DEFAULT)
        return String(encoded)
    }

    fun decodeData(name: String): String{
        val encoded = Base64.decode(name.toByteArray(), Base64.DEFAULT)
        return String(encoded)
    }
}