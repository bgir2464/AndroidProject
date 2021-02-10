package com.example.myapplication

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.core.TAG
import com.example.myapplication.data.authRepo
import com.example.myapplication.model.Medicament
import kotlinx.android.synthetic.main.fragment_medicament_list.*
import kotlinx.android.synthetic.main.view_item.*

/**
 * A fragment representing a list of Items.
 */
class MedicamentFragment : Fragment() {
    //cu lateinit pentru ca se initializeaza mai incolo, se putea face si cu val
    private lateinit var itemListAdapter: MedicamentListAdapter
    private lateinit var itemsModel: ItemListViewModel
    private  var userid: String="none"
    private var from_login:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
    }

    override fun onCreateView(
//            conectivityManager
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userid= arguments?.getString("USER_ID").toString()

        return inflater.inflate(R.layout.fragment_medicament_list, container, false)
    }

    override fun onResume() {
        super.onResume()
        if( arguments?.getBoolean("FR_LOGIN")!=null ) {
            from_login = arguments?.getBoolean("FR_LOGIN")!!
        userid= arguments?.getString("USER_ID").toString()
        itemsModel.refresh(userid,MainActivity.isON)
        Log.i(TAG, "on resume "+userid)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(MainActivity.isON==true){

            var tx=view.findViewById<TextView>(R.id.mode)
            tx.text ="ONLINE"

        }
        view.findViewById<SearchView>(R.id.search).setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                Log.i(TAG, "querytextchange "+newText)

//                itemListAdapter.filter.filter(newText)
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.i(TAG, "querytextsubmit ")
                itemListAdapter.filter.filter(query)
//                findNavController().navigate(R.id.action_FirstFragment_to_fragment_blank)
                return false
            }

        })
        view?.findViewById<Button>(R.id.button2)?.setOnClickListener {

            findNavController().navigate(R.id.action_medicamentFragment_to_fragment_login)
        }
        view?.findViewById<Button>(R.id.add)?.setOnClickListener {


            val args = Bundle()
            args.putString("USER_ID", userid)
            findNavController().navigate(R.id.action_medicamentFragment_to_editFragment,args)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i(TAG, "onActivityCreated")
if(!authRepo.isLoggedIn){
    findNavController().navigate(R.id.fragment_login);
    return;
}

        setupItemList()
    }

    private fun setupItemList() {
        itemListAdapter = MedicamentListAdapter(this,userid)
        item_list.adapter = itemListAdapter
        itemsModel = ViewModelProvider(this).get(ItemListViewModel::class.java)
//        item_list.adapter.

        itemsModel.items.observe(viewLifecycleOwner, { items ->
            Log.i(TAG, "update items")
            itemListAdapter.items = items
            itemListAdapter.filterItems=items
        })
        //daca se face loading se afisaza un progress bar
        itemsModel.loading.observe(viewLifecycleOwner, { loading ->
            Log.i(TAG, "update loading")
            progress.visibility = if (loading) View.VISIBLE else View.GONE
        })
        itemsModel.loadingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.i(TAG, "update loading error")
                val message = "Loading exception ${exception.message}"
                //toast un mesaj care se afisaza pentru o durata scurta de timp
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        })
        itemsModel.refresh(userid,MainActivity.isON);
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }
}