# Castor Client - A Java Library to fetch Tuples from and Upload tuples to Castor

This Java library provides clients to communicate with a
[Castor](../castor-service) service over its gRPC interface. It comes with three
clients: one for downloading tuples, one for retrieving telemetry information
and one for uploading tuples. Details about the individual clients are described
in the following.

## Provided Clients

### DownloadClient

The `DownloadClient` is used to communicate with a [Castor](../castor-service)
service in order to download tuples used for e.g. secret-sharing or secure
computations.

The interface is described in
`io.carbynestack.castor.client.download.CastorIntraVcpClient` and the default
implementation is
`io.carbynestack.castor.client.download.DefaultCastorIntraVcpClient`.

> :bulb: **NOTE**: _Castor_ operates in a so-called Master/Slave setting (see
> also the [Castor Service description](../castor-service/README.md)). It is
> vital for correct operation that shares belonging to the same tuple are used
> consistently by all providers within a Virtual Cloud. Therefore, users should
> ensure that tuple requests are either sent to all services in parallel, or in
> the case of sequential processing, to the master service first.

#### Usage Example For Download Tuples

The following example shows a class that is instantiated with a single _Castor
Service_ endpoint URI. `getInputMasks` demonstrates how to download tuples of a
given tuple type from all providers in a _Carbyne Stack_ Virtual Cloud.
`getTupleTypesBelowThreshold` shows how to receive a list of all tuple types for
which the number of available tuples is below a given threshold.

```java
package io.carbynestack.castor.client.download;

import io.carbynestack.castor.common.entities.TupleList;
import io.carbynestack.castor.common.entities.TupleMetric;
import io.carbynestack.castor.common.entities.TupleType;
import io.carbynestack.castor.client.download.CastorIntraVcpClient;
import io.carbynestack.castor.client.download.DefaultCastorIntraVcpClient;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Example {

    private final CastorIntraVcpClient castorIntraVcpClient;

    public Example(String serviceAddress) {
        this.castorIntraVcpClient = DefaultCastorIntraVcpClient.builder(serviceAddress).build();
    }

    public TupleList getInputMasks(UUID requestId, int count) {
        return castorIntraVcpClient.downloadTupleShares(requestId,
                TupleType.INPUT_MASK_GFP,
                count);
    }

    public List<TupleType> getTupleTypesBelowThreshold(int threshold) {
        return castorIntraVcpClient.getTelemetryData().getMetrics().stream()
                .filter(tupleMetric -> tupleMetric.getAvailable() < threshold)
                .map(TupleMetric::getType)
                .collect(Collectors.toList());
    }

}
```

## Getting Started

Castor uses Maven for dependency and build management. You can add
_castor-java-client_ to your project by declaring the following maven dependency
in your `pom.xml`:

### Maven

```xml
<dependency>
    <groupId>io.carbynestack</groupId>
    <artifactId>castor-java-client</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```

### Building from Source

The _castor-java-client_ library can be build and installed in the local maven
repository using:

```bash
../mvnw install
```

### UploadClient

This client can be used to upload chunks of tuple shares to a _Castor_ service
and to activate them for consumption. Uploading will not activate a chunk
automatically. Therefore, it is required to explicitly activate the chunk on all
parties only if the upload request was confirmed by all parties.

The interface is described in `CastorUploadClient` and the default
implementation is
`io.carbynestack.castor.client.upload,DefaultCastorUploadClient`.

#### Usage Example For Upload Tuples

The following example shows a class that is instantiated with the address of a
_Castor Service_ endpoint. `uploadTuples` shows how to read tuple shares from a
file and upload the data as a tuple chunk to a provider within a _Carbyne Stack_
Virtual Cluster. On successful upload, the just uploaded tuple chunk will be
activated for consumption.

```java
import io.carbynestack.castor.common.entities.TupleChunk;
import io.carbynestack.castor.common.entities.TupleType;
import io.carbynestack.castor.common.exceptions.CastorClientException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.apache.commons.io.IOUtils.toByteArray;

public class Example {

    private static final long DEFAULT_CONNECTION_TIMEOUT_MILLIS = 10000;
    private CastorUploadClient castorUploadClient;

    public Example(String grpcAddress,String grpcPort) {
        this.castorUploadClient =
                DefaultCastorUploadClient.builder(grpcAddress,grpcPort).build();
    }
    
    public void uploadTuples(File tupleFile, TupleType tupleType)
            throws IOException {
        UUID chunkId = UUID.randomUUID();
        TupleChunk tupleChunk;
        try (FileInputStream fileInputStream = new FileInputStream(tupleFile)) {
            tupleChunk = TupleChunk.of(tupleType.getTupleCls(),
                    tupleType.getField(),
                    chunkId,
                    toByteArray(fileInputStream));
        }
        if (castorUploadClient.uploadTupleChunk(tupleChunk)) {
            castorUploadClient.activateTupleChunk(chunkId);
        }
    }

}
```
