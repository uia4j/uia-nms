/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uia.nms;

/**
 *
 * @author kyle
 */
public interface SubjectFactory {

    public SubjectPublisher createPub(SubjectProfile profile) throws SubjectException;

    public SubjectSubscriber createSub(SubjectProfile profile) throws SubjectException;
}
