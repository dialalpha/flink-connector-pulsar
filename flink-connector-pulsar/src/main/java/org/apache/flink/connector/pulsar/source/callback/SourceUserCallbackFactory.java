package org.apache.flink.connector.pulsar.source.callback;

import java.io.Serializable;

public interface SourceUserCallbackFactory<T> extends Serializable {
    SourceUserCallback<T> create();
}
