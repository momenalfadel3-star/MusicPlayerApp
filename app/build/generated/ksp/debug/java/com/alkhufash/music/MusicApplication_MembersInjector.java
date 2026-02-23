package com.alkhufash.music;

import androidx.hilt.work.HiltWorkerFactory;
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
public final class MusicApplication_MembersInjector implements MembersInjector<MusicApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public MusicApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<MusicApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new MusicApplication_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(MusicApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.alkhufash.music.MusicApplication.workerFactory")
  public static void injectWorkerFactory(MusicApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
