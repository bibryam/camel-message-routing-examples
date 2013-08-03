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

public class SplittingMessageRouteSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/splitting-message-context.xml");
    }

    @EndpointInject(uri = "mock:result")
    private MockEndpoint mockResult;

    @EndpointInject(uri = "mock:products")
    private MockEndpoint mockProducts;

    @Produce(uri = "direct:start")
    protected ProducerTemplate start;

    private static final String XML_INPUT =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<invoice>\n" +
                    "    <item>\n" +
                    "        <product name=\"widget\" quantity=\"2\"/>\n" +
                    "    </item>\n" +
                    "    <item>\n" +
                    "        <product name=\"gadget\" quantity=\"1\"/>\n" +
                    "    </item>\n" +
                    "</invoice>";

    @Test
    public void splitsInputMessageIntoTwoMessages() throws Exception {
        mockResult.expectedBodiesReceived(XML_INPUT);
        mockProducts.expectedMessageCount(2);
        mockProducts.expectedBodiesReceived("<product name=\"widget\" quantity=\"2\"/>", "<product name=\"gadget\" quantity=\"1\"/>");

        start.sendBody(XML_INPUT);
        assertMockEndpointsSatisfied();
    }

}
