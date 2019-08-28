/*
 * Copyright (C) 2017-2019 Dremio Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dremio.datastore;

import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * KVStore created by CoreStoreProvider.
 * TODO : replace preconditions with assert
 */
public class CoreKVStoreImpl<KEY, VALUE> implements CoreKVStore<KEY, VALUE> {

  private final KVStore<byte[], byte[]> rawStore;
  private final Serializer<KEY> keySerializer;
  private final Serializer<VALUE> valueSerializer;
  private final VersionExtractor<VALUE> versionExtractor;

  private final Function<KVStoreTuple<KEY>, byte[]> keyToBytes = KVStoreTuple::getSerializedBytes;
  private final Function<byte[], KVStoreTuple<VALUE>> bytesToValue = input -> newValue().setSerializedBytes(input);

  public CoreKVStoreImpl(KVStore<byte[], byte[]> rawStore,
                     Serializer<KEY> keySerializer,
                     Serializer<VALUE> valueSerializer,
                     VersionExtractor<VALUE> versionExtractor) {
    this.rawStore = rawStore;
    this.keySerializer = keySerializer;
    this.valueSerializer = valueSerializer;
    this.versionExtractor = versionExtractor;
  }

  @Override
  public KVStoreTuple<KEY> newKey() {
    return new KVStoreTuple<>(keySerializer);
  }

  @Override
  public KVStoreTuple<VALUE> newValue() {
    return new KVStoreTuple<>(valueSerializer, versionExtractor);
  }

  @Override
  public KVStoreTuple<VALUE> get(KVStoreTuple<KEY> key) {
    return newValue().setSerializedBytes(rawStore.get(key.getSerializedBytes()));
  }

  @Override
  public List<KVStoreTuple<VALUE>> get(List<KVStoreTuple<KEY>> keys) {
    List<byte[]> convertedKeys = Lists.transform(keys, keyToBytes);
    List<byte[]> convertedValues = rawStore.get(convertedKeys);
    return Lists.transform(convertedValues, bytesToValue);
  }

  @Override
  public void put(KVStoreTuple<KEY> key, KVStoreTuple<VALUE> value) {
    rawStore.put(key.getSerializedBytes(), value.getSerializedBytes());
  }

  @Override
  public boolean validateAndPut(KVStoreTuple<KEY> key, KVStoreTuple<VALUE> newValue, ValueValidator<VALUE> validator) {
    return  ((ByteStore) rawStore).validateAndPut(key.getSerializedBytes(), newValue.getSerializedBytes(),
      (oldValue) -> {
        // run the validation one level up
        KVStoreTuple<VALUE> oldValueTuple = newValue().setSerializedBytes(oldValue);
        return validator.validate(oldValueTuple);
      }
    );
  }

  @Override
  public boolean contains(KVStoreTuple<KEY> key) {
    return rawStore.contains(key.getSerializedBytes());
  }

  @Override
  public void delete(KVStoreTuple<KEY> key) {
    rawStore.delete(key.getSerializedBytes());
  }

  /**
   * Validate the currently stored value before removing from the store
   *
   * @param key the key
   * @param validator a ValueValidator that ensures that the current item stored in the store for the key is valid
   * @return if the validation succeeded or not
   */
  public boolean validateAndDelete(KVStoreTuple<KEY> key, ValueValidator<VALUE> validator) {
    return  ((ByteStore) rawStore).validateAndDelete(key.getSerializedBytes(),
      (oldValue) -> {
        // run the validation one level up
        KVStoreTuple<VALUE> oldValueTuple = newValue().setSerializedBytes(oldValue);
        return validator.validate(oldValueTuple);
      }
    );
  }

  @Override
  public Iterable<Map.Entry<KVStoreTuple<KEY>, KVStoreTuple<VALUE>>> find(FindByRange<KVStoreTuple<KEY>> find) {
    final FindByRange<byte[]> convertedRange = new FindByRange<byte[]>()
      .setStart(find.getStart().getSerializedBytes(), find.isStartInclusive())
      .setEnd(find.getEnd().getSerializedBytes(), find.isEndInclusive());
    final Iterable<Map.Entry<byte[], byte[]>> range = rawStore.find(convertedRange);
    return Iterables.transform(range, CoreKVStoreEntry::new);
  }

  @Override
  public Iterable<Map.Entry<KVStoreTuple<KEY>, KVStoreTuple<VALUE>>> find() {
    return Iterables.transform(rawStore.find(), CoreKVStoreEntry::new);
  }

  @Override
  public void delete(KVStoreTuple<KEY> key, String previousVersion) {
    rawStore.delete(key.getSerializedBytes(), previousVersion);
  }

  final class CoreKVStoreEntry implements Map.Entry<KVStoreTuple<KEY>, KVStoreTuple<VALUE>> {

    private final KVStoreTuple<KEY> key;
    private final KVStoreTuple<VALUE> value;

    public CoreKVStoreEntry(final Map.Entry<byte[], byte[]> input) {
      this.key = newKey().setSerializedBytes(input.getKey());
      this.value = newValue().setSerializedBytes(input.getValue());
    }

    @Override
    public KVStoreTuple<KEY> getKey() {
      return key;
    }

    @Override
    public KVStoreTuple<VALUE> getValue() {
      return value;
    }

    @Override
    public KVStoreTuple<VALUE> setValue(KVStoreTuple<VALUE> value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(getKey(), getValue());
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
      if (!(o instanceof Map.Entry)) {
        return false;
      }

      final Map.Entry<?, ?> other = (Map.Entry<?, ?>) o;
      final KVStoreTuple<KEY> otherKey = (KVStoreTuple<KEY>) other.getKey();
      final KVStoreTuple<VALUE> otherValue = (KVStoreTuple<VALUE>) other.getValue();

      return Objects.equal(getKey(), otherKey) && Objects.equal(getValue(), otherValue);
    }
  }

  @Override
  public KVAdmin getAdmin() {
    return rawStore.getAdmin();
  }

}
