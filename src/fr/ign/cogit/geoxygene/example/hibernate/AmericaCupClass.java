package fr.ign.cogit.geoxygene.example.hibernate;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 * @author Julien Perret
 *
 */
@Entity
@PrimaryKeyJoinColumn(name="BOAT_ID")
@AttributeOverride(name="id", column = @Column(name="BOAT_ID"))
public class AmericaCupClass extends Boat {
	
}
