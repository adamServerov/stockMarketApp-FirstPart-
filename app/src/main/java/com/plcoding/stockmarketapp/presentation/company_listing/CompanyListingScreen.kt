package com.plcoding.stockmarketapp.presentation.company_listing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun CompanyListingScreen(
    navigator: DestinationsNavigator,
    viewModel: CompanyListingViewModel = hiltViewModel()
) {

    val state = viewModel.state

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = state.isRefreshing)

    OutlinedTextField(
        value = state.searchQuery,
        onValueChange = {
            viewModel.onEvent(CompanyListingEvent.OnSearchQueryChange(it))
        },
        placeholder = {
            Text(
                "Search..."
            )
        },
        singleLine = true,
        maxLines = 1
    )

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            viewModel.onEvent(CompanyListingEvent.Refresh)
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            items(state.companies.size) { i ->
                val company = state.companies[i]

                CompanyItem(
                    company = company,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {

                        }
                )

                if (i < state.companies.size) {
                    Divider(
                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        )
                    )
                }

            }
        }

    }


}