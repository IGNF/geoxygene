package fr.ign.cogit.mapping.datastructure.management;

public class HexaTreeMultiLevelException  extends RuntimeException {

    
    private static final long serialVersionUID = 1L;

    /** Construit un exception avec <code>null</code> et le detail du message. */
    public HexaTreeMultiLevelException(ManageHexaTreeMultiLevel manager) {
            super(manager.toString());
    }

    /** Construit une exception avec les details spécifiés du message*/
    public HexaTreeMultiLevelException(ManageHexaTreeMultiLevel manager, String message) {
            super(String.format("%s: %s", manager.toString(), message));
    }

    /**
     * Construit une exception avec les details et causes spécifiés du message       * <code>(cause==null ? null : cause.toString())</code> (which typically contains the
     * class and detail message of <code>cause</code>).
     */
    public HexaTreeMultiLevelException(ManageHexaTreeMultiLevel manager, Throwable cause) {
            super(manager.toString(), cause);
    }

    /** Construit une nouvelle exception avec les details et causes spécifiés du message. */
    public HexaTreeMultiLevelException(ManageHexaTreeMultiLevel manager, String message, Throwable cause) {
            super(String.format("%s: %s", manager.toString(), message), cause);
    }
    
}
