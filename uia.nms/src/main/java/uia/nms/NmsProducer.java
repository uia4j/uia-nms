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

public interface NmsProducer {
	
	/**
	 * Test if the client and server need to perform time synchronization check.
	 * 
	 * @return Check or not.
	 */
	public boolean isTimeSync();

	/**
	 * Sets if the client and server need to perform time synchronization check.
	 * 
	 * @param timeSync Check or not.
	 */
	public void setTimeSync(boolean timeSync);
	
	/**
	 * Sets time to live of a message.
	 * 
	 * @param timeToLive Time to live.
	 */
	public void setTimeToLive(int timeToLive);

	/**
	 * Returns time to live of a message.
	 * 
	 * @return Time to live.
	 */
	public int getTimeToLive();

	/**
	 * Starts the producer.
	 * 
	 * @throws NmsException
	 */
    public void start() throws NmsException;

    /**
     * stops the producer.
     */
    public void stop();

    /**
     * Sends a new message.
     * 
     * @param subjectName The subject name.
     * @param label The label of the content.
     * @param content The content.
     * @param persistent Persistent or not. Depending on the implementation.
     * @return Success or not.
     */
    public boolean send(String subjectName, String label, String content, boolean persistent);

    /**
     * Sends a new message.
     * 
     * @param subjectName The subject name.
     * @param label The label of the content.
     * @param content The content.
     * @param persistent Persistent or not. Depending on the implementation.
     * @param correlationID The correlation id.
     * @return Success or not.
     */
    public boolean send(String subjectName, String label, String content, boolean persistent, String correlationID);

    /**
     * Sends a new message and receive a response synchronously.
     * 
     * @param subjectName The subject name.
     * @param label The label of the content.
     * @param content The content.
     * @param persistent Persistent or not. Depending on the implementation.
     * @param timeout The timeout.
     * @return The response message.
     */
    public String send(String subjectName, String label, String content, boolean persistent, long timeout);

    /**
     * Sends a new message and receive a response synchronously.
     * 
     * @param subjectName The subject name.
     * @param label The label of the content.
     * @param content The content.
     * @param persistent Persistent or not. Depending on the implementation.
     * @param timeout The timeout.
     * @param replyName The reply name.
     * @param matching The matching helper of the reponse message.
     * @return The response message.
     */
    public String send(String subjectName, String label, String content, boolean persistent, long timeout, String replyName, NmsMatching matching);
}
