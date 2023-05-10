/*
 * Copyright (c) 2023 - for information on the respective copyright owner
 * see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package io.carbynestack.castor.client.download;

import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;
import java.util.concurrent.Executor;

public class AuthenticationCallCredentials extends CallCredentials {

  public static Metadata.Key<String> META_DATA_KEY =
      Metadata.Key.of("Authentication", Metadata.ASCII_STRING_MARSHALLER);
  private String token;

  public AuthenticationCallCredentials(String token) {
    this.token = token;
  }

  @Override
  public void applyRequestMetadata(
      RequestInfo requestInfo, Executor executor, MetadataApplier metadataApplier) {
    executor.execute(
        () -> {
          try {
            Metadata headers = new Metadata();
            headers.put(META_DATA_KEY, "Bearer " + token);
            metadataApplier.apply(headers);
          } catch (Exception e) {
            e.printStackTrace();
            metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
          }
        });
  }

  @Override
  public void thisUsesUnstableApi() {
    // According to https://github.com/grpc/grpc-java/issues/1914 CallCredentials can be considered
    // production code
  }
}
