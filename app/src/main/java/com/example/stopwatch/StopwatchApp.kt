package com.example.stopwatch

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

private const val DB_NAME = "postDB.realm"

class StopwatchApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        val configuration = RealmConfiguration.Builder()
                .name(DB_NAME)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()

        Realm.setDefaultConfiguration(configuration)
    }
}