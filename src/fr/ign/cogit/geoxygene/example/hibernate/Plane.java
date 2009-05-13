package fr.ign.cogit.geoxygene.example.hibernate;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
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
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="planetype",
    discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue("Plane")
public class Plane  {
	protected int id;
	@Id @GeneratedValue
	public int getId() {return id;}	
	public void setId(int id) {this.id = id;}

	protected String name;
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
}
