/*
 * Copyright (c) 2021-2023 - for information on the respective copyright owner
 * see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package io.carbynestack.castor.client.download;

import static io.carbynestack.castor.client.download.ClientBuilderSupport.ADDRESSES_MUST_NOT_BE_EMPTY_EXCEPTION_MSG;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import io.carbynestack.castor.common.entities.ActivationStatus;
import io.carbynestack.castor.common.entities.Reservation;
import io.carbynestack.castor.common.entities.Utils;
import io.carbynestack.castor.common.exceptions.CastorClientException;
import io.carbynestack.castor.common.grpc.*;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCastorInterVcpClientTest {

  private final List<String> serviceAddresses =
      Arrays.asList("castor.carbynestack.io:8080", "castor.carbynestack.io:8081");

  MockedStatic<DefaultCastorInterVcpClient> clientUtilities;
  MockedStatic<Utils> protoUtils;

  InterServiceGrpc.InterServiceBlockingStub stub1;
  InterServiceGrpc.InterServiceBlockingStub stub2;

  CastorInterVcpClient castorInterVcpClient;

  DefaultCastorInterVcpClient.Builder noSSLbuilder;

  public DefaultCastorInterVcpClientTest() {}

  void init() {
    noSSLbuilder = DefaultCastorInterVcpClient.builder(serviceAddresses);
    noSSLbuilder = noSSLbuilder.withoutSslCertificateValidation();
    clientUtilities = Mockito.mockStatic(DefaultCastorInterVcpClient.class);
    protoUtils = Mockito.mockStatic(Utils.class);
    stub1 = mock(InterServiceGrpc.InterServiceBlockingStub.class);
    stub2 = mock(InterServiceGrpc.InterServiceBlockingStub.class);
    castorInterVcpClient = new DefaultCastorInterVcpClient(noSSLbuilder);
  }

  void close() {
    clientUtilities.close();
    protoUtils.close();
  }

  @Test
  public void givenServiceAddressIsNull_whenGetBuilderInstance_thenThrowIllegalArgumentException() {
    NullPointerException actualNpe =
        assertThrows(NullPointerException.class, () -> DefaultCastorInterVcpClient.builder(null));
    assertEquals("serviceAddresses is marked non-null but is null", actualNpe.getMessage());
  }

  @Test
  public void
      givenSingleServiceAddressIsNull_whenGetBuilderInstance_thenThrowIllegalArgumentException() {
    List<String> listWithNullAddress = singletonList(null);
    IllegalArgumentException actualIae =
        assertThrows(
            IllegalArgumentException.class,
            () -> DefaultCastorInterVcpClient.builder(listWithNullAddress));
    assertEquals(ADDRESSES_MUST_NOT_BE_EMPTY_EXCEPTION_MSG, actualIae.getMessage());
  }

  @Test
  public void
      givenSingleServiceAddressIsEmpty_whenGetBuilderInstance_thenThrowIllegalArgumentException() {
    List<String> listWithEmptyAddress = singletonList("");
    IllegalArgumentException actualIae =
        assertThrows(
            IllegalArgumentException.class,
            () -> DefaultCastorInterVcpClient.builder(listWithEmptyAddress));
    assertEquals(ADDRESSES_MUST_NOT_BE_EMPTY_EXCEPTION_MSG, actualIae.getMessage());
  }

  @Test
  public void givenSingleEndpointReturnsFailure_whenShareReservation_thenReturnFalse() {
    init();

    GrpcEmpty successResponse = GrpcEmpty.newBuilder().build();
    GrpcEmpty failureResponse = null;

    GrpcReservation grpcSharedReservation = mock(GrpcReservation.class);
    Reservation sharedReservation = mock(Reservation.class);

    protoUtils
        .when(() -> Utils.convertToProtoReservation(sharedReservation))
        .thenReturn(grpcSharedReservation);

    clientUtilities
        .when(() -> DefaultCastorInterVcpClient.createStub(serviceAddresses.get(0), noSSLbuilder))
        .thenReturn(stub1);
    clientUtilities
        .when(() -> DefaultCastorInterVcpClient.createStub(serviceAddresses.get(1), noSSLbuilder))
        .thenReturn(stub2);

    when(stub1.keepAndApplyReservation(grpcSharedReservation)).thenReturn(successResponse);
    when(stub2.keepAndApplyReservation(grpcSharedReservation)).thenReturn(failureResponse);

    castorInterVcpClient = new DefaultCastorInterVcpClient(noSSLbuilder);

    assertFalse(castorInterVcpClient.shareReservation(sharedReservation));
    close();
  }

  @Test
  public void
      givenSingleEndpointThrowsException_whenShareReservation_thenThrowCastorClientException() {
    init();

    GrpcReservation grpcSharedReservation = mock(GrpcReservation.class);
    Reservation sharedReservation = mock(Reservation.class);

    protoUtils
        .when(() -> Utils.convertToProtoReservation(sharedReservation))
        .thenReturn(grpcSharedReservation);

    clientUtilities
        .when(() -> DefaultCastorInterVcpClient.createStub(serviceAddresses.get(0), noSSLbuilder))
        .thenReturn(stub1);
    clientUtilities
        .when(() -> DefaultCastorInterVcpClient.createStub(serviceAddresses.get(1), noSSLbuilder))
        .thenReturn(stub2);

    assertThrows(
        CastorClientException.class,
        () -> castorInterVcpClient.shareReservation(sharedReservation));

    close();
  }

  @Test
  public void
      givenSingleEndpointThrowsException_whenUpdateReservation_thenThrowCastorClientException() {
    init();

    clientUtilities
        .when(() -> DefaultCastorInterVcpClient.createStub(serviceAddresses.get(0), noSSLbuilder))
        .thenReturn(stub1);
    clientUtilities
        .when(() -> DefaultCastorInterVcpClient.createStub(serviceAddresses.get(1), noSSLbuilder))
        .thenReturn(stub2);

    assertThrows(
        CastorClientException.class, () -> castorInterVcpClient.updateReservationStatus("", null));

    close();
  }

  @Test
  public void givenEndpointsNotThrowsException_whenUpdateReservation_thenNoExceptionThrown() {
    init();

    ActivationStatus status = ActivationStatus.LOCKED;

    clientUtilities
        .when(() -> DefaultCastorInterVcpClient.createStub(serviceAddresses.get(0), noSSLbuilder))
        .thenReturn(stub1);
    clientUtilities
        .when(() -> DefaultCastorInterVcpClient.createStub(serviceAddresses.get(1), noSSLbuilder))
        .thenReturn(stub2);

    castorInterVcpClient = new DefaultCastorInterVcpClient(noSSLbuilder);

    Assertions.assertThatCode(() -> castorInterVcpClient.updateReservationStatus("", status))
        .doesNotThrowAnyException();

    close();
  }
}
