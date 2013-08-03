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

public class MessageTransformationRouteSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/message-transformation-context.xml");
    }
    @EndpointInject(uri = "mock:result")
    private MockEndpoint mockResult;

    @Produce(uri = "direct:start")
    protected ProducerTemplate start;

    private static final String XML_INPUT =
            "<root>\n" +
            "    <a>1</a>\n" +
            "    <b>2</b>\n" +
            "    <c>\n" +
            "        <ca>3</ca>\n" +
            "        <cb>4</cb>\n" +
            "    </c>\n" +
            "</root>";

    private static final String EXPECTED_JSON =
            "{\"root\":{" +
                    "\"a\":\"1\"," +
                    "\"b\":\"2\"," +
                    "\"c\":{" +
                        "\"ca\":\"3\"," +
                        "\"cb\":\"4\"}" +
                    "}" +
             "}";

    @Test
    public void transformsXmlIntoJson() throws Exception {
        mockResult.expectedBodiesReceived(EXPECTED_JSON);

        start.sendBody(XML_INPUT);
        assertMockEndpointsSatisfied();
    }

}
