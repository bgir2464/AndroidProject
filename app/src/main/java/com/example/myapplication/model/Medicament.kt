package com.example.myapplication.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "medicamente")
data class Medicament(
    @PrimaryKey @ColumnInfo(name = "_id") var id: Int=-1,
    @ColumnInfo(name = "description")  var description: String,
    @ColumnInfo(name = "name")  var name:String,
    @ColumnInfo(name = "id_user")  var id_user:String,
    @ColumnInfo(name = "longitudine")  var longitudine:String,
    @ColumnInfo(name = "latitudine")  var latitudine:String,
    @ColumnInfo(name = "uri")  var uri:String?
) {
    override fun toString(): String = description
}