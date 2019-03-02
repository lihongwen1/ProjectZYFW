package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.select_item.view.*

class SelectListAdapter(val list:ArrayList<Model>): RecyclerView.Adapter<SelectListAdapter.ViewHolder>() {
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view)
    var onItemClickListener:OnItemClickListener? = null
    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list[position].apply {
            holder.itemView.apply {
                when (getItemViewType(position)) {
                    0, 1 -> {
                        Glide.with(context)
                                .load(path)
                                .into(imageSelectItem)
                        cancelSelectItem.setOnClickListener {
                            list.removeAt(position)
                            notifyDataSetChanged()
                        }
                    }
                    else -> {
                        Glide.with(context)
                                .load(R.drawable.ic_add_black_48dp)
                                .into(imageSelectItem)
                        cancelSelectItem.isVisible = false
                        imageSelectItem.setOnClickListener {
                            onItemClickListener?.onItemClick()
                        }
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.select_item,parent,false))
    }

    override fun getItemViewType(position: Int): Int {
        return when(list[position]){
            is Image -> 0
            is Video -> 1
            else -> 2
        }
    }
    abstract class Model(val path: String)
    class Image(path: String):Model(path)
    class Video(path: String):Model(path)
    class Action(path: String):Model(path)
    interface OnItemClickListener {
       fun onItemClick()
    }
}