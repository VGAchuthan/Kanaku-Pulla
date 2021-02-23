package com.example.user.sharewith

/**
 * Created by User on 17-01-2021.
 */
enum class expenseStyle{ OWNER , LENDER}
class TranscationShare {
    var transId : Long = 0
    var amount  : Double = 0.0
    var paid : Double = 0.0
    var balance : Double = 0.0
    var settle_flag : Int = 0
    var owner : String = ""
    var isGroup : Int = 0
    var style : expenseStyle = expenseStyle.OWNER

    lateinit var basic_info : TransactionInfo
    lateinit var contact : TransactionContactInfo
    constructor()

    constructor(amount: Double) {
        this.amount = amount
    }
    constructor(transid : Long,amount: Double, paid: Double, balance: Double, settle_flag: Int, owner: String, basic_info: TransactionInfo, contact: TransactionContactInfo, isGroup : Int) {
        this.amount = amount
        this.paid = paid
        this.balance = balance
        this.settle_flag = settle_flag
        this.owner = owner
        this.basic_info = basic_info
        this.contact = contact
        this.isGroup = isGroup
        this.transId = transid

    }



    constructor(transid : Long,amount: Double, paid: Double, balance: Double, settle_flag: Int, owner: String, basic_info: TransactionInfo, contact: TransactionContactInfo, isGroup : Int, style : expenseStyle) {
        this.amount = amount
        this.paid = paid
        this.balance = balance
        this.settle_flag = settle_flag
        this.owner = owner
        this.basic_info = basic_info
        this.contact = contact
        this.isGroup = isGroup
        this.transId = transid
        this.style = style
    }
    fun printAllData( values : ArrayList<TranscationShare>)
    {
        for(v in values)
        {
            print( " ${v.transId} :: ${v.amount} :: ${v.balance} :: ${v.paid} :: settleflag ${v.settle_flag} " )
        }
    }
    companion object {
        var shareIdMap : HashMap<Int, TranscationShare> = HashMap()
    }

}