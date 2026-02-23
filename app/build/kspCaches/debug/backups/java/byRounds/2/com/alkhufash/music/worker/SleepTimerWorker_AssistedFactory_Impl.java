package com.alkhufash.music.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SleepTimerWorker_AssistedFactory_Impl implements SleepTimerWorker_AssistedFactory {
  private final SleepTimerWorker_Factory delegateFactory;

  SleepTimerWorker_AssistedFactory_Impl(SleepTimerWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public SleepTimerWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<SleepTimerWorker_AssistedFactory> create(
      SleepTimerWorker_Factory delegateFactory) {
    return InstanceFactory.create(new SleepTimerWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<SleepTimerWorker_AssistedFactory> createFactoryProvider(
      SleepTimerWorker_Factory delegateFactory) {
    return InstanceFactory.create(new SleepTimerWorker_AssistedFactory_Impl(delegateFactory));
  }
}
