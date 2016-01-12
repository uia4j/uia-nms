/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uia.nms.amq;

import uia.nms.SubjectException;
import uia.nms.SubjectFactory;
import uia.nms.SubjectProfile;
import uia.nms.SubjectPublisher;
import uia.nms.SubjectSubscriber;

/**
 *
 * @author FW
 */
public class AmqQueueFactory implements SubjectFactory {

    public AmqQueueFactory() {

    }

    @Override
    public SubjectPublisher createPub(SubjectProfile profile) throws SubjectException {
        try {
            return new AmqQueuePublisher(profile);
        }
        catch (Exception ex) {
            throw new SubjectException("createPub failure", ex);
        }
    }

    @Override
    public SubjectSubscriber createSub(SubjectProfile profile) throws SubjectException {
        try {
            return new AmqQueueSubscriber(profile);
        }
        catch (Exception ex) {
            throw new SubjectException("createSub failure", ex);
        }
    }
}
