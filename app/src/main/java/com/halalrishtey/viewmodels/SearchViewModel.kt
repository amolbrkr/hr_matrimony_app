package com.halalrishtey.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.halalrishtey.models.ProfileCardData

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

    fun applySearchFilters(listOfUsers: ArrayList<ProfileCardData>): ArrayList<ProfileCardData> {
        var r = listOfUsers

        if (!query.value.isNullOrBlank())
            r =
                r.filter { u ->
                    u.data.displayName.contains(
                        query.value!!,
                        ignoreCase = true
                    )
                } as ArrayList<ProfileCardData>

        filters.value?.let {
            if (it.minAge != defaultValues.minAge)
                r = r.filter { u -> u.data.age!! > it.minAge + 18 } as ArrayList<ProfileCardData>

            if (it.maxAge != defaultValues.maxAge)
                r = r.filter { u -> u.data.age!! < it.maxAge + 18 } as ArrayList<ProfileCardData>

            //Convert both values to cm before comparing
            if (it.minHeight != it.minHeight)
                r =
                    r.filter { u -> u.data.height[0].toInt() * 30.5 > it.minHeight + 137 } as ArrayList<ProfileCardData>

            if (it.maxHeight != it.maxHeight)
                r =
                    r.filter { u -> u.data.height[0].toInt() * 30.5 < it.maxHeight + 137 } as ArrayList<ProfileCardData>

            if (it.location.isNotBlank())
                r =
                    r.filter { u ->
                        u.data.address.contains(
                            it.location,
                            ignoreCase = true
                        )
                    } as ArrayList<ProfileCardData>

            if (it.highestEdu != "Qualification")
                r =
                    r.filter { u -> u.data.qualification == it.highestEdu } as ArrayList<ProfileCardData>
        }

        Log.d("SearchViewModel", "Applying filters removed ${listOfUsers.size - r.size} profiles!")
        return r
    }
}