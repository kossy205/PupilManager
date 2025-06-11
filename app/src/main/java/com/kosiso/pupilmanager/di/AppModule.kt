package com.kosiso.pupilmanager.di

import android.content.Context
import androidx.room.Room
import com.kosiso.pupilmanager.data.ApiResponseHelper
import com.kosiso.pupilmanager.data.local.PaginationDao
import com.kosiso.pupilmanager.data.local.PupilDao
import com.kosiso.pupilmanager.data.local.RoomDatabase
import com.kosiso.pupilmanager.data.remote.Interceptor
import com.kosiso.pupilmanager.data.remote.PupilApi
import com.kosiso.pupilmanager.data.repository.MainRepoImpl
import com.kosiso.pupilmanager.data.repository.MainRepository
import com.kosiso.pupilmanager.utils.Constants.ROOM_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    val baseUrl = "https://androidtechnicaltestapi-test.bridgeinternationalacademies.com"

    @Singleton
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RoomDatabase::class.java,
        ROOM_DATABASE_NAME
    )
        .fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun providePupilDao(db: RoomDatabase) = db.pupilDao()

    @Singleton
    @Provides
    fun providePaginationDao(db: RoomDatabase) = db.paginationDao()


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(Interceptor())
            .connectTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun providePupilApi(retrofit: Retrofit): PupilApi =
        retrofit.create(PupilApi::class.java)

    @Singleton
    @Provides
    fun provideApiResponseHelper(): ApiResponseHelper = ApiResponseHelper()

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Singleton
    @Provides
    fun provideMainRepo(
        pupilDao: PupilDao,
        paginationDao: PaginationDao,
        pupilApi: PupilApi,
        @ApplicationContext context: Context,
        apiResponseHelper: ApiResponseHelper
    ): MainRepository{
        return MainRepoImpl(
            pupilDao,
            paginationDao,
            pupilApi,
            context,
            apiResponseHelper
        )
    }
}