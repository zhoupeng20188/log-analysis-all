package com.zp.protobuf;// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Msg.proto

public final class MsgPOJO {
  private MsgPOJO() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface MsgOrBuilder extends
      // @@protoc_insertion_point(interface_extends:Msg)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     * int32对应java中的int型，1表示属性序号，不是值
     * </pre>
     *
     * <code>int64 msgId = 1;</code>
     * @return The msgId.
     */
    long getMsgId();

    /**
     * <pre>
     * 消息类型
     * </pre>
     *
     * <code>int32 type = 2;</code>
     * @return The type.
     */
    int getType();

    /**
     * <pre>
     * 消息index
     * </pre>
     *
     * <code>int32 index = 3;</code>
     * @return The index.
     */
    int getIndex();

    /**
     * <pre>
     * projectId
     * </pre>
     *
     * <code>string projectId = 4;</code>
     * @return The projectId.
     */
    String getProjectId();
    /**
     * <pre>
     * projectId
     * </pre>
     *
     * <code>string projectId = 4;</code>
     * @return The bytes for projectId.
     */
    com.google.protobuf.ByteString
        getProjectIdBytes();

    /**
     * <pre>
     * 内容
     * </pre>
     *
     * <code>string content = 5;</code>
     * @return The content.
     */
    String getContent();
    /**
     * <pre>
     * 内容
     * </pre>
     *
     * <code>string content = 5;</code>
     * @return The bytes for content.
     */
    com.google.protobuf.ByteString
        getContentBytes();
  }
  /**
   * <pre>
   * protobuf使用message管理数据
   * </pre>
   *
   * Protobuf type {@code Msg}
   */
  public static final class Msg extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:Msg)
      MsgOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Msg.newBuilder() to construct.
    private Msg(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Msg() {
      projectId_ = "";
      content_ = "";
    }

    @Override
    @SuppressWarnings({"unused"})
    protected Object newInstance(
        UnusedPrivateParameter unused) {
      return new Msg();
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Msg(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new NullPointerException();
      }
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {

              msgId_ = input.readInt64();
              break;
            }
            case 16: {

              type_ = input.readInt32();
              break;
            }
            case 24: {

              index_ = input.readInt32();
              break;
            }
            case 34: {
              String s = input.readStringRequireUtf8();

              projectId_ = s;
              break;
            }
            case 42: {
              String s = input.readStringRequireUtf8();

              content_ = s;
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return MsgPOJO.internal_static_Msg_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return MsgPOJO.internal_static_Msg_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              Msg.class, Builder.class);
    }

    public static final int MSGID_FIELD_NUMBER = 1;
    private long msgId_;
    /**
     * <pre>
     * int32对应java中的int型，1表示属性序号，不是值
     * </pre>
     *
     * <code>int64 msgId = 1;</code>
     * @return The msgId.
     */
    @Override
    public long getMsgId() {
      return msgId_;
    }

    public static final int TYPE_FIELD_NUMBER = 2;
    private int type_;
    /**
     * <pre>
     * 消息类型
     * </pre>
     *
     * <code>int32 type = 2;</code>
     * @return The type.
     */
    @Override
    public int getType() {
      return type_;
    }

    public static final int INDEX_FIELD_NUMBER = 3;
    private int index_;
    /**
     * <pre>
     * 消息index
     * </pre>
     *
     * <code>int32 index = 3;</code>
     * @return The index.
     */
    @Override
    public int getIndex() {
      return index_;
    }

    public static final int PROJECTID_FIELD_NUMBER = 4;
    private volatile Object projectId_;
    /**
     * <pre>
     * projectId
     * </pre>
     *
     * <code>string projectId = 4;</code>
     * @return The projectId.
     */
    @Override
    public String getProjectId() {
      Object ref = projectId_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        projectId_ = s;
        return s;
      }
    }
    /**
     * <pre>
     * projectId
     * </pre>
     *
     * <code>string projectId = 4;</code>
     * @return The bytes for projectId.
     */
    @Override
    public com.google.protobuf.ByteString
        getProjectIdBytes() {
      Object ref = projectId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        projectId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int CONTENT_FIELD_NUMBER = 5;
    private volatile Object content_;
    /**
     * <pre>
     * 内容
     * </pre>
     *
     * <code>string content = 5;</code>
     * @return The content.
     */
    @Override
    public String getContent() {
      Object ref = content_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        content_ = s;
        return s;
      }
    }
    /**
     * <pre>
     * 内容
     * </pre>
     *
     * <code>string content = 5;</code>
     * @return The bytes for content.
     */
    @Override
    public com.google.protobuf.ByteString
        getContentBytes() {
      Object ref = content_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        content_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    @Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (msgId_ != 0L) {
        output.writeInt64(1, msgId_);
      }
      if (type_ != 0) {
        output.writeInt32(2, type_);
      }
      if (index_ != 0) {
        output.writeInt32(3, index_);
      }
      if (!getProjectIdBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 4, projectId_);
      }
      if (!getContentBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 5, content_);
      }
      unknownFields.writeTo(output);
    }

    @Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (msgId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(1, msgId_);
      }
      if (type_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, type_);
      }
      if (index_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, index_);
      }
      if (!getProjectIdBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, projectId_);
      }
      if (!getContentBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(5, content_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof Msg)) {
        return super.equals(obj);
      }
      Msg other = (Msg) obj;

      if (getMsgId()
          != other.getMsgId()) return false;
      if (getType()
          != other.getType()) return false;
      if (getIndex()
          != other.getIndex()) return false;
      if (!getProjectId()
          .equals(other.getProjectId())) return false;
      if (!getContent()
          .equals(other.getContent())) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + MSGID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getMsgId());
      hash = (37 * hash) + TYPE_FIELD_NUMBER;
      hash = (53 * hash) + getType();
      hash = (37 * hash) + INDEX_FIELD_NUMBER;
      hash = (53 * hash) + getIndex();
      hash = (37 * hash) + PROJECTID_FIELD_NUMBER;
      hash = (53 * hash) + getProjectId().hashCode();
      hash = (37 * hash) + CONTENT_FIELD_NUMBER;
      hash = (53 * hash) + getContent().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static Msg parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static Msg parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static Msg parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static Msg parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static Msg parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static Msg parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static Msg parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static Msg parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static Msg parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static Msg parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static Msg parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static Msg parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(Msg prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * <pre>
     * protobuf使用message管理数据
     * </pre>
     *
     * Protobuf type {@code Msg}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:Msg)
        MsgOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return MsgPOJO.internal_static_Msg_descriptor;
      }

      @Override
      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return MsgPOJO.internal_static_Msg_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                Msg.class, Builder.class);
      }

      // Construct using MsgPOJO.Msg.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @Override
      public Builder clear() {
        super.clear();
        msgId_ = 0L;

        type_ = 0;

        index_ = 0;

        projectId_ = "";

        content_ = "";

        return this;
      }

      @Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return MsgPOJO.internal_static_Msg_descriptor;
      }

      @Override
      public Msg getDefaultInstanceForType() {
        return Msg.getDefaultInstance();
      }

      @Override
      public Msg build() {
        Msg result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @Override
      public Msg buildPartial() {
        Msg result = new Msg(this);
        result.msgId_ = msgId_;
        result.type_ = type_;
        result.index_ = index_;
        result.projectId_ = projectId_;
        result.content_ = content_;
        onBuilt();
        return result;
      }

      @Override
      public Builder clone() {
        return super.clone();
      }
      @Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.setField(field, value);
      }
      @Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.addRepeatedField(field, value);
      }
      @Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof Msg) {
          return mergeFrom((Msg)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(Msg other) {
        if (other == Msg.getDefaultInstance()) return this;
        if (other.getMsgId() != 0L) {
          setMsgId(other.getMsgId());
        }
        if (other.getType() != 0) {
          setType(other.getType());
        }
        if (other.getIndex() != 0) {
          setIndex(other.getIndex());
        }
        if (!other.getProjectId().isEmpty()) {
          projectId_ = other.projectId_;
          onChanged();
        }
        if (!other.getContent().isEmpty()) {
          content_ = other.content_;
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @Override
      public final boolean isInitialized() {
        return true;
      }

      @Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        Msg parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (Msg) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private long msgId_ ;
      /**
       * <pre>
       * int32对应java中的int型，1表示属性序号，不是值
       * </pre>
       *
       * <code>int64 msgId = 1;</code>
       * @return The msgId.
       */
      @Override
      public long getMsgId() {
        return msgId_;
      }
      /**
       * <pre>
       * int32对应java中的int型，1表示属性序号，不是值
       * </pre>
       *
       * <code>int64 msgId = 1;</code>
       * @param value The msgId to set.
       * @return This builder for chaining.
       */
      public Builder setMsgId(long value) {
        
        msgId_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * int32对应java中的int型，1表示属性序号，不是值
       * </pre>
       *
       * <code>int64 msgId = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearMsgId() {
        
        msgId_ = 0L;
        onChanged();
        return this;
      }

      private int type_ ;
      /**
       * <pre>
       * 消息类型
       * </pre>
       *
       * <code>int32 type = 2;</code>
       * @return The type.
       */
      @Override
      public int getType() {
        return type_;
      }
      /**
       * <pre>
       * 消息类型
       * </pre>
       *
       * <code>int32 type = 2;</code>
       * @param value The type to set.
       * @return This builder for chaining.
       */
      public Builder setType(int value) {
        
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 消息类型
       * </pre>
       *
       * <code>int32 type = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearType() {
        
        type_ = 0;
        onChanged();
        return this;
      }

      private int index_ ;
      /**
       * <pre>
       * 消息index
       * </pre>
       *
       * <code>int32 index = 3;</code>
       * @return The index.
       */
      @Override
      public int getIndex() {
        return index_;
      }
      /**
       * <pre>
       * 消息index
       * </pre>
       *
       * <code>int32 index = 3;</code>
       * @param value The index to set.
       * @return This builder for chaining.
       */
      public Builder setIndex(int value) {
        
        index_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 消息index
       * </pre>
       *
       * <code>int32 index = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearIndex() {
        
        index_ = 0;
        onChanged();
        return this;
      }

      private Object projectId_ = "";
      /**
       * <pre>
       * projectId
       * </pre>
       *
       * <code>string projectId = 4;</code>
       * @return The projectId.
       */
      public String getProjectId() {
        Object ref = projectId_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          projectId_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <pre>
       * projectId
       * </pre>
       *
       * <code>string projectId = 4;</code>
       * @return The bytes for projectId.
       */
      public com.google.protobuf.ByteString
          getProjectIdBytes() {
        Object ref = projectId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          projectId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <pre>
       * projectId
       * </pre>
       *
       * <code>string projectId = 4;</code>
       * @param value The projectId to set.
       * @return This builder for chaining.
       */
      public Builder setProjectId(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        projectId_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * projectId
       * </pre>
       *
       * <code>string projectId = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearProjectId() {
        
        projectId_ = getDefaultInstance().getProjectId();
        onChanged();
        return this;
      }
      /**
       * <pre>
       * projectId
       * </pre>
       *
       * <code>string projectId = 4;</code>
       * @param value The bytes for projectId to set.
       * @return This builder for chaining.
       */
      public Builder setProjectIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        projectId_ = value;
        onChanged();
        return this;
      }

      private Object content_ = "";
      /**
       * <pre>
       * 内容
       * </pre>
       *
       * <code>string content = 5;</code>
       * @return The content.
       */
      public String getContent() {
        Object ref = content_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          content_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <pre>
       * 内容
       * </pre>
       *
       * <code>string content = 5;</code>
       * @return The bytes for content.
       */
      public com.google.protobuf.ByteString
          getContentBytes() {
        Object ref = content_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          content_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <pre>
       * 内容
       * </pre>
       *
       * <code>string content = 5;</code>
       * @param value The content to set.
       * @return This builder for chaining.
       */
      public Builder setContent(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        content_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 内容
       * </pre>
       *
       * <code>string content = 5;</code>
       * @return This builder for chaining.
       */
      public Builder clearContent() {
        
        content_ = getDefaultInstance().getContent();
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 内容
       * </pre>
       *
       * <code>string content = 5;</code>
       * @param value The bytes for content to set.
       * @return This builder for chaining.
       */
      public Builder setContentBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        content_ = value;
        onChanged();
        return this;
      }
      @Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:Msg)
    }

    // @@protoc_insertion_point(class_scope:Msg)
    private static final Msg DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new Msg();
    }

    public static Msg getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Msg>
        PARSER = new com.google.protobuf.AbstractParser<Msg>() {
      @Override
      public Msg parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Msg(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Msg> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<Msg> getParserForType() {
      return PARSER;
    }

    @Override
    public Msg getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Msg_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Msg_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\tMsg.proto\"U\n\003Msg\022\r\n\005msgId\030\001 \001(\003\022\014\n\004typ" +
      "e\030\002 \001(\005\022\r\n\005index\030\003 \001(\005\022\021\n\tprojectId\030\004 \001(" +
      "\t\022\017\n\007content\030\005 \001(\tB\tB\007MsgPOJOb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_Msg_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_Msg_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Msg_descriptor,
        new String[] { "MsgId", "Type", "Index", "ProjectId", "Content", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
