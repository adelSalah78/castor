/*
 * Copyright (c) 2021-2023 - for information on the respective copyright owner
 * see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package io.carbynestack.castor.client.download;

import io.carbynestack.castor.common.entities.*;
import io.carbynestack.castor.common.exceptions.CastorClientException;
import java.util.List;
import java.util.UUID;

/**
 * Client interface for all MPC-Cluster internal Service-to-Castor-Service operations used to
 * download {@link TupleList}s or to download telemetry data containing tuple consumption rate and
 * number of available tuples from castor.<br>
 * Consumption rate is always calculated as consumption per second for a given interval (the last n
 * seconds)..
 */
public interface CastorIntraVcpClient {

  /** Default connection timeout for gRPC connections to a castor service in milliseconds. */
  long DEFAULT_CONNECTION_TIMEOUT = 60000L;

  /**
   * Retrieves a requested amount of {@link Tuple}s from the Castor Service using a pre-defined
   * request id.
   *
   * @param requestId Unique id to identify related requests across multiple castor services.
   * @param tupleType The tuple type to be downloaded
   * @param count Requested number of {@link Tuple}s.
   * @return A {@link TupleList} containing the requested tuples.
   * @throws CastorClientException if composing the request tuples URI failed
   * @throws CastorClientException if download the tuples from the service failed
   */
  List<TupleList> downloadTupleShares(UUID requestId, TupleType tupleType, long count);

  /**
   * Retrieves latest telemetry data with an interval preconfigured in castor.
   *
   * @return Telemetry data for each tuple type
   * @throws CastorClientException if composing the request telemetry URI failed
   * @throws CastorClientException if retrieving the telemetry metrics failed
   */
  TelemetryData getTelemetryData();

  /**
   * Retrieves latest telemetry data with a custom interval.
   *
   * @param interval Time period in seconds.
   * @return Telemetry data for each tuple type
   * @throws CastorClientException if composing the request telemetry URI failed
   * @throws CastorClientException if retrieving the telemetry metrics failed
   */
  TelemetryData getTelemetryData(long interval);

  /**
   * Uploads a {@link TupleChunk} to castor using a gRPC connection and wait until for upload to
   * complete or the {@link CastorIntraVcpClient#DEFAULT_CONNECTION_TIMEOUT} to be elapsed.
   *
   * @param tupleChunk The {@link TupleChunk} to upload
   * @return <CODE>true</CODE> when upload was successful or <CODE>false</CODE> if not
   * @throws IllegalArgumentException if number of tuples in the given chunk exceed the maximum
   *     number of allowed tuples per chunk
   * @throws CastorClientException if the gRPC connection has not been established or error happened
   *     in processing request
   */
  boolean uploadTupleChunk(TupleChunk tupleChunk) throws CastorClientException;

  /**
   * Uploads a {@link TupleChunk} to castor using a gRPC connection and wait until for upload to
   * complete or a given timeout to be elapsed.
   *
   * @param tupleChunk The {@link TupleChunk} to upload
   * @param timeout the maximum time in milliseconds to wait for the {@link TupleChunk }to be
   *     uploaded
   * @return <CODE>true</CODE> when upload was successful or <CODE>false</CODE> if not
   * @throws IllegalArgumentException if number of tuples in the given chunk exceed the maximum
   *     number of allowed tuples per chunk
   * @throws CastorClientException if the gRPC connection has not been established or error happened
   *     in processing request
   */
  boolean uploadTupleChunk(TupleChunk tupleChunk, long timeout) throws CastorClientException;

  /**
   * Activates a {@link TupleChunk} by setting its linked {@link ActivationStatus} to {@link
   * ActivationStatus#UNLOCKED}.
   *
   * @param tupleChunkId Unique identifier of the {@link TupleChunk} that should be activated.
   * @throws CastorClientException if the communication with the CastorService failed.
   */
  void activateTupleChunk(UUID tupleChunkId);
}
