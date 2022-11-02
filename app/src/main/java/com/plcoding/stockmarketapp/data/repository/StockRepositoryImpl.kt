package com.plcoding.stockmarketapp.data.repository

import com.plcoding.stockmarketapp.data.csv.CSVParser
import com.plcoding.stockmarketapp.data.csv.CompanyListingParser
import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.mapper.toCompanyListing
import com.plcoding.stockmarketapp.data.mapper.toCompanyListingEntity
import com.plcoding.stockmarketapp.data.remote.StockApi
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockApi,
    val db: StockDatabase,
    val companyListingParser: CSVParser<CompanyListing>
) : StockRepository {

    val dao = db.dao

    override suspend fun getCompanyListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {

        return flow {

            emit(Resource.Loading(isLoading = true))
            val localListing = dao.searchCompanyListings(query)
            emit(Resource.Success(
                data = localListing.map { it.toCompanyListing() }
            ))
            val isDbEmpty = localListing.isEmpty() && query.isBlank()
            val shouldJustGetFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustGetFromCache) {
                emit(Resource.Loading(isLoading = false))
                return@flow
            }


            val remoteListing = try {
                val listing = api.getListings()
                companyListingParser.parse(listing.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("couldnt load data from remote"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("couldnt load data from remote"))
                null
            }

            remoteListing?.let { list ->
                dao.clerAllListings()
                dao.insertCompanyListings(list.map { it.toCompanyListingEntity() })

                emit(Resource.Success(
                    data = dao
                        .searchCompanyListings("")
                        .map { it.toCompanyListing() }
                ))

            }
        }

    }
}