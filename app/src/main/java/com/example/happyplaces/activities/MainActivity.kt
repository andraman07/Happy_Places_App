package com.example.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.adapter.HappyPlaceAdapter
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.model.HappyPlaceModel
import com.example.happyplaces.utilis.SwipeToDeleteCallback
import com.example.happyplaces.utilis.SwipeToEditCallback

class MainActivity : AppCompatActivity() {
    private lateinit var bindingMain: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        bindingMain= ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)

        bindingMain.fabAddHappyPlace.setOnClickListener {
            val intent= Intent(this, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }

   //     deleteSwipeHandler()
    //    editSwipeHandler()

        getHappyPlaceFromDb()
    }

  /*  private fun deleteSwipeHandler() {

        val deleteSwipeHandler=object :SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter=bindingMain.recyclerView.adapter as HappyPlaceAdapter
                adapter.removeAt(viewHolder.bindingAdapterPosition)
            }
        }
        val deleteItemTouchHelper= ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(bindingMain.recyclerView)
        getHappyPlaceFromDb()
    }  */

  /*  private fun editSwipeHandler() {
        val editSwipeHandler=object :SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter=bindingMain.recyclerView.adapter as HappyPlaceAdapter
                adapter.notifyEditItem(viewHolder.bindingAdapterPosition)
            }
        }
        val editItemTouchHelper= ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(bindingMain.recyclerView)
    }*/

    private fun setHappyPlaceRecyclerView(happyPlaceList:ArrayList<HappyPlaceModel>){
        bindingMain.recyclerView.setHasFixedSize(true)
        if(happyPlaceList.size > 0){

            bindingMain.recyclerView.visibility=View.VISIBLE
            bindingMain.noPlaceText.visibility=View.GONE
            bindingMain.recyclerView.adapter=HappyPlaceAdapter(this,happyPlaceList)

        }else{
            bindingMain.recyclerView.visibility=View.GONE
            bindingMain.noPlaceText.visibility=View.VISIBLE
        }

    }

    private fun getHappyPlaceFromDb(){
        val db = DatabaseHandler(this)
        val happyPlaceList:ArrayList<HappyPlaceModel> = db.getHappyPLace()
        setHappyPlaceRecyclerView(happyPlaceList)
    }


   /* override fun onResume() {
        super.onResume()
        getHappyPlaceFromDb()
    }

*/
    companion object{
        var EXTRA_DETAIL_PLACE="extra_place_details"
    }

}