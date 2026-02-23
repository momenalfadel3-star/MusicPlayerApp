package com.alkhufash.music.data.repository;

import com.alkhufash.music.data.db.MusicDao;
import com.alkhufash.music.data.model.MediaStoreHelper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class MusicRepositoryImpl_Factory implements Factory<MusicRepositoryImpl> {
  private final Provider<MediaStoreHelper> mediaStoreHelperProvider;

  private final Provider<MusicDao> musicDaoProvider;

  public MusicRepositoryImpl_Factory(Provider<MediaStoreHelper> mediaStoreHelperProvider,
      Provider<MusicDao> musicDaoProvider) {
    this.mediaStoreHelperProvider = mediaStoreHelperProvider;
    this.musicDaoProvider = musicDaoProvider;
  }

  @Override
  public MusicRepositoryImpl get() {
    return newInstance(mediaStoreHelperProvider.get(), musicDaoProvider.get());
  }

  public static MusicRepositoryImpl_Factory create(
      Provider<MediaStoreHelper> mediaStoreHelperProvider, Provider<MusicDao> musicDaoProvider) {
    return new MusicRepositoryImpl_Factory(mediaStoreHelperProvider, musicDaoProvider);
  }

  public static MusicRepositoryImpl newInstance(MediaStoreHelper mediaStoreHelper,
      MusicDao musicDao) {
    return new MusicRepositoryImpl(mediaStoreHelper, musicDao);
  }
}
