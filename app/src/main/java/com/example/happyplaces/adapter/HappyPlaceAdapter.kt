package com.example.happyplaces.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.R
import com.example.happyplaces.activities.AddHappyPlaceActivity
import com.example.happyplaces.activities.AddHappyPlaceDetail
import com.example.happyplaces.activities.MainActivity.Companion.EXTRA_DETAIL_PLACE
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.model.HappyPlaceModel
import de.hdodenhof.circleimageview.CircleImageView

class HappyPlaceAdapter(private val context:Context,private val happyPlaceList:ArrayList<HappyPlaceModel>)
    :RecyclerView.Adapter<HappyPlaceAdapter.ItemViewHolder>(){

    class ItemViewHolder(view:View):RecyclerView.ViewHolder(view){

                  val profileImage:CircleImageView=view.findViewById(R.id.profile_image)
                  val profileTitle:TextView=view.findViewById(R.id.profile_title)
                  val profileDescription:TextView=view.findViewById(R.id.profile_description)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val happyPlace=happyPlaceList[position]

        holder.profileTitle.text = happyPlace.title
        holder.profileDescription.text=happyPlace.description
        holder.profileImage.setImageURI(Uri.parse(happyPlace.image))
        holder.itemView.setOnClickListener {
             val intent=Intent(context, AddHappyPlaceDetail::class.java)
             intent.putExtra(EXTRA_DETAIL_PLACE,happyPlace)
             context.startActivity(intent)
       }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
             val happyPlaceObject=LayoutInflater.from(parent.context)
                 .inflate(R.layout.happy_palce_item_view,parent,false)
        return ItemViewHolder(happyPlaceObject)
    }

    override fun getItemCount(): Int=happyPlaceList.size

    fun notifyEditItem(position:Int){
          val intent=Intent(context,AddHappyPlaceActivity::class.java)
          intent.putExtra(EXTRA_DETAIL_PLACE,happyPlaceList[position])
          context.startActivity(intent)
          notifyItemChanged(position)
    }

   fun removeAt(position:Int){
         val db=DatabaseHandler(context)
         val isDelete=db.deleteHappyPlace(happyPlaceList[position])
         if(isDelete>0){
            happyPlaceList.removeAt(position)
             notifyItemRemoved(position)
         }
   }

}