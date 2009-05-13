package fr.ign.cogit.geoxygene.example.hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * @author Julien Perret
 *
 */
@Entity
public class Hunter {
	protected int id;
	@Id @GeneratedValue
	public int getId() {return id;}	
	public void setId(int id) {this.id = id;}

	protected String name;
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	
	protected List<Bird> kills = new ArrayList<Bird>();
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public List<Bird> getKills() {return this.kills;}
	public void setKills(List<Bird> kills) {this.kills = kills;}
}
