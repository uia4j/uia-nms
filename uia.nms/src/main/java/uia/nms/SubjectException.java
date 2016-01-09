/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uia.nms;

/**
 *
 * @author kyle
 */
public class SubjectException extends Exception {


	private static final long serialVersionUID = 1518526814993755911L;

	public SubjectException(String message) {
        super(message);
    }

    public SubjectException(String message, Exception ex) {
        super(message, ex);
    }
}
