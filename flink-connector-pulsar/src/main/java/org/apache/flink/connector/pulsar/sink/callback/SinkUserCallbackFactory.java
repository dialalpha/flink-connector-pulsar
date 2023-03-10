package org.apache.flink.connector.pulsar.sink.callback;

import java.io.Serializable;

public interface SinkUserCallbackFactory extends Serializable {
    SinkUserCallback create();
}
