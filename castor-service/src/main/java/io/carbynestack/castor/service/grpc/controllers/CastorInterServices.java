/*
 * Copyright (c) 2023 - for information on the respective copyright owner
 * see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package io.carbynestack.castor.service.grpc.controllers;

import io.carbynestack.castor.common.entities.ActivationStatus;
import io.carbynestack.castor.common.entities.Utils;
import io.carbynestack.castor.common.exceptions.CastorServiceException;
import io.carbynestack.castor.common.grpc.GrpcEmpty;
import io.carbynestack.castor.common.grpc.GrpcReservation;
import io.carbynestack.castor.common.grpc.GrpcUpdateReservationRequest;
import io.carbynestack.castor.common.grpc.InterServiceGrpc;
import io.carbynestack.castor.service.persistence.cache.ReservationCachingService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CastorInterServices extends InterServiceGrpc.InterServiceImplBase {
  /** The underlying reservation caching service. */
  ReservationCachingService reservationCachingService;

  /**
   * Applies a reservation via gRPC.
   *
   * @param request the request containing the reservation to apply
   * @param responseObserver the observer to send the response to
   */
  @Override
  public void keepAndApplyReservation(
      GrpcReservation request, StreamObserver<GrpcEmpty> responseObserver) {
    reservationCachingService.keepAndApplyReservation(Utils.convertFromProtoReservation(request));
    responseObserver.onNext(GrpcEmpty.newBuilder().build());
    responseObserver.onCompleted();
  }

  /**
   * Updates a reservation via gRPC.
   *
   * @param request the request containing the reservation ID and activation status
   * @param responseObserver the observer to send the response to
   */
  @Override
  public void updateReservation(
      GrpcUpdateReservationRequest request, StreamObserver<GrpcEmpty> responseObserver) {
    try {
      reservationCachingService.updateReservation(
          request.getReservationId(),
          ActivationStatus.valueOf(request.getActivationStatus().name()));
      responseObserver.onNext(GrpcEmpty.newBuilder().build());
      responseObserver.onCompleted();
    } catch (CastorServiceException e) {
      responseObserver.onError(e);
    } catch (IllegalArgumentException e) {
      responseObserver.onError(
          Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asException());
    } catch (Exception e) {
      responseObserver.onError(Status.UNKNOWN.withDescription("General Error").asException());
    }
  }
}
