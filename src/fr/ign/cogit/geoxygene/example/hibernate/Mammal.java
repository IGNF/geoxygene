/**
 * 
 */
package fr.ign.cogit.geoxygene.example.hibernate;

import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

/**
 * @author Julien Perret
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="mammalType",
    discriminatorType=DiscriminatorType.STRING
)
public interface Mammal {
	@Id @GeneratedValue
	public int getId();	
	public void setId(int id);

	public String getName();
	public void setName(String name);
	
	@OneToMany
    @JoinTable(
            name="Predators",
            joinColumns = @JoinColumn( name="prey_id"),
            inverseJoinColumns = @JoinColumn( name="predator_id")
    )
	public List<Mammal> getPredators();
	public void setPredators(List<Mammal> predators);
}
