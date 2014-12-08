/**
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 * 
 */
package fr.ign.cogit.geoxygene.matching.beeri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.util.string.TraitementChainesDeCaracteres;

public class Matrice {

  public double [][]valeurs;
  public int nbLignes ;
  public int nbColonnes ;
  
  //définition des constructeurs
  public Matrice() { }
  public Matrice(double[][] matriceATraiter) {
      this.valeurs = matriceATraiter;
      this.nbLignes = matriceATraiter.length;
      this.nbColonnes = matriceATraiter[0].length;
  }
  
  // Matrice pour deux populations d'objets géo
  public Matrice(IPopulation<IFeature> popRef, IPopulation<IFeature> popComp) {
      this.nbLignes = popRef.size()+1 ;
      this.nbColonnes = popComp.size()+1;
      this.valeurs = new double[this.nbLignes][this.nbColonnes];
  }
  
  // Matrice pour deux strings
  public Matrice(String ch1, String ch2) {
      this.nbLignes = TraitementChainesDeCaracteres.compteToken(ch1);
      this.nbColonnes = TraitementChainesDeCaracteres.compteToken(ch2);
      this.valeurs = new double[this.nbLignes][this.nbColonnes];
  }
  
  public Matrice(Set<?> ensemble) {
      this.nbLignes = 2;//ligne 1 = influence; ligne 2 = passivité;
      this.nbColonnes = ensemble.size();
      this.valeurs = new double[this.nbLignes][this.nbColonnes];
  }
  
  // Méthode qui calcule la somme des éléments d'une colonne et qui renvoie une liste
  public List<Double> sommeColonneMatriceList() {
      double sommeC;
      List<Double> listSommes =new ArrayList<Double>();
      for ( int j = 0 ; j < this.nbColonnes; j++ ){
          sommeC = 0;
          for (int i=0; i < this.nbLignes;i++) {
              sommeC = sommeC + this.valeurs[i][j];
          }
          listSommes.add(sommeC);
      }
      
      return listSommes;
  }
  
  // Méthode qui calcule la somme des éléments d'une ligne et qui renvoie une liste
  private List<Double> sommeLigneMatriceList() {
      double sommeL = 0;
      List<Double> listSommes =new ArrayList<Double>();
      for ( int i = 0 ; i < this.nbLignes; i++ ){
          sommeL = 0;
          for (int j=0; j < this.nbColonnes;j++) {
              sommeL = sommeL + this.valeurs[i][j];
          }
          listSommes.add(sommeL);
      }
      return listSommes;
  }
  
  // Méthode qui calcule la somme des éléments d'une colonne et qui renvoie une liste
  public Double sommeLigneMatrice(int ligne) {
      double sommeC=0;
      for ( int j = 0 ; j < this.nbColonnes; j++ ){
              sommeC = sommeC + this.valeurs[ligne][j];
      }
      
      return sommeC;
  }
  //méthode qui divise une colonne d'une matrice par un double
  //nC est le numéro de la colonne à traiter
  private void divisionColonneMatrice(double sommeC, int nC) {
      if (sommeC == 0) return;
      for ( int i = 0 ; i < this.nbLignes ; i++ ){
          this.valeurs[i][nC]=this.valeurs[i][nC]/sommeC;
      }   
  } 

  //méthode qui divise une ligne d'une matrice par un double 
  //nL est le numéro de la ligne à traiter
  private void divisionLigneMatrice(double sommeL, int nL) {
      if (sommeL == 0) return;
      for ( int j = 0 ; j < this.nbColonnes ; j++ ){
       this.valeurs[nL][j]=this.valeurs[nL][j]/sommeL;
      
      }
  } 
  
  /** méthode qui réalise la normalisation d'une matrice; 
   * la matrice est normalisée quand toutes les lignes et toutes les colonnes sont normalisées
   * une ligne(colonne)est dite normalisée si la somme de tous les éléments de la ligne(colonne) = 1) 
   * la normalisation d'une ligne(colonne) consiste à diviser chaque élément d'une ligne 
   * par la somme des éléments de la ligne( colonne); 
   */
  public void normalisationMatrice( ){
      int k=0;
      while(true){
          List<Double> listeSommeLignes = new ArrayList<Double>(); 
          List<Double> listeSommeColonnes = new ArrayList<Double>();
          k++;
          System.out.println("itération"+k);
          listeSommeLignes = sommeLigneMatriceList();
          for ( int i = 0 ; i < this.nbLignes-1; i++ ) {
              divisionLigneMatrice(listeSommeLignes.get(i).doubleValue(),i);
              //System.out.println("somme ligne : "+((Double)listeSommeLignes.get(i)));
          }
          listeSommeColonnes = sommeColonneMatriceList();
          for ( int j = 0 ; j < this.nbColonnes-1; j++ ) {
              divisionColonneMatrice(listeSommeColonnes.get(j).doubleValue(),j);
              //System.out.println("somme colonne : "+((Double)listeSommeColonnes.get(j)));
          }
          if(testMatriceNormalisee()){
              System.out.println("matrice normalisée");
              break;  
          }
      }
  }
  public boolean testConvergenceMatrice (){
      List<Double> sommeLignes = new ArrayList<Double>();
      List<Double> sommeColonnes= new ArrayList<Double>();
      double  tolerance=0.3,sommeLigne,sommeColonne;
            
      sommeLignes = sommeLigneMatriceList();
      sommeColonnes = sommeColonneMatriceList();

      // version1 : on accepte une certaine erreur  de +-0.1
      for ( int i = 0 ; i < sommeLignes.size()-1 ; i++ ){
          sommeLigne =sommeLignes.get(i).doubleValue();
          System.out.println("sommeLIGNE "+sommeLigne);
          if (!((sommeLigne >=1-tolerance  && sommeLigne <=1+tolerance )||sommeLigne ==0)){
              return false;
          }
      }
      for ( int j = 0 ; j < sommeColonnes .size()-1 ; j++ ){
          sommeColonne =sommeColonnes.get(j).doubleValue();
          System.out.println("sommeCOLONNE"+sommeColonne);
          if (!((sommeColonne  >=1-tolerance  && sommeColonne  <=1+tolerance )||sommeColonne  ==0)){
              return false;
          }
      }
      return true;
  }
  @SuppressWarnings("unused")
  private boolean verificationConvergence(){
      try{
          for(int i=1; i<=nbLignes; i++){
              double totalAlpha = 0;
              for (int j=1; j<=i-1; j++){
                  totalAlpha += Math.abs(this.valeurs[i][j]);
              } 
              for (int j=i+1; j<=nbColonnes; j++){
                  totalAlpha += Math.abs(this.valeurs[i][j]);
              } 
              if(Math.abs(this.valeurs[i][i])<=totalAlpha) return false;
          }
          } catch (Exception e) {
              throw new InternalError();
          } return true;
      } 
  
  public boolean comparaisonDeuxMatrices (){
      boolean resultatComparaison=false;
      
      return resultatComparaison;
  }
  
  /** méthode qui teste si la somme de toutes les lignes(colonnes) d'une matrice vaut 1*/
  public boolean testMatriceNormalisee(){
      List<Double> sommeLignes = new ArrayList<Double>();
      List<Double> sommeColonnes= new ArrayList<Double>();
      double  tolerance=0.3,sommeLigne,sommeColonne;
            
      sommeLignes = sommeLigneMatriceList();
      sommeColonnes = sommeColonneMatriceList();

      // version1 : on accepte une certaine erreur  de +-0.1
      for ( int i = 0 ; i < sommeLignes.size()-1 ; i++ ){
          sommeLigne =sommeLignes.get(i).doubleValue();
          System.out.println("sommeLIGNE "+sommeLigne);
          if (!((sommeLigne >=1-tolerance  && sommeLigne <=1+tolerance )||sommeLigne ==0)){
              return false;
          }
      }
      for ( int j = 0 ; j < sommeColonnes .size()-1 ; j++ ){
          sommeColonne =sommeColonnes.get(j).doubleValue();
          System.out.println("sommeCOLONNE"+sommeColonne);
          if (!((sommeColonne  >=1-tolerance  && sommeColonne  <=1+tolerance )||sommeColonne  ==0)){
              return false;
          }
      }
      return true;
      
      // version 2 : on n'accepte pas l'erreur; la ligne(colonne) est normalisée si et seulement si la somme vaut 1)
      /*for ( int i = 0 ; i < sommeLignes.size()-1 ; i++ ){
          sommeListeLignes=sommeListeLignes + ((Double)sommeLignes.get(i)).doubleValue();
         }
        for ( int j = 0 ; j < sommeColonnes .size()-1 ; j++ ){
          sommeListeColonnes=sommeListeColonnes + ((Double)sommeColonnes.get(j)).doubleValue();
          System.out.println("val colonne : "+ ((Double)sommeColonnes .get(j)).doubleValue());
      }
      if(sommeListeLignes == sommeLignes .size()-1 && sommeListeColonnes == sommeColonnes.size()-1) return true;
      else return false;
      */
  }
  //méthode qui affiche une matrice
  public void affichageMatriceAppariement(){
      String ligne ="";
      for (int i=0; i<this.nbLignes;i++){
          for (int j=0; j<this.nbColonnes;j++){
              ligne = ligne.concat(this.valeurs[i][j]+" ");
           }
          System.out.println(ligne +";");
          ligne = "";
      }
  }
  //méthode qui affiche une matrice
  public void ecritMatriceAppariement(){
       String adressedufichier = "E:/Data/Graphes/CSV/arc_influence_passivity.csv";
       try {
              FileWriter fw = new FileWriter(adressedufichier, true);
              BufferedWriter output = new BufferedWriter(fw); 
              for (int j=0; j<this.nbColonnes;j++){
                      
                  output.write(""+this.valeurs[0][j]);
                  output.newLine();
              }
          output.flush();
          output.close(); 

          }catch (Exception e) {
              e.printStackTrace();
          }
          try {
              String ligne ;
              BufferedReader fichierALire=new BufferedReader(new FileReader("E:/Data/Graphes/CSV/arc_influence_passivity.csv"));
              BufferedWriter fichierAEcrire= new BufferedWriter(new FileWriter("E:/Data/Graphes/CSV/matrice_influence_passivity.csv"));           
              
              while ((ligne = fichierALire.readLine()) != null) {
                  for (int j=0; j<this.nbColonnes;j++){                               
                      System.out.println(ligne);          
                      fichierAEcrire.write(ligne + ";" + this.valeurs[1][j]);
                      fichierAEcrire.newLine();
                  }
              }
              fichierAEcrire.flush();
              fichierALire.close();   
              fichierAEcrire.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
          
      
  }
  public static void affichageMatriceAppariement(double [][] matrice){
      String ligne ="";
      for (int i=0; i<matrice.length ;i++){
          for (int j=0; j<matrice[0] .length ;j++){
              ligne = ligne.concat(matrice[i][j]+" ");
           }
          System.out.println(ligne +";");
          ligne = "";
      }
  }
  //méthode qui calcule la somme des éléments d'une colonne et qui renvoie un double
  //nc est le nombre de colonnes
  public static double sommeColonneMatrice(double[][] matrice, int nc) {
         double sommeC = 0;
         //List listColonne =new ArrayList();
          for ( int i = 0 ; i < matrice.length ; i++ ){
            sommeC =sommeC+ matrice[i][nc];
          }
        return sommeC;
  }
  // méthode qui calcule la valeur maximale d'une matrice
  @SuppressWarnings("rawtypes")
  public double confianceMaxsLignes(){
      @SuppressWarnings("unused")
      List<?> maxLigne = new ArrayList ();
      double valeurMax,confiance=0,moyenne;
      @SuppressWarnings("unused")
      int positionMax=0;
      for(int i=0;i<this.nbLignes; i++){
          valeurMax =0;
          for(int j=0; j<this.nbColonnes ; j++){
              if(this.valeurs[i][j] >=valeurMax ){
                  valeurMax  =this.valeurs[i][j];
                  positionMax = j;
              }
          }
          confiance=confiance+ valeurMax;
      }
      moyenne = (double)(this.nbLignes + this.nbColonnes )/2;
      confiance=confiance/moyenne;
      return confiance;
  }
  //méthode qui calcule la valeur maximale d'une matrice
  public List<Double> maxColonneMatrice(){
      List<Double> maxColonne = new ArrayList<Double> (); 
      double valeurMax;
      @SuppressWarnings("unused")
      int positionMax ;
      for(int j=0;j<this.nbColonnes; j++){
          valeurMax =0;
          for(int i=0; i<this.nbLignes ; i++){
              if(this.valeurs[i][j] >=valeurMax  ){
                  valeurMax   =this.valeurs[i][j];
                  positionMax = i;
              }
          }
          maxColonne.add(valeurMax);
      }
      return maxColonne ;
  }
}
