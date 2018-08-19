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
package uia.nms;

public interface NmsConsumer {

    public void setTransportListener(NmsTransportListener transportListener);

    public NmsTransportListener getTransportListener();

    public void addLabel(String label);

    public void addMessageListener(NmsMessageListener l);

    public void removeMessageListener(NmsMessageListener l);

    public void start(String subjectName) throws NmsException;

    public void stop();

    public NmsProducer createProducer();
}
