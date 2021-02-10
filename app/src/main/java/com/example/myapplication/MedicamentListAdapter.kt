package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.core.TAG
import com.example.myapplication.model.Medicament
import kotlinx.android.synthetic.main.fragment_medicament.view.*
import java.util.*
import kotlin.collections.ArrayList


class MedicamentListAdapter(
    private val fragment: Fragment,var userid:String
) : RecyclerView.Adapter<MedicamentListAdapter.ViewHolder>() , Filterable {

    var items = emptyList<Medicament>()
        set(value) {
            field = value
            notifyDataSetChanged();
        }
    var anim:Boolean = false
    var filterItems=emptyList<Medicament>()
        set(value) {
            field = value
//            notifyDataSetChanged();
        }
    private var onItemClick: View.OnClickListener;

    init {
        filterItems=items
        Log.v(TAG, "init adapter ")
        onItemClick = View.OnClickListener { view ->
            val item = view.tag as Medicament
            Log.v(TAG, item.id.toString())

            val args = Bundle()
            args.putString("ITEM_ID", item.id.toString())
            args.putString("USER_ID", userid)
            Log.v(TAG,"user id")
            args.getString("USER_ID")?.let { Log.v(TAG, it) }
            fragment.findNavController().navigate(R.id.editFragment,args)
        }
    }
    //se apeleaza doar pana se umple ecranul
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.medicament, parent, false)
        Log.v(TAG, "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.v(TAG, "onBindViewHolder $position")
        val item =filterItems[position]
        Log.v(TAG, item.id.toString())
        holder.itemView.tag = item
        holder.description.text = item.description
        holder.name.text = item.name
        if(item.uri!=null&&item.uri!="")
        holder.photo.setImageURI(Uri.parse(item.uri))
        setFadeAnimation(holder.itemView)
        holder.itemView.setOnClickListener(onItemClick)

    }

    override fun getItemCount() = filterItems.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.name_med
        val description: TextView = view.description_ded
        val photo: ImageView = view.imageView
    }

    private fun setFadeAnimation(view: View) {

            Log.i(TAG, "animation")
            val anim = ScaleAnimation(
                0.0f,
                1.0f,
                0.0f,
                1.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            anim.duration = 2000
            view.startAnimation(anim)


    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                Log.i(TAG, "perform filtering")
                Log.i(TAG, charSearch)
                if (charSearch.isEmpty()) {
                    filterItems = items
                    Log.i(TAG, "is empty")
                } else {
                    val resultList = ArrayList<Medicament>()
                    for (row in items) {
                        Log.i(TAG, row.name)
                        if (row.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            Log.i(TAG, row.name)
                            resultList.add(row)
                        }
                    }
                    Log.i(TAG, resultList.size.toString())
                    filterItems = resultList
//                    items=resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterItems
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterItems = if(results == null || results.values == null)
                    ArrayList<Medicament>()
                else
                    results.values as List<Medicament>
//                items=filterItems
                Log.i(TAG, filterItems.size.toString())
                notifyDataSetChanged()
            }

        }
    }
}