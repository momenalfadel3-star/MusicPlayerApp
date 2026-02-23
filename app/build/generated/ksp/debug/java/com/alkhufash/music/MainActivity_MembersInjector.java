package com.alkhufash.music;

import com.alkhufash.music.service.MusicController;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<MusicController> musicControllerProvider;

  public MainActivity_MembersInjector(Provider<MusicController> musicControllerProvider) {
    this.musicControllerProvider = musicControllerProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<MusicController> musicControllerProvider) {
    return new MainActivity_MembersInjector(musicControllerProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectMusicController(instance, musicControllerProvider.get());
  }

  @InjectedFieldSignature("com.alkhufash.music.MainActivity.musicController")
  public static void injectMusicController(MainActivity instance, MusicController musicController) {
    instance.musicController = musicController;
  }
}
