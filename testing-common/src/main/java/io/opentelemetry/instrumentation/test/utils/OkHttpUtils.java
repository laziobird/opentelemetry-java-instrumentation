/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.test.utils;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OkHttpUtils {

  private static final Logger CLIENT_LOGGER = LoggerFactory.getLogger("http-client");

  static {
    LoggerUtils.setLevel(CLIENT_LOGGER, ch.qos.logback.classic.Level.DEBUG);
  }

  private static final HttpLoggingInterceptor LOGGING_INTERCEPTOR =
      new HttpLoggingInterceptor(
          new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
              CLIENT_LOGGER.debug(message);
            }
          });

  static {
    LOGGING_INTERCEPTOR.setLevel(Level.BASIC);
  }

  static OkHttpClient.Builder clientBuilder() {
    TimeUnit unit = TimeUnit.MINUTES;
    return new OkHttpClient.Builder()
        .addInterceptor(LOGGING_INTERCEPTOR)
        .connectTimeout(1, unit)
        .writeTimeout(1, unit)
        .readTimeout(1, unit);
  }

  public static OkHttpClient client() {
    return client(false);
  }

  public static OkHttpClient client(boolean followRedirects) {
    return clientBuilder().followRedirects(followRedirects).build();
  }

  private OkHttpUtils() {}
}
