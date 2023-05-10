/*
 * Copyright (c) 2021-2023 - for information on the respective copyright owner
 * see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package io.carbynestack.castor.service.config;

import io.carbynestack.castor.client.download.CastorInterVcpClient;
import io.carbynestack.castor.client.download.DefaultCastorInterVcpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CastorInterVcpConfig {

  @Bean
  @ConditionalOnProperty(value = "carbynestack.castor.master", havingValue = "true")
  public CastorInterVcpClient castorInterVcpClient(
      CastorServiceProperties castorServiceProperties) {
    DefaultCastorInterVcpClient.Builder builder =
        DefaultCastorInterVcpClient.builder(castorServiceProperties.getSlaveUris());
    builder.withoutSslCertificateValidation();
    return builder.build();
  }
}
