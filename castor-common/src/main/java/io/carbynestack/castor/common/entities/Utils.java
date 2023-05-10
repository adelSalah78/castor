/*
 * Copyright (c) 2023 - for information on the respective copyright owner
 * see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package io.carbynestack.castor.common.entities;

import com.google.protobuf.ByteString;
import io.carbynestack.castor.common.exceptions.CastorServiceException;
import io.carbynestack.castor.common.grpc.*;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * a set of utility methods for converting between various types of objects and their corresponding
 * Protobuf representations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

  /**
   * Converts a gRPC reservation message to a Reservation object.
   *
   * @param reservation the gRPC reservation message to convert
   * @return the converted Reservation object
   */
  public static Reservation convertFromProtoReservation(GrpcReservation reservation) {
    List<ReservationElement> reservationElementList = new ArrayList<>();
    for (int i = 0; i < reservation.getReservationsCount(); i++) {
      reservationElementList.add(
          convertFromProtoReservationElement(reservation.getReservations(i)));
    }
    return new Reservation(
        reservation.getReservationId(),
        TupleType.valueOf(reservation.getTupleType()),
        reservationElementList);
  }

  /**
   * Converts a {@link Reservation} object to its corresponding gRPC {@link GrpcReservation} object.
   *
   * @param reservation the {@link Reservation} object to be converted
   * @return the corresponding gRPC {@link GrpcReservation} object
   */
  public static GrpcReservation convertToProtoReservation(Reservation reservation) {
    List<GrpcReservationElement> reservationElementList = new ArrayList<>();
    reservation
        .getReservations()
        .forEach(reserve -> reservationElementList.add(convertToProtoReservationElement(reserve)));
    return GrpcReservation.newBuilder()
        .setTupleType(reservation.getTupleType().name())
        .setReservationId(reservation.getReservationId())
        .setActivationStatus(GrpcActivationStatus.valueOf(reservation.getStatus().name()))
        .addAllReservations(reservationElementList)
        .build();
  }

  /** Convert to protobuf Share from entity Share */
  private static GrpcShare convertToProtoShare(Share share) {
    return GrpcShare.newBuilder()
        .setMac(ByteString.copyFrom(share.getMac()))
        .setValue(ByteString.copyFrom(share.getValue()))
        .build();
  }

  /** Convert to protobuf ResponsePayload from entity Tuple */
  private static GrpcResponsePayloadData convertToProtoResponsePayload(Tuple tuple) {
    List<GrpcShare> grpcShares = new ArrayList<>();
    for (int i = 0; i < tuple.getShares().length; i++) {
      grpcShares.add(convertToProtoShare(tuple.getShares()[i]));
    }
    return GrpcResponsePayloadData.newBuilder().addAllShares(grpcShares).build();
  }

  /**
   * Creates a gRPC tuples list response message from a TupleList object.
   *
   * @param tupleList the TupleList object to convert to a gRPC message
   * @return a GrpcTuplesListResponse object containing the gRPC message
   */
  public static GrpcTuplesListResponse createProtoTuplesListResponse(TupleList tupleList) {
    List<GrpcResponsePayloadData> data = new ArrayList<>();
    for (int i = 0; i < tupleList.size(); i++) {
      data.add(convertToProtoResponsePayload((Tuple) tupleList.get(i)));
    }
    return GrpcTuplesListResponse.newBuilder()
        .setTupleClass(tupleList.getClass().getName())
        .setTuplesField(tupleList.getField().getName())
        .addAllGrpcTuples(data)
        .build();
  }
  /**
   * Creates a new {@link TupleList} instance from a {@link GrpcTuplesListResponse} protobuf message
   * containing tuples in the specified tuple type and returns it.
   *
   * @param tupleList the protobuf message containing the tuples
   * @param tupleTypeName the name of the tuple type to create the {@link TupleList} instance from
   * @return a new {@link TupleList} instance containing the tuples in the protobuf message
   * @throws CastorServiceException if there is an error parsing the tuples
   */
  public static TupleList createFromProtoTuplesListResponse(
      GrpcTuplesListResponse tupleList, String tupleTypeName) {
    TupleType tupleType = TupleType.valueOf(tupleTypeName);
    int numberOfByte = tupleList.getGrpcTuplesCount() * tupleType.getTupleSize();
    ByteBuffer bb = ByteBuffer.allocate(numberOfByte);
    for (GrpcResponsePayloadData tuple : tupleList.getGrpcTuplesList()) {
      for (GrpcShare s : tuple.getSharesList()) {
        bb.put(s.getValue().toByteArray());
        bb.put(s.getMac().toByteArray());
      }
    }
    try {
      return TupleList.fromStream(
          tupleType.getTupleCls(),
          tupleType.getField(),
          new ByteArrayInputStream(bb.array()),
          numberOfByte);
    } catch (IOException e) {
      throw new CastorServiceException("Error parsing the tuples", e);
    }
  }

  /**
   * Converts a gRPC reservation element to a {@link ReservationElement} object.
   *
   * @param reservationElement the gRPC reservation element to convert
   * @return a {@link ReservationElement} object converted from the gRPC reservation element
   */
  public static ReservationElement convertFromProtoReservationElement(
      GrpcReservationElement reservationElement) {
    return new ReservationElement(
        UUID.fromString(reservationElement.getTupleChunkId()),
        reservationElement.getReservedTuples(),
        reservationElement.getStartIndex());
  }

  /**
   * Converts a {@link ReservationElement} object to its gRPC equivalent {@link
   * GrpcReservationElement} object.
   *
   * @param reservationElement The {@link ReservationElement} object to convert.
   * @return The corresponding {@link GrpcReservationElement} object.
   */
  public static GrpcReservationElement convertToProtoReservationElement(
      ReservationElement reservationElement) {
    return GrpcReservationElement.newBuilder()
        .setReservedTuples(reservationElement.getReservedTuples())
        .setStartIndex(reservationElement.getStartIndex())
        .setTupleChunkId(reservationElement.getTupleChunkId().toString())
        .build();
  }

  /**
   * Converts an {@link UploadTupleChunkResponse} object to a {@link GrpcUploadTupleChunkResponse}
   * protobuf message.
   *
   * @param uploadTupleChunkResponse the {@link UploadTupleChunkResponse} object to convert
   * @return the converted {@link GrpcUploadTupleChunkResponse} protobuf message
   */
  public static GrpcUploadTupleChunkResponse convertToUploadTupleChunkResponseProto(
      UploadTupleChunkResponse uploadTupleChunkResponse) {
    return GrpcUploadTupleChunkResponse.newBuilder()
        .setErrorMessage(
            uploadTupleChunkResponse.getErrorMsg() == null
                ? ""
                : uploadTupleChunkResponse.getErrorMsg())
        .setUuid(uploadTupleChunkResponse.getChunkId().toString())
        .setIsSuccess(uploadTupleChunkResponse.isSuccess())
        .build();
  }

  /**
   * The convertToProtoTupleMetric() and convertFromProtoTupleMetric() methods convert between
   * TupleMetric objects and their Protobuf representation GrpcTupleMetric.
   */
  private static GrpcTupleMetric convertToProtoTupleMetric(TupleMetric metric) {
    return GrpcTupleMetric.newBuilder()
        .setTupleType(metric.getType().name())
        .setAvailable(metric.getAvailable())
        .setConsumptionRate(metric.getConsumptionRate())
        .build();
  }

  /**
   * The convertToProtoTupleMetric() and convertFromProtoTupleMetric() methods convert between
   * TupleMetric objects and their Protobuf representation GrpcTupleMetric.
   */
  private static TupleMetric convertFromProtoTupleMetric(GrpcTupleMetric metric) {
    return TupleMetric.of(
        metric.getAvailable(),
        metric.getConsumptionRate(),
        TupleType.valueOf(metric.getTupleType()));
  }

  /**
   * Converts a {@link TelemetryData} object to its corresponding Protocol Buffer format.
   *
   * @param telemetryData the telemetry data to be converted
   * @return the Protocol Buffer representation of the telemetry data
   */
  public static GrpcTelemetryDataResponse convertToProtoTelemetryData(TelemetryData telemetryData) {
    List<GrpcTupleMetric> metrics = new ArrayList<>();
    if (telemetryData != null && telemetryData.getMetrics() != null) {
      for (int i = 0; i < telemetryData.getMetrics().size(); i++) {
        metrics.add(convertToProtoTupleMetric(telemetryData.getMetrics().get(i)));
      }
    }
    return GrpcTelemetryDataResponse.newBuilder()
        .setInterval(telemetryData == null ? 0L : telemetryData.getInterval())
        .addAllMetrics(metrics)
        .build();
  }

  /**
   * Converts a gRPC TelemetryDataResponse to a TelemetryData object.
   *
   * @param response the gRPC TelemetryDataResponse to be converted
   * @return the converted TelemetryData object
   */
  public static TelemetryData convertFromProtoTelemetryData(GrpcTelemetryDataResponse response) {
    List<TupleMetric> metrics = new ArrayList<>();
    for (GrpcTupleMetric tupleMetric : response.getMetricsList()) {
      metrics.add(convertFromProtoTupleMetric(tupleMetric));
    }
    return new TelemetryData(metrics, response.getInterval());
  }

  public static Channel createGrpcChannel(String castorServiceUri) {
    String[] addressAndPort = castorServiceUri.split(":");
    String grpcClientAddress = addressAndPort[0];
    String grpcClientPort = addressAndPort[1];
    return ManagedChannelBuilder.forAddress(grpcClientAddress, Integer.parseInt(grpcClientPort))
        .usePlaintext()
        .build();
  }
}
