// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: backup_keys.proto

package io.golos.cyber_android.core.backup.raw_data;

public final class BackupKeysRawData {
  private BackupKeysRawData() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }
  public interface KeyInfoOrBuilder extends
      // @@protoc_insertion_point(interface_extends:backup.KeyInfo)
      com.google.protobuf.MessageLiteOrBuilder {

    /**
     * <code>required int64 userName = 1;</code>
     */
    boolean hasUserName();
    /**
     * <code>required int64 userName = 1;</code>
     */
    long getUserName();

    /**
     * <code>required string masterKey = 2;</code>
     */
    boolean hasMasterKey();
    /**
     * <code>required string masterKey = 2;</code>
     */
    String getMasterKey();
    /**
     * <code>required string masterKey = 2;</code>
     */
    com.google.protobuf.ByteString
        getMasterKeyBytes();
  }
  /**
   * Protobuf type {@code backup.KeyInfo}
   */
  public  static final class KeyInfo extends
      com.google.protobuf.GeneratedMessageLite<
          KeyInfo, KeyInfo.Builder> implements
      // @@protoc_insertion_point(message_implements:backup.KeyInfo)
      KeyInfoOrBuilder {
    private KeyInfo() {
      masterKey_ = "";
    }
    private int bitField0_;
    public static final int USERNAME_FIELD_NUMBER = 1;
    private long userName_;
    /**
     * <code>required int64 userName = 1;</code>
     */
    @Override
    public boolean hasUserName() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>required int64 userName = 1;</code>
     */
    @Override
    public long getUserName() {
      return userName_;
    }
    /**
     * <code>required int64 userName = 1;</code>
     */
    private void setUserName(long value) {
      bitField0_ |= 0x00000001;
      userName_ = value;
    }
    /**
     * <code>required int64 userName = 1;</code>
     */
    private void clearUserName() {
      bitField0_ = (bitField0_ & ~0x00000001);
      userName_ = 0L;
    }

    public static final int MASTERKEY_FIELD_NUMBER = 2;
    private String masterKey_;
    /**
     * <code>required string masterKey = 2;</code>
     */
    @Override
    public boolean hasMasterKey() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>required string masterKey = 2;</code>
     */
    @Override
    public String getMasterKey() {
      return masterKey_;
    }
    /**
     * <code>required string masterKey = 2;</code>
     */
    @Override
    public com.google.protobuf.ByteString
        getMasterKeyBytes() {
      return com.google.protobuf.ByteString.copyFromUtf8(masterKey_);
    }
    /**
     * <code>required string masterKey = 2;</code>
     */
    private void setMasterKey(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
      masterKey_ = value;
    }
    /**
     * <code>required string masterKey = 2;</code>
     */
    private void clearMasterKey() {
      bitField0_ = (bitField0_ & ~0x00000002);
      masterKey_ = getDefaultInstance().getMasterKey();
    }
    /**
     * <code>required string masterKey = 2;</code>
     */
    private void setMasterKeyBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
      masterKey_ = value.toStringUtf8();
    }

    public static KeyInfo parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static KeyInfo parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static KeyInfo parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static KeyInfo parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static KeyInfo parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static KeyInfo parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static KeyInfo parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input);
    }
    public static KeyInfo parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input, extensionRegistry);
    }
    public static KeyInfo parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return parseDelimitedFrom(DEFAULT_INSTANCE, input);
    }
    public static KeyInfo parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
    }
    public static KeyInfo parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input);
    }
    public static KeyInfo parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input, extensionRegistry);
    }

    public static Builder newBuilder() {
      return (Builder) DEFAULT_INSTANCE.createBuilder();
    }
    public static Builder newBuilder(KeyInfo prototype) {
      return (Builder) DEFAULT_INSTANCE.createBuilder(prototype);
    }

    /**
     * Protobuf type {@code backup.KeyInfo}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageLite.Builder<
          KeyInfo, Builder> implements
        // @@protoc_insertion_point(builder_implements:backup.KeyInfo)
        KeyInfoOrBuilder {
      // Construct using io.golos.cyber_android.core.backup.raw_data.BackupKeysRawData.KeyInfo.newBuilder()
      private Builder() {
        super(DEFAULT_INSTANCE);
      }


      /**
       * <code>required int64 userName = 1;</code>
       */
      @Override
      public boolean hasUserName() {
        return instance.hasUserName();
      }
      /**
       * <code>required int64 userName = 1;</code>
       */
      @Override
      public long getUserName() {
        return instance.getUserName();
      }
      /**
       * <code>required int64 userName = 1;</code>
       */
      public Builder setUserName(long value) {
        copyOnWrite();
        instance.setUserName(value);
        return this;
      }
      /**
       * <code>required int64 userName = 1;</code>
       */
      public Builder clearUserName() {
        copyOnWrite();
        instance.clearUserName();
        return this;
      }

      /**
       * <code>required string masterKey = 2;</code>
       */
      @Override
      public boolean hasMasterKey() {
        return instance.hasMasterKey();
      }
      /**
       * <code>required string masterKey = 2;</code>
       */
      @Override
      public String getMasterKey() {
        return instance.getMasterKey();
      }
      /**
       * <code>required string masterKey = 2;</code>
       */
      @Override
      public com.google.protobuf.ByteString
          getMasterKeyBytes() {
        return instance.getMasterKeyBytes();
      }
      /**
       * <code>required string masterKey = 2;</code>
       */
      public Builder setMasterKey(
          String value) {
        copyOnWrite();
        instance.setMasterKey(value);
        return this;
      }
      /**
       * <code>required string masterKey = 2;</code>
       */
      public Builder clearMasterKey() {
        copyOnWrite();
        instance.clearMasterKey();
        return this;
      }
      /**
       * <code>required string masterKey = 2;</code>
       */
      public Builder setMasterKeyBytes(
          com.google.protobuf.ByteString value) {
        copyOnWrite();
        instance.setMasterKeyBytes(value);
        return this;
      }

      // @@protoc_insertion_point(builder_scope:backup.KeyInfo)
    }
    private byte memoizedIsInitialized = 2;
    @Override
    @SuppressWarnings({"unchecked", "fallthrough"})
    protected final Object dynamicMethod(
        MethodToInvoke method,
        Object arg0, Object arg1) {
      switch (method) {
        case NEW_MUTABLE_INSTANCE: {
          return new KeyInfo();
        }
        case NEW_BUILDER: {
          return new Builder();
        }
        case BUILD_MESSAGE_INFO: {
            Object[] objects = new Object[] {
              "bitField0_",
              "userName_",
              "masterKey_",
            };
            String info =
                "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0002\u0001\u0502\u0000\u0002" +
                "\u0508\u0001";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
        }
        // fall through
        case GET_DEFAULT_INSTANCE: {
          return DEFAULT_INSTANCE;
        }
        case GET_PARSER: {
          com.google.protobuf.Parser<KeyInfo> parser = PARSER;
          if (parser == null) {
            synchronized (KeyInfo.class) {
              parser = PARSER;
              if (parser == null) {
                parser = new DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                PARSER = parser;
              }
            }
          }
          return parser;
      }
      case GET_MEMOIZED_IS_INITIALIZED: {
        return memoizedIsInitialized;
      }
      case SET_MEMOIZED_IS_INITIALIZED: {
        memoizedIsInitialized = (byte) (arg0 == null ? 0 : 1);
        return null;
      }
      }
      throw new UnsupportedOperationException();
    }


    // @@protoc_insertion_point(class_scope:backup.KeyInfo)
    private static final KeyInfo DEFAULT_INSTANCE;
    static {
      // New instances are implicitly immutable so no need to make
      // immutable.
      DEFAULT_INSTANCE = new KeyInfo();
    }

    static {
      com.google.protobuf.GeneratedMessageLite.registerDefaultInstance(
        KeyInfo.class, DEFAULT_INSTANCE);
    }
    public static KeyInfo getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static volatile com.google.protobuf.Parser<KeyInfo> PARSER;

    public static com.google.protobuf.Parser<KeyInfo> parser() {
      return DEFAULT_INSTANCE.getParserForType();
    }
  }

  public interface KeysListOrBuilder extends
      // @@protoc_insertion_point(interface_extends:backup.KeysList)
      com.google.protobuf.MessageLiteOrBuilder {

    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    java.util.List<KeyInfo>
        getKeyList();
    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    KeyInfo getKey(int index);
    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    int getKeyCount();
  }
  /**
   * Protobuf type {@code backup.KeysList}
   */
  public  static final class KeysList extends
      com.google.protobuf.GeneratedMessageLite<
          KeysList, KeysList.Builder> implements
      // @@protoc_insertion_point(message_implements:backup.KeysList)
      KeysListOrBuilder {
    private KeysList() {
      key_ = emptyProtobufList();
    }
    public static final int KEY_FIELD_NUMBER = 1;
    private com.google.protobuf.Internal.ProtobufList<KeyInfo> key_;
    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    @Override
    public java.util.List<KeyInfo> getKeyList() {
      return key_;
    }
    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    public java.util.List<? extends KeyInfoOrBuilder>
        getKeyOrBuilderList() {
      return key_;
    }
    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    @Override
    public int getKeyCount() {
      return key_.size();
    }
    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    @Override
    public KeyInfo getKey(int index) {
      return key_.get(index);
    }
    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    public KeyInfoOrBuilder getKeyOrBuilder(
        int index) {
      return key_.get(index);
    }
    private void ensureKeyIsMutable() {
      if (!key_.isModifiable()) {
        key_ =
            com.google.protobuf.GeneratedMessageLite.mutableCopy(key_);
       }
    }

    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    private void setKey(
        int index, KeyInfo value) {
      if (value == null) {
        throw new NullPointerException();
      }
      ensureKeyIsMutable();
      key_.set(index, value);
    }
    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    private void setKey(
        int index, KeyInfo.Builder builderForValue) {
      ensureKeyIsMutable();
      key_.set(index, builderForValue.build());
    }
    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    private void addKey(KeyInfo value) {
      if (value == null) {
        throw new NullPointerException();
      }
      ensureKeyIsMutable();
      key_.add(value);
    }
    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    private void addKey(
        int index, KeyInfo value) {
      if (value == null) {
        throw new NullPointerException();
      }
      ensureKeyIsMutable();
      key_.add(index, value);
    }
    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    private void addKey(
        KeyInfo.Builder builderForValue) {
      ensureKeyIsMutable();
      key_.add(builderForValue.build());
    }
    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    private void addKey(
        int index, KeyInfo.Builder builderForValue) {
      ensureKeyIsMutable();
      key_.add(index, builderForValue.build());
    }
    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    private void addAllKey(
        Iterable<? extends KeyInfo> values) {
      ensureKeyIsMutable();
      com.google.protobuf.AbstractMessageLite.addAll(
          values, key_);
    }
    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    private void clearKey() {
      key_ = emptyProtobufList();
    }
    /**
     * <code>repeated .backup.KeyInfo key = 1;</code>
     */
    private void removeKey(int index) {
      ensureKeyIsMutable();
      key_.remove(index);
    }

    public static KeysList parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static KeysList parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static KeysList parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static KeysList parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static KeysList parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static KeysList parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static KeysList parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input);
    }
    public static KeysList parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input, extensionRegistry);
    }
    public static KeysList parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return parseDelimitedFrom(DEFAULT_INSTANCE, input);
    }
    public static KeysList parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
    }
    public static KeysList parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input);
    }
    public static KeysList parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input, extensionRegistry);
    }

    public static Builder newBuilder() {
      return (Builder) DEFAULT_INSTANCE.createBuilder();
    }
    public static Builder newBuilder(KeysList prototype) {
      return (Builder) DEFAULT_INSTANCE.createBuilder(prototype);
    }

    /**
     * Protobuf type {@code backup.KeysList}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageLite.Builder<
          KeysList, Builder> implements
        // @@protoc_insertion_point(builder_implements:backup.KeysList)
        KeysListOrBuilder {
      // Construct using io.golos.cyber_android.core.backup.raw_data.BackupKeysRawData.KeysList.newBuilder()
      private Builder() {
        super(DEFAULT_INSTANCE);
      }


      /**
       * <code>repeated .backup.KeyInfo key = 1;</code>
       */
      @Override
      public java.util.List<KeyInfo> getKeyList() {
        return java.util.Collections.unmodifiableList(
            instance.getKeyList());
      }
      /**
       * <code>repeated .backup.KeyInfo key = 1;</code>
       */
      @Override
      public int getKeyCount() {
        return instance.getKeyCount();
      }/**
       * <code>repeated .backup.KeyInfo key = 1;</code>
       */
      @Override
      public KeyInfo getKey(int index) {
        return instance.getKey(index);
      }
      /**
       * <code>repeated .backup.KeyInfo key = 1;</code>
       */
      public Builder setKey(
          int index, KeyInfo value) {
        copyOnWrite();
        instance.setKey(index, value);
        return this;
      }
      /**
       * <code>repeated .backup.KeyInfo key = 1;</code>
       */
      public Builder setKey(
          int index, KeyInfo.Builder builderForValue) {
        copyOnWrite();
        instance.setKey(index, builderForValue);
        return this;
      }
      /**
       * <code>repeated .backup.KeyInfo key = 1;</code>
       */
      public Builder addKey(KeyInfo value) {
        copyOnWrite();
        instance.addKey(value);
        return this;
      }
      /**
       * <code>repeated .backup.KeyInfo key = 1;</code>
       */
      public Builder addKey(
          int index, KeyInfo value) {
        copyOnWrite();
        instance.addKey(index, value);
        return this;
      }
      /**
       * <code>repeated .backup.KeyInfo key = 1;</code>
       */
      public Builder addKey(
          KeyInfo.Builder builderForValue) {
        copyOnWrite();
        instance.addKey(builderForValue);
        return this;
      }
      /**
       * <code>repeated .backup.KeyInfo key = 1;</code>
       */
      public Builder addKey(
          int index, KeyInfo.Builder builderForValue) {
        copyOnWrite();
        instance.addKey(index, builderForValue);
        return this;
      }
      /**
       * <code>repeated .backup.KeyInfo key = 1;</code>
       */
      public Builder addAllKey(
          Iterable<? extends KeyInfo> values) {
        copyOnWrite();
        instance.addAllKey(values);
        return this;
      }
      /**
       * <code>repeated .backup.KeyInfo key = 1;</code>
       */
      public Builder clearKey() {
        copyOnWrite();
        instance.clearKey();
        return this;
      }
      /**
       * <code>repeated .backup.KeyInfo key = 1;</code>
       */
      public Builder removeKey(int index) {
        copyOnWrite();
        instance.removeKey(index);
        return this;
      }

      // @@protoc_insertion_point(builder_scope:backup.KeysList)
    }
    private byte memoizedIsInitialized = 2;
    @Override
    @SuppressWarnings({"unchecked", "fallthrough"})
    protected final Object dynamicMethod(
        MethodToInvoke method,
        Object arg0, Object arg1) {
      switch (method) {
        case NEW_MUTABLE_INSTANCE: {
          return new KeysList();
        }
        case NEW_BUILDER: {
          return new Builder();
        }
        case BUILD_MESSAGE_INFO: {
            Object[] objects = new Object[] {
              "key_",
              KeyInfo.class,
            };
            String info =
                "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0001\u0001\u041b";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
        }
        // fall through
        case GET_DEFAULT_INSTANCE: {
          return DEFAULT_INSTANCE;
        }
        case GET_PARSER: {
          com.google.protobuf.Parser<KeysList> parser = PARSER;
          if (parser == null) {
            synchronized (KeysList.class) {
              parser = PARSER;
              if (parser == null) {
                parser = new DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                PARSER = parser;
              }
            }
          }
          return parser;
      }
      case GET_MEMOIZED_IS_INITIALIZED: {
        return memoizedIsInitialized;
      }
      case SET_MEMOIZED_IS_INITIALIZED: {
        memoizedIsInitialized = (byte) (arg0 == null ? 0 : 1);
        return null;
      }
      }
      throw new UnsupportedOperationException();
    }


    // @@protoc_insertion_point(class_scope:backup.KeysList)
    private static final KeysList DEFAULT_INSTANCE;
    static {
      // New instances are implicitly immutable so no need to make
      // immutable.
      DEFAULT_INSTANCE = new KeysList();
    }

    static {
      com.google.protobuf.GeneratedMessageLite.registerDefaultInstance(
        KeysList.class, DEFAULT_INSTANCE);
    }
    public static KeysList getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static volatile com.google.protobuf.Parser<KeysList> PARSER;

    public static com.google.protobuf.Parser<KeysList> parser() {
      return DEFAULT_INSTANCE.getParserForType();
    }
  }


  static {
  }

  // @@protoc_insertion_point(outer_class_scope)
}
