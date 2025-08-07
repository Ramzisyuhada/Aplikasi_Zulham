package com.example.aplikasi_zulham.Interface

import CuacaResponse
import com.example.aplikasi_zulham.Model.Aduan
import com.example.aplikasi_zulham.Model.Cuaca
import com.example.aplikasi_zulham.Model.Rating
import com.example.aplikasi_zulham.repository.LoginResponse
import com.example.aplikasi_zulham.repository.Users
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {



    @GET("publik/prakiraan-cuaca")
    suspend fun getWeatherData(@Query("adm4") adm4: String): Response<CuacaResponse>


    @POST("register")
    suspend fun AddUser(@Body user : Users): Response<Any>

    @JvmSuppressWildcards
    @POST("login")
    suspend fun Login(@Body user: Map<String, Any>): Response<ResponseBody>

    @POST("forget-password")
    suspend fun ResetPassword(@Body User: Map<String, String>) : Response<Any>



    @Multipart
    @POST("complaints")
    suspend fun AddComplaint(
        @Part("complaint") complaint: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("complete_address") completeAddress: RequestBody,
        @Part media: List<MultipartBody.Part>
    ): Response<ResponseBody>

    @GET("complaints")
    suspend fun GetAllAduan(@Query("id_tour") userId: Int):Response<ResponseBody>

    @GET("complaints/{id}")
    suspend fun getComplaintById(@Path("id") id: Int):Response<ResponseBody>

    @JvmSuppressWildcards

    @POST("ratings")
    suspend fun AddRating(@Body rating: Map<String, Any>): Response<Any>
    @GET("ratings")
    suspend fun GetRating(@Query("id_users") userId: Int ,  @Query("id_tour") IdRating: Int):Response<ResponseBody>

    @GET("get-rating")
    suspend fun GetAllRating():Response<ResponseBody>

    @PUT("ratings/{id}")
    suspend fun UpdateRating(
        @Path("id") id: Int,
        @Body rating: Map<String, @JvmSuppressWildcards Any>
    ): Response<Any>

    @POST("logout")
    suspend fun Logout():Response<ResponseBody>

    @PUT("users/{id}")
    suspend fun UpdateUser(
        @Path("id") id: Int,
        @Body rating: Map<String, @JvmSuppressWildcards Any>
    ): Response<Any>

    @GET("users/{id}")
    suspend fun GetUserById(
        @Path("id") id: Int
    ): Response<ResponseBody>

    @GET("complaints/user/{userId}/tour/{tourId}")
    suspend fun getAllComplaintsByUserAndTour(
        @Path("userId") userId: Int,
        @Path("tourId") tourId: Int
    ): Response<ResponseBody>
    @GET("ratings/tour/{tourId}")
    suspend fun GetRatingByTour( @Path("tourId") tourId: Int):Response<ResponseBody>


    /*
    * API ADMIIN
    * */

//    /showPengaduan/{id}
    @GET("admin/complaints/tour/{id_tour}")
    suspend fun GetAllAduanAdmin(@Path("id_tour") userId: Int):Response<ResponseBody>
    @GET("showPengaduan/{id}")
    suspend fun GetAduanbyAdmin(@Path("id") userId: Int):Response<ResponseBody>

}
