package fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util;

import java.util.Date;

/**
 * Simple classe héritant de la classe Date de l'API
 * Java et qui applique des méthodes d'anonymisation
 * après la création de l'instance.
 * Rend aussi les champs d'origne de Date immutables
 * afin que des informations supplémentaires ne soient 
 * pas rajoutées après anonymisation. 
 * 
 * @author Matthieu Dufait
 */
@SuppressWarnings("deprecation")
public final class AnonymizedDate extends Date {
  private static final long serialVersionUID = -8046479588482251865L;
  
  // permet de différencier les différentes façons d'anonymiser les dates
  public static final int SIMPLE_GENERALISATION = 0;
  public static final int RANDOM_PERTURBATION = 1;
  public static final int ROUNDED_GENERALISATION = 2;
  public static final int SIMPLE_GENERALISATION_KEEP_DAY = 3;
  public static final int RANDOM_PERTURBATION_KEEP_DAY = 4;
  public static final int ROUNDED_GENERALISATION_KEEP_DAY = 5;
  
  private static final int MIN_MODE_VALUE = 0;
  private static final int MAX_MODE_VALUE = 5;
  
  // constantes utilisées dans les méthodes anonymisations
  /**
   * Généralise les heures sur des périodes stockées 
   * dans cette constante.
   */
  private static final int GENERALISATION_HOUR_FRAME = 3;
  /**
   * Perturbe les heures sur des périodes stockées 
   * dans cette constante.
   */
  private static final int PERTURBATION_HOUR_FRAME = 6;
  
  /**
   * Pourcentage de la durée du changeset qui indique
   * que si seulement PERCENT_BEFORE_NEXT_FRAME du temps du 
   * changeset se trouve dans la période de temps d'origine, 
   * on change de période
   */
  private static final float PERCENT_BEFORE_NEXT_FRAME = 0.25f;
  
  /**
   * Pourcentage de la durée de la période temporelle 
   * qui indique qu'un changeset peut être assigné à 
   * la période ultérieure uniquement s'il débute dans 
   * les PERCENT_END_FRAME derniers pourcents de la période
   */
  private static final float PERCENT_END_FRAME = 0.15f;
  
  private static int anonymizationMode = AnonymizedDate.ROUNDED_GENERALISATION_KEEP_DAY;

  /*
   * Re définition des constructeurs de la classe mère
   */
  public AnonymizedDate(long date) {
    super(date);
    this.anonymize();
  }

  public AnonymizedDate(String s) {
    super(s);
    this.anonymize();
  }
  
  public AnonymizedDate(long date, Date duration) {
    super(date);
    this.anonymize(duration);
  }

  public AnonymizedDate(String s, Date duration) {
    super(s);
    this.anonymize(duration);
  }
  
  private void anonymize()
  {
    this.anonymize(null);
  }

  /**
   * Applique les méthodes d'anonymisation
   * à la date courante
   */
  private void anonymize(Date duration)
  {
    switch (AnonymizedDate.anonymizationMode)
    {
      case AnonymizedDate.SIMPLE_GENERALISATION:
        caseSimpleGeneralisation(true);
      break;
      case AnonymizedDate.RANDOM_PERTURBATION:
        randomPerturbation(true);
      break;
      case AnonymizedDate.ROUNDED_GENERALISATION:
        roundingDate(duration);
        caseSimpleGeneralisation(true);
      break;
      case AnonymizedDate.SIMPLE_GENERALISATION_KEEP_DAY:
        caseSimpleGeneralisation(false);
      break;
      case AnonymizedDate.RANDOM_PERTURBATION_KEEP_DAY:
        randomPerturbation(false);
      break;
      case AnonymizedDate.ROUNDED_GENERALISATION_KEEP_DAY:
        roundingDate(duration);
        caseSimpleGeneralisation(false);
      break;
      default: 
        throw new IllegalStateException("Anonymization process "
            + "with incorrect anonymization "
            + "mode value : "+anonymizationMode);
    }
  }
  
  private void randomPerturbation(boolean anonymizeDays) {
    // perturbe les heures sur une période donnée
    // autour de l'heure d'origine
    super.setHours(
        super.getHours() + (int) (Math.random()*PERTURBATION_HOUR_FRAME-
            (PERTURBATION_HOUR_FRAME/2.0)) 
        );
    // tire aléatoirement secondes et minutes
    super.setMinutes((int) (Math.random()*60));
    super.setSeconds((int) (Math.random()*60));
    // places le numéro de jours sur le premier jours du mois 
    // qui est le même jour de la semaine
    if(anonymizeDays)
      super.setDate(super.getDate()%7);
  }
  
  /**
   * Arrondi de certaines dates vers la période suivante,
   * place aux même valeurs relatives à la période 
   * (départ XX:59:59 -> XX+période:59:59)
   * @param duration
   */
  private void roundingDate(Date duration) {
    // si une durée est fournie on vérifie 
    // si le changeset n'existe pas principalement 
    // pendant une période ultérieure
    if(duration != null) {
      // calcule le temps avant la fin de la période
      long secondsBeforeEnd =  
          3600 * (AnonymizedDate.GENERALISATION_HOUR_FRAME 
          - super.getHours()%AnonymizedDate.GENERALISATION_HOUR_FRAME) -
          this.getMinutes()*60 -
          this.getSeconds();
      
      // récupère la durée du changeset
      // que l'on limite au maximum à la durée 
      // d'une période de temps
      long secondsTotalDuration = duration.getTime()/1000;
      
      if(secondsBeforeEnd < PERCENT_END_FRAME *
          3600 * GENERALISATION_HOUR_FRAME
          && 
          secondsBeforeEnd < PERCENT_BEFORE_NEXT_FRAME *
          secondsTotalDuration)
        // on passe donc à la période suivante
        super.setHours(
            super.getHours()-super.getHours()+AnonymizedDate.GENERALISATION_HOUR_FRAME);      
    }
  }
  
  /**
   * cas de la simple généralisation des dates
   */
  private void caseSimpleGeneralisation(boolean anonymizeDays)  {
    // place les heures au début de leur périodes temporelles
    super.setHours(
        super.getHours()-super.getHours()%AnonymizedDate.GENERALISATION_HOUR_FRAME);
    
    // annule les valeurs de minutes et secondes
    super.setMinutes(0);
    super.setSeconds(0);
    // place le numéro de jours sur le premier jours du mois 
    // qui est le même jour de la semaine
    if(anonymizeDays)
      super.setDate(super.getDate()%7);
  }
  
  /**
   * Retourne la valeur du mode d'anonymisation
   * @return AnonymizedDate.anonymizationMode
   */
  public static int getAnonymizationMode() {
    return AnonymizedDate.anonymizationMode;
  }


  /**
   * Modifie la valeur du mode d'anonymisation
   * @return AnonymizedDate.anonymizationMode
   */
  public static void setAnonymizationMode(int anonymizationMode) {
    if(anonymizationMode < MIN_MODE_VALUE || 
        anonymizationMode > MAX_MODE_VALUE)
      throw new IllegalArgumentException("anonymizationMode value "
          +anonymizationMode+ " incorrect, should be between "
          +MIN_MODE_VALUE+" and "+MAX_MODE_VALUE);
    AnonymizedDate.anonymizationMode = anonymizationMode;
  }
  
  @Override
  /**
   * Class is immutable so this method is 
   * unsupported and throws an exception
   * @throw UnsupportedOperationException
   */
  public void setDate(int date) {
    throw new UnsupportedOperationException();
  }

  @Override
  /**
   * Class is immutable so this method is 
   * unsupported and throws an exception
   * @throw UnsupportedOperationException
   */
  public void setHours(int hours) {
    throw new UnsupportedOperationException();
  }

  @Override
  /**
   * Class is immutable so this method is 
   * unsupported and throws an exception
   * @throw UnsupportedOperationException
   */
  public void setMinutes(int minutes) {
    throw new UnsupportedOperationException();
  }

  @Override
  /**
   * Class is immutable so this method is 
   * unsupported and throws an exception
   * @throw UnsupportedOperationException
   */
  public void setMonth(int month) {
    throw new UnsupportedOperationException();
  }

  @Override
  /**
   * Class is immutable so this method is 
   * unsupported and throws an exception
   * @throw UnsupportedOperationException
   */
  public void setSeconds(int seconds) {
    throw new UnsupportedOperationException();
  }

  @Override
  /**
   * Class is immutable so this method is 
   * unsupported and throws an exception
   * @throw UnsupportedOperationException
   */
  public void setTime(long time) {
    throw new UnsupportedOperationException();
  }

  @Override
  /**
   * Class is immutable so this method is 
   * unsupported and throws an exception
   * @throw UnsupportedOperationException
   */
  public void setYear(int year) {
    throw new UnsupportedOperationException();
  }
}

