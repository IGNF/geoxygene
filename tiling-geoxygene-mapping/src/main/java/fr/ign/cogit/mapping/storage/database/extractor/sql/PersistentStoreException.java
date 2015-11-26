package fr.ign.cogit.mapping.storage.database.extractor.sql;
// Cette classe est fondée sur le projet Parliament...
/** @author Dr Tsatcha D. */
public class PersistentStoreException extends Exception {

	private static final long serialVersionUID = 1887660080864194263L;

	public PersistentStoreException(String tableName) {
		super(String.format("Table: %s", tableName));
	}

	public PersistentStoreException(String tableName, String message) {
		super(String.format("Table: %s, %s", tableName, message));
		// TODO Auto-generated constructor stub
	}

	public PersistentStoreException(String tableName, Throwable cause) {
		super(String.format("Table: %s", tableName), cause);
		// TODO Auto-generated constructor stub
	}

	public PersistentStoreException(String tableName, String message, Throwable cause) {
		super(String.format("Table: %s, %s", tableName, message), cause);
		// TODO Auto-generated constructor stub
	}
}
