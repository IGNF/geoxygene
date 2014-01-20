package fr.ign.cogit.geoxygene.user.exercice;

import fr.ign.cogit.geoxygene.feature.FT_Feature;

public class Salle extends FT_Feature {
  
  protected String nom;
  public String getNom() {return nom;}
  public void setNom(String nom) {this.nom = nom;}

  protected int numero;
  public int getNumero() {return numero;}

  public void setNumero(int numero) {this.numero = numero;}

  protected double superficie;
  public double getSuperficie() {return superficie;}
  public void setSuperficie(double superficie) {this.superficie = superficie;}

}
