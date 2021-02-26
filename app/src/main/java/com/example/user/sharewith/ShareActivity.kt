package com.example.user.sharewith

import android.animation.Animator
import android.app.Activity
import android.app.TabActivity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TabHost
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_share.*
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.util.*

class ShareActivity : Activity() {
    lateinit var sharedPref : SharedPreferences
    lateinit var dbhelper : DBHelper
    lateinit var  userid : String
    //val transact = ArrayList<TranscationShare>()
    var isFabExpanded : Boolean = false
    var isFABOpen : Boolean =false
    lateinit var recyclerView : RecyclerView
    lateinit var pie : PieChart
    lateinit var chartDetailsLayout : LinearLayout
    lateinit var amount : TextView
    companion object {
        var flag : Int = 1
        var historyTransaction = ArrayList<TranscationShare>()
        var transact = ArrayList<TranscationShare>()
        //lateinit
       //  var dbhelper : DBHelper = DBHelper(ShareActivity::class.java,null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        dbhelper = DBHelper(this,null)
       // userid : String = ""
        sharedPref = getSharedPreferences(LoginActivity.sharedPreFile, Context.MODE_PRIVATE)
        amount  = findViewById<TextView>(R.id.chart_amount) as TextView

        userid = sharedPref.getString("MOBILE_KEY","DEFAULT")

        Toast.makeText(this,"welcome $userid",Toast.LENGTH_LONG).show()
        Log.e("user Count", "${Users.userMap.size}")
        //val recyclerView : RecyclerView = findViewById<RecyclerView>(R.id.recycler_view_share) as RecyclerView
        //recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL,false)

       fillAdapter(userid)
        chartDetailsLayout = findViewById<LinearLayout>(R.id.chart_details_layout) as LinearLayout
        pie = findViewById<PieChart>(R.id.piechart) as PieChart
        pie.legendTextSize = 20.0f
        fillPieChart()
        /*pie.addPieSlice(
                PieModel(
                        "R",
                (20f),
                Color.parseColor("#FFFF26")))
        pie.addPieSlice(
                PieModel(
                        "R",
                        (20f),
                        Color.parseColor("#FFA726")))
        pie.addPieSlice(
                PieModel(
                        "R",
                        (40f),
                        Color.parseColor("#FFAAFF")))
        pie.addPieSlice(
                PieModel(
                        "R",
                        (20f),
                        Color.parseColor("#FFA726")))*/
        if(pie.innerValueSize.toInt() == -1)
        {
            Log.e("Pie"," Pie is null da ambi")
        }
        val temppie: PieChart = pie
        if(chartDetailsLayout.childCount == 0)
        {
            Log.e("share activity on create","null chart details")
            val chart_card : CardView = findViewById<CardView>(R.id.chart_card) as CardView
            //chart_card.removeAllViews()
            chart_card.visibility = View.GONE
        }
        pie.startAnimation()
        /* fab.setOnClickListener {

             if (View.GONE == fabBGLayout.visibility) {
                 showFABMenu()
             } else {
                 closeFABMenu()
             }
         }

         fabBGLayout.setOnClickListener { closeFABMenu() }*/


    }

    override fun onResume() {
        super.onResume()
        fillAdapter(this.userid)
        pie.clearChart()
        //pie = findViewById<PieChart>(R.id.piechart) as PieChart
        fillPieChart()
        pie.startAnimation()
    }

   /* override fun onRestart() {
        super.onRestart()
        fillAdapter(this.userid)
    }*/
    fun fillAdapter(userId : String)
    {
        val recyclerView : RecyclerView = findViewById<RecyclerView>(R.id.recycler_view_share) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL,false)
       // var transact =ArrayList<TranscationShare>()
        ShareActivity.transact = getAllTransact(userId)
        var t : TranscationShare = TranscationShare()
      //  t.printAllData(transact)
        for(v in ShareActivity.transact)
        {
            Log.e( "VAlues : ", " ${v.transId} :: ${v.amount} :: ${v.balance} :: ${v.paid} :: settleflag ${v.settle_flag} " )
        }

       val adapter = ShareCardAdapter(this,ShareActivity.transact,userId)
       recyclerView.adapter= adapter
    }
    fun fillPieChart(){

        pie.clearChart()
        chartDetailsLayout.removeAllViews()
        var total = getTotalAmountOwe()
        var groupedTransaction = ShareActivity.transact.groupBy {it.owner}

        for(groupTransaction in groupedTransaction.values){
            var model : PieModel
            Log.e("in fill pie chart group trans","${groupTransaction.first().owner} : ${groupTransaction.size} : ${groupTransaction.first().amount}")
            var groupAmount : Double = 0.0
            for(value in groupTransaction)
            {
                Log.e("\tin values","${value.owner} : ${value.balance} : ${value.style} : ${value.contact.sharing_count}")
                if(value.style == expenseStyle.LENDER && value.settle_flag == 0)
                {
                    groupAmount+=value.balance


                }
                if(value.isGroup == 1 &&  value.style == expenseStyle.LENDER && value.settle_flag == 0 )
                {
                    groupAmount+=value.balance / value.contact.sharing_count
                    groupAmount-=value.balance

                }



            }
           // if(groupTransaction != null || groupedTransaction.values.is)
            //{
                val random: Random = Random()
                model = PieModel(
                        groupTransaction.first().owner,
                        ((groupAmount / total) * 100).toFloat(),
                        Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                model.setShowLabel(true)
                model.value = ((groupAmount / total) * 100).toFloat()
                Log.e("chart model ", " ${model.value.toInt()} : ${model.legendLabel}")
                fillChartDetails(model)
            if(model.value.toInt() !=0 ){
                pie.addPieSlice(model)
            }

           // }




        }



        }
    fun fillChartDetails(pieModel : PieModel){
        //chartDetailsLayout
        Log.e("in chart pie details","${pieModel.value.toInt()}")
        if(pieModel.value.toInt() != 0 ){

            var chartChildLayout: LinearLayout = LinearLayout(this)
            chartChildLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            chartChildLayout.orientation = LinearLayout.HORIZONTAL
            var view: View = View(this)
            val colorIconparam: LinearLayout.LayoutParams = LinearLayout.LayoutParams(40, 40)
            colorIconparam.setMargins(0, 8, 0, 8)
            view.layoutParams = colorIconparam
            view.setBackgroundColor(pieModel.color)
            val chartText: TextView = TextView(this)
            val textParam: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            //(pieModel.value)*(amount.text.toString().toDouble())
            //Math.round(pieModel.value).toString()
            chartText.text = Math.round((((pieModel.value)*(amount.text.toString().toDouble()))/100.0)).toString() + " -> " + Users.userMap[pieModel.legendLabel]

            pieModel.value.toFloat()

            chartText.layoutParams = textParam
            chartText.setTextColor(resources.getColor(R.color.text_color))
            chartChildLayout.addView(view)
            chartChildLayout.addView(chartText)
            chartDetailsLayout.addView(chartChildLayout)
        }

        /* for(share in ShareActivity.transact)
         {

             val random : Random = Random()
             val model =  PieModel(
                     share.owner,
                     ((share.balance/total) * 100).toFloat() ,
                     Color.argb(255,random.nextInt(256),random.nextInt(256),random.nextInt(256)  ))
             model.setShowLabel(true)

             pie.addPieSlice(model)      }*/


    }
    fun getTotalAmountOwe() : Double{
        var total_amount : Double = 0.0
        for(share in ShareActivity.transact)
        {
            if(share.style == expenseStyle.LENDER){
            total_amount+= share.balance
                if(share.isGroup == 1 )
                {
                    total_amount-= share.balance
                    total_amount+=share.amount / share.contact.sharing_count
                }
            }
        }

        amount.text = total_amount.toString()
        return total_amount
    }
    fun getAllTransact(userid : String): ArrayList<TranscationShare>{
        var transactionContact: TransactionContactInfo
        var transactionInfo : TransactionInfo
        var transactionShare : TranscationShare
        var title : String
        var desc : String
        var place : String
        var date : String
        var amount  : String
        var paid :String
        var balance : String
        var settleFlag  :Int
        var isGroup : Int
        var transid : Int
        var owner : String
        var lender : String
        var sharing_count : Int
        var contact = ArrayList<String>()
        var share = ArrayList<TranscationShare>()
        var style = expenseStyle.OWNER


        try {
            //code to get contact where one of contact is user
            var cursorForContact: Cursor = dbhelper.getTransactContact(userid)
            if(cursorForContact!!.moveToFirst()) {
                while (cursorForContact.isAfterLast == false) {
                     transid = (cursorForContact.getInt(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACTION_ID)))
                     owner  = (cursorForContact.getString(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_OWNERID)))
                     //style = expenseStyle.OWNER
                    if(userid.equals(owner))
                    {
                        style = expenseStyle.OWNER
                    }
                     sharing_count  = (cursorForContact.getInt(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_SHARING_COUNT)))
                    // contact = ArrayList<String>()

                    if(sharing_count == 1)
                    {
                        lender   = (cursorForContact.getString(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_CONTACT_LIST_01)))
                        if(userid.equals(lender))
                        {
                            style = expenseStyle.LENDER
                        }
                        Log.e("style from share activity", " $transid $style - $lender")
                        contact.add(lender)
                    }
                    else
                    {
                        for(i in 1..sharing_count){
                            if(sharing_count == 10)
                            {
                                lender   = (cursorForContact.getString(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_CONTACT_LIST_10)))
                                if(userid.equals(lender))
                                {
                                    style = expenseStyle.LENDER
                                }
                                contact.add(lender)
                            }
                            else {
                                lender = (cursorForContact.getString(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_CONTACT_LIST_0 + "$i")))
                                if(userid.equals(lender))
                                {
                                    style = expenseStyle.LENDER
                                }
                                contact.add(lender)
                            }
                            Log.e("style from share activity", " $transid $style - $lender")
                        }



                    }


                    // code to get basic info by transid
                    var cursorForInfo : Cursor = dbhelper.getInfoByTransId(transid)
                    if(cursorForInfo!!.moveToFirst()) {
                        while (cursorForInfo.isAfterLast == false) {
                             title  = (cursorForInfo.getString(cursorForInfo.getColumnIndex(DBHelper.COLUMN_TRANSACT_BASIC_TITLE)))
                             desc  = (cursorForInfo.getString(cursorForInfo.getColumnIndex(DBHelper.COLUMN_TRANSACT_BASIC_DESCRIPTION)))
                             place  = (cursorForInfo.getString(cursorForInfo.getColumnIndex(DBHelper.COLUMN_TRANSACT_BASIC_PLACE)))
                             date  = (cursorForInfo.getString(cursorForInfo.getColumnIndex(DBHelper.COLUMN_TRANSACT_BASIC_DATE)))
                            // place to store share details
                            var cursorForShare : Cursor = dbhelper.getShareByTransId(transid)
                            if(cursorForShare!!.moveToFirst()) {
                                while (cursorForShare.isAfterLast == false) {
                                    // var owner : String  = (cursorForShare.getString(cursorForShare.getColumnIndex(DBHelper.COLUMN_TRANSACT_BASIC_TITLE)))
                                     amount   = (cursorForShare.getString(cursorForShare.getColumnIndex(DBHelper.COLUMN_AMOUNT)))
                                     paid   = (cursorForShare.getString(cursorForShare.getColumnIndex(DBHelper.COLUMN_PAID)))
                                     balance   = (cursorForShare.getString(cursorForShare.getColumnIndex(DBHelper.COLUMN_BALANCE)))
                                     settleFlag = (cursorForShare.getInt(cursorForShare.getColumnIndex(DBHelper.COLUMN_SETTLE_FLAG)))
                                     isGroup  = (cursorForShare.getInt(cursorForShare.getColumnIndex(DBHelper.COLUMN_IS_GROUP)))

                                    transactionContact = TransactionContactInfo(owner,sharing_count, contact)
                                    transactionInfo = TransactionInfo(title,desc,date,place)
                                    transactionShare = TranscationShare(transid.toLong(),amount.toDouble(),paid.toDouble(), balance.toDouble(),settleFlag,owner,transactionInfo,transactionContact,isGroup,style)

                                    if(settleFlag == 0){
                                        share.add(transactionShare)
                                    }
                                    else if (settleFlag == 1)
                                    {

                                        if(!ShareActivity.historyTransaction.contains(transactionShare)){
                                            ShareActivity.historyTransaction.add(transactionShare)
                                        }

                                    }

                                    TranscationShare.shareIdMap.put(transid,transactionShare)
                                    cursorForShare.moveToNext()

                                }
                            }

                            cursorForInfo.moveToNext()
                            cursorForShare.close()




                        }
                    }
                    cursorForContact.moveToNext()
                    cursorForInfo.close()





                }
            }
            cursorForContact.close()
        }
        catch(e : Exception)
        {
            Log.e("bye","exception $e ")
        }

        return  share
    }

    fun getTransact(transid: Int) : TranscationShare{
        var share : TranscationShare = TranscationShare()



        return share


    }

    fun getProcess(ownerid : String): ArrayList<Int>
    {
        var transids = ArrayList<Int>()
        var dbhelper1 = DBHelper(this,null)
      //  val id : Int

        var cursorForContact  : Cursor = dbhelper1.getTransactContact(ownerid)
        if(cursorForContact.moveToFirst()) {
            while (cursorForContact.isAfterLast == false) {
                /*transid = (cursorForContact.getInt(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACTION_ID)))
                owner = (cursorForContact.getString(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_OWNERID)))
                lender = (cursorForContact.getString(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_CONTACT_LIST_01)))
                sharing_count = (cursorForContact.getInt(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_SHARING_COUNT)))
                // contact = ArrayList<String>()
                contact.add(lender)*/
                val id = (cursorForContact.getInt(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACTION_ID)))
                transids.add(id)
                cursorForContact.moveToNext()
            }
        }
        cursorForContact.close()

        return transids
    }

    fun getBasicInfo(transid : Int) : TransactionInfo{
        var transids = ArrayList<Int>()
        var dbhelper1 = DBHelper(this,null)
        //  val id : Int
        var info : TransactionInfo  = TransactionInfo()

        var cursorForInfo  : Cursor = dbhelper.getInfoByTransId(transid)
        if(cursorForInfo.moveToFirst()) {
            while (cursorForInfo.isAfterLast == false) {
               val  title  = (cursorForInfo.getString(cursorForInfo.getColumnIndex(DBHelper.COLUMN_TRANSACT_BASIC_TITLE)))
                val desc  = (cursorForInfo.getString(cursorForInfo.getColumnIndex(DBHelper.COLUMN_TRANSACT_BASIC_DESCRIPTION)))
                val place  = (cursorForInfo.getString(cursorForInfo.getColumnIndex(DBHelper.COLUMN_TRANSACT_BASIC_PLACE)))
                val date  = (cursorForInfo.getString(cursorForInfo.getColumnIndex(DBHelper.COLUMN_TRANSACT_BASIC_DATE)))

                //val id = (cursorForInfo.getInt(cursorForInfo.getColumnIndex(DBHelper.COLUMN_TRANSACTION_ID)))
                info = TransactionInfo(title,desc,place,date)
                cursorForInfo.moveToNext()
            }
        }
        cursorForInfo.close()

        return info


    }

    fun getContactInfo(transid: Int): TransactionContactInfo{
        var contacts : TransactionContactInfo = TransactionContactInfo()
        var list  = ArrayList<String>()
        var owner : String
        var lender : String
        var sharing_count : Int
        var tid : Int

        var cursorForContact: Cursor = dbhelper.getContactByTransId(transid)
        if(cursorForContact.moveToFirst()) {
            while (cursorForContact.isAfterLast == false) {
                tid = (cursorForContact.getInt(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACTION_ID)))
                owner = (cursorForContact.getString(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_OWNERID)))
                lender = (cursorForContact.getString(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_CONTACT_LIST_01)))
                sharing_count = (cursorForContact.getInt(cursorForContact.getColumnIndex(DBHelper.COLUMN_TRANSACT_SHARING_COUNT)))
                // contact = ArrayList<String>()
                list.add(lender)
                contacts = TransactionContactInfo(owner,sharing_count,list)
                cursorForContact.moveToNext()
            }
        }
        return contacts

    }
   /* fun  showFABMenu(){
        fabLayout1.visibility = View.VISIBLE
        fabLayout2.visibility = View.VISIBLE
        fabBGLayout.visibility = View.VISIBLE
        fab.animate().rotationBy(180F)
        fabLayout1.animate().translationY(45.0f)
        fabLayout2.animate().translationY(75.0f)

    }
    fun closeFABMenu(){
        fabBGLayout.visibility = View.GONE
        fab.animate().rotation(0F)
        fabLayout1.animate().translationY(0f)
        fabLayout2.animate().translationY(0f)

                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {}
                    override fun onAnimationEnd(animator: Animator) {
                        if (View.GONE == fabBGLayout.visibility) {
                            fabLayout1.visibility = View.GONE
                            fabLayout2.visibility = View.GONE

                        }
                    }

                    override fun onAnimationCancel(animator: Animator) {}
                    override fun onAnimationRepeat(animator: Animator) {}
                })

    }*/

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_activity_menu, menu)
        return true
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if(id == R.id.action_new)
        {
           // insertHelper.truncateLoginTable()
            val intent = Intent(this, CreateNewActivity::class.java)
            startActivity(intent)
        }
        if(id == R.id.action_new_group)
        {
            // insertHelper.truncateLoginTable()
            val intent = Intent(this, CreateNewGroupActivity::class.java)
            startActivity(intent)
        }
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

        if(id == R.id.logout_icon)
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
