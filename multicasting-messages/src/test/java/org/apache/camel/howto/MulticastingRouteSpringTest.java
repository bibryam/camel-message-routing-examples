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

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MulticastingRouteSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/route-first-message-context.xml");
    }

    @Produce(uri = "direct:start")
    protected ProducerTemplate start;

    @EndpointInject(uri = "mock:result")
    private MockEndpoint mockResult;

    @EndpointInject(uri = "mock:a")
    private MockEndpoint mockA;

    @EndpointInject(uri = "mock:b")
    private MockEndpoint mockB;

    @EndpointInject(uri = "mock:c")
    private MockEndpoint mockC;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        mockA.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Thread.sleep(950);
                exchange.getIn().setBody(BigDecimal.valueOf(3));
            }
        });
        mockB.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Thread.sleep(900);
                exchange.getIn().setBody(BigDecimal.valueOf(5));
            }
        });
        mockC.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Thread.sleep(850);
                exchange.getIn().setBody(BigDecimal.valueOf(1));
            }
        });
    }

    @Test
    public void getsTheHighestQuoteProcessedInParallel() throws Exception {
        mockResult.expectedBodiesReceived(5);

        long start = System.currentTimeMillis();
        this.start.sendBody("Get Best Quote");
        mockResult.assertIsSatisfied();
        long endTime = System.currentTimeMillis();

        assertTrue(endTime - start < 2000);

    }
}
