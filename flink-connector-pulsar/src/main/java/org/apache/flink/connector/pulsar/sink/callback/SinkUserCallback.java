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
     * @param topic the pulsar topic or partition that the message will be routed to.
     * @return the message to send to the pulsar topic, potentially a new message.
     */
    default PulsarMessage<?> beforeSend(IN element, PulsarMessage<?> message, String topic) {
        return message;
    }

    /**
     * This method is called after producer has tried to write the message to the topic.
     * @param element the element received from the previous operator.
     * @param message the message that was sent to the topic.
     * @param messageId the topic MessageId, if the send was successfull.
     */
    void onSendSucceeded(IN element, PulsarMessage<?> message, String topic, MessageId messageId);

    /**
     * This method is called after producer has tried to write the message to the topic.
     * @param element the element received from the previous operator.
     * @param message the message that was sent to the topic.
     * @param topic the topic or partition that the message was sent to.
     * @param ex the exception.
     */
    void onSendFailed(IN element, PulsarMessage<?> message, String topic, @Nullable Throwable ex);
}
