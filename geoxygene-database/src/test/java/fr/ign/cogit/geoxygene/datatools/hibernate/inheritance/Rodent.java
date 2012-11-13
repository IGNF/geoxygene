package fr.ign.cogit.geoxygene.datatools.hibernate.inheritance;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

/**
 * @author Julien Perret
 * 
 */
@Entity
public class Rodent implements Mammal {

  protected int id;

  @Override
  @Id
  @GeneratedValue
  public int getId() {
    return this.id;
  }

  protected String name;;

  @Override
  public String getName() {
    return this.name;
  }

  protected List<Mammal> predators = new ArrayList<Mammal>();

  @OneToMany
  @JoinTable(name = "Predators", joinColumns = @JoinColumn(name = "prey_id"), inverseJoinColumns = @JoinColumn(name = "predator_id"))
  @Override
  public List<Mammal> getPredators() {
    return this.predators;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void setPredators(List<Mammal> predators) {
    this.predators = predators;
  }
}
