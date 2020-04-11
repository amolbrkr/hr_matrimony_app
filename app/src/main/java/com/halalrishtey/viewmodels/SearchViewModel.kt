package com.halalrishtey.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.halalrishtey.models.User

data class SearchParams(
    var minAge: Int = 0,
    var maxAge: Int = 27,
    var minHeight: Int = 0,
    var maxHeight: Int = 107,
    var location: String = "",
    var highestEdu: String = ""
) {
    override fun toString(): String {
        return "Age: $minAge - $maxAge, Height: $minHeight - $maxHeight, Loc: $location, Highest Education: $highestEdu"
    }
}

class SearchViewModel : ViewModel() {
    val query = MutableLiveData<String>()
    val filters = MutableLiveData<SearchParams>(null)
    private val defaultValues = SearchParams()

    fun applySearchFilters(listOfUsers: ArrayList<User>): ArrayList<User> {
        var r = listOfUsers

        if (query.value != null && query.value!!.length > 2)
            r = listOfUsers.filter { u -> u.displayName.contains(query.value!!) } as ArrayList<User>

        filters.value?.let {
            if (it.minAge != defaultValues.minAge)
                r = r.filter { u -> u.age!! + 18 > it.minAge } as ArrayList<User>

            if (it.maxAge != defaultValues.maxAge)
                r = r.filter { u -> u.age!! + 18 < it.maxAge } as ArrayList<User>

            //Convert both values to cm before comparing
            if (it.minHeight != it.minHeight)
                r =
                    r.filter { u -> u.height[0].toInt() * 30.5 > it.minHeight + 137 } as ArrayList<User>

            if (it.maxHeight != it.maxHeight)
                r =
                    r.filter { u -> u.height[0].toInt() * 30.5 < it.maxHeight + 137 } as ArrayList<User>
        }

        Log.d("SearchViewModel", "Applying filters removed ${listOfUsers.size - r.size} profiles!")
        return r
    }
}