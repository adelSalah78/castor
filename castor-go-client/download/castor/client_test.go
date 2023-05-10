//
// Copyright (c) 2022 - for information on the respective copyright owner
// see the NOTICE file and/or the repository https://github.com/carbynestack/castor.
//
// SPDX-License-Identifier: Apache-2.0
//

package castor_test

import (
	"context"
	"errors"

	// . "github.com/carbynestack/ephemeral/pkg/utils"

	"github.com/google/uuid"
	"google.golang.org/grpc"

	pb "github.com/carbynestack/castor/common_grpc"
	. "github.com/onsi/ginkgo"
	. "github.com/onsi/gomega"

	"github.com/carbynestack/castor/download/castor"
	. "github.com/carbynestack/castor/download/castor"
)

type GrpcClientTest struct {
	GRPC_URL string
}

type GrpcClientErrorTest struct {
	GRPC_URL string
}

type GrpcClientErrorJsonResponseBodyTest struct {
	GRPC_URL string
}

func (ct GrpcClientTest) GetTupleList(ctx context.Context, in *pb.GrpcTuplesListRequest, opts ...grpc.CallOption) (*pb.GrpcTuplesListResponse, error) {
	var data []*pb.GrpcResponsePayloadData
	data = append(data, &pb.GrpcResponsePayloadData{})
	data[0].Shares = []*pb.GrpcShare{&pb.GrpcShare{Value: []byte("val"), Mac: []byte("mac")}}
	return &pb.GrpcTuplesListResponse{
		GrpcTuples: data,
	}, nil
}

func (ct GrpcClientTest) ActivateFragmentsForTupleChunk(ctx context.Context, in *pb.GrpcTupleChunkRequest, opts ...grpc.CallOption) (*pb.GrpcEmpty, error) {
	return &pb.GrpcEmpty{}, nil
}

func (ct GrpcClientTest) GetTelemetryData(ctx context.Context, in *pb.GrpcTelemetryDataRequest, opts ...grpc.CallOption) (*pb.GrpcTelemetryDataResponse, error) {
	return &pb.GrpcTelemetryDataResponse{}, nil
}

func (ct GrpcClientTest) UploadTupleChunk(ctx context.Context, in *pb.GrpcUploadTupleChunkRequest, opts ...grpc.CallOption) (*pb.GrpcUploadTupleChunkResponse, error) {
	return &pb.GrpcUploadTupleChunkResponse{}, nil
}

func (ct GrpcClientErrorTest) GetTupleList(ctx context.Context, in *pb.GrpcTuplesListRequest, opts ...grpc.CallOption) (*pb.GrpcTuplesListResponse, error) {
	return nil, errors.New("getting tuples failed")
}

func (ct GrpcClientErrorTest) ActivateFragmentsForTupleChunk(ctx context.Context, in *pb.GrpcTupleChunkRequest, opts ...grpc.CallOption) (*pb.GrpcEmpty, error) {
	return &pb.GrpcEmpty{}, nil
}

func (ct GrpcClientErrorTest) GetTelemetryData(ctx context.Context, in *pb.GrpcTelemetryDataRequest, opts ...grpc.CallOption) (*pb.GrpcTelemetryDataResponse, error) {
	return &pb.GrpcTelemetryDataResponse{}, nil
}

func (ct GrpcClientErrorTest) UploadTupleChunk(ctx context.Context, in *pb.GrpcUploadTupleChunkRequest, opts ...grpc.CallOption) (*pb.GrpcUploadTupleChunkResponse, error) {
	return &pb.GrpcUploadTupleChunkResponse{}, nil
}

func (ct GrpcClientErrorJsonResponseBodyTest) GetTupleList(ctx context.Context, in *pb.GrpcTuplesListRequest, opts ...grpc.CallOption) (*pb.GrpcTuplesListResponse, error) {
	return nil, errors.New("castor has returned an invalid response body")
}

func (ct GrpcClientErrorJsonResponseBodyTest) ActivateFragmentsForTupleChunk(ctx context.Context, in *pb.GrpcTupleChunkRequest, opts ...grpc.CallOption) (*pb.GrpcEmpty, error) {
	return &pb.GrpcEmpty{}, nil
}

func (ct GrpcClientErrorJsonResponseBodyTest) GetTelemetryData(ctx context.Context, in *pb.GrpcTelemetryDataRequest, opts ...grpc.CallOption) (*pb.GrpcTelemetryDataResponse, error) {
	return &pb.GrpcTelemetryDataResponse{}, nil
}

func (ct GrpcClientErrorJsonResponseBodyTest) UploadTupleChunk(ctx context.Context, in *pb.GrpcUploadTupleChunkRequest, opts ...grpc.CallOption) (*pb.GrpcUploadTupleChunkResponse, error) {
	return &pb.GrpcUploadTupleChunkResponse{}, nil
}

var _ = Describe("Castor", func() {

	Context("passing an URL to constructor", func() {
		Context("when url is invalid", func() {
			It("responds with error", func() {
				invalidURL := "http://host:8080"
				clientInfo := GrpcClient{
					GRPC_URL: invalidURL,
				}
				_, err := GrpcClientConn(clientInfo)
				Expect(err).To(HaveOccurred())
			})
		})
		Context("when url is valid", func() {
			It("returns a new client", func() {
				validURL := "host:8080"
				castor.GrpcDialer = func(target string, opts ...grpc.DialOption) (*grpc.ClientConn, error) {
					return &grpc.ClientConn{}, nil
				}
				clientInfo := GrpcClient{
					GRPC_URL: validURL,
				}
				client, err := GrpcClientConn(clientInfo)
				Expect(err).NotTo(HaveOccurred())
				Expect(client).NotTo(BeNil())
			})
		})
	})

	Context("testing castor client", func() {
		var (
			tupleList *TupleList
			// jsn       []byte
			myURL string
		)
		BeforeEach(func() {
			shares := []Share{Share{Value: "val", Mac: "mac"}}
			tuples := []Tuple{Tuple{Shares: shares}}
			tupleList = &TupleList{Tuples: tuples}
			// jsn, _ = json.Marshal(tupleList)
			myURL = "host:8080"

			castor.GrpcDialer = func(target string, opts ...grpc.DialOption) (*grpc.ClientConn, error) {
				return &grpc.ClientConn{}, nil
			}
			castor.GetClient = func(conn *grpc.ClientConn) pb.IntraServiceClient {
				return GrpcClientTest{GRPC_URL: myURL}
			}
			castor.GrpcClientConn = func(clientInfo castor.GrpcClient) (*grpc.ClientConn, error) {
				return &grpc.ClientConn{}, nil
			}
			castor.GrpcConnCloser = func(conn *grpc.ClientConn) error {
				return nil
			}
		})
		Context("when the path is correct", func() {
			It("returns tuples", func() {
				client := GrpcClient{GRPC_URL: myURL}
				tuples, err := client.GetTuples(0, BitGfp, uuid.MustParse("acc23dc8-7855-4a2f-bc89-494ba30a74d2"))

				Expect(tuples).To(Equal(tupleList))
				Expect(err).NotTo(HaveOccurred())
			})
		})
		Context("when castor get tuples returns a non-OK response code", func() {
			It("returns an error", func() {
				client := GrpcClient{GRPC_URL: myURL}
				castor.GetClient = func(conn *grpc.ClientConn) pb.IntraServiceClient {
					return GrpcClientErrorTest{GRPC_URL: myURL}
				}
				Expect(func() { client.GetTuples(0, BitGfp, uuid.MustParse("acc23dc8-7855-4a2f-bc89-494ba30a74d2")) }).
					To(PanicWith(MatchError("getting tuples failed")))
			})
		})
		Context("when request to castor fails", func() {
			It("returns an error", func() {
				client := GrpcClient{GRPC_URL: myURL}
				castor.GrpcClientConn = func(clientInfo GrpcClient) (*grpc.ClientConn, error) {
					return nil, errors.New("communication with castor failed")
				}
				Expect(func() { client.GetTuples(0, BitGfp, uuid.MustParse("acc23dc8-7855-4a2f-bc89-494ba30a74d2")) }).
					To(PanicWith(MatchError("communication with castor failed")))
			})
		})
		Context("when castor get tuples returns invalid json body", func() {
			It("returns an error", func() {
				// jsn = []byte("invalid JSON String")
				client := GrpcClient{GRPC_URL: myURL}
				castor.GetClient = func(conn *grpc.ClientConn) pb.IntraServiceClient {
					return GrpcClientErrorJsonResponseBodyTest{GRPC_URL: myURL}
				}
				Expect(func() { client.GetTuples(0, BitGfp, uuid.MustParse("acc23dc8-7855-4a2f-bc89-494ba30a74d2")) }).
					To(PanicWith(MatchError("castor has returned an invalid response body")))
			})
		})

		Context("upload tuples chunks", func() {
			Context("returns non ok response", func() {

				It("returns an error if invalid file passed", func() {
					client := GrpcClient{GRPC_URL: myURL}
					Expect(func() { client.UploadTupleChunk("acc23dc8-7855-4a2f-bc89-494ba30a74d2", "BIT_GFP", "FilePath") }).
						To(PanicWith(HaveOccurred()))
				})

				It("invalid byte stream passed should return error", func() {
					client := GrpcClient{GRPC_URL: myURL}
					castor.GrpcFileReader = func(filePath string) ([]byte, error) {
						byteArray := []byte{97, 98, 99, 100, 101, 102, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3,
							97, 98, 99, 100, 101, 102, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 5, 6, 33, 44, 55}
						return byteArray, nil
					}
					defer func() {
						err := recover()
						Expect(err).To(Not(BeNil()))
						Expect(err.(error).Error()).To(ContainSubstring("slice bounds out of range"))
					}()
					client.UploadTupleChunk("", "", "")
				})

				It("valid byte stream passed should return success", func() {
					client := GrpcClient{GRPC_URL: myURL}
					castor.GrpcFileReader = func(filePath string) ([]byte, error) {
						byteArray := []byte{97, 98, 99, 100, 101, 102, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3,
							97, 98, 99, 100, 101, 102, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 5, 6, 33, 44, 55}
						return byteArray, nil
					}
					castor.GetSpdzHeaderLengthWrapper = func(data []byte) int64 {
						return 37
					}
					defer func() {
						err := recover()
						Expect(err).To(BeNil())
					}()
					client.UploadTupleChunk("", "", "")
				})
			})
		})

		Context("get telemetry data", func() {
			It("returns ok response for not passing specific interval", func() {
				client := GrpcClient{GRPC_URL: myURL}
				defer func() {
					err := recover()
					Expect(err).To(BeNil())
				}()
				client.GetTelemetryData(-1)
			})

			It("returns ok response for passing specific interval", func() {
				client := GrpcClient{GRPC_URL: myURL}
				defer func() {
					err := recover()
					Expect(err).To(BeNil())
				}()
				client.GetTelemetryData(10)
			})
		})

		Context("activate tuple chunk", func() {
			It("returns ok response", func() {
				client := GrpcClient{GRPC_URL: myURL}
				defer func() {
					err := recover()
					Expect(err).To(BeNil())
				}()
				client.ActivateTupleChunk("")
			})
		})

	})
})
