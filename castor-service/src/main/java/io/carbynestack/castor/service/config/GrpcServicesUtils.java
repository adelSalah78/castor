/*
 * Copyright (c) 2021-2023 - for information on the respective copyright owner
 * see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package io.carbynestack.castor.service.config;

import io.carbynestack.castor.service.download.TelemetryService;
import io.carbynestack.castor.service.download.TuplesDownloadService;
import io.carbynestack.castor.service.grpc.DefaultCastorUploadService;
import io.carbynestack.castor.service.grpc.controllers.*;
import io.carbynestack.castor.service.persistence.cache.ReservationCachingService;
import io.carbynestack.castor.service.persistence.fragmentstore.TupleChunkFragmentStorageService;
import io.grpc.ServerBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Data
public class GrpcServicesUtils {
  @Autowired GrpcServerConfig grpcServerConfig;

  @Autowired TupleChunkFragmentStorageService fragmentStorageService;

  @Autowired TuplesDownloadService tuplesDownloadService;

  @Autowired ReservationCachingService reservationCachingService;

  @Autowired DefaultCastorUploadService defaultCastorUploadService;

  @Autowired TelemetryService telemetryService;

  public ServerBuilder addServices(ServerBuilder serverBuilder) {
    return serverBuilder
        .addService(new CastorInterServices(reservationCachingService))
        .addService(
            new CastorIntraServices(
                defaultCastorUploadService,
                telemetryService,
                fragmentStorageService,
                tuplesDownloadService));
  }
}
