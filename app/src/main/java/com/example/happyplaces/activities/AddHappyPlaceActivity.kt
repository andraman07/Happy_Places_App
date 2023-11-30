package com.example.happyplaces.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import com.example.happyplaces.R
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplaces.model.HappyPlaceModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(),View.OnClickListener{

    private lateinit var binding: ActivityAddHappyPlaceBinding
    private val cal =Calendar.getInstance()
    private var saveImageToInternalStorage:Uri?=null
    private lateinit var dateSetListener:DatePickerDialog.OnDateSetListener
    private lateinit var galleryImageResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraImageResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var  startAutocomplete:ActivityResultLauncher<Intent>
    private var mLongitude:Double=0.0
    private var mLatitude:Double=0.0
    private var mHappyPlaceModel:HappyPlaceModel?=null


       override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolBar:Toolbar=findViewById(R.id.tool_bar)

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolBar.setNavigationOnClickListener {
            onBackPressed()
        }

           if(!Places.isInitialized()){
               Places.initialize(this@AddHappyPlaceActivity,resources.getString(R.string.google_maps_api_key))
           }

        dateSetListener=DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDateInView()
        }
           updateDateInView()

           if(intent.hasExtra(MainActivity.EXTRA_DETAIL_PLACE)){
               mHappyPlaceModel= intent.getParcelableExtra<Parcelable>(MainActivity.EXTRA_DETAIL_PLACE) as HappyPlaceModel?
           }
           if(mHappyPlaceModel!=null){

               supportActionBar?.title="Edit Happy Place"
               binding.etTitle.setText(mHappyPlaceModel!!.title)
               binding.etDescription.setText(mHappyPlaceModel!!.description)
               binding.etDate.setText(mHappyPlaceModel!!.date)
               binding.etLocation.setText(mHappyPlaceModel!!.location)
               mLatitude= mHappyPlaceModel?.latitude!!
               mLongitude = mHappyPlaceModel?.longitude!!

               saveImageToInternalStorage=Uri.parse(mHappyPlaceModel!!.image)

               binding.ivPlaceImage.setImageURI(saveImageToInternalStorage)

               binding.btnSave.text = getString(R.string.update_)

           }

           binding.etDate.setOnClickListener(this)

           binding.tvAddImage.setOnClickListener(this)
           binding.btnSave.setOnClickListener(this)
           binding.etLocation.setOnClickListener(this)

        registerOnActivityForGalleryResult()
        registerOnActivityForCameraResult()
        startAutocompleteResult()

    }


    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.et_date ->{
                       DatePickerDialog(this@AddHappyPlaceActivity,dateSetListener,
                           cal.get(Calendar.YEAR),
                           cal.get(Calendar.MONTH),
                           cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.tv_add_image ->{
                val pictureDialog=AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems= arrayOf("Select photo from Gallery","Capture photo form Camera")
                pictureDialog.setItems(pictureDialogItems){
                        _, which->
                    when(which){
                        0-> choosePhotoFromGallery()

                        1->clickPhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }
            R.id.btn_save ->{

                when{
                    binding.etTitle.text.isNullOrEmpty()->{
                        Toast.makeText(this,"Title must not be empty",Toast.LENGTH_SHORT).show()
                    }
                    binding.etDescription.text.isNullOrEmpty()->{
                        Toast.makeText(this,"Description must not be empty",Toast.LENGTH_SHORT).show()
                    }
                    binding.etLocation.text.isNullOrEmpty()->{
                        Toast.makeText(this,"Location must not be empty",Toast.LENGTH_SHORT).show()
                    }
                    saveImageToInternalStorage==null->{
                        Toast.makeText(this,"Please select image",Toast.LENGTH_SHORT).show()
                    }else->{
                           val happyPlace=HappyPlaceModel(
                               if(mHappyPlaceModel!=null) mHappyPlaceModel!!.id else 0
                               ,
                               binding.etTitle.text.toString(),
                               binding.etDescription.text.toString(),
                               saveImageToInternalStorage.toString(),
                               binding.etDate.text.toString(),
                               binding.etLocation.text.toString(),
                               mLatitude,
                               mLongitude

                               )
                         val dbHandler=DatabaseHandler(this)

                          if(mHappyPlaceModel!=null){
                             val success= dbHandler.updateHappyPlace(happyPlace)
                              if(success<0){
                                  Toast.makeText(this,"Failed to update" ,Toast.LENGTH_SHORT).show()
                              }else{
                                  Toast.makeText(this,"updated successfully" ,Toast.LENGTH_SHORT).show()
                              }

                          }else{
                              dbHandler.addHappyPlace(happyPlace)
                          }

                           finish()

                    }

                }

            }
            R.id.et_location->
                try{
                    val fields= listOf(Place.Field.ID,Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS)
                    val intent=
                        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields)
                            .build(this)
                    startAutocomplete.launch(intent)

                }catch (e:Exception){
                    e.printStackTrace()
                }
        }
    }

         private fun startAutocompleteResult() {
             startAutocomplete =
                 registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                     if (result.resultCode == Activity.RESULT_OK) {
                         val data: Intent? = result.data
                         if (data != null) {
                             val place:Place=Autocomplete.getPlaceFromIntent(data)
                             binding.etLocation.setText(place.address)
                             mLatitude=place.latLng!!.latitude
                             mLongitude=place.latLng!!.longitude
                         }
                     }
                 }
         }

       private fun registerOnActivityForGalleryResult() {
       galleryImageResultLauncher =
           registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
               if (result.resultCode == Activity.RESULT_OK) {
                   val data: Intent? = result.data
                   if(data!=null) {

                       val contentUri =data.data
                       try {
                           val selectedImageBitmap:Bitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,contentUri)
                           saveImageToInternalStorage=saveImageToInternalStorage(selectedImageBitmap)
                           binding.ivPlaceImage.setImageBitmap(selectedImageBitmap)

                       } catch (e: IOException) {
                           e.printStackTrace()
                           Toast.makeText(
                               this,
                               "Failed to load image from gallery",
                               Toast.LENGTH_SHORT
                           ).show()
                       }
                   }
               }
           }
   }

       private fun registerOnActivityForCameraResult() {
        cameraImageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if(data!=null) {
                        try {
                            val thumbNail: Bitmap = result!!.data!!.extras?.get("data") as Bitmap
                           saveImageToInternalStorage= saveImageToInternalStorage(thumbNail)
                            binding.ivPlaceImage.setImageBitmap(thumbNail)

                          } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(
                                this,
                                "Failed to open camera",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }


            }
    }



       private fun clickPhotoFromCamera() {
        Dexter.withContext(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA

        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                if (report != null) {
                    if (report.areAllPermissionsGranted()) {

                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        cameraImageResultLauncher.launch(cameraIntent)

                    } else if (report.isAnyPermissionPermanentlyDenied) {

                        showRationalDialogForPermissions()

                    }
                }
            }
            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                token?.continuePermissionRequest()
                showRationalDialogForPermissions()
            }

        }).onSameThread().check()

    }

       private fun choosePhotoFromGallery() {
           Dexter.withContext(this).withPermissions(
               android.Manifest.permission.READ_EXTERNAL_STORAGE,
               android.Manifest.permission.WRITE_EXTERNAL_STORAGE

           ).withListener(object : MultiplePermissionsListener {
               override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                   if (report != null) {
                       if (report.areAllPermissionsGranted()) {

                           val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                           try {

                               galleryImageResultLauncher.launch(galleryIntent)

                           }catch (e:ActivityNotFoundException){
                               Log.e("",e.message.toString())
                           }

                       } else if (report.isAnyPermissionPermanentlyDenied) {
                           showRationalDialogForPermissions()
                       }
                   }
               }
               override fun onPermissionRationaleShouldBeShown(
                   permissions: MutableList<PermissionRequest>?,
                   token: PermissionToken?
               ) {
                   token?.continuePermissionRequest()
                   showRationalDialogForPermissions()
               }

           }).onSameThread().check()

       }

       private fun showRationalDialogForPermissions() {
           AlertDialog.Builder(this)
               .setMessage("It looks like you have turned off permission request for this feature It can be enabled under the Applications settings ")
               .setPositiveButton("GO TO SETTINGS") { _, _ ->
                   try {
                       val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                       val uri = Uri.fromParts("package", packageName, null)
                       intent.data = uri
                       startActivity(intent)
                   } catch (e: ActivityNotFoundException) {
                       e.printStackTrace()
                   }
               }
               .setNegativeButton("CANCEL") { dialog, _ ->
                   dialog.dismiss()
               }
               .show()

       }


       private fun saveImageToInternalStorage(bitmap:Bitmap):Uri{
        val wrapper=ContextWrapper(applicationContext)
        var file=wrapper.getDir("HappyPlaceImage",Context.MODE_PRIVATE)
        file= File(file,"${UUID.randomUUID()}.jpg")
        try {
            val stream:OutputStream=FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()

        }catch (e:IOException){
            e.printStackTrace()
        }
           return Uri.parse(file.absolutePath)

    }

       private fun updateDateInView() {
           val myFormat = "dd.MM.yyyy"
           val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
           binding.etDate.setText(sdf.format(cal.time).toString())

       }

   }
