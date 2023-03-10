package org.apache.flink.connector.pulsar.sink.callback;

import javax.annotation.Nullable;
import org.apache.flink.connector.pulsar.sink.writer.message.PulsarMessage;
import org.apache.pulsar.client.api.MessageId;

public interface SinkUserCallback<T> extends AutoCloseable {
    /**
     * This method is called before the message is sent to the topic.
     * The user can modify the message. By default, the same message will be returned.
     * @param message the message received from the previous operator.
     * @return the message to send to the pulsar topic.
     */
    default PulsarMessage<T> beforeSend(PulsarMessage<T> message) {
        return message;
    }


    /**
     * This method is called after producer has tried to write the message to the topic.
     * @param message the message that was sent to the topic
     * @param messageId the topic MessageId, if the send was successfull
     * @param ex the exception if the send failed.
     */
    void afterSend(PulsarMessage<T> message, @Nullable MessageId messageId, @Nullable Throwable ex);
}
