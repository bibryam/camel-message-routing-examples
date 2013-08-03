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
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

public class RoutingToDifferentDestinationsRouteTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RoutingToDifferentDestinationsRoute();
    }

    @Produce(uri = "direct:start")
    protected ProducerTemplate start;

    @EndpointInject(uri = "mock:file:widget")
    private MockEndpoint mockWidget;

    @EndpointInject(uri = "mock:file:gadget")
    private MockEndpoint mockGadget;

    @EndpointInject(uri = "mock:log:org.apache.camel.howto")
    private MockEndpoint mockOther;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:start");
                mockEndpointsAndSkip("*");
            }
        });
    }

    @Test
    public void sendsAGadgetMessage() throws Exception {
        mockWidget.expectedBodiesReceived("Test message");
        mockGadget.expectedMessageCount(0);
        mockOther.expectedMessageCount(0);

        start.sendBodyAndHeader("Test message", "CamelFileName", "widget.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void sendsAWidgetAndAGadgetMessages() throws Exception {
        mockWidget.expectedBodiesReceived("widget message");
        mockGadget.expectedBodiesReceived("gadget message");
        mockOther.expectedMessageCount(0);

        start.sendBodyAndHeader("widget message", "CamelFileName", "widget.txt");
        start.sendBodyAndHeader("gadget message", "CamelFileName", "gadget.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void sendsAnotherMessage() throws Exception {
        mockWidget.expectedMessageCount(0);
        mockGadget.expectedMessageCount(0);
        mockOther.expectedMessageCount(1);

        start.sendBodyAndHeader("Test message", "CamelFileName", "other.txt");
        assertMockEndpointsSatisfied();
    }
}





