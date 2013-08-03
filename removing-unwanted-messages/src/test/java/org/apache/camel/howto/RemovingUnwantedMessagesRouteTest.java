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
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class RemovingUnwantedMessagesRouteTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RemovingUnwantedMessagesRoute();
    }

    @EndpointInject(uri = "mock:valid")
    private MockEndpoint mockValid;

    @EndpointInject(uri = "mock:all")
    private MockEndpoint mockAll;

    @Produce(uri = "direct:start")
    protected ProducerTemplate start;

    @Test
    public void validMessagesArePassed() throws Exception {
        mockValid.expectedBodiesReceived("Test message");
        mockAll.expectedBodiesReceived("Test message");

        start.sendBodyAndHeader("Test message", "userStatus", "valid");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void invalidMessagesAreFilteredOut() throws Exception {
        mockValid.expectedMessageCount(0);
        mockAll.expectedBodiesReceived("Test message");

        start.sendBodyAndHeader("Test message", "userStatus", "invalid");
        assertMockEndpointsSatisfied();
    }

}
