package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.core.TAG
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.android.synthetic.main.fragment_medicament.*
import kotlinx.android.synthetic.main.fragment_medicament_list.*

import kotlinx.android.synthetic.main.fragment_medicament_list.progress
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditFragment : Fragment() {
    companion object {
        const val ITEM_ID = "ITEM_ID"
        const val USER_ID="USER_ID"
    }

    private val REQUEST_PERMISSION = 10
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2

    private lateinit var mMap: GoogleMap
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var connectivityLiveData: ConnectivityLiveData
    private lateinit var mark:Marker
    lateinit var currentPhotoPath: String
    private val callback = OnMapReadyCallback { googleMap ->
        mMap=googleMap
       var l= viewModel.item.value?.latitudine
        var ll= viewModel.item.value?.longitudine


            if ((l != null)&&(ll!=null) ){

               var pos= LatLng(l.toDouble(), ll.toDouble())
                mark=googleMap.addMarker(MarkerOptions().position(pos))
                mark.position=pos
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(pos))
            }
//        mMap.addMarker(l.toDouble(), ll.toDouble())

        mMap.setOnMapClickListener {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(it))
            Log.v(TAG, "on map click:" +it.latitude)
            mark.position=it
            longitude.setText(it.longitude.toString())
            latitude.setText(it.latitude.toString())
        }

    }

    private lateinit var viewModel: ItemEditViewModel
    private var itemId: String? = null
    private var userid: String? = null
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        currentPhotoPath=""
//        map.mo
        Log.v(TAG, "onCreate")
        arguments?.let {
            if (it.containsKey(ITEM_ID)) {
                Log.v(TAG, ITEM_ID)
                itemId = it.getString(ITEM_ID).toString()
            }
        }

        userid=arguments?.getString(USER_ID)
        itemId= arguments?.getString(ITEM_ID)
    }

    override fun onResume() {
        super.onResume()
        checkCameraPermission()
    }
    private fun checkCameraPermission() {
        if (this.context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.context as Activity,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_PERMISSION)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val uri = Uri.parse(currentPhotoPath)
                Log.d(TAG, "photo taken!");
                Log.d(TAG, uri.toString());
//                toUri
//                imageView.setImageURI(uri)
//                (view?.findViewById<Button>(R.id.imageView) as ImageView).setImageURI(uri)
//                ivImage.setImageURI(uri)
            }
            else if (requestCode == REQUEST_PICK_IMAGE) {
                val uri = data?.getData()
//                ivImage.setImageURI(uri)
            }
        }
    }
    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            this.activity?.let {
                intent.resolveActivity(it.packageManager)?.also {
                    val photoFile: File? = try {
                        createCapturedPhoto()
                    } catch (ex: IOException) {
                        null
                    }
                    Log.d(TAG, "photofile $photoFile");
                    photoFile?.also {
                        val photoURI = this.context?.let { it1 ->
                            FileProvider.getUriForFile(
                                    it1,
                                    "com.example.myapplication.fileprovider",
                                    it
                            )
                        }
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createCapturedPhoto(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        val storageDir = this.activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PHOTO_${timestamp}",".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?

    ): View? {
        userid= arguments?.getString(USER_ID)
        itemId= arguments?.getString(ITEM_ID)
        Log.v(TAG, "argument:"+arguments?.getString(USER_ID))
        Log.v(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v(TAG, "onViewCreated")
        //item_text.setText(itemId.toString())

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        connectivityManager = activity?.getSystemService(android.net.ConnectivityManager::class.java)!!
        connectivityLiveData = ConnectivityLiveData(connectivityManager)
        Log.v(TAG, "onActivityCreated")
        val mapFragment = childFragmentManager.findFragmentById(R.id.map2) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


        setupViewModel()
        view?.findViewById<Button>(R.id.take_photo)?.setOnClickListener{
            openCamera()
        }
        view?.findViewById<Button>(R.id.save)?.setOnClickListener {
            Log.v(TAG, "onActivityCreated "+longitude.text.toString())

            var bol= MainActivity.isON
            if(bol!=null) {
                if(bol==false){
                    val inflater: LayoutInflater? = getActivity()?.getLayoutInflater()
                    // Inflate a custom view using layout inflater
                    val view = inflater?.inflate(R.layout.popup,null)


                    val popupWindow = PopupWindow(
                        view, // Custom view to show in popup window
                        LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                        LinearLayout.LayoutParams.WRAP_CONTENT // Window height
                    )

                    // Set an elevation for the popup window
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        popupWindow.elevation = 10.0F
                    }



                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        // Create a new slide animation for popup window enter transition
                        val slideIn = Slide()
                        slideIn.slideEdge = Gravity.TOP
                        popupWindow.enterTransition = slideIn

                        // Slide animation for popup window exit transition
                        val slideOut = Slide()
                        slideOut.slideEdge = Gravity.RIGHT
                        popupWindow.exitTransition = slideOut

                    }

                    // Get the widgets reference from custom view
                    val tv = view?.findViewById<TextView>(R.id.text_view)
                    val buttonPopup = view?.findViewById<Button>(R.id.button_popup)

                    // Set click listener for popup window's text view
                    if (tv != null) {
                        tv.setOnClickListener{
                            // Change the text color of popup window's text view
                            if (tv != null) {
                                tv.setTextColor(Color.RED)
                            }
                        }
                    }

                    // Set a click listener for popup's button widget
                    if (buttonPopup != null) {
                        buttonPopup.setOnClickListener{
                            // Dismiss the popup window
                            popupWindow.dismiss()
                        }
                    }

                    // Set a dismiss listener for popup window
                    popupWindow.setOnDismissListener {
                        Toast.makeText(requireActivity().applicationContext,"Popup closed",Toast.LENGTH_SHORT).show()
                    }


                    // Finally, show the popup window on app
                    TransitionManager.beginDelayedTransition(requireActivity().getWindow().getDecorView().findViewById(android.R.id.content))
                    popupWindow.showAtLocation(
                        requireActivity().getWindow().getDecorView().findViewById(android.R.id.content), // Location to display popup window
                        Gravity.CENTER, // Exact position of layout to display popup
                        0, // X offset
                        0 // Y offset
                    )
                }
                if(userid!=null) {
                    viewModel.saveOrUpdateItem(
                        userid!!,
                        bol,
                        name.text.toString(),
                        description.text.toString(),
                        longitude.text.toString(),
                        latitude.text.toString(),
                            currentPhotoPath
                    )
                }

            } else{
                Log.v(TAG, "bool is null")
        }
//            activity.conec
        }
//        mapFragment?.getMapAsync {  }


    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(ItemEditViewModel::class.java)
        viewModel.item.observe(viewLifecycleOwner, { item ->
            Log.v(TAG, "update items")
            description.setText(item.description)
            name.setText(item.name)
//map.
            var l= item.latitudine
            var ll= item.longitudine

            longitude.setText(item.longitudine)
            latitude.setText(item.latitudine)
            Log.v(TAG, "lat and long: "+l+' '+ll)
            if ((l != null)&&(ll!=null)&&::mMap.isInitialized ){
                Log.v(TAG, "lat and long2: "+l+' '+ll)

                try {
                    var pos = LatLng(l.toDouble(), ll.toDouble())
//                    mMap.addMarker(MarkerOptions().position(pos))
                    mark.position=pos
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(pos))
                }
                catch (e: NumberFormatException) { Log.v(TAG, "Nu e ok reprezentarea") }


            }
            Log.v(TAG, "lat load" +item.toString())

        })
        viewModel.fetching.observe(viewLifecycleOwner, { fetching ->
            Log.v(TAG, "update fetching")
            progress.visibility = if (fetching) View.VISIBLE else View.GONE
        })
        viewModel.fetchingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.v(TAG, "update fetching error")
                val message = "Fetching exception ${exception.message}"
                val parentActivity = activity?.parent
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.completed.observe(viewLifecycleOwner, Observer { completed ->
            if (completed) {
                Log.v(TAG, "completed, navigate back")
                findNavController().navigateUp()
            }
        })
        val id = itemId
        if (id != null) {
            viewModel.loadItem(id.toInt())
        }
    }
}