/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.util.loader;

import java.io.BufferedReader;

/**
 * Usage interne. Outil pour écrire des message en console, et pour lire les
 * réponses.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class Message {

  private String reponse;

  public Message(BufferedReader br, String prompt, String choix1, String choix2) {
    this.reponse = ""; //$NON-NLS-1$
    try {
      while (!((this.reponse.compareToIgnoreCase(choix1) == 0)
          || (this.reponse.compareToIgnoreCase(choix2) == 0) || (this.reponse
          .compareToIgnoreCase("q") == 0))) { //$NON-NLS-1$
        System.out.println(prompt + " (" + choix1 + "/" + choix2 + "/q)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        this.reponse = br.readLine();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (this.reponse.compareToIgnoreCase("q") == 0) { //$NON-NLS-1$
      System.out.println("Au revoir");
      System.exit(0);
    }
  }

  public Message(BufferedReader br, String prompt, String choix1,
      String choix2, String choix3, String choix4) {
    this.reponse = ""; //$NON-NLS-1$
    try {
      while (!((this.reponse.compareToIgnoreCase(choix1) == 0)
          || (this.reponse.compareToIgnoreCase(choix2) == 0)
          || (this.reponse.compareToIgnoreCase(choix3) == 0)
          || (this.reponse.compareToIgnoreCase(choix4) == 0) || (this.reponse
          .compareToIgnoreCase("q") == 0))) { //$NON-NLS-1$
        System.out
            .println(prompt
                + " (" + choix1 + "/" + choix2 + "/" + choix3 + "/" + choix4 + "/q)"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        this.reponse = br.readLine();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (this.reponse.compareToIgnoreCase("q") == 0) { //$NON-NLS-1$
      System.out.println("Au revoir");
      System.exit(0);
    }
  }

  public Message(BufferedReader br, String prompt, String choix1,
      String choix2, String choix3) {
    this.reponse = ""; //$NON-NLS-1$
    try {
      while (!((this.reponse.compareToIgnoreCase(choix1) == 0)
          || (this.reponse.compareToIgnoreCase(choix2) == 0)
          || (this.reponse.compareToIgnoreCase(choix3) == 0) || (this.reponse
          .compareToIgnoreCase("q") == 0))) { //$NON-NLS-1$
        System.out.println(prompt
            + " (" + choix1 + "/" + choix2 + "/" + choix3 + "/q)"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        this.reponse = br.readLine();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (this.reponse.compareToIgnoreCase("q") == 0) { //$NON-NLS-1$
      System.out.println("Au revoir");
      System.exit(0);
    }
  }

  public Message(BufferedReader br, String prompt) {
    System.out.println(prompt);
    try {
      this.reponse = br.readLine();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getAnswer() {
    return this.reponse;
  }

}
