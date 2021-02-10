package com.example.myapplication

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.myapplication.core.TAG
import com.example.myapplication.data.MedicamentRepo
import com.example.myapplication.local.TodoDatabase
import com.example.myapplication.model.Medicament
import kotlinx.coroutines.launch


class ItemEditViewModel(application: Application) : AndroidViewModel(application){
    private val mutableItem = MutableLiveData<Medicament>().apply { value = Medicament(
        -1,
        "",
        "",
        "0",
        "0.0",
        "0.0",
            ""
    ) }
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val item: LiveData<Medicament> = mutableItem
    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

       val itemRepository: MedicamentRepo

       init {
           val itemDao = TodoDatabase.getDatabase(application, viewModelScope).itemDao()
//        val database = TodoDatabase
//            .fallbackToDestructiveMigration()
//            .build()
           itemRepository = MedicamentRepo(itemDao)
       }

    fun loadItem(itemId: Int) {
        viewModelScope.launch {
            Log.i(TAG, "loadItem...")
            mutableFetching.value = true
            mutableException.value = null
            try {
                mutableItem.value = itemRepository.load(itemId)
                Log.i(TAG, "loadItem succeeded")
                mutableFetching.value = false
            } catch (e: Exception) {
                Log.w(TAG, "loadItem failed", e)
                mutableException.value = e
                mutableFetching.value = false
            }
        }
    }

    fun saveOrUpdateItem(user_id: String, ison: Boolean, name: String, description: String, longi: String, lati: String, currentPhotoPath: String) {

        viewModelScope.launch {
            Log.i(TAG, "saveOrUpdateItem...");
            Log.i(TAG,"is on:"+ison.toString())
            val item = mutableItem.value ?: return@launch
            item.description = description
            item.name=name
            item.longitudine=longi
            item.latitudine=lati
            item.id_user=user_id
            item.uri=currentPhotoPath

            if(ison==false){
                if (item != null) {
                    item.uri=currentPhotoPath
                }
//            item.
                if (item != null) {
                    item.id_user=user_id
                }
//            item.
                if (item != null) {
                    if (item.id==-1) {
                        Log.i(TAG, "offline saved");
                        mutableItem.value=itemRepository.save_offline(item)
                    }
                    else{
                        mutableItem.value=itemRepository.update_offline(item)
                    }
                    Log.i(TAG, "saveOrUpdateItem offline succeeded");
//                    mutableCompleted.value = true
            }}
            else {
                    mutableFetching.value = true
                    mutableException.value = null
                    Log.i(TAG, "item id: " + item.id);
                    Log.i(TAG, "item longi: " + item.longitudine);
                    try {
                        Log.i(TAG, item.id.toString());
                        //se face update in loc de save, vezi de cee
                        if (item.id != -1) {

                            mutableItem.value = itemRepository.update(item)
                        } else {
                            mutableItem.value = itemRepository.save(item)
                        }
                        Log.i(TAG, "saveOrUpdateItem succeeded");
                        mutableCompleted.value = true
                        mutableFetching.value = false
                    } catch (e: Exception) {
                        Log.w(TAG, "saveOrUpdateItem failed", e);
                        mutableException.value = e
                        mutableFetching.value = false
                    }
                }
        }

    }
}