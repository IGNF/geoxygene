package fr.ign.cogit.geoxygene.datatools.hibernate.inheritance;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Julien Perret
 * 
 */
@Entity
@DiscriminatorValue("Boeing747")
public class Boeing747 extends Plane {
}
