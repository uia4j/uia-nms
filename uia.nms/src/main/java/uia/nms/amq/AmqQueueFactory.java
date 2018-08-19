/*******************************************************************************
 * Copyright 2018 UIA
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package uia.nms.amq;

import org.apache.activemq.ActiveMQConnectionFactory;

import uia.nms.NmsConsumer;
import uia.nms.NmsEndPoint;
import uia.nms.NmsException;
import uia.nms.NmsFactory;
import uia.nms.NmsProducer;

public class AmqQueueFactory extends NmsFactory {

    @Override
    public NmsProducer createProducer(NmsEndPoint endPoint) throws NmsException {
        try {
            return new AmqQueueProducer(connectionFactory(endPoint));
        }
        catch (Exception ex) {
            throw new NmsException("createProducer failed", ex);
        }
    }

    @Override
    public NmsConsumer createConsumer(NmsEndPoint endPoint) throws NmsException {
        try {
            return new AmqQueueConsumer(connectionFactory(endPoint));
        }
        catch (Exception ex) {
            throw new NmsException("createConsumer failed", ex);
        }
    }

    private ActiveMQConnectionFactory connectionFactory(NmsEndPoint endPoint) {
        if ("failover".equals(endPoint.getService())) {
            return new ActiveMQConnectionFactory("failover:" + endPoint.getTarget());
        }
        else {
            return new ActiveMQConnectionFactory(endPoint.getTarget() + ":" + endPoint.getPort());
        }
    }
}
