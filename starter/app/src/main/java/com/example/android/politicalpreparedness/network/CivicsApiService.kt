package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.network.models.response.ElectionResponse
import com.example.android.politicalpreparedness.network.models.response.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.response.VoterInfoResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

private const val BASE_URL = "https://www.googleapis.com/civicinfo/v2/"

private val moshi = Moshi.Builder()
    .add(Date::class.java, Rfc3339DateJsonAdapter())
    .add(ElectionAdapter())
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(CivicsHttpClient.getClient())
    .baseUrl(BASE_URL)
    .build()

/**
 *  Documentation for the Google Civics API Service can be found at https://developers.google.com/civic-information/docs/v2
 */

interface CivicsApiService {
    @GET("elections")
    suspend fun queryElections(): ElectionResponse

    @GET("voterinfo")
    suspend fun voterInfo(
        @Query("address") address: String,
        @Query("electionId") electionId: Long? = null,
        @Query("officialOnly") officialOnly: Boolean? = false
    ): VoterInfoResponse

    @GET("representatives")
    suspend fun queryRepresentatives(
        @Query("address") address: String,
        @Query("includeOffices") includeOffices: Boolean = true,
        @Query("levels") levels: String? = null,
        @Query("roles") roles: String? = null
    ): RepresentativeResponse
}

object CivicsApi {
    val retrofitService: CivicsApiService by lazy {
        retrofit.create(CivicsApiService::class.java)
    }
}