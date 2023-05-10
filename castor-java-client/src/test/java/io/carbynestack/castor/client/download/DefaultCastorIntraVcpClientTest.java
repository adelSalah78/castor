/*
 * Copyright (c) 2021-2023 - for information on the respective copyright owner
 * see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package io.carbynestack.castor.client.download;

import static io.carbynestack.castor.client.download.ClientBuilderSupport.ADDRESSES_MUST_NOT_BE_EMPTY_EXCEPTION_MSG;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.carbynestack.castor.common.entities.*;
import io.carbynestack.castor.common.entities.TupleChunk;
import io.carbynestack.castor.common.exceptions.CastorClientException;
import io.carbynestack.castor.common.grpc.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCastorIntraVcpClientTest {

  private IntraServiceGrpc.IntraServiceBlockingStub stub;

  private CastorIntraVcpClient castorIntraVcpClient;

  MockedStatic<DefaultCastorInterVcpClient> clientUtilities;
  MockedStatic<Utils> protoUtils;

  final byte[] arr = {
    12, 67, 45, -2, 32, 4, 5, 6, 7, 8, 9, 56, 12, 45, 21, 45, 12, 67, 45, -2, 32, 4, 5, 6, 7, 8, 9,
    56, 12, 45, 21, 45
  };

  long DEFAULT_CONNECTION_TIMEOUT = 60000L;

  public DefaultCastorIntraVcpClientTest() {}

  void init() {
    clientUtilities = Mockito.mockStatic(DefaultCastorInterVcpClient.class);
    protoUtils = Mockito.mockStatic(Utils.class);

    stub = mock(IntraServiceGrpc.IntraServiceBlockingStub.class);
    castorIntraVcpClient = new DefaultCastorIntraVcpClient(stub);
  }

  void close() {
    clientUtilities.close();
    protoUtils.close();
  }

  @Test
  public void givenServiceAddressIsNull_whenGetBuilderInstance_thenThrowIllegalArgumentException() {
    IllegalArgumentException actualIae =
        assertThrows(
            IllegalArgumentException.class, () -> DefaultCastorIntraVcpClient.builder(null));
    assertEquals(ADDRESSES_MUST_NOT_BE_EMPTY_EXCEPTION_MSG, actualIae.getMessage());
  }

  @Test
  public void givenClientCallPasses_whenDownloadTupleShares_thenTupleListDownloaded() {
    init();
    protoUtils
        .when(() -> Utils.createFromProtoTuplesListResponse(any(), any()))
        .thenReturn(new TupleList(TupleType.class, TupleType.SQUARE_TUPLE_GF2N.getField()));
    List<TupleList> response =
        castorIntraVcpClient.downloadTupleShares(UUID.randomUUID(), TupleType.SQUARE_TUPLE_GF2N, 1);
    Assert.assertNotNull(response);
    close();
  }

  @Test
  public void givenClientCallFails_whenDownloadTupleShares_thenThrowCastorClientException() {
    init();
    Mockito.when(stub.getTupleList(any(GrpcTuplesListRequest.class)))
        .thenThrow(new RuntimeException());
    assertThrows(
        CastorClientException.class,
        () -> {
          castorIntraVcpClient.downloadTupleShares(
              UUID.randomUUID(), TupleType.SQUARE_TUPLE_GF2N, 1);
        });
    close();
  }

  @Test
  public void givenClientCallPasses_whenFetchingTelemetryData_thenRetrieveTelemetryData() {
    init();
    protoUtils
        .when(() -> Utils.convertFromProtoTelemetryData(any()))
        .thenReturn(new TelemetryData(new ArrayList<>(), 10L));
    TelemetryData response = castorIntraVcpClient.getTelemetryData();
    assertNotNull(response);
    close();
  }

  @Test
  public void givenClientCallFails_whenFetchingTelemetryData_thenThrowCastorClientException() {
    init();
    when(stub.getTelemetryData(any(GrpcTelemetryDataRequest.class)))
        .thenThrow(new RuntimeException());
    assertThrows(CastorClientException.class, () -> castorIntraVcpClient.getTelemetryData());
    close();
  }

  @Test
  public void givenClientCallPasses_whenActivateTupleChunk_thenDoNotThrowException() {
    init();
    when(stub.activateFragmentsForTupleChunk(any())).thenReturn(GrpcEmpty.newBuilder().build());
    castorIntraVcpClient.activateTupleChunk(UUID.randomUUID());
    close();
  }

  @Test
  public void givenClientCallFails_whenActivateTupleChunk_thenThrowCastorClientException() {
    init();
    when(stub.activateFragmentsForTupleChunk(any())).thenThrow(new RuntimeException());
    Assert.assertThrows(
        CastorClientException.class,
        () -> castorIntraVcpClient.activateTupleChunk(UUID.randomUUID()));
    close();
  }

  @Test
  public void givenCallingClientPasses_whenUploadTupleChunk_thenReturnTrue() {
    init();
    TupleType tupleType = TupleType.BIT_GFP;
    TupleChunk tupleChunk =
        TupleChunk.of(tupleType.getTupleCls(), tupleType.getField(), UUID.randomUUID(), arr);
    Mockito.doReturn(stub)
        .when(stub)
        .withDeadlineAfter(Mockito.anyLong(), Mockito.any(TimeUnit.class));
    boolean result = castorIntraVcpClient.uploadTupleChunk(tupleChunk, 1000L);
    Mockito.verify(stub).withDeadlineAfter(Mockito.eq(1000L), Mockito.eq(TimeUnit.MILLISECONDS));
    assertTrue(result);
    close();
  }

  @Test
  public void givenClientCallFails_whenUploadTupleChunk_thenReturnFalse() {
    init();
    TupleType tupleType = TupleType.BIT_GFP;
    TupleChunk tupleChunk =
        TupleChunk.of(tupleType.getTupleCls(), tupleType.getField(), UUID.randomUUID(), arr);
    Mockito.doThrow(new RuntimeException())
        .when(stub)
        .withDeadlineAfter(Mockito.anyLong(), Mockito.any(TimeUnit.class));
    assertFalse(castorIntraVcpClient.uploadTupleChunk(tupleChunk));
    close();
  }
}
