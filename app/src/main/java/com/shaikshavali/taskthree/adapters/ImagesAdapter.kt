package com.shaikshavali.taskthree.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shaikshavali.taskthree.R

import kotlinx.android.synthetic.main.item_image.view.*

class ImagesAdapter(val context : Context, private val list: ArrayList<String>)
        :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemListener : OnItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_image,parent,false))

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if(holder is MyViewHolder){
            Glide.with(context)
                .load(item)
                .centerCrop()
                .placeholder(R.drawable.user_images)
                .into(holder.itemView.item_img_view)


            holder.itemView.setOnClickListener {
                onItemListener?.onClick(position,item)
            }
        }


    }

    fun setOnClickListener(onItemListener: OnItemListener){
        this.onItemListener = onItemListener
    }

    interface OnItemListener{
        fun onClick(position: Int,item : String)
    }

    private class MyViewHolder(view : View): RecyclerView.ViewHolder(view)

}