/*
 * Copyright (c) 2021-2023 - for information on the respective copyright owner
 * see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package io.carbynestack.castor.client.download;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

/**
 * An abstract class to provide a base builder for castor clients.
 *
 * <p>This {@link ClientBuilderSupport} will ensure that at least one grpc address is defined for
 * {@link #serviceUris}.
 *
 * @param <T> The individual implementation of this builder for an individual client.
 * @param <C> The actual client to be build with this builder.
 */
public abstract class ClientBuilderSupport<T extends ClientBuilderSupport<T, C>, C> {
  public static final String ADDRESSES_MUST_NOT_BE_EMPTY_EXCEPTION_MSG =
      "Declared service URIs must not be empty";
  protected final List<String> serviceUris;

  /**
   * Creates a new {@link ClientBuilderSupport} with the given service addresses.
   *
   * @param serviceAddresses Addresses of the services to communicate with.
   * @throws NullPointerException if given serviceAddresses is null
   * @throws IllegalArgumentException if given serviceAddresses is empty
   * @throws IllegalArgumentException if a single given serviceAddress is null or empty
   */
  protected ClientBuilderSupport(@NonNull List<String> serviceAddresses) {
    if (serviceAddresses.isEmpty() || isAnyAddressEmpty(serviceAddresses)) {
      throw new IllegalArgumentException(ADDRESSES_MUST_NOT_BE_EMPTY_EXCEPTION_MSG);
    }
    this.serviceUris = new ArrayList<>();
    for (String address : serviceAddresses) {
      serviceUris.add(address);
    }
  }

  private boolean isAnyAddressEmpty(List<String> serviceAddresses) {
    if (serviceAddresses == null || serviceAddresses.size() == 0) return true;
    boolean result = false;
    for (String serviceAddress : serviceAddresses) {
      if (serviceAddress == null || serviceAddress.trim().equals("")) {
        result = true;
        break;
      }
    }
    return result;
  }

  protected abstract T getThis();

  /**
   * Disables the SSL certificate validation check.
   *
   * <p><b>WARNING</b><br>
   * Please be aware, that this option leads to insecure web connections and is meant to be used in
   * a local test setup only. Using this option in a productive environment is explicitly <u>not
   * recommended</u>.
   */
  public T withoutSslCertificateValidation() {
    return getThis();
  }

  public abstract C build();
}
