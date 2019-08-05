package com.shady_selim.travelmantics

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_deal.*
import kotlinx.android.synthetic.main.rv_row.view.*

class DealAdapter(val dealsList: ArrayList<TravelDeal>)  : RecyclerView.Adapter<DealAdapter.DealViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_row, parent, false)
        return DealViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dealsList.size
    }

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        holder.bindItems(dealsList[position])
    }

    class DealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(deal: TravelDeal) = with(itemView) {
            tvTitle.text = deal.title
            tvDescription.text = deal.description
            tvPrice.text = deal.price
            if (deal.imageUrl != "")
                Picasso.get().load(deal.imageUrl).resize(160,160).centerCrop().into(imageDeal)

            setOnClickListener{
                val intent = Intent(context, DealActivity::class.java)
                intent.putExtra("Deal", deal)
                context.startActivity(intent)
//                Toast.makeText(context, adapterPosition.toString(), Toast.LENGTH_LONG).show()
            }
//            itemImage.loadUrl(item.url)
//            setOnClickListener { listener(item) }
        }
    }



}