package at.htl.activitiy_android.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // Für Android Emulator: 10.0.2.2 = localhost des Host-Rechners
    // Für echtes Gerät: IP-Adresse deines Rechners im gleichen Netzwerk
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val api: ActivityApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ActivityApi::class.java)
    }
}