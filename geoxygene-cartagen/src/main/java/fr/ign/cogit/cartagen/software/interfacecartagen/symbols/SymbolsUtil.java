/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.symbols;

import java.util.Map;

import fr.ign.cogit.cartagen.software.dataset.SourceDLM;

public class SymbolsUtil {

  public static SymbolGroup getSymbolGroup(SourceDLM sourceDlm, double scale) {
    // TODO
    if (sourceDlm.equals(SourceDLM.BD_TOPO_V2)) {
      if (scale < 35000 && scale >= 20000) {
        return SymbolGroup.BD_TOPO_25;
      }
    }
    if (sourceDlm.equals(SourceDLM.SPECIAL_CARTAGEN)) {
      return SymbolGroup.SPECIAL_Cartagen;
    }

    return SymbolGroup.Simple;
  }

  public static RoadSymbolResult getRoadSymbolFromFields(SourceDLM sourceDlm,
      SymbolList list, Map<String, Object> fields) {

    if (sourceDlm.equals(SourceDLM.BD_TOPO_V2)) {
      return SymbolsUtil.getRoadSymbolFromFieldsTopoV2(list, fields);
    } else if (sourceDlm.equals(SourceDLM.BD_CARTO)) {
      return SymbolsUtil.getRoadSymbolFromFieldsCarto(list, fields);
    } else if (sourceDlm.equals(SourceDLM.SPECIAL_CARTAGEN)) {
      return SymbolsUtil.getRoadSymbolFromFieldsDefault(list, fields);
    } else {
      return new RoadSymbolResult(0, 4);
    }
  }

  public static RoadSymbolResult getPathSymbolFromFields(SourceDLM sourceDlm,
      SymbolList list, Map<String, Object> fields) {

    if (sourceDlm.equals(SourceDLM.BD_TOPO_V2)) {
      return SymbolsUtil.getPathSymbolFromFieldsTopoV2(list, fields);
    }
    return new RoadSymbolResult(0, 0);
    // TODO fill for other databases
  }

  /**
   * In BD TOPO V1, field numbers are 0=SOURCE, 1=NATURE, 2=CLASSEMENT,
   * 3=DEP_GEST, 4=FICTIF, 5=FRANCHISST, 6=LARGEUR, 7=NOM, 8=NB_VOIES, 9=NUMERO,
   * 10=POSIT_SOL, 11=Z_INI, 12=Z_FIN.
   * 
   * @param list
   * @param fields
   * @param fieldNames
   * @return
   */
  private static RoadSymbolResult getRoadSymbolFromFieldsTopoV1(
      SymbolList list, Map<String, Object> fields) {
    // initialise
    int ATT_Importance = 5;
    int Att_Nb_voies = 1;
    double Att_Largeur = 0;
    String symbolVarName = "";

    // analyse fields to compute the symbol
    if (fields.containsKey("NB_VOIES")) {
      if (fields.get("NB_VOIES").toString().compareTo("") != 0) {
        Att_Nb_voies = Integer.parseInt(fields.get("NB_VOIES").toString());
      }
    }

    /*
     * if (a[10] != -1 && champs[a[10]].toString().compareTo("") != 0) {
     * ATT_Posit_sol = Integer.parseInt(champs[a[10]].toString()); }
     */
    if (fields.containsKey("LARGEUR")) {
      if (fields.get("LARGEUR").toString().compareTo("") != 0) {
        Att_Largeur = Double.parseDouble(fields.get("LARGEUR").toString());
      }
    }

    if (fields.containsKey("NATURE")
        && fields.get("NATURE").toString().compareTo("") != 0) {
      if (fields.get("NATURE").toString().compareTo("Autoroutière") == 0) {
        ATT_Importance = 1;
      } else if (fields.get("NATURE").toString().compareTo("Principale") == 0) {
        ATT_Importance = 2;
      }
    }
    // else if(champs[a[1]].toString().substring(champs[0].toString().length()
    // - 7, 7)=="gionale")
    if (fields.get("NATURE").toString().compareTo("Régionale") == 0) {
      ATT_Importance = 3;
    } else if (fields.get("NATURE").toString().compareTo("Locale") == 0) {
      ATT_Importance = 4;
    } else if (fields.get("NATURE").toString().compareTo("En construction") == 0) {
      // Att_ETAT = "En construction";
    } // else {
    // ATT_Importance = 5;
    // }

    if (ATT_Importance == 5) {
      symbolVarName = "VAL_rte_non_classee";
    }

    if (ATT_Importance == 4) {
      if (Att_Nb_voies == 0) {
        symbolVarName = "VAL_rte_non_classee";
      } else if (Att_Nb_voies == 1) {
        symbolVarName = "VAL_rte_l_1";
      } else if ((Att_Nb_voies == 2) && (Att_Largeur < 7)) {
        symbolVarName = "VAL_rte_l_2";
      } else if ((Att_Nb_voies == 3)
          || ((Att_Nb_voies == 2) && (Att_Largeur >= 7))) {
        symbolVarName = "VAL_rte_l_3";
      } else if (Att_Nb_voies >= 3) {
        symbolVarName = "VAL_rte_l_4";
      }
    }

    if (ATT_Importance == 3) {
      if (Att_Nb_voies == 0) {
        symbolVarName = "VAL_rte_non_classee";
      } else if (Att_Nb_voies == 1) {
        symbolVarName = "VAL_rte_r_1";
      } else if ((Att_Nb_voies == 2) && (Att_Largeur < 7)) {
        symbolVarName = "VAL_rte_r_2";
      } else if ((Att_Nb_voies == 3)
          || ((Att_Nb_voies == 2) && (Att_Largeur >= 7))) {
        symbolVarName = "VAL_rte_r_3";
      } else {
        symbolVarName = "VAL_rte_r_4";
      }
    }

    if ((ATT_Importance == 1) || ATT_Importance == 2) {
      if (Att_Nb_voies == 1) {
        symbolVarName = "VAL_rte_p_1";
      } else if ((Att_Nb_voies == 2) && (Att_Largeur < 7)) {
        symbolVarName = "VAL_rte_p_2";
      } else if ((Att_Nb_voies == 3)
          || ((Att_Nb_voies == 2) && (Att_Largeur >= 7))) {
        symbolVarName = "VAL_rte_p_3";
      } else {
        symbolVarName = "VAL_rte_p_4";
      }
    }
    return new RoadSymbolResult(list
        .getSymbolShapeIdBySymbolVarName(symbolVarName), ATT_Importance);

  }

  /**
   * In BD TOPO V2, field numbers are 0=ID, 1=PREC_PLANI, 2=PREC_ALTI, 3=NATURE,
   * 4=NUMERO, 5=NOM_RUE_G, 6=NOM_RUE_D, 7=IMPORTANCE, 8=CL_ADMIN, 9=GESTION,
   * 10=MISE_SERV, 11=IT_VERT, 12=IT_EUROP, 13=FICTIF, 14=FRANCHISST,
   * 15=LARGEUR, 16=NOM_ITI, 17=NB_VOIES, 18=POS_SOL, 19=SENS, 20=INSEECOM_G,
   * 21=INSEECOM_D, 22=CODEVOIE_G, 23=CODEVOIE_D, 24=TYP_ADRES, 25=BORNEDEB_G,
   * 26=BORNEDEB_D, 27=BORNEFIN_G, 28=BORNEFIN_D, 29=ETAT, 30=Z_INI, 31=Z_FIN.
   * 
   * @param list
   * @param fields
   * @param fieldNames
   * @return
   */
  private static RoadSymbolResult getRoadSymbolFromFieldsTopoV2(
      SymbolList list, Map<String, Object> fields) {
    // initialise
    String symbolVarName = "";
    boolean voies_larges = false;
    String att_Nature = "";
    // String att_acces="";
    String att_Importance = "5";
    int att_Nb_voies = 0;
    double att_Largeur = 0;
    String att_ETAT = "";

    // analyse fields to compute the symbol
    if (fields.containsKey("NATURE")
        && fields.get("NATURE").toString().compareTo("") != 0) {
      att_Nature = fields.get("NATURE").toString();
    }

    if (fields.containsKey("IMPORTANCE")
        && fields.get("IMPORTANCE").toString().compareTo("") != 0) {
      att_Importance = fields.get("IMPORTANCE").toString();

    }
    if (fields.containsKey("NB_VOIES")
        && fields.get("NB_VOIES").toString().compareTo("") != 0) {
      att_Nb_voies = Integer.parseInt(fields.get("NB_VOIES").toString());
    }

    // if (a[10] != -1 && champs[a[10]].toString().compareTo("") != 0) {
    // att_Posit_sol = Integer.parseInt(champs[a[10]].toString());

    // }
    if (fields.containsKey("LARGEUR")
        && fields.get("LARGEUR").toString().compareTo("") != 0) {
      att_Largeur = Double.parseDouble(fields.get("LARGEUR").toString());
    }
    if (fields.containsKey("ETAT")
        && fields.get("ETAT").toString().compareTo("") != 0) {
      att_ETAT = fields.get("ETAT").toString();
    }

    if (att_Largeur >= 7) {
      voies_larges = true;// else false
    }

    if (att_Nature.compareTo("Bac auto") == 0) {
      symbolVarName = "VAL_bac_auto";
    }

    if (att_Nature.compareTo("Bac piéton") == 0) {
      symbolVarName = "VAL_bac_pieton";
    }

    if ((att_Nature.compareTo("Type autoroutier") == 0)
        && att_ETAT.compareTo("En construction") == 0) {
      symbolVarName = "VAL_autorte_en_constr";
    }

    if (att_Nature.compareTo("Route à 2 chaussées") == 0
        || (att_Nature.compareTo("Route à 1 chaussée") == 0)
        && (att_ETAT.compareTo("En construction") == 0)) {
      symbolVarName = "VAL_rte_en_constr";
    }

    if ((att_Nature.compareTo("Route empierrée") == 0)
        || (att_Nature.compareTo("Chemin") == 0)
        || (att_Nature.compareTo("Sentier") == 0)
        || (att_Nature.compareTo("Escalier") == 0)
        || (att_Nature.compareTo("Piste cyclable") == 0)) {
      symbolVarName = att_Nature;
    }

    if (att_Importance.compareTo("NC") == 0) {
      // ((att_acces == "Interdit légalement") || (att_acces =="Impossible")))
      symbolVarName = "VAL_sentier";
    }

    if (att_Importance.compareTo("5") == 0) {
      symbolVarName = "VAL_rte_non_classee";
    }
    if (att_Nb_voies == 0) {
      symbolVarName = "VAL_rte_non_classee";
    }

    if (att_Nb_voies == 0) {
      symbolVarName = "VAL_rte_non_classee";
    }

    if (att_Importance.compareTo("4") == 0) {
      if (att_Nb_voies == 1) {
        symbolVarName = "VAL_rte_l_1";
      } else if ((att_Nb_voies == 2) && !(voies_larges)) {
        symbolVarName = "VAL_rte_l_2";
      } else if ((att_Nb_voies == 3) || ((att_Nb_voies == 2) && voies_larges)) {
        symbolVarName = "VAL_rte_l_3";
      } else {
        symbolVarName = "VAL_rte_l_4";
      }
    }

    if (att_Importance.compareTo("3") == 0) {
      if (att_Nb_voies == 1) {
        symbolVarName = "VAL_rte_r_1";
      } else if ((att_Nb_voies == 2) && !(voies_larges)) {
        symbolVarName = "VAL_rte_r_2";
      } else if ((att_Nb_voies == 3) || ((att_Nb_voies == 2) && voies_larges)) {
        symbolVarName = "VAL_rte_r_3";
      } else {
        symbolVarName = "VAL_rte_r_4";
      }
    }

    if ((att_Importance.compareTo("1") == 0)
        || (att_Importance.compareTo("2") == 0)) {
      if (att_Nb_voies == 1) {
        symbolVarName = "VAL_rte_p_1";
      } else if ((att_Nb_voies == 2) && !(voies_larges)) {
        symbolVarName = "VAL_rte_p_2";
      } else if ((att_Nb_voies == 3) || ((att_Nb_voies == 2) && voies_larges)) {
        symbolVarName = "VAL_rte_p_3";
      } else {
        symbolVarName = "VAL_rte_p_4";
      }
    }

    if ((att_Nature.compareTo("Type autoroutier") == 0)
        || ((att_Nature.compareTo("Bretelle") == 0))) {
      if (att_Nb_voies == 1) {
        symbolVarName = "VAL_rte_a_1";
      } else if (att_Nb_voies == 2) {
        symbolVarName = "VAL_rte_a_2";
      } else if (att_Nb_voies == 3) {
        symbolVarName = "VAL_rte_a_3";
      } else {
        symbolVarName = "VAL_rte_a_4";
      }
    }

    int att_ImportanceINT = 5;
    if (att_Importance.compareTo("NC") != 0) {
      att_ImportanceINT = Integer.parseInt(att_Importance);
    }

    // WARNING (symbo incohérente) SI ON ARRIVE ICI #

    return new RoadSymbolResult(list
        .getSymbolShapeIdBySymbolVarName(symbolVarName), att_ImportanceINT);
  }

  /**
   * In BD TOPO V2, field numbers are 0=ID, 1=PREC_PLANI, 2=PREC_ALTI, 3=NATURE,
   * 4=NUMERO, 5=NOM_RUE_G, 6=NOM_RUE_D, 7=IMPORTANCE, 8=CL_ADMIN, 9=GESTION,
   * 10=MISE_SERV, 11=IT_VERT, 12=IT_EUROP, 13=FICTIF, 14=FRANCHISST,
   * 15=LARGEUR, 16=NOM_ITI, 17=NB_VOIES, 18=POS_SOL, 19=SENS, 20=INSEECOM_G,
   * 21=INSEECOM_D, 22=CODEVOIE_G, 23=CODEVOIE_D, 24=TYP_ADRES, 25=BORNEDEB_G,
   * 26=BORNEDEB_D, 27=BORNEFIN_G, 28=BORNEFIN_D, 29=ETAT, 30=Z_INI, 31=Z_FIN.
   * 
   * @param list
   * @param fields
   * @param fieldNames
   * @return
   */
  private static RoadSymbolResult getPathSymbolFromFieldsTopoV2(
      SymbolList list, Map<String, Object> fields) {
    // initialise
    String symbolVarName = "";
    String att_Nature = "";
    int importance = 0;

    // analyse fields to compute the symbol
    if (fields.containsKey("NATURE")
        && fields.get("NATURE").toString().compareTo("") != 0) {
      att_Nature = fields.get("NATURE").toString();
    }

    if (att_Nature.equals("Chemin")) {
      importance = 1;
      symbolVarName = "VAL_chemin";
    }

    if (att_Nature.equals("Sentier")) {
      importance = 0;
      symbolVarName = "VAL_sentier";
    }

    if (att_Nature.equals("Escalier")) {
      importance = 1;
      symbolVarName = "VAL_escalier";
    }

    return new RoadSymbolResult(list
        .getSymbolShapeIdBySymbolVarName(symbolVarName), importance);
  }

  /**
   * Retrieve the road symbol according to fields for BD CARTO roads.
   * @param list
   * @param fields
   * @return
   */
  private static RoadSymbolResult getRoadSymbolFromFieldsCarto(SymbolList list,
      Map<String, Object> fields) {
    // TODO
    int importance = Integer.parseInt(fields.get("importance").toString());
    return new RoadSymbolResult(1, importance);
  }

  /**
   * Retrieve the road symbol according to fields for CartAGen modified road
   * shapefiles (only one field "importance").
   * @param list
   * @param fields
   * @return
   */
  private static RoadSymbolResult getRoadSymbolFromFieldsDefault(
      SymbolList list, Map<String, Object> fields) {
    int importance = 1;
    if (fields.get("importance") != null) {
      importance = Integer.parseInt(fields.get("importance").toString());
    }
    return new RoadSymbolResult(-1, importance);
  }

}
