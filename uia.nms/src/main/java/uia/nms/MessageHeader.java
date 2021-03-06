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
 * The message header.
 * 
 * @author Kan
 *
 */
public class MessageHeader {

	/**
	 * The subject name of the request.
	 */
    public final String requestSubject;

	/**
	 * The subject name of the response.
	 */
    public final String responseSubject;

	/**
	 * The correlation id.
	 */
    public final String correlationID;

    /**
     * The constructor.
     * 
     * @param requestSubject The subject name of the request.
     * @param responseSubject The subject name of the response.
     * @param correlationID The correlation id.
     */
    public MessageHeader(String requestSubject, String responseSubject, String correlationID) {
        this.requestSubject = requestSubject;
        this.responseSubject = responseSubject;
        this.correlationID = correlationID;
    }

    @Override
    public String toString() {
        return this.correlationID;
    }
}
