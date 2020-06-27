package com.makeshaadi.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.makeshaadi.CustomUtils
import com.makeshaadi.models.ProfileCardData

data class SearchParams(
    var minAge: Int = 18,
    var maxAge: Int = 45,
    var minHeight: Int = 122,
    var maxHeight: Int = 227,
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
    val appliedFilters = MutableLiveData<String>("")

    private val defaultValues = SearchParams()

    init {
        query.value = ""
        filters.value = null
    }

    fun isFilteringOn(): Boolean {
        return query.value?.length!! > 0 || filters.value != null;
    }

    fun applySearchFilters(listOfUsers: ArrayList<ProfileCardData>): ArrayList<ProfileCardData> {
        var r = listOfUsers
        appliedFilters.value = ""

        if (!query.value.isNullOrBlank())
            r =
                r.filter { u ->
                    u.data.displayName.contains(
                        query.value!!,
                        ignoreCase = true
                    )
                } as ArrayList<ProfileCardData>

        filters.value?.let {
            if (it.minAge != defaultValues.minAge) {
                r = r.filter { u -> u.data.age!! > it.minAge } as ArrayList<ProfileCardData>
                appliedFilters.value += "Minimum Age, "
            }

            if (it.maxAge != defaultValues.maxAge) {
                r = r.filter { u -> u.data.age!! < it.maxAge } as ArrayList<ProfileCardData>
                appliedFilters.value += "Maximum Age, "
            }
            //Convert both values to cm before comparing
            if (it.minHeight != defaultValues.minHeight) {
                r =
                    r.filter { u -> CustomUtils.convertFtIntoCm(u.data.height) > it.minHeight } as ArrayList<ProfileCardData>
                appliedFilters.value += "Minimum Height, "
            }

            if (it.maxHeight != defaultValues.maxHeight) {
                r =
                    r.filter { u -> CustomUtils.convertFtIntoCm(u.data.height) < it.maxHeight } as ArrayList<ProfileCardData>
                appliedFilters.value += "Maximum Height, "
            }

            if (it.location.isNotBlank()) {
                appliedFilters.value += "Location, "
                r =
                    r.filter { u ->
                        u.data.address.contains(
                            it.location,
                            ignoreCase = true
                        )
                    } as ArrayList<ProfileCardData>
            }

            if (it.highestEdu != "Qualification") {
                appliedFilters.value += "Qualification, "
                r =
                    r.filter { u -> u.data.qualification == it.highestEdu } as ArrayList<ProfileCardData>
            }
        }
        Log.d("SearchViewModel", "Filters: ${appliedFilters.value}")
        Log.d("SearchViewModel", "Applying filters removed ${listOfUsers.size - r.size} profiles!")
        return r
    }
}