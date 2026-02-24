package com.alkhufash.music.di

import android.content.Context
import androidx.room.Room
import com.alkhufash.music.data.db.MusicDao
import com.alkhufash.music.data.db.MusicDatabase
import com.alkhufash.music.data.model.MediaStoreHelper
import com.alkhufash.music.data.repository.MusicRepositoryImpl
import com.alkhufash.music.domain.repository.MusicRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MusicDatabase {
        return Room.databaseBuilder(
            context,
            MusicDatabase::class.java,
            MusicDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMusicDao(database: MusicDatabase): MusicDao {
        return database.musicDao()
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMusicRepository(
        impl: MusicRepositoryImpl
    ): MusicRepository
}
