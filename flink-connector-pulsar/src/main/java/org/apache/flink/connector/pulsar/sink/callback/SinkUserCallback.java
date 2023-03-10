package org.apache.flink.connector.pulsar.sink.callback;

import javax.annotation.Nullable;
import org.apache.flink.connector.pulsar.sink.writer.message.PulsarMessage;
import org.apache.pulsar.client.api.MessageId;

public interface SinkUserCallback<IN> extends AutoCloseable {
    /**
     * This method is called before the message is sent to the topic.
     * The user can modify the message. By default, the same message will be returned.
     * @param element the element received from the previous operator.
     * @param message the message wrapper with the element already serialized.
     * @return the message to send to the pulsar topic.
     */
    default PulsarMessage<?> beforeSend(IN element, PulsarMessage<?> message) {
        return message;
    }


    /**
     * This method is called after producer has tried to write the message to the topic.
     * @param message the message that was sent to the topic
     * @param messageId the topic MessageId, if the send was successfull
     * @param ex the exception if the send failed.
     */
    void afterSend(IN element, PulsarMessage<?> message, @Nullable MessageId messageId, @Nullable Throwable ex);
}
