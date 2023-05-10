/*
 * Copyright (c) 2021-2023 - for information on the respective copyright owner
 * see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package io.carbynestack.castor.service;

import io.carbynestack.castor.service.grpc.ServerStarter;
import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class CastorServiceApplication {

  public static void main(String[] args) throws IOException, InterruptedException {
    ConfigurableApplicationContext context =
        SpringApplication.run(CastorServiceApplication.class, args);
    context.getBean(ServerStarter.class).startGrpcServer();
  }
}
