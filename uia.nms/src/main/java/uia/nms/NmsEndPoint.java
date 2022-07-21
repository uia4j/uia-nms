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
 * The end point.
 *
 * uri = tcp://address:port
 * uri = failover:(uri1,...,uriN)?initialReconnectDelay=100<br>
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

    private String user;

    private String password;

    /**
     * The constructor.
     *
     * @param service The service name.
     * @param network The network name.
     * @param target The target name.
     * @param port The port number.
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

    public NmsEndPoint(String service, String network, String target, String port, String user, String password) {
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
        this.user = user;
        this.password = password;
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

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
