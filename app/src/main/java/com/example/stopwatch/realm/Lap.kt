package com.example.stopwatch.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Lap(
        @PrimaryKey var lap: String? = null, var addingTime:Long?=null
) : RealmObject()