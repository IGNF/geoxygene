package fr.ign.cogit.mapping.generalindex;


/**
 * Attrape les erreurs générées pour un {@link Index}.
 *
 * @author Dr Tsatcha D.
 */
public class IndexException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/** Construit un exception avec <code>null</code> et le detail du message. */
	public IndexException(Index<?> index) {
		super(index.toString());
	}

	/** Construit une exception avec les details spécifiés du message*/
	public IndexException(Index<?> index, String message) {
		super(String.format("%s: %s", index.toString(), message));
	}

	/**
	 * Construit une exception avec les details et causes spécifiés du message 	 * <code>(cause==null ? null : cause.toString())</code> (which typically contains the
	 * class and detail message of <code>cause</code>).
	 */
	public IndexException(Index<?> index, Throwable cause) {
		super(index.toString(), cause);
	}

	/** Construit une nouvelle exception avec les details et causes spécifiés du message. */
	public IndexException(Index<?> index, String message, Throwable cause) {
		super(String.format("%s: %s", index.toString(), message), cause);
	}
}
