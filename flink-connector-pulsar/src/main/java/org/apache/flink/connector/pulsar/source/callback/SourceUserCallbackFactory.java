package org.apache.flink.connector.pulsar.source.callback;

import java.io.Serializable;

 /** A serializable factory for SourceUserCallback.
 * @param <T> the outupt type of the source*/
public interface SourceUserCallbackFactory<T> extends Serializable {
    SourceUserCallback<T> create();
}
