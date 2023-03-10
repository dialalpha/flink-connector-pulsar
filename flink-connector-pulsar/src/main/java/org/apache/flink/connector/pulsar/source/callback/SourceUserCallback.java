package org.apache.flink.connector.pulsar.source.callback;

import org.apache.pulsar.client.api.Message;

/**
 * An optional interface that users can plug into PulsarSource.
 *
 * @param <T> The output type of Source
 */
public interface SourceUserCallback<T> extends AutoCloseable {
    /**
     * This method is called before the message is passed forward to the Collector The user can
     * modify the message here before it is passed along.
     *
     * <p>By default, this will return the unmodified message.
     *
     * @param rawMessage the raw message from the pulsar topic
     * @return Either the same message or a modified message
     */
    default Message<byte[]> beforeCollect(Message<byte[]> rawMessage) {
        return rawMessage;
    }

    /**
     * This method is called after the message is handed off to the Collector, with the raw message
     * from pulsar, as well as the deserialized value.
     *
     * <p>Modifications to the message will not carry forward.
     *
     * @param rawMessage the raw message from the pulsar topic
     * @param deserializedElement the deserialized message body
     */
    void afterCollect(Message<byte[]> rawMessage, T deserializedElement);
}
