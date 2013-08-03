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

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

public class DeadLetterChannelRouteTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new DeadLetterChannelRoute();
    }

    @Produce(uri = "direct:start")
    protected ProducerTemplate start;

    @EndpointInject(uri = "mock:result")
    private MockEndpoint mockResult;

    @EndpointInject(uri = "mock:error")
    private MockEndpoint mockError;

    private boolean failedOnce = false;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        mockResult.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                String body = exchange.getIn().getBody(String.class);
                if (body.startsWith("FAIL ALWAYS")) {
                    throw new Exception("Error publishing message");
                }

                if (body.startsWith("FAIL ONCE") && !failedOnce) {
                    failedOnce = true;
                    throw new Exception("Error publishing message");
                }
            }
        });
    }

    @Test
    public void failingMessageIsRetied4TimesWithExponentialBackOffBeforeMovingToDLQ() throws Exception {
        mockResult.expectedMessageCount(4);
        mockError.expectedMessageCount(1);
        mockError.expectedBodiesReceived("FAIL ALWAYS");

        long startTime = System.currentTimeMillis();
        start.sendBody("FAIL ALWAYS");
        assertMockEndpointsSatisfied();

        long endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime > 7000);
    }

    @Test
    public void failingMessageIsRetiedOnceBeforeSucceeding() throws Exception {
        mockResult.expectedMessageCount(2);
        mockError.expectedMessageCount(0);

        start.sendBody("FAIL ONCE");
        assertMockEndpointsSatisfied();
    }
}
