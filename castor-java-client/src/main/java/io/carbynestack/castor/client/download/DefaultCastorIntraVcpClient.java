/*
 * Copyright (c) 2021-2023 - for information on the respective copyright owner
 * see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package io.carbynestack.castor.client.download;

import static io.carbynestack.castor.common.entities.TelemetryData.DEFAULT_REQUEST_INTERVAL;
import static java.util.Collections.singletonList;

import com.google.protobuf.ByteString;
import io.carbynestack.castor.common.entities.*;
import io.carbynestack.castor.common.entities.TupleChunk;
import io.carbynestack.castor.common.exceptions.CastorClientException;
import io.carbynestack.castor.common.grpc.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;
import lombok.Value;

/**
 * The default implementation of a {@link CastorIntraVcpClient}. It can be used to download {@link
 * io.carbynestack.castor.common.entities.MultiplicationTriple}s or {@link InputMask}s from one ore
 * more castor service(s).
 */
@Value
public class DefaultCastorIntraVcpClient implements CastorIntraVcpClient {
  public static final String FAILED_DOWNLOADING_TUPLES_EXCEPTION_MSG =
      "Failed downloading tuples from service %s: %s";
  public static final String FAILED_FETCHING_TELEMETRY_DATA_EXCEPTION_MSG =
      "Failed fetching telemetry data from service %s: %s";

  static final String ERROR_ACTIVATING_TUPLE_CHUNK =
      "Error while activating tuple chunk with chunk id %s";

  IntraServiceGrpc.IntraServiceBlockingStub stub;

  /**
   * The client is capable to communicate with the given services using gRPC, according to the
   * scheme defined by the give url. In addition trustworthy SSL certificates can be defined to
   * allow secure communication with services that provide self-signed certificates or ssl
   * certificate validation can be disabled.
   *
   * @param builder An {@link Builder} object containing the client's configuration.
   * @throws CastorClientException if an SSLException occurs while building the gRPC client
   */
  public DefaultCastorIntraVcpClient(Builder builder) {
    try {
      stub = buildIntraGrpcClient(builder);
    } catch (SSLException e) {
      throw new CastorClientException("Error creating gRPC client: " + e.getMessage());
    }
  }

  /*for passing stubs from unit test*/
  public DefaultCastorIntraVcpClient(IntraServiceGrpc.IntraServiceBlockingStub stub) {
    this.stub = stub;
  }

  private IntraServiceGrpc.IntraServiceBlockingStub buildIntraGrpcClient(Builder builder)
      throws SSLException {
    return IntraServiceGrpc.newBlockingStub(Utils.createGrpcChannel(builder.serviceUris.get(0)));
  }

  @Override
  public List<TupleList> downloadTupleShares(UUID requestId, TupleType tupleType, long count) {
    try {
      GrpcTuplesListRequest request =
          GrpcTuplesListRequest.newBuilder()
              .setRequestId(requestId.toString())
              .setCount(count)
              .setType(tupleType.name())
              .build();
      return Utils.createFromProtoTuplesListResponse(stub.getTupleList(request), tupleType.name());
    } catch (Exception e) {
      throw new CastorClientException(
          String.format(FAILED_DOWNLOADING_TUPLES_EXCEPTION_MSG, "Grpc", e.getMessage()), e);
    }
  }

  @Override
  public TelemetryData getTelemetryData() {
    try {
      GrpcTelemetryDataRequest request =
          GrpcTelemetryDataRequest.newBuilder()
              .setRequestInterval(DEFAULT_REQUEST_INTERVAL.getSeconds())
              .build();
      return Utils.convertFromProtoTelemetryData(stub.getTelemetryData(request));
    } catch (Exception chce) {
      throw new CastorClientException(
          String.format(
              FAILED_FETCHING_TELEMETRY_DATA_EXCEPTION_MSG, "TelemetryService", chce.getMessage()),
          chce);
    }
  }

  @Override
  public TelemetryData getTelemetryData(long interval) {
    try {
      GrpcTelemetryDataRequest request =
          GrpcTelemetryDataRequest.newBuilder().setRequestInterval(interval).build();
      return Utils.convertFromProtoTelemetryData(stub.getTelemetryData(request));
    } catch (Exception chce) {
      throw new CastorClientException(
          String.format(
              FAILED_FETCHING_TELEMETRY_DATA_EXCEPTION_MSG, "TelemetryService", chce.getMessage()),
          chce);
    }
  }

  @Override
  public boolean uploadTupleChunk(TupleChunk tupleChunk) throws CastorClientException {
    return uploadTupleChunk(tupleChunk, DEFAULT_CONNECTION_TIMEOUT);
  }

  @Override
  public boolean uploadTupleChunk(TupleChunk tupleChunk, long timeout)
      throws CastorClientException {
    try {
      GrpcUploadTupleChunkRequest request =
          GrpcUploadTupleChunkRequest.newBuilder()
              .setChunkId(tupleChunk.getChunkId().toString())
              .setTupleType(tupleChunk.getTupleType().name())
              .setTuples(ByteString.copyFrom(tupleChunk.getTuples()))
              .build();
      stub.withDeadlineAfter(timeout, TimeUnit.MILLISECONDS).uploadTupleChunk(request);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public void activateTupleChunk(UUID tupleChunkId) {
    try {
      GrpcTupleChunkRequest request =
          GrpcTupleChunkRequest.newBuilder().setChunkId(tupleChunkId.toString()).build();
      stub.activateFragmentsForTupleChunk(request);
    } catch (Exception e) {
      throw new CastorClientException(
          String.format(ERROR_ACTIVATING_TUPLE_CHUNK, tupleChunkId.toString()), e);
    }
  }

  /**
   * Create a new {@link DefaultCastorIntraVcpClient.Builder} to easily configure and create a new
   * {@link DefaultCastorIntraVcpClient}.
   *
   * @throws IllegalArgumentException if the given service addresses is null, empty, or cannot be
   *     parsed
   */
  public static Builder builder(String castorServiceUri) {
    return new Builder(castorServiceUri);
  }

  /** Builder class to create a new {@link DefaultCastorIntraVcpClient}. */
  public static class Builder extends ClientBuilderSupport<Builder, DefaultCastorIntraVcpClient> {

    /**
     * Create a new {@link DefaultCastorIntraVcpClient.Builder} to easily configure and create a new
     * {@link DefaultCastorIntraVcpClient}.
     *
     * @param castorServiceUri gRPC Address of the service the new {@link
     *     DefaultCastorIntraVcpClient} should communicate with.
     * @throws IllegalArgumentException if the given service addresses is null, empty, or cannot be
     *     parsed
     */
    private Builder(String castorServiceUri) {
      super(singletonList(castorServiceUri));
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    /**
     * Builds and returns a new {@link DefaultCastorIntraVcpClient} according to the given
     * configuration.
     *
     * @throws CastorClientException If the {@link DefaultCastorIntraVcpClient} could not be
     *     instantiated.
     */
    public DefaultCastorIntraVcpClient build() {
      return new DefaultCastorIntraVcpClient(getThis());
    }
  }
}
