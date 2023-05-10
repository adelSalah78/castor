/*
 * Copyright (c) 2023 - for information on the respective copyright owner
 * see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package io.carbynestack.castor.common.entities;

import io.carbynestack.castor.common.grpc.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UtilsTest extends TestCase {
  @Test
  public void givenProtoReservation_whenCreatingReservation_thenMakeSure2WayConversionPasses() {
    List<GrpcReservationElement> grpcReservationElements = new ArrayList<>();
    grpcReservationElements.add(
        GrpcReservationElement.newBuilder()
            .setReservedTuples(8)
            .setStartIndex(0)
            .setTupleChunkId(UUID.randomUUID().toString())
            .build());
    GrpcReservation grpcReservationBeforeConversion =
        GrpcReservation.newBuilder()
            .setReservationId("1")
            .setTupleType(TupleType.BIT_GFP.name())
            .addAllReservations(grpcReservationElements)
            .setActivationStatus(GrpcActivationStatus.LOCKED)
            .build();
    Reservation reservation = Utils.convertFromProtoReservation(grpcReservationBeforeConversion);

    GrpcReservation grpcReservationAfterConversionBack =
        Utils.convertToProtoReservation(reservation);
    assertEquals(grpcReservationBeforeConversion, grpcReservationAfterConversionBack);
  }

  @Test
  public void
      givenProtoReservationElement_whenCreatingReservationElement_thenMakeSure2WayConversionPasses() {
    GrpcReservationElement grpcReservationElement =
        GrpcReservationElement.newBuilder()
            .setReservedTuples(2)
            .setStartIndex(1)
            .setTupleChunkId(UUID.randomUUID().toString())
            .build();
    ReservationElement reservationElementBeforeConversion =
        Utils.convertFromProtoReservationElement(grpcReservationElement);
    GrpcReservationElement grpcReservationElementAfterConversionBack =
        Utils.convertToProtoReservationElement(reservationElementBeforeConversion);
    assertEquals(grpcReservationElement, grpcReservationElementAfterConversionBack);
  }

  @Test
  public void givenUploadTupleChunkResponseSuccess_whenConversionToProto_thenMakeSureTheyEqual() {
    UploadTupleChunkResponse uploadTupleChunkResponse =
        UploadTupleChunkResponse.success(UUID.randomUUID());
    GrpcUploadTupleChunkResponse grpcUploadTupleChunkResponse =
        Utils.convertToUploadTupleChunkResponseProto(uploadTupleChunkResponse);

    assertEquals(
        uploadTupleChunkResponse.getChunkId().toString(), grpcUploadTupleChunkResponse.getUuid());
    assertEquals("", grpcUploadTupleChunkResponse.getErrorMessage());
    assertTrue(grpcUploadTupleChunkResponse.getIsSuccess());
  }

  @Test
  public void
      givenUploadTupleChunkResponseProtoFailure_whenConversionToProto_thenMakeSureTheyEqual() {
    UploadTupleChunkResponse uploadTupleChunkResponse =
        UploadTupleChunkResponse.failure(UUID.randomUUID(), "fail");
    GrpcUploadTupleChunkResponse grpcUploadTupleChunkResponse =
        Utils.convertToUploadTupleChunkResponseProto(uploadTupleChunkResponse);

    assertEquals(
        uploadTupleChunkResponse.getChunkId().toString(), grpcUploadTupleChunkResponse.getUuid());
    assertEquals(
        uploadTupleChunkResponse.getErrorMsg(), grpcUploadTupleChunkResponse.getErrorMessage());
    assertFalse(grpcUploadTupleChunkResponse.getIsSuccess());
  }

  @Test
  public void givenProtoTelemetryData_whenCreatingTelemetryData_MakeSure2WayConversionPasses() {
    GrpcTupleMetric grpcTupleMetric =
        GrpcTupleMetric.newBuilder()
            .setTupleType(TupleType.BIT_GFP.name())
            .setAvailable(11L)
            .setConsumptionRate(12L)
            .build();
    List<GrpcTupleMetric> grpcTupleMetrics = new ArrayList<>();
    grpcTupleMetrics.add(grpcTupleMetric);
    GrpcTelemetryDataResponse grpcTelemetryDataResponseBeforeConversion =
        GrpcTelemetryDataResponse.newBuilder()
            .setInterval(10L)
            .addAllMetrics(grpcTupleMetrics)
            .build();
    TelemetryData telemetryData =
        Utils.convertFromProtoTelemetryData(grpcTelemetryDataResponseBeforeConversion);
    GrpcTelemetryDataResponse grpcTelemetryDataResponseAfterConversionBack =
        Utils.convertToProtoTelemetryData(telemetryData);
    assertEquals(
        grpcTelemetryDataResponseBeforeConversion, grpcTelemetryDataResponseAfterConversionBack);
  }

  @Test
  public void givenTupleList_whenCreatingProtoTupleList_thenMakeSure2WayConversionPasses() {
    List<MultiplicationTriple<Field.Gfp>> triples =
        IntStream.range(0, 5)
            .mapToObj(
                i ->
                    new MultiplicationTriple<>(
                        Field.GFP,
                        IntStream.range(0, 3)
                            .mapToObj(
                                k ->
                                    Share.of(
                                        Arrays.copyOf(
                                            ("value_" + i + "_" + k).getBytes(),
                                            TupleType.MULTIPLICATION_TRIPLE_GFP
                                                .getField()
                                                .getElementSize()),
                                        Arrays.copyOf(
                                            ("mac_" + i + "_" + k).getBytes(),
                                            TupleType.MULTIPLICATION_TRIPLE_GFP
                                                .getField()
                                                .getElementSize())))
                            .toArray(Share[]::new)))
            .collect(Collectors.toList());
    TupleList tupleList = new TupleList(MultiplicationTriple.class, Field.GFP, triples);
    GrpcTuplesListResponse protoTuplesListResponse = Utils.createProtoTuplesListResponse(tupleList);
    TupleList fromProtoTuplesListResponse =
        Utils.createFromProtoTuplesListResponse(
            protoTuplesListResponse, TupleType.MULTIPLICATION_TRIPLE_GFP.name());
    assertEquals(tupleList, fromProtoTuplesListResponse);
  }
}
