package com.example.authfeature.di

import android.content.Context
import androidx.room.Room
import com.example.authfeature.data.api.AuthApi
import com.example.authfeature.data.api.FakeServerInterceptor
import com.example.authfeature.data.api.NotesApi
import com.example.authfeature.data.database.AppDatabase
import com.example.authfeature.data.database.NotesDao
import com.example.authfeature.data.database.UserDao
import com.example.authfeature.data.repository.AuthRepositoryImpl
import com.example.authfeature.data.repository.NotesRepositoryImpl
import com.example.authfeature.domain.repository.AuthRepository
import com.example.authfeature.domain.repository.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(FakeServerInterceptor()) // для реального сервера — убрать
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideNotesApi(retrofit: Retrofit): NotesApi = retrofit.create(NotesApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "auth_feature.db").build()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideNotesDao(db: AppDatabase): NotesDao = db.notesDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: NotesRepositoryImpl): NoteRepository
}
