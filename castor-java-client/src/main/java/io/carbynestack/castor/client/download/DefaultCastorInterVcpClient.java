/*
 * Copyright (c) 2021-2023 - for information on the respective copyright owner
 * see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package io.carbynestack.castor.client.download;

import io.carbynestack.castor.common.entities.ActivationStatus;
import io.carbynestack.castor.common.entities.Reservation;
import io.carbynestack.castor.common.entities.Utils;
import io.carbynestack.castor.common.exceptions.CastorClientException;
import io.carbynestack.castor.common.grpc.*;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
@Slf4j
public class DefaultCastorInterVcpClient implements CastorInterVcpClient {
  public static final String FAILED_SHARING_RESERVATION_EXCEPTION_MSG =
      "Failed sharing Reservation.";
  public static final String FAILED_UPDATING_RESERVATION_EXCEPTION_MSG =
      "Failed sending reservation update.";

  List<InterServiceGrpc.InterServiceBlockingStub> stubs;

  public DefaultCastorInterVcpClient(Builder builder) {
    this.stubs = new ArrayList<>(builder.serviceUris.size());
    builder.serviceUris.forEach(
        address -> {
          try {
            this.stubs.add(createStub(address, builder));
          } catch (SSLException e) {
            throw new CastorClientException(
                "Exception initiating one of the gRPC clients: " + e.getMessage());
          }
        });
  }

  @Override
  public boolean shareReservation(Reservation reservation) {
    boolean result = true;
    log.debug("Sharing reservation {}", reservation);
    GrpcReservation request = Utils.convertToProtoReservation(reservation);
    try {
      for (int i = 0; i < stubs.size(); i++) {
        GrpcEmpty response = stubs.get(i).keepAndApplyReservation(request);
        if (response == null) {
          log.debug("Failed to share reservation");
          result = false;
          break;
        }
      }
      return result;
    } catch (Exception e) {
      throw new CastorClientException(FAILED_SHARING_RESERVATION_EXCEPTION_MSG, e);
    }
  }

  @Override
  public void updateReservationStatus(String reservationId, ActivationStatus status) {
    try {
      GrpcUpdateReservationRequest request =
          GrpcUpdateReservationRequest.newBuilder()
              .setReservationId(reservationId)
              .setActivationStatus(GrpcActivationStatus.valueOf(status.name()))
              .build();
      for (int i = 0; i < stubs.size(); i++) {
        log.debug("Sending reservation update for reservation #{}: {}", reservationId, status);
        GrpcEmpty response = stubs.get(i).updateReservation(request);
        if (response == null) {
          log.debug("Grp call to update reservation failed");
        }
      }
    } catch (Exception e) {
      throw new CastorClientException(FAILED_UPDATING_RESERVATION_EXCEPTION_MSG, e);
    }
  }

  static InterServiceGrpc.InterServiceBlockingStub createStub(String address, Builder builder)
      throws SSLException {
    return InterServiceGrpc.newBlockingStub(Utils.createGrpcChannel(address));
  }

  public static Builder builder(List<String> serviceAddresses) {
    return new Builder(serviceAddresses);
  }

  /** Builder class to create a new {@link DefaultCastorInterVcpClient}. */
  public static class Builder extends ClientBuilderSupport<Builder, DefaultCastorInterVcpClient> {

    /**
     * Create a new {@link Builder} to easily configure and create a new {@link
     * DefaultCastorInterVcpClient}.
     *
     * @param serviceAddresses Addresses of the service(s) the new {@link
     *     DefaultCastorInterVcpClient} should communicate with.
     * @throws NullPointerException if given serviceAddresses is null
     * @throws IllegalArgumentException if a single service address is null or cannot be parsed
     */
    private Builder(List<String> serviceAddresses) {
      super(serviceAddresses);
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    /**
     * Builds and returns a new {@link DefaultCastorInterVcpClient} according to the given
     * configuration.
     *
     * @throws CastorClientException If the {@link CastorInterVcpClient} could not be instantiated.
     */
    public DefaultCastorInterVcpClient build() {
      return new DefaultCastorInterVcpClient(getThis());
    }
  }
}
