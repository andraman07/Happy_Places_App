package com.example.happyplaces.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplaces.databinding.ActivityAddHappyPlaceDetailBinding
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.model.HappyPlaceModel

class AddHappyPlaceDetail : AppCompatActivity() {

    private lateinit var binding: ActivityAddHappyPlaceDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddHappyPlaceDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        var happyPlaceDetailModel:HappyPlaceModel?=null

        if(intent.hasExtra(MainActivity.EXTRA_DETAIL_PLACE)){
            happyPlaceDetailModel= intent.getParcelableExtra(MainActivity.EXTRA_DETAIL_PLACE) as HappyPlaceModel?
        }

        if(happyPlaceDetailModel!=null){
            setSupportActionBar(binding.detailToolBar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title=happyPlaceDetailModel.title

            binding.detailToolBar.setNavigationOnClickListener{
                onBackPressed()
            }

            binding.detailImage.setImageURI(happyPlaceDetailModel.image!!.toUri())
            binding.detailDescription.text=happyPlaceDetailModel.description
            binding.detailLocation.text=happyPlaceDetailModel.location
        }

    }
}

