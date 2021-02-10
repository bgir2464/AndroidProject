package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.core.TAG
import com.example.myapplication.local.MedicamentDao
import com.example.myapplication.model.Medicament
import com.example.myapplication.core.Result
class MedicamentRepo(private val itemDao: MedicamentDao) {
    private var cachedItems: MutableList<Medicament>? = null;
    private var notsaved: MutableList<Medicament>? = mutableListOf();
    private var notupdated: MutableList<Medicament>? = mutableListOf();
    val items = itemDao.getAll()

    //se adauga refresh in listfragment si modelviewlist
    suspend fun refresh(userid:String): Result<Boolean> {
        try {
            itemDao.deleteAll()
            val items = MedicamentApi.service.find(userid)
            for (item in items) {
                itemDao.insert(item)
            }
            return Result.Success(true)
        } catch(e: Exception) {
            return Result.Error(e)
        }
    }
    suspend fun refresh_offline(userid:String): Result<Boolean> {
        try {
            Log.i(TAG, "refresh_offline")
//            itemDao.deleteAll()
//            val items = MedicamentApi.service.find(userid)
            for (item in notsaved!!) {
                itemDao.insert(item)
            }
            return Result.Success(true)
        } catch(e: Exception) {
            return Result.Error(e)
        }
    }


//    suspend fun loadAll(): List<Medicament> {
//        Log.i(TAG, "loadAll")
//        if (cachedItems != null) {
//            return cachedItems as List<Medicament>;
//        }
//        cachedItems = mutableListOf()
//        val items = MedicamentApi.service.find()
//        cachedItems?.addAll(items)
//        return cachedItems as List<Medicament>
//    }

    suspend fun load(itemId: Int): Medicament {
        Log.i(TAG, "load")
        Log.i(TAG, itemId.toString())
        val item = cachedItems?.find { it.id == itemId }
        if (item != null) {
            return item
        }
        return MedicamentApi.service.read(itemId)
    }

    suspend fun save_online(){
        val numberIterator = notsaved?.iterator()
        if (numberIterator != null) {
            while (numberIterator.hasNext()) {
                var elem = numberIterator.next()
                try {
                    val createdItem = MedicamentApi.service.create(elem)

                    cachedItems?.add(createdItem)
                    numberIterator.remove()
                }
                catch (e:Exception){
                Log.i(TAG, "exceptie save online" +e.message)
                }
            }

            }
        }
    suspend fun save_offline(item: Medicament): Medicament {
        Log.i(TAG, "save_offline")
//        val createdItem = MedicamentApi.service.create(item)
        notsaved?.add(item)
         cachedItems?.add(item)
         itemDao.insert(item)
        Log.i(TAG, notsaved?.size.toString())
        return item
    }

    suspend fun update_offline(item: Medicament): Medicament {
        Log.i(TAG, "update_offline")
//        val createdItem = MedicamentApi.service.create(item)
        notupdated?.add(item)
        itemDao.update(item)
//        val index = cachedItems?.indexOfFirst { it.id == item.id }
//        Log.i(TAG, item.description)
//        if (index != null) {
//
//            cachedItems?.set(index, item)
//        }
//        Log.i(TAG, notsaved?.size.toString())
        return item
    }


    suspend fun save(item: Medicament): Medicament {
        Log.i(TAG, "save")
        Log.i(TAG, item.longitudine)
        Log.i(TAG, item.latitudine)
        Log.i(TAG, item.id_user)
        val createdItem = MedicamentApi.service.create(item)
        itemDao.insert(createdItem)
        cachedItems?.add(createdItem)
        return createdItem
    }

    suspend fun update(item: Medicament): Medicament{
        Log.i(TAG, "update")
        val updatedItem = MedicamentApi.service.update(item.id, item)
        //asta trebuie adaugata eventuala
        //cachedItems?.add(updatedItem)
        Log.i(TAG, updatedItem.id.toString())
                val index = cachedItems?.indexOfFirst { it.id == updatedItem.id }
        Log.i(TAG, updatedItem.description)
        if (index != null) {
            cachedItems?.set(index, updatedItem)
            itemDao.update(updatedItem)
        }
        return updatedItem
    }
}