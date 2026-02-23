package com.alkhufash.music.data.model;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class MediaStoreHelper_Factory implements Factory<MediaStoreHelper> {
  private final Provider<Context> contextProvider;

  public MediaStoreHelper_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public MediaStoreHelper get() {
    return newInstance(contextProvider.get());
  }

  public static MediaStoreHelper_Factory create(Provider<Context> contextProvider) {
    return new MediaStoreHelper_Factory(contextProvider);
  }

  public static MediaStoreHelper newInstance(Context context) {
    return new MediaStoreHelper(context);
  }
}
