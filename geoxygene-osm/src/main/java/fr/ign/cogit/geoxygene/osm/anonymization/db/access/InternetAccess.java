package fr.ign.cogit.geoxygene.osm.anonymization.db.access;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * Permet de gérer de fournir le proxy
 * permettant de se connecter à Internet
 * pour télécharger des données.
 * @author Matthieu Dufait
 */
public class InternetAccess {  
  
  private static Proxy IGNProxy = initIGNProxy();
  
  /**
   * Génération d'une instance de {@code Proxy} permettant
   * de se connecter à via le protocole HTTP à travers 
   * le proxy de l'IGN.
   * 
   * @return une nouvelle instance de {@code Proxy}
   */
  private static Proxy initIGNProxy() {
    String proxyAddress = "proxy.ign.fr";
    int proxyPort = 3128;
    InetSocketAddress sa = new InetSocketAddress(proxyAddress, proxyPort);
    return new Proxy(Proxy.Type.HTTP, sa);
  }

  public static Proxy getIGNProxy() {
    return IGNProxy;
  }
}
