package com.plcoding.stockmarketapp.presentation.company_listing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListingViewModel @Inject constructor(
    val repository: StockRepository
) : ViewModel() {

    var state by mutableStateOf(CompanyListingState())

    var searchJob: Job? = null


    fun onEvent(event: CompanyListingEvent) {
        when (event) {
            is CompanyListingEvent.Refresh -> {
                getCompanyListing(true)
            }
            is CompanyListingEvent.OnSearchQueryChange -> {
                searchJob?.cancel()
                state = state.copy(
                    searchQuery = event.query
                )
                searchJob = viewModelScope.launch {
                    getCompanyListing()
                }
            }
        }
    }


    fun getCompanyListing(
        fetchFromRemote: Boolean = false,
        query: String = state.searchQuery.lowercase()
    ) {
        viewModelScope.launch {
            repository
                .getCompanyListing(fetchFromRemote, query)
                .collect { result ->

                    when (result) {
                        is Resource.Success -> {

                            result.data?.let { list ->
                                state = state.copy(
                                    companies = list
                                )
                            }
                        }
                        is Resource.Error -> Unit
                        is Resource.Loading -> {
                            state = state.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }

                }

        }


    }


}