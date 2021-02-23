package com.example.user.sharewith

/**
 * Created by User on 05-01-2021.
 */
class Users {

    var user_name : String = ""
    var user_email : String = ""
    var  user_mobile : String = ""


    constructor(user_name : String, user_email: String, user_mobile : String)
    {
        this.user_email = user_email
        this.user_name = user_name
        this.user_mobile = user_mobile

    }

    fun getUsername():String{
        return this.user_name
    }

    fun getUseremail() : String{
        return this.user_email
    }

    fun getUserMobile():String{
        return this.user_mobile
    }
    companion object{
        var userMap : HashMap<String, String> = HashMap()
        //userMap.put("")
    }


}