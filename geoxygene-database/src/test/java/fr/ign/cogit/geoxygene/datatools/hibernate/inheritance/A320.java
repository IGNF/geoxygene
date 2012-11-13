package fr.ign.cogit.geoxygene.datatools.hibernate.inheritance;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Julien Perret
 * 
 */
@Entity
@DiscriminatorValue("A320")
public class A320 extends Plane {
}
