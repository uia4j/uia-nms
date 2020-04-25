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

/**
 * The consumer.
 * 
 * @author Kan
 *
 */
public interface NmsConsumer {

	/**
	 * Sets a transport listener.
	 * 
	 * @param transportListener A transport listener.
	 */
    public void setTransportListener(NmsTransportListener transportListener);

    /**
     * Returns the transport listener.
     * 
     * @return The transport listener.
     */
    public NmsTransportListener getTransportListener();

    /**
     * Adds a label to be handled.
     * 
     * @param label The label.
     */
    public void addLabel(String label);

    /**
     * Adds a message listener.
     * 
     * @param l A message listener.
     */
    public void addMessageListener(NmsMessageListener l);

    /**
     * Removes a message listener.
     * @param l
     */
    public void removeMessageListener(NmsMessageListener l);

    /**
     * Start to handle messages on specific subject.
     * 
     * @param subjectName The subject name.
     * @throws NmsException Failed to start.
     */
    public void start(String subjectName) throws NmsException;

    /**
     * Stop to handle messages.
     */
    public void stop();

    /**
     * Create a new producer.
     * @return
     */
    public NmsProducer createProducer();
}
