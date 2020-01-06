package de.rose53.marvin.platform;

/**
 *
 * @author rose
 */
public class MessageException extends Exception {

	private static final long serialVersionUID = -961981978882825092L;

	/**
     * Creates a new instance of <code>MessageException</code> without detail message.
     */
    public MessageException() {
    }

    /**
     * Constructs an instance of <code>MessageException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public MessageException(String msg) {
        super(msg);
    }
}
