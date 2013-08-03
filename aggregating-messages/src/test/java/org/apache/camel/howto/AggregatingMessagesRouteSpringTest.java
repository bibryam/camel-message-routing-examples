/*
 * Copyright (C) Bilgin Ibryam http://www.ofbizian.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.camel.howto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AggregatingMessagesRouteSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/aggregating-messages-context.xml");
    }

    @Produce(uri = "direct:start")
    protected ProducerTemplate start;

    @EndpointInject(uri = "mock:aggregated")
    private MockEndpoint mockAggregated;

    @Test
    public void aggregatesTwoMessagesIntoOne() throws Exception {
        mockAggregated.expectedHeaderReceived("invoiceItemTotal", BigDecimal.valueOf(5));
        mockAggregated.expectedMessageCount(1);

        start.sendBodyAndHeaders(null, toHeadersMap("invoiceId", "invoiceOne", "invoiceItemTotal", BigDecimal.valueOf(2)));
        start.sendBodyAndHeaders(null, toHeadersMap("invoiceId", "invoiceOne", "invoiceItemTotal", BigDecimal.valueOf(3)));
        assertMockEndpointsSatisfied();
    }

    @Test
    public void aggregatesMessagesByCorrelationKey() throws Exception {
        mockAggregated.expectedHeaderValuesReceivedInAnyOrder("invoiceItemTotal", BigDecimal.valueOf(5), BigDecimal.valueOf(4));
        mockAggregated.expectedMessageCount(2);

        start.sendBodyAndHeaders(null, toHeadersMap("invoiceId", "invoiceOne", "invoiceItemTotal", BigDecimal.valueOf(2)));
        start.sendBodyAndHeaders(null, toHeadersMap("invoiceId", "invoiceTwo", "invoiceItemTotal", BigDecimal.valueOf(4)));
        start.sendBodyAndHeaders(null, toHeadersMap("invoiceId", "invoiceOne", "invoiceItemTotal", BigDecimal.valueOf(3)));
        assertMockEndpointsSatisfied();
    }

    private Map<String, Object> toHeadersMap(Object... keyValuePairs) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            result.put(keyValuePairs[i].toString(), keyValuePairs[i + 1]);
        }
        return result;
    }
}
