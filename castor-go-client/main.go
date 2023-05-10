package main

import (
	"fmt"

	"github.com/carbynestack/castor/download/castor"
	l "github.com/carbynestack/castor/logger"
	"github.com/google/uuid"
)

func download() {
	logger, err := l.NewDevelopmentLogger()
	if err != nil {
		panic(err)
	}
	castorClient := castor.GrpcClient{
		GRPC_URL: "localhost:10100",
	}

	strUUID := "bb7a262e-b909-11ed-afa1-0242ac120002"
	uuidObj, err := uuid.Parse(strUUID)
	if err != nil {
		fmt.Println("Error parsing UUID string:", err)
		return
	}

	// arr, err := uuid.FromBytes([]byte("bb7a262e-b909-11ed-afa1-0242ac120002"))
	tuples, err := castorClient.GetTuples(1, castor.BitGfp, uuidObj)
	if err != nil {
		panic(err)
	}
	logger.Info(tuples)
}

func activateTupleChunk() {
	logger, err := l.NewDevelopmentLogger()
	if err != nil {
		panic(err)
	}
	castorClient := castor.GrpcClient{
		GRPC_URL: "localhost:10100",
	}
	castorClient.ActivateTupleChunk("bb7a262e-b909-11ed-afa1-0242ac120002")
	logger.Info("Chunk activated")
}

func getTelemetryData() {
	logger, err := l.NewDevelopmentLogger()
	if err != nil {
		panic(err)
	}

	castorClient := castor.GrpcClient{
		GRPC_URL: "localhost:10100",
	}
	response := castorClient.GetTelemetryData(-1)

	logger.Info(response)
}

func UploadTupleChunk() {
	logger, err := l.NewDevelopmentLogger()
	if err != nil {
		panic(err)
	}

	castorClient := castor.GrpcClient{
		GRPC_URL: "localhost:10100",
	}

	response := castorClient.UploadTupleChunk("bb7a262e-b909-11ed-afa1-0242ac120002", "BIT_GFP", "C:/Bosch/gRPC-CLI/cli/crypto-material/2-p-128/Bits-p-P0")
	logger.Info(response)
}

func main() {
	// download()
	// activateTupleChunk()
	getTelemetryData()
	// UploadTupleChunk()
}
