package com.alkhufash.music.presentation.viewmodel;

import android.content.Context;
import com.alkhufash.music.domain.repository.MusicRepository;
import com.alkhufash.music.service.MusicController;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class MusicViewModel_Factory implements Factory<MusicViewModel> {
  private final Provider<MusicRepository> repositoryProvider;

  private final Provider<MusicController> musicControllerProvider;

  private final Provider<Context> contextProvider;

  public MusicViewModel_Factory(Provider<MusicRepository> repositoryProvider,
      Provider<MusicController> musicControllerProvider, Provider<Context> contextProvider) {
    this.repositoryProvider = repositoryProvider;
    this.musicControllerProvider = musicControllerProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public MusicViewModel get() {
    return newInstance(repositoryProvider.get(), musicControllerProvider.get(), contextProvider.get());
  }

  public static MusicViewModel_Factory create(Provider<MusicRepository> repositoryProvider,
      Provider<MusicController> musicControllerProvider, Provider<Context> contextProvider) {
    return new MusicViewModel_Factory(repositoryProvider, musicControllerProvider, contextProvider);
  }

  public static MusicViewModel newInstance(MusicRepository repository,
      MusicController musicController, Context context) {
    return new MusicViewModel(repository, musicController, context);
  }
}
