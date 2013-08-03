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
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SimpleChoiceRouteSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/simple-choice-route-context.xml");
    }

    @Produce(uri = "direct:start")
    protected ProducerTemplate start;

    @EndpointInject(uri = "mock:oranges")
    private MockEndpoint mockOranges;

    @EndpointInject(uri = "mock:apples")
    private MockEndpoint mockApples;

    @Test
    public void routesMessagesToSeparateEndpoints() throws Exception {
        mockOranges.expectedBodiesReceived("orange");
        mockApples.expectedBodiesReceived("apple");

        start.sendBody("orange");
        start.sendBody("apple");
        assertMockEndpointsSatisfied();
    }
}
