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
 * End point<br>
 * 
 * @author Kyle K. Lin
 *
 */
public class NmsEndPoint {

    private final String service;

    private final String network;

    private final String target;

    private final String port;

    private final String desc;

    /**
	 * ActiveMQ:<br>
	 * service: [null, failover]<br>
	 * network: null<br>
	 * target: [tcp://addr, tcp://addr1:port1,tcp://addr2:port2...]<br>
	 * port: port [port, null]<br>
     * 
     * @param service Service.
     * @param network Network.
     * @param target Target.
     * @param port Port No.
     */
    public NmsEndPoint(String service, String network, String target, String port) {
        this.service = service;
        this.network = network;
        if (target != null) {
            this.target = target;
        }
        else {
            String host = null;
            try {
                host = java.net.Inet4Address.getLocalHost().getHostName();
            }
            catch (Exception ex) {
                host = "localhost";
            }
            this.target = host;
        }
        this.port = port;
        this.desc = this.target + ":" + this.port;
    }

    public String getService() {
        return this.service;
    }

    public String getNetwork() {
        return this.network;
    }

    public String getTarget() {
        return this.target;
    }

    public String getPort() {
        return this.port;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
