//
// Copyright (c) 2022 - for information on the respective copyright owner
// see the NOTICE file and/or the repository https://github.com/carbynestack/ephemeral.
//
// SPDX-License-Identifier: Apache-2.0
//

// Package castor implements a client to interact with _Carbyne Stack Castor_ services
package castor

import (
	"context"
	"encoding/binary"
	"errors"
	"fmt"
	"io/ioutil"
	"regexp"

	pb "github.com/carbynestack/castor/common_grpc"
	"github.com/google/uuid"
	"google.golang.org/grpc"
)

var GrpcDialer = func(target string, opts ...grpc.DialOption) (*grpc.ClientConn, error) {
	if opts == nil {
		return grpc.Dial(target, grpc.WithInsecure())
	}
	return grpc.Dial(target, opts[0])
}

var GetClient = func(conn *grpc.ClientConn) pb.IntraServiceClient {
	return pb.NewIntraServiceClient(conn)
}

var GrpcConnCloser = func(conn *grpc.ClientConn) error {
	return conn.Close()
}

var GrpcFileReader = func(filePath string) ([]byte, error) {
	return ioutil.ReadFile(filePath)
}

var GetSpdzHeaderLengthWrapper = func(data []byte) int64 {
	return GetSpdzHeaderLength(data)
}

var GrpcClientConn = func(clientInfo GrpcClient) (*grpc.ClientConn, error) {

	// Define the regex pattern
	pattern := `^(\w+\.)*\w+:\d+$`

	// Compile the regex pattern
	regex, err := regexp.Compile(pattern)
	if err != nil {
		fmt.Printf("Error compiling regex: %s", err.Error())
		return nil, err
	}

	if regex.MatchString(clientInfo.GRPC_URL) {
		return GrpcDialer(clientInfo.GRPC_URL, grpc.WithInsecure())
	} else {
		return nil, errors.New("Invalid URL!")
	}
}

// AbstractClient is an interface for castor tuple client.
type AbstractClient interface {
	GetTuples(tupleCount int32, tupleType TupleType, requestID uuid.UUID) (*TupleList, error)
}

// GrpcClient is a client for the Castor tuple storage service
type GrpcClient struct {
	GRPC_URL string
}

const tupleURI = "/intra-vcp/tuples"
const tupleTypeParam = "tupletype"
const countParam = "count"
const reservationIDParam = "reservationId"
const defaultTelemetryInterval = 60

func (c GrpcClient) ActivateTupleChunk(uuid string) (*pb.GrpcEmpty, error) {
	conn, client, ctx, error := InitServiceClientAndContext(c)
	defer GrpcConnCloser(conn)
	handleErrorIfExists(error)

	response, error := client.ActivateFragmentsForTupleChunk(ctx, &pb.GrpcTupleChunkRequest{ChunkId: uuid})
	handleErrorIfExists(error)
	fmt.Printf("chunk with id %s is activated", uuid)
	return response, nil
}

func (c GrpcClient) GetTelemetryData(interval int64) *pb.GrpcTelemetryDataResponse {
	conn, client, ctx, error := InitServiceClientAndContext(c)
	defer GrpcConnCloser(conn)
	handleErrorIfExists(error)

	var requestInterval int64

	if interval == -1 {
		requestInterval = defaultTelemetryInterval
	} else {
		requestInterval = interval
	}

	response, error := client.GetTelemetryData(ctx, &pb.GrpcTelemetryDataRequest{
		RequestInterval: requestInterval,
	})
	handleErrorIfExists(error)
	return response
}

func GetSpdzHeaderLength(data []byte) int64 {
	const skipBytes = 8
	headerLengthBytes := data[:skipBytes]
	headerLength := binary.LittleEndian.Uint64(headerLengthBytes)
	return int64(headerLength) + skipBytes
}

func (c GrpcClient) UploadTupleChunk(chunkId string, tupleType string, tuplesFile string) *pb.GrpcUploadTupleChunkResponse {
	conn, client, ctx, error := InitServiceClientAndContext(c)
	defer GrpcConnCloser(conn)
	handleErrorIfExists(error)

	data, err := GrpcFileReader(tuplesFile)

	handleErrorIfExists(err)

	data = data[GetSpdzHeaderLengthWrapper(data):]

	response, error := client.UploadTupleChunk(ctx, &pb.GrpcUploadTupleChunkRequest{
		ChunkId:   chunkId,
		TupleType: tupleType,
		Tuples:    data,
	})
	handleErrorIfExists(error)
	return response
}

func InitServiceClientAndContext(c GrpcClient) (*grpc.ClientConn, pb.IntraServiceClient, context.Context, error) {
	conn, err := GrpcClientConn(c)
	if err != nil {
		fmt.Println(err)
		return nil, nil, nil, err
	}
	ctx := context.Background()
	return conn, GetClient(conn), ctx, nil
}

func handleErrorIfExists(err error) {
	if err != nil {
		fmt.Println(err)
		panic(err)
	}
}

// GetTuples retrieves a list of tuples matching the given criteria from Castor
func (c GrpcClient) GetTuples(count int32, tt TupleType, requestID uuid.UUID) (*TupleList, error) {

	conn, client, ctx, error := InitServiceClientAndContext(c)
	defer GrpcConnCloser(conn)
	handleErrorIfExists(error)

	tuples, error := client.GetTupleList(ctx, &pb.GrpcTuplesListRequest{
		Type:      tt.Name,
		Count:     int64(count),
		RequestId: requestID.String(),
	})
	handleErrorIfExists(error)

	response := tuples.GetGrpcTuples()

	var tuplesResult []Tuple

	for _, entry := range response {
		var shares []Share
		for _, share := range entry.GetShares() {
			shares = append(shares, Share{
				Value: string(share.Value),
				Mac:   string(share.Mac),
			})
		}
		tuplesResult = append(tuplesResult, Tuple{
			Shares: shares,
		})
	}

	return &TupleList{
		Tuples: tuplesResult,
	}, nil
}
