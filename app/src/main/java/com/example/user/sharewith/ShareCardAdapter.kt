package com.example.user.sharewith

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_new.view.*

/**
 * Created by User on 20-01-2021.
 */
class ShareCardAdapter(val context: Context, val shareList : ArrayList<TranscationShare>, val userId :String ): RecyclerView.Adapter<ShareCardAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shares: TranscationShare = shareList[position]
        holder.setItem(shares, position, userId)



    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //To change body of created functions use File | Settings | File Templates.
        val v = LayoutInflater.from(context).inflate(R.layout.activity_transaction_card, parent, false)
        return ViewHolder(v)

    }

    override fun getItemCount(): Int {
//
        return shareList.size
    }


    inner class ViewHolder(shareView: View) : RecyclerView.ViewHolder(shareView) {

        // val context = this
        val title = shareView.findViewById<TextView>(R.id.card_info) as TextView
        val balance = shareView.findViewById<TextView>(R.id.card_balance) as TextView
        val owner = shareView.findViewById<TextView>(R.id.card_owner) as TextView



        var currentUser: String = ""
        var currentShare: TranscationShare? = null
        var currentPosition: Int = 0

        init {
            shareView.setOnClickListener {
                Log.e("hai","${this.currentShare?.transId?.toInt()}")
                var tid = this.currentShare?.transId?.toInt()
                if(currentShare?.settle_flag == 0 && currentShare?.style == expenseStyle.LENDER){
                    val intent = Intent(shareView.context, TransactionInfoActivity::class.java)
                    intent.putExtra("transId", tid.toString())
                    shareView.context.startActivity(intent)

                }


            }
        }

        fun callBy(currentItem: Int) {


        }

        fun setItem(share: TranscationShare?, pos: Int, userId: String) {


           // Log.e("in card","${Users.userMap[share?.owner]}")

            if(share?.style ==expenseStyle.OWNER)
            {

                balance.text = share!!.balance.toString() + " <-"
                balance.setTextColor(Color.GREEN)
                if(share?.isGroup == 1)
                {
                    title.text = share?.basic_info?.desc
                    owner.text = "You Owned : "+Users.userMap[share?.owner] + " Via : " + share?.basic_info?.title
                }
                else{
                    title.text = share?.basic_info?.desc
                    owner.text = "You Owned : "+Users.userMap[share?.owner]
                }

                //balance. = Color.RED
            }
            else
            {
                balance.text = share!!.balance.toString()+ " ->"
                balance.setTextColor(Color.RED)
                if(share?.isGroup == 1)
                {
                    title.text = share?.basic_info?.desc
                    owner.text = "You Owe : "+Users.userMap[share?.owner] + " Via : " + share?.basic_info?.title
                }
                else{
                    title.text = share?.basic_info?.desc
                    owner.text = "You Owe : " + Users.userMap[share?.owner]
                }

            }

            if(share?.settle_flag == 1)
            {
                balance.text = share!!.amount.toString() + " Settled"
                balance.setTextColor(Color.BLACK)

            }

            //orderButton
            this.currentUser = userId
            this.currentShare = share
            this.currentPosition = pos


        }
    }

}