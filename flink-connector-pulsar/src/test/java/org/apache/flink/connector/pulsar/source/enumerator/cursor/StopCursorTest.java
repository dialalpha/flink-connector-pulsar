/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.connector.pulsar.source.enumerator.cursor;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.base.source.reader.RecordsWithSplitIds;
import org.apache.flink.connector.base.source.reader.splitreader.SplitsAddition;
import org.apache.flink.connector.pulsar.common.crypto.PulsarCrypto;
import org.apache.flink.connector.pulsar.source.config.SourceConfiguration;
import org.apache.flink.connector.pulsar.source.enumerator.topic.TopicPartition;
import org.apache.flink.connector.pulsar.source.reader.PulsarPartitionSplitReader;
import org.apache.flink.connector.pulsar.source.split.PulsarPartitionSplit;
import org.apache.flink.connector.pulsar.testutils.PulsarTestSuiteBase;

import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Schema;
import org.junit.jupiter.api.Test;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.flink.connector.pulsar.source.PulsarSourceOptions.PULSAR_ENABLE_AUTO_ACKNOWLEDGE_MESSAGE;
import static org.apache.flink.connector.pulsar.source.PulsarSourceOptions.PULSAR_FETCH_ONE_MESSAGE_TIME;
import static org.apache.flink.connector.pulsar.source.PulsarSourceOptions.PULSAR_MAX_FETCH_RECORDS;
import static org.apache.flink.connector.pulsar.source.PulsarSourceOptions.PULSAR_MAX_FETCH_TIME;
import static org.apache.flink.connector.pulsar.source.PulsarSourceOptions.PULSAR_SUBSCRIPTION_NAME;
import static org.apache.flink.connector.pulsar.source.enumerator.topic.TopicNameUtils.topicNameWithPartition;
import static org.apache.flink.metrics.groups.UnregisteredMetricsGroup.createSourceReaderMetricGroup;
import static org.assertj.core.api.Assertions.assertThat;

/** Test different implementation of StopCursor. */
class StopCursorTest extends PulsarTestSuiteBase {

    @Test
    void publishTimeStopCursor() throws Exception {
        String topicName = "stop-cursor-" + randomAlphanumeric(5);
        operator().createTopic(topicName, 2);

        PulsarPartitionSplitReader splitReader =
                new PulsarPartitionSplitReader(
                        operator().client(),
                        operator().admin(),
                        sourceConfig(),
                        Schema.BYTES,
                        PulsarCrypto.disabled(),
                        createSourceReaderMetricGroup());
        // send the first message and set the stopCursor to filter any late stopCursor
        operator()
                .sendMessage(
                        topicNameWithPartition(topicName, 0),
                        Schema.STRING,
                        randomAlphanumeric(10));
        long currentTimeStamp = System.currentTimeMillis();
        TopicPartition partition = new TopicPartition(topicName, 0);
        PulsarPartitionSplit split =
                new PulsarPartitionSplit(
                        partition,
                        StopCursor.atPublishTime(currentTimeStamp),
                        MessageId.earliest,
                        null);
        SplitsAddition<PulsarPartitionSplit> addition = new SplitsAddition<>(singletonList(split));
        splitReader.handleSplitsChanges(addition);
        // first fetch should have result
        RecordsWithSplitIds<Message<byte[]>> firstResult = splitReader.fetch();
        assertThat(firstResult.nextSplit()).isNotNull();
        assertThat(firstResult.nextRecordFromSplit()).isNotNull();
        assertThat(firstResult.finishedSplits()).isEmpty();
        // send the second message and expect it will not be received
        operator()
                .sendMessage(
                        topicNameWithPartition(topicName, 0),
                        Schema.STRING,
                        randomAlphanumeric(10));
        RecordsWithSplitIds<Message<byte[]>> secondResult = splitReader.fetch();
        assertThat(secondResult.nextSplit()).isNull();
        assertThat(secondResult.finishedSplits()).isNotEmpty();
    }

    private SourceConfiguration sourceConfig() {
        Configuration config = operator().config();
        config.set(PULSAR_MAX_FETCH_RECORDS, 1);
        config.set(PULSAR_FETCH_ONE_MESSAGE_TIME, 2000);
        config.set(PULSAR_MAX_FETCH_TIME, 3000L);
        config.set(PULSAR_SUBSCRIPTION_NAME, randomAlphabetic(10));
        config.set(PULSAR_ENABLE_AUTO_ACKNOWLEDGE_MESSAGE, true);
        return new SourceConfiguration(config);
    }
}
