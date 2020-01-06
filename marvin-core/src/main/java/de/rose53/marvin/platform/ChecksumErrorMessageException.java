package de.rose53.marvin.platform;

/**
 *
 * @author rose
 */
public class ChecksumErrorMessageException extends MessageException {

	private static final long serialVersionUID = -5723765259288882609L;

	/**
     * Creates a new instance of <code>ChecksumErrorMessageException</code> without detail message.
     */
    public ChecksumErrorMessageException() {
    }

    /**
     * Constructs an instance of <code>ChecksumErrorMessageException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ChecksumErrorMessageException(String msg) {
        super(msg);
    }
}
