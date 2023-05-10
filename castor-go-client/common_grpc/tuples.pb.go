// Code generated by protoc-gen-go. DO NOT EDIT.
// versions:
// 	protoc-gen-go v1.29.1
// 	protoc        v4.22.2
// source: tuples.proto

package common_grpc

import (
	protoreflect "google.golang.org/protobuf/reflect/protoreflect"
	protoimpl "google.golang.org/protobuf/runtime/protoimpl"
	reflect "reflect"
	sync "sync"
)

const (
	// Verify that this generated code is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(20 - protoimpl.MinVersion)
	// Verify that runtime/protoimpl is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(protoimpl.MaxVersion - 20)
)

type GrpcTuplesListRequest struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	Type      string `protobuf:"bytes,1,opt,name=type,proto3" json:"type,omitempty"`
	Count     int64  `protobuf:"varint,2,opt,name=count,proto3" json:"count,omitempty"`
	RequestId string `protobuf:"bytes,3,opt,name=request_id,json=requestId,proto3" json:"request_id,omitempty"`
}

func (x *GrpcTuplesListRequest) Reset() {
	*x = GrpcTuplesListRequest{}
	if protoimpl.UnsafeEnabled {
		mi := &file_tuples_proto_msgTypes[0]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *GrpcTuplesListRequest) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*GrpcTuplesListRequest) ProtoMessage() {}

func (x *GrpcTuplesListRequest) ProtoReflect() protoreflect.Message {
	mi := &file_tuples_proto_msgTypes[0]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use GrpcTuplesListRequest.ProtoReflect.Descriptor instead.
func (*GrpcTuplesListRequest) Descriptor() ([]byte, []int) {
	return file_tuples_proto_rawDescGZIP(), []int{0}
}

func (x *GrpcTuplesListRequest) GetType() string {
	if x != nil {
		return x.Type
	}
	return ""
}

func (x *GrpcTuplesListRequest) GetCount() int64 {
	if x != nil {
		return x.Count
	}
	return 0
}

func (x *GrpcTuplesListRequest) GetRequestId() string {
	if x != nil {
		return x.RequestId
	}
	return ""
}

type GrpcShare struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	Value []byte `protobuf:"bytes,1,opt,name=value,proto3" json:"value,omitempty"`
	Mac   []byte `protobuf:"bytes,2,opt,name=mac,proto3" json:"mac,omitempty"`
}

func (x *GrpcShare) Reset() {
	*x = GrpcShare{}
	if protoimpl.UnsafeEnabled {
		mi := &file_tuples_proto_msgTypes[1]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *GrpcShare) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*GrpcShare) ProtoMessage() {}

func (x *GrpcShare) ProtoReflect() protoreflect.Message {
	mi := &file_tuples_proto_msgTypes[1]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use GrpcShare.ProtoReflect.Descriptor instead.
func (*GrpcShare) Descriptor() ([]byte, []int) {
	return file_tuples_proto_rawDescGZIP(), []int{1}
}

func (x *GrpcShare) GetValue() []byte {
	if x != nil {
		return x.Value
	}
	return nil
}

func (x *GrpcShare) GetMac() []byte {
	if x != nil {
		return x.Mac
	}
	return nil
}

type GrpcResponsePayloadData struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	Shares []*GrpcShare `protobuf:"bytes,1,rep,name=shares,proto3" json:"shares,omitempty"`
}

func (x *GrpcResponsePayloadData) Reset() {
	*x = GrpcResponsePayloadData{}
	if protoimpl.UnsafeEnabled {
		mi := &file_tuples_proto_msgTypes[2]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *GrpcResponsePayloadData) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*GrpcResponsePayloadData) ProtoMessage() {}

func (x *GrpcResponsePayloadData) ProtoReflect() protoreflect.Message {
	mi := &file_tuples_proto_msgTypes[2]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use GrpcResponsePayloadData.ProtoReflect.Descriptor instead.
func (*GrpcResponsePayloadData) Descriptor() ([]byte, []int) {
	return file_tuples_proto_rawDescGZIP(), []int{2}
}

func (x *GrpcResponsePayloadData) GetShares() []*GrpcShare {
	if x != nil {
		return x.Shares
	}
	return nil
}

type GrpcTuplesListResponse struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	TuplesField string                     `protobuf:"bytes,1,opt,name=tuples_field,json=tuplesField,proto3" json:"tuples_field,omitempty"`
	TupleClass  string                     `protobuf:"bytes,2,opt,name=tuple_class,json=tupleClass,proto3" json:"tuple_class,omitempty"`
	GrpcTuples  []*GrpcResponsePayloadData `protobuf:"bytes,3,rep,name=grpc_tuples,json=grpcTuples,proto3" json:"grpc_tuples,omitempty"`
}

func (x *GrpcTuplesListResponse) Reset() {
	*x = GrpcTuplesListResponse{}
	if protoimpl.UnsafeEnabled {
		mi := &file_tuples_proto_msgTypes[3]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *GrpcTuplesListResponse) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*GrpcTuplesListResponse) ProtoMessage() {}

func (x *GrpcTuplesListResponse) ProtoReflect() protoreflect.Message {
	mi := &file_tuples_proto_msgTypes[3]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use GrpcTuplesListResponse.ProtoReflect.Descriptor instead.
func (*GrpcTuplesListResponse) Descriptor() ([]byte, []int) {
	return file_tuples_proto_rawDescGZIP(), []int{3}
}

func (x *GrpcTuplesListResponse) GetTuplesField() string {
	if x != nil {
		return x.TuplesField
	}
	return ""
}

func (x *GrpcTuplesListResponse) GetTupleClass() string {
	if x != nil {
		return x.TupleClass
	}
	return ""
}

func (x *GrpcTuplesListResponse) GetGrpcTuples() []*GrpcResponsePayloadData {
	if x != nil {
		return x.GrpcTuples
	}
	return nil
}

var File_tuples_proto protoreflect.FileDescriptor

var file_tuples_proto_rawDesc = []byte{
	0x0a, 0x0c, 0x74, 0x75, 0x70, 0x6c, 0x65, 0x73, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x12, 0x22,
	0x69, 0x6f, 0x2e, 0x63, 0x61, 0x72, 0x62, 0x79, 0x6e, 0x65, 0x73, 0x74, 0x61, 0x63, 0x6b, 0x2e,
	0x63, 0x61, 0x73, 0x74, 0x6f, 0x72, 0x2e, 0x63, 0x6f, 0x6d, 0x6d, 0x6f, 0x6e, 0x2e, 0x67, 0x72,
	0x70, 0x63, 0x22, 0x60, 0x0a, 0x15, 0x47, 0x72, 0x70, 0x63, 0x54, 0x75, 0x70, 0x6c, 0x65, 0x73,
	0x4c, 0x69, 0x73, 0x74, 0x52, 0x65, 0x71, 0x75, 0x65, 0x73, 0x74, 0x12, 0x12, 0x0a, 0x04, 0x74,
	0x79, 0x70, 0x65, 0x18, 0x01, 0x20, 0x01, 0x28, 0x09, 0x52, 0x04, 0x74, 0x79, 0x70, 0x65, 0x12,
	0x14, 0x0a, 0x05, 0x63, 0x6f, 0x75, 0x6e, 0x74, 0x18, 0x02, 0x20, 0x01, 0x28, 0x03, 0x52, 0x05,
	0x63, 0x6f, 0x75, 0x6e, 0x74, 0x12, 0x1d, 0x0a, 0x0a, 0x72, 0x65, 0x71, 0x75, 0x65, 0x73, 0x74,
	0x5f, 0x69, 0x64, 0x18, 0x03, 0x20, 0x01, 0x28, 0x09, 0x52, 0x09, 0x72, 0x65, 0x71, 0x75, 0x65,
	0x73, 0x74, 0x49, 0x64, 0x22, 0x33, 0x0a, 0x09, 0x47, 0x72, 0x70, 0x63, 0x53, 0x68, 0x61, 0x72,
	0x65, 0x12, 0x14, 0x0a, 0x05, 0x76, 0x61, 0x6c, 0x75, 0x65, 0x18, 0x01, 0x20, 0x01, 0x28, 0x0c,
	0x52, 0x05, 0x76, 0x61, 0x6c, 0x75, 0x65, 0x12, 0x10, 0x0a, 0x03, 0x6d, 0x61, 0x63, 0x18, 0x02,
	0x20, 0x01, 0x28, 0x0c, 0x52, 0x03, 0x6d, 0x61, 0x63, 0x22, 0x60, 0x0a, 0x17, 0x47, 0x72, 0x70,
	0x63, 0x52, 0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x50, 0x61, 0x79, 0x6c, 0x6f, 0x61, 0x64,
	0x44, 0x61, 0x74, 0x61, 0x12, 0x45, 0x0a, 0x06, 0x73, 0x68, 0x61, 0x72, 0x65, 0x73, 0x18, 0x01,
	0x20, 0x03, 0x28, 0x0b, 0x32, 0x2d, 0x2e, 0x69, 0x6f, 0x2e, 0x63, 0x61, 0x72, 0x62, 0x79, 0x6e,
	0x65, 0x73, 0x74, 0x61, 0x63, 0x6b, 0x2e, 0x63, 0x61, 0x73, 0x74, 0x6f, 0x72, 0x2e, 0x63, 0x6f,
	0x6d, 0x6d, 0x6f, 0x6e, 0x2e, 0x67, 0x72, 0x70, 0x63, 0x2e, 0x47, 0x72, 0x70, 0x63, 0x53, 0x68,
	0x61, 0x72, 0x65, 0x52, 0x06, 0x73, 0x68, 0x61, 0x72, 0x65, 0x73, 0x22, 0xba, 0x01, 0x0a, 0x16,
	0x47, 0x72, 0x70, 0x63, 0x54, 0x75, 0x70, 0x6c, 0x65, 0x73, 0x4c, 0x69, 0x73, 0x74, 0x52, 0x65,
	0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x12, 0x21, 0x0a, 0x0c, 0x74, 0x75, 0x70, 0x6c, 0x65, 0x73,
	0x5f, 0x66, 0x69, 0x65, 0x6c, 0x64, 0x18, 0x01, 0x20, 0x01, 0x28, 0x09, 0x52, 0x0b, 0x74, 0x75,
	0x70, 0x6c, 0x65, 0x73, 0x46, 0x69, 0x65, 0x6c, 0x64, 0x12, 0x1f, 0x0a, 0x0b, 0x74, 0x75, 0x70,
	0x6c, 0x65, 0x5f, 0x63, 0x6c, 0x61, 0x73, 0x73, 0x18, 0x02, 0x20, 0x01, 0x28, 0x09, 0x52, 0x0a,
	0x74, 0x75, 0x70, 0x6c, 0x65, 0x43, 0x6c, 0x61, 0x73, 0x73, 0x12, 0x5c, 0x0a, 0x0b, 0x67, 0x72,
	0x70, 0x63, 0x5f, 0x74, 0x75, 0x70, 0x6c, 0x65, 0x73, 0x18, 0x03, 0x20, 0x03, 0x28, 0x0b, 0x32,
	0x3b, 0x2e, 0x69, 0x6f, 0x2e, 0x63, 0x61, 0x72, 0x62, 0x79, 0x6e, 0x65, 0x73, 0x74, 0x61, 0x63,
	0x6b, 0x2e, 0x63, 0x61, 0x73, 0x74, 0x6f, 0x72, 0x2e, 0x63, 0x6f, 0x6d, 0x6d, 0x6f, 0x6e, 0x2e,
	0x67, 0x72, 0x70, 0x63, 0x2e, 0x47, 0x72, 0x70, 0x63, 0x52, 0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73,
	0x65, 0x50, 0x61, 0x79, 0x6c, 0x6f, 0x61, 0x64, 0x44, 0x61, 0x74, 0x61, 0x52, 0x0a, 0x67, 0x72,
	0x70, 0x63, 0x54, 0x75, 0x70, 0x6c, 0x65, 0x73, 0x42, 0x0d, 0x5a, 0x0b, 0x63, 0x6f, 0x6d, 0x6d,
	0x6f, 0x6e, 0x2e, 0x67, 0x72, 0x70, 0x63, 0x62, 0x06, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x33,
}

var (
	file_tuples_proto_rawDescOnce sync.Once
	file_tuples_proto_rawDescData = file_tuples_proto_rawDesc
)

func file_tuples_proto_rawDescGZIP() []byte {
	file_tuples_proto_rawDescOnce.Do(func() {
		file_tuples_proto_rawDescData = protoimpl.X.CompressGZIP(file_tuples_proto_rawDescData)
	})
	return file_tuples_proto_rawDescData
}

var file_tuples_proto_msgTypes = make([]protoimpl.MessageInfo, 4)
var file_tuples_proto_goTypes = []interface{}{
	(*GrpcTuplesListRequest)(nil),   // 0: io.carbynestack.castor.common.grpc.GrpcTuplesListRequest
	(*GrpcShare)(nil),               // 1: io.carbynestack.castor.common.grpc.GrpcShare
	(*GrpcResponsePayloadData)(nil), // 2: io.carbynestack.castor.common.grpc.GrpcResponsePayloadData
	(*GrpcTuplesListResponse)(nil),  // 3: io.carbynestack.castor.common.grpc.GrpcTuplesListResponse
}
var file_tuples_proto_depIdxs = []int32{
	1, // 0: io.carbynestack.castor.common.grpc.GrpcResponsePayloadData.shares:type_name -> io.carbynestack.castor.common.grpc.GrpcShare
	2, // 1: io.carbynestack.castor.common.grpc.GrpcTuplesListResponse.grpc_tuples:type_name -> io.carbynestack.castor.common.grpc.GrpcResponsePayloadData
	2, // [2:2] is the sub-list for method output_type
	2, // [2:2] is the sub-list for method input_type
	2, // [2:2] is the sub-list for extension type_name
	2, // [2:2] is the sub-list for extension extendee
	0, // [0:2] is the sub-list for field type_name
}

func init() { file_tuples_proto_init() }
func file_tuples_proto_init() {
	if File_tuples_proto != nil {
		return
	}
	if !protoimpl.UnsafeEnabled {
		file_tuples_proto_msgTypes[0].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*GrpcTuplesListRequest); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_tuples_proto_msgTypes[1].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*GrpcShare); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_tuples_proto_msgTypes[2].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*GrpcResponsePayloadData); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_tuples_proto_msgTypes[3].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*GrpcTuplesListResponse); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
	}
	type x struct{}
	out := protoimpl.TypeBuilder{
		File: protoimpl.DescBuilder{
			GoPackagePath: reflect.TypeOf(x{}).PkgPath(),
			RawDescriptor: file_tuples_proto_rawDesc,
			NumEnums:      0,
			NumMessages:   4,
			NumExtensions: 0,
			NumServices:   0,
		},
		GoTypes:           file_tuples_proto_goTypes,
		DependencyIndexes: file_tuples_proto_depIdxs,
		MessageInfos:      file_tuples_proto_msgTypes,
	}.Build()
	File_tuples_proto = out.File
	file_tuples_proto_rawDesc = nil
	file_tuples_proto_goTypes = nil
	file_tuples_proto_depIdxs = nil
}
