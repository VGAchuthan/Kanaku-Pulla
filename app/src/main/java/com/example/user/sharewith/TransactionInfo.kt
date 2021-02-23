package com.example.user.sharewith

import java.util.*

/**
 * Created by User on 17-01-2021.
 */
class TransactionInfo {
    var title : String = ""
    var desc : String = ""
    var date : String = ""
    var place : String = ""
    constructor()


    constructor(title : String, desc : String, date : String , place : String)
    {
        this.title = title
        this.desc = desc
        this.date = date
        this.place = place
    }
    fun getTransactionTitle():String{
        return this.title
    }
    fun getTransactionDesc() :String{
        return this.desc
    }
    fun getTransactionDate() :String{
        return this.date
    }
    fun getTransactionPlace() :String{
        return this.place
    }
}