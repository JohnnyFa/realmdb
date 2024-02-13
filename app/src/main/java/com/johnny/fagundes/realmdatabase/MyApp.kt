package com.johnny.fagundes.realmdatabase

import android.app.Application
import com.johnny.fagundes.realmdatabase.models.Address
import com.johnny.fagundes.realmdatabase.models.Course
import com.johnny.fagundes.realmdatabase.models.Student
import com.johnny.fagundes.realmdatabase.models.Teacher
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class MyApp: Application() {

    companion object {
        lateinit var realm: Realm
    }

    override fun onCreate() {
        super.onCreate()
        realm = Realm.open(configuration =  RealmConfiguration.create(
            setOf(
                Address::class,
                Teacher::class,
                Course::class,
                Student::class
            )
        ))
    }

}