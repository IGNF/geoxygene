package fr.ign.cogit.mapping.storage.database.extractor.sql.postgres;

 /*
  * @author Dr Tsatcha D.
  */
public class ExtractorException extends RuntimeException {
    private static final long serialVersionUID = 1L;


  /** Construit une exception avec les details spécifiés du message*/
    public ExtractorException(String message) {
            super(message);
    }

   
}