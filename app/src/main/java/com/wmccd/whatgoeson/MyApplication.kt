package com.wmccd.whatgoeson

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.wmccd.whatgoeson.repository.database.AppDatabase
import com.wmccd.whatgoeson.repository.datastore.AppDataStore
import com.wmccd.whatgoeson.repository.webservice.setlist.api.SetListFmApiService
import com.wmccd.whatgoeson.repository.webservice.setlist.SetListFmRepository
import com.wmccd.whatgoeson.utility.device.InstalledAppChecker
import com.wmccd.whatgoeson.utility.logger.ILogger
import com.wmccd.whatgoeson.utility.logger.Logger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //create the global objects that can be accessed anywhere inside the app
        appContext = this
        repository = Repository(
            createAppDataStore(),
            createAppDatabaseInstance(),
            createSetListFmApiInstance(),
            createSetListFmRepository()
        )
        utilities = Utilities(
            gson = Gson(),
            logger = Logger()
        )
        val installedAppChecker = InstalledAppChecker()
        device = Device(
            spotifyInstalled = installedAppChecker.check(InstalledAppChecker.AppPackage.SPOTIFY),
            youTubeMusicInstalled = installedAppChecker.check(InstalledAppChecker.AppPackage.YOUTUBE_MUSIC)
        )
    }

    private fun createSetListFmRepository() = SetListFmRepository()

    private fun createAppDataStore() = AppDataStore(this)

    private fun createAppDatabaseInstance() = AppDatabase.getDatabase(this)

    private fun createSetListFmApiInstance(): SetListFmApiService {
        //sets up the logging interceptor that will send request and response data to logcat
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Set the desired logging level
        }

        //add the logging interceptor to the client
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(10, TimeUnit.SECONDS)    // Read timeout
            .writeTimeout(10, TimeUnit.SECONDS)   // Write timeout
            .build()

        //set up the Retrofit instance that will call all the service end points at the base URL
        val retrofit = Retrofit.Builder()
            .baseUrl(SetListFmApiService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(SetListFmApiService::class.java)
    }

    //Gather all the sources of data into one object so easier to work with
    data class Repository(
        val appDataStore: AppDataStore,
        val appDatabase: AppDatabase,
        val setListFmApiService: SetListFmApiService,
        val setListFmRepository: SetListFmRepository
    )

    data class Utilities(
        val gson: Gson,
        val logger: ILogger,
    )

    data class Device(
        val spotifyInstalled: Boolean,
        val youTubeMusicInstalled: Boolean,
    )

    companion object {
        //Declares global variables that can be accessed from anywhere in the app
        lateinit var appContext: Context
            private set
        lateinit var repository: Repository
            private set
        lateinit var utilities: Utilities
            private set
        lateinit var device: Device
            private set
    }
}