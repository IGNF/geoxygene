/**
 * 
 */
package fr.ign.cogit.geoxygene.example.hibernate;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Julien Perret
 *
 */
public class BirdImpl implements BirdInterface {
	protected int id;
	@Id @GeneratedValue
	@Override
	public int getId() {return this.id;}	
	@Override
	public void setId(int id) {this.id = id;}

	protected String name;
	@Override
	public String getName() {return this.name;}
	@Override
	public void setName(String name) {this.name = name;}

}
