package fr.ign.cogit.geoxygene.example.hibernate;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author Julien Perret
 *
 */
@MappedSuperclass
public abstract class Flight {
	protected int id;

	@Id @GeneratedValue
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	protected String name;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
