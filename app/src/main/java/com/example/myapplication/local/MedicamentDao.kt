package com.example.myapplication.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myapplication.model.Medicament

@Dao
interface MedicamentDao {

        @Query("SELECT * from medicamente ORDER BY name ASC")
        fun getAll(): LiveData<List<Medicament>>

        @Query("SELECT * FROM medicamente WHERE _id=:id ")
        fun getById(id: String): LiveData<Medicament>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insert(item: Medicament)

        @Update(onConflict = OnConflictStrategy.REPLACE)
        suspend fun update(item: Medicament)

        @Query("DELETE FROM medicamente")
        suspend fun deleteAll()

}