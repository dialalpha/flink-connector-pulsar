package org.apache.flink.connector.pulsar.sink.callback;

import java.io.Serializable;

public interface SinkUserCallbackFactory<IN> extends Serializable {
    SinkUserCallback<IN> create();
}
