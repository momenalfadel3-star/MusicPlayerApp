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
public final class StartTimerWorker_AssistedFactory_Impl implements StartTimerWorker_AssistedFactory {
  private final StartTimerWorker_Factory delegateFactory;

  StartTimerWorker_AssistedFactory_Impl(StartTimerWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public StartTimerWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<StartTimerWorker_AssistedFactory> create(
      StartTimerWorker_Factory delegateFactory) {
    return InstanceFactory.create(new StartTimerWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<StartTimerWorker_AssistedFactory> createFactoryProvider(
      StartTimerWorker_Factory delegateFactory) {
    return InstanceFactory.create(new StartTimerWorker_AssistedFactory_Impl(delegateFactory));
  }
}
