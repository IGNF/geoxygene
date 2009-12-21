package fr.ign.cogit.geoxygene.example.hibernate;

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
public abstract class Bird {
	protected int id;
	@Id @GeneratedValue
	public int getId() {return this.id;}	
	public void setId(int id) {this.id = id;}

	protected String name;
	public String getName() {return this.name;}
	public void setName(String name) {this.name = name;}
}
