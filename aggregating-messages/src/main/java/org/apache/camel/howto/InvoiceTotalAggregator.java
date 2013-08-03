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

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class InvoiceTotalAggregator implements AggregationStrategy {

    public static final String ITEM_TOTAL_HEADER_NAME = "invoiceItemTotal";

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }
        BigDecimal currentTotal = oldExchange.getIn().getHeader(ITEM_TOTAL_HEADER_NAME, BigDecimal.class);
        BigDecimal itemTotal = newExchange.getIn().getHeader(ITEM_TOTAL_HEADER_NAME, BigDecimal.class);

        oldExchange.getIn().setHeader(ITEM_TOTAL_HEADER_NAME, currentTotal.add(itemTotal));
        return oldExchange;
    }
}
