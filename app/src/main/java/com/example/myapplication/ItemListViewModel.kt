package com.example.myapplication

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.myapplication.core.TAG
import com.example.myapplication.core.Result
import com.example.myapplication.data.MedicamentRepo
import com.example.myapplication.local.TodoDatabase
import com.example.myapplication.model.Medicament
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//se deriveaza din viewModel care e definita in android
class ItemListViewModel(application: Application) : AndroidViewModel(application) {

//value poate sa fie de orice tip
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    //MutableLiveData este o clasa care poate sa fie modificata
    private val mutableItems = MutableLiveData<List<Medicament>>().apply { value = emptyList() }

    //permite modificari dar nu ofera functii pentru modificarea obictului

    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

var items: LiveData<List<Medicament>> = mutableItems
    val itemRepository: MedicamentRepo

    init {
        val itemDao = TodoDatabase.getDatabase(application, viewModelScope).itemDao()
        itemRepository = MedicamentRepo(itemDao)
        items = itemRepository.items
    }

    fun refresh(userid:String,ison:Boolean) {
        viewModelScope.launch {
            if(ison==true) {
                Log.v(TAG, "refresh...");
                mutableLoading.value = true
                mutableException.value = null
                when (val result = itemRepository.refresh(userid)) {
                    is Result.Success -> {
                        Log.d(TAG, "refresh succeeded");
                    }
                    is Result.Error -> {
                        Log.w(TAG, "refresh failed", result.exception);
                        mutableException.value = result.exception
                    }
                }
                mutableLoading.value = false
            }else{
//                Log.v(TAG, "refresh...");
//                mutableLoading.value = true
//                mutableException.value = null
//                when (val result = itemRepository.refresh_offline(userid)) {
//                    is Result.Success -> {
//                        Log.d(TAG, "refresh succeeded");
//                    }
//                    is Result.Error -> {
//                        Log.w(TAG, "refresh failed", result.exception);
//                        mutableException.value = result.exception
//                    }
//                }
//                mutableLoading.value = false
            }

        }
    }

//    fun loadItems() {
//        //se apeleaza aceasta funcie in fragment o singura data
//        viewModelScope.launch {
//            Log.v(TAG, "loadItems...");
//            mutableLoading.value = true
//            mutableException.value = null
//            try {
//                mutableItems.value = itemRepository.loadAll()
//                Log.d(TAG, "loadItems succeeded");
//                mutableLoading.value = false
//            } catch (e: Exception) {
//                Log.w(TAG, "loadItems failed", e);
//                mutableException.value = e
//                mutableLoading.value = false
//            }
//        }
//    }
}
