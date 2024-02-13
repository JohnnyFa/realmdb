package com.johnny.fagundes.realmdatabase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johnny.fagundes.realmdatabase.models.Address
import com.johnny.fagundes.realmdatabase.models.Course
import com.johnny.fagundes.realmdatabase.models.Student
import com.johnny.fagundes.realmdatabase.models.Teacher
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val realm = MyApp.realm
//
//    val courses = realm.query<Course>().asFlow().map { results ->
//        results.list.toList()
//    }.stateIn(
//        viewModelScope, SharingStarted.WhileSubscribed(),
//        emptyList()
//    )
//
//    val courses = realm.query<Course>(
//        "enrolledStudents.name == $0",
//        "John Junior"
//    ).asFlow().map { results ->
//        results.list.toList()
//    }.stateIn(
//        viewModelScope, SharingStarted.WhileSubscribed(),
//        emptyList()
//    )
//
//    val courses = realm.query<Course>(
//        "enrolledStudents.@count >= 2",
//    ).asFlow().map { results ->
//        results.list.toList()
//    }.stateIn(
//        viewModelScope, SharingStarted.WhileSubscribed(),
//        emptyList()
//    )

    val courses = realm.query<Course>(
        "teacher.address.fullName CONTAINS $0",
        "janw"
    ).asFlow().map { results ->
        results.list.toList()
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(),
        emptyList()
    )

    var courseDetails: Course? by mutableStateOf(null)
        private set

//
//    init {
//        createSampleEntries()
//    }

    fun showCourseDetails(course: Course) {
        courseDetails = course
    }

    fun hideCourseDetails() {
        courseDetails = null
    }

    private fun createSampleEntries() {
        viewModelScope.launch {
            realm.write {
                val address1 = Address().apply {
                    fullName = "johnn doe"
                    street = "Jon Doe Street"
                    houseNumber = 25
                    zip = 12001200
                    city = "cities"
                }
                val address2 = Address().apply {
                    fullName = "janw doe"
                    street = "janw Doe Street"
                    houseNumber = 26
                    zip = 12001200
                    city = "cities"
                }

                val course1 = Course().apply {
                    name = "Kotlin dataBase Realm"
                }
                val course2 = Course().apply {
                    name = "Kotlin Basics"
                }
                val course3 = Course().apply {
                    name = "Kotlin Coroutines"
                }

                val teacher1 = Teacher().apply {
                    address = address1
                    courses = realmListOf(course1, course2)
                }

                val teacher2 = Teacher().apply {
                    address = address2
                    courses = realmListOf(course3)
                }

                course1.teacher = teacher1
                course2.teacher = teacher1
                course3.teacher = teacher2

                address1.teacher = teacher1
                address2.teacher = teacher2

                val student1 = Student().apply {
                    name = "John Junior"
                }

                val student2 = Student().apply {
                    name = "Jane Junior"
                }

                course1.enrolledStudents.add(student1)
                course2.enrolledStudents.add(student2)
                course3.enrolledStudents.addAll(listOf(student1, student2))


                copyToRealm(teacher1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(teacher2, updatePolicy = UpdatePolicy.ALL)

                copyToRealm(course1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(course2, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(course3, updatePolicy = UpdatePolicy.ALL)

                copyToRealm(student1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(student2, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    fun deleteCourse() {
        viewModelScope.launch {
            realm.write {
                val course = courseDetails ?: return@write
                val latestCourse = findLatest(course) ?: return@write
                delete(latestCourse)

                courseDetails = null
            }
        }
    }
}