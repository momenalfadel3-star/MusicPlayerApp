package com.alkhufash.music.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.alkhufash.music.service.MusicController;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SleepTimerWorker_Factory {
  private final Provider<MusicController> musicControllerProvider;

  public SleepTimerWorker_Factory(Provider<MusicController> musicControllerProvider) {
    this.musicControllerProvider = musicControllerProvider;
  }

  public SleepTimerWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, musicControllerProvider.get());
  }

  public static SleepTimerWorker_Factory create(Provider<MusicController> musicControllerProvider) {
    return new SleepTimerWorker_Factory(musicControllerProvider);
  }

  public static SleepTimerWorker newInstance(Context context, WorkerParameters params,
      MusicController musicController) {
    return new SleepTimerWorker(context, params, musicController);
  }
}
