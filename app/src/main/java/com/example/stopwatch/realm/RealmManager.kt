package com.example.stopwatch.realm

import android.util.Log
import io.realm.Realm
import io.realm.exceptions.RealmPrimaryKeyConstraintException

class RealmManager {
    private var realm: Realm? = null

    init {
        realm = Realm.getDefaultInstance()
    }

    fun saveLap(lap: Lap) {
        realm!!.beginTransaction()
        try {
            realm!!.insert(lap)
        } catch (r: RealmPrimaryKeyConstraintException) {
            Log.d("INSERT_ERROR", "Value already exists")
        }
        realm!!.commitTransaction()
    }

    fun getAllLap(): List<Lap> {
        return sort(realm!!.where(Lap::class.java).findAll())
    }

    fun removeLastAddedLap() {
        if (!realm!!.isEmpty) {
            realm!!.beginTransaction()
            var lastAddedLap = realm!!.where(Lap::class.java).equalTo("lap", getLastAddedLapName()).findFirst()!!
            lastAddedLap.deleteFromRealm()
            realm!!.commitTransaction()
        }
    }

    fun deleteAllLaps() {
        if (!realm!!.isEmpty) {
            realm!!.beginTransaction()
            realm!!.deleteAll()
            realm!!.commitTransaction()
        }
    }

    private fun getLastAddedLapName(): String? {
        return getAllLap().last().lap
    }

    fun closeRealm() {
        realm!!.close()
    }

    private fun sort(list: List<Lap>): List<Lap> {
        return list.sortedBy { it.addingTime }
    }

}