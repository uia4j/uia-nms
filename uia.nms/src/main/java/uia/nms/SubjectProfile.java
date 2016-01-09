/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uia.nms;


/**
 *
 * @author kyle
 */
public class SubjectProfile {

    private final String service;

    private final String network;

    private final String target;

    private final String port;

    private final String desc;

    public SubjectProfile(String service, String network, String target, String port) {
        this.service = service;
        this.network = network;
        if(target != null) {
            this.target = target;
        }
        else {
            String host = null;
            try {
                host = java.net.Inet4Address.getLocalHost().getHostName();
            }
            catch(Exception ex) {
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
