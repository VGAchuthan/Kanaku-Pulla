package com.example.user.sharewith

/**
 * Created by User on 17-01-2021.
 */
class TransactionContactInfo {
    var owner : String = ""
    var sharing_count : Int= 0
    var contacts = ArrayList<String>()
    constructor()

    constructor(owner : String, sharing_count : Int, contacts : ArrayList<String>)
    {
        this.owner = owner
        this.sharing_count = sharing_count
        this.contacts = contacts

    }
    fun getSharingContacts() : ArrayList<String>
    {
        return this.contacts
    }

    fun getSharingCount() : Int
    {
        return this.sharing_count
    }
    fun getSharingOwner() : String{
        return this.owner
    }

}