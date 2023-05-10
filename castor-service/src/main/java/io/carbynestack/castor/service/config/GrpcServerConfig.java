/*
 * Copyright (c) 2021-2023 - for information on the respective copyright owner
 * see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package io.carbynestack.castor.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration class for the GRPC server. Contains fields for the server port and various GRPC
 * services required by the server.
 */
@ConfigurationProperties(prefix = "carbynestack.castor.grpc.server")
@Component
@Data
public class GrpcServerConfig {
  int port;
  String hostName;
}
