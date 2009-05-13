package fr.ign.cogit.geoxygene.example.hibernate;

import java.util.List;

import fr.ign.cogit.geoxygene.datatools.hibernate.GeodatabaseHibernate;

/**
 * @author Julien Perret
 *
 */
public class TestInheritance {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GeodatabaseHibernate db = new GeodatabaseHibernate();
		
		db.begin();
		ParisBarcelone parisBarcelone = new ParisBarcelone();
		parisBarcelone.setName("ParisBarcelone");
		ParisNewYork parisNewYork = new ParisNewYork();
		parisNewYork.setName("ParisNewYork");
		db.makePersistent(parisBarcelone);
		db.makePersistent(parisNewYork);
		db.commit();
		
		db.begin();
		A320 a320 = new A320();
		a320.setName("A320");
		Boeing747  boeing747 = new Boeing747();
		boeing747.setName("Boeing747");
		db.makePersistent(a320);
		db.makePersistent(boeing747);
		db.commit();
		
		db.begin();
		Boat boat=new Boat();
		boat.setName("machin");
		Ferry ferry = new Ferry();
		ferry.setName("truc");
		db.makePersistent(boat);
		db.makePersistent(ferry);
		db.commit();
		
		AmericaCupClass americaCupClass = new AmericaCupClass();
		americaCupClass.setName("america");
		AmericaCupClass americaCupClass2 = new AmericaCupClass();
		americaCupClass2.setName("america2");
		
		db.begin();
		db.makePersistent(americaCupClass);
		db.makePersistent(americaCupClass2);
		db.commit();

		Pigeon pigeon = new Pigeon();
		pigeon.setName("pigeon");
		Goose goose = new Goose();
		goose.setName("tom the goose");
		Hunter hunter = new Hunter();
		hunter.setName("Claude the hunter");
		hunter.getKills().add(pigeon);
		hunter.getKills().add(goose);
		BirdInterface birdy=new BirdImpl();
		birdy.setName("Birdy");
		db.begin();
		db.makePersistent(pigeon);
		db.makePersistent(goose);
		db.makePersistent(hunter);
		db.makePersistent(BirdProxy.newInstance(birdy,new Class[]{BirdInterface.class}));
		db.commit();
		
		Canine canine = new Canine();
		canine.setName("dog");
		Feline feline = new Feline();
		feline.setName("cat");
		Rodent rodent = new Rodent();
		rodent.setName("Mouse");
		rodent.getPredators().add(canine);
		rodent.getPredators().add(feline);
		db.begin();
		db.makePersistent(canine);
		db.makePersistent(feline);
		db.makePersistent(rodent);
		db.commit();
		
		List<BirdInterface>list=db.loadAll(BirdInterface.class);
		for(BirdInterface b:list) {
			System.out.println(b.getId()+" : "+b.getName());
		}
		List<Mammal>listMammals=db.loadAll(Mammal.class);
		for(Mammal m:listMammals) {
			System.out.println(m.getId()+" : "+m.getName());
			for (Mammal p:m.getPredators()) {
				System.out.println("prédator : "+p.getId()+" : "+p.getName());
			}
		}
		
	}

}
