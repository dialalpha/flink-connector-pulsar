package org.apache.flink.connector.pulsar.source.callback;

public interface SourceUserCallbackFactory<T> {
    SourceUserCallback<T> create();
}
