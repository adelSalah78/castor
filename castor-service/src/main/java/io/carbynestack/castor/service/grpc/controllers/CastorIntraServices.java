/*
 * Copyright (c) 2023 - for information on the respective copyright owner
 * see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package io.carbynestack.castor.service.grpc.controllers;

import static io.carbynestack.castor.common.entities.TelemetryData.DEFAULT_REQUEST_INTERVAL;

import io.carbynestack.castor.common.entities.TupleList;
import io.carbynestack.castor.common.entities.TupleType;
import io.carbynestack.castor.common.entities.Utils;
import io.carbynestack.castor.common.grpc.*;
import io.carbynestack.castor.service.download.TelemetryService;
import io.carbynestack.castor.service.download.TuplesDownloadService;
import io.carbynestack.castor.service.grpc.DefaultCastorUploadService;
import io.carbynestack.castor.service.persistence.fragmentstore.TupleChunkFragmentStorageService;
import io.grpc.stub.StreamObserver;
import java.time.Duration;
import java.util.UUID;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CastorIntraServices extends IntraServiceGrpc.IntraServiceImplBase {

  private final DefaultCastorUploadService uploadService;
  private final TelemetryService telemetryService;

  private final TupleChunkFragmentStorageService fragmentStorageService;

  private final TuplesDownloadService tuplesDownloadService;

  @Override
  public void getTelemetryData(
      GrpcTelemetryDataRequest request,
      StreamObserver<GrpcTelemetryDataResponse> responseObserver) {
    Duration interval =
        request.getRequestInterval() == 0L
            ? DEFAULT_REQUEST_INTERVAL
            : Duration.ofSeconds(request.getRequestInterval());
    responseObserver.onNext(
        Utils.convertToProtoTelemetryData(telemetryService.getTelemetryDataForInterval(interval)));
    responseObserver.onCompleted();
  }

  @Override
  public void activateFragmentsForTupleChunk(
      GrpcTupleChunkRequest request, StreamObserver<GrpcEmpty> responseObserver) {
    fragmentStorageService.activateFragmentsForTupleChunk(UUID.fromString(request.getChunkId()));
    responseObserver.onNext(GrpcEmpty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void getTupleList(
      GrpcTuplesListRequest request, StreamObserver<GrpcTuplesListResponse> responseObserver) {

    io.carbynestack.castor.common.entities.TupleType tupleType =
        TupleType.valueOf(request.getType());

    TupleList tupleList =
        tuplesDownloadService.getTupleList(
            tupleType.getTupleCls(),
            tupleType.getField(),
            request.getCount(),
            UUID.fromString(request.getRequestId()));
    responseObserver.onNext(Utils.createProtoTuplesListResponse(tupleList));
    responseObserver.onCompleted();
  }

  @Override
  public void uploadTupleChunk(
      GrpcUploadTupleChunkRequest request,
      StreamObserver<GrpcUploadTupleChunkResponse> responseObserver) {
    GrpcUploadTupleChunkResponse response =
        Utils.convertToUploadTupleChunkResponseProto(uploadService.uploadTupleChunk(request));
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
