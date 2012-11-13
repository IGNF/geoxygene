package fr.ign.cogit.geoxygene.datatools.hibernate.inheritance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * @author Julien Perret
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public interface BirdInterface {
  @Id
  @GeneratedValue
  public int getId();

  public void setId(int id);

  public String getName();

  public void setName(String name);
}
