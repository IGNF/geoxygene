package fr.ign.cogit.geoxygene.distance;

import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.util.conversion.ParseException;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

public class PolygonTurningSimilarityTest {

  @Test
  public void testH_t0min() throws ParseException {
    PolygonTurningSimilarity pts = new PolygonTurningSimilarity();
//    IPolygon p1 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 281 476, 281 739, 476 739, 476 476, 281 476 ))");
//    IPolygon p2 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 264 487, 264 752, 464 752, 464 487, 264 487 ))");
//    TurnRep turnRep1 = pts.poly_to_turn_rep(p1);
//    TurnRep turnRep2 = pts.poly_to_turn_rep(p2);
//    InitVals vals = pts.init_vals(turnRep1, turnRep2);
//    pts.init_events(turnRep1, turnRep2);
//    Result result = pts.h_t0min(turnRep1, turnRep2, vals.ht0, vals.slope, vals.alpha, 0);
//    System.out.println(result.h_t0min);
    
    IPolygon p3 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 587 455, 594 763, 840 756, 841 571, 703 618, 702 436, 587 455 ))");
    IPolygon p4 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 587 455, 584 774, 826 773, 852 672, 850 593, 711 598, 710 448, 587 455 ))");
    TurnRep turnRep3 = pts.poly_to_turn_rep(p3);
    TurnRep turnRep4 = pts.poly_to_turn_rep(p4);
    InitVals  vals = pts.init_vals(turnRep3, turnRep4);
    pts.init_events(turnRep3, turnRep4);
    Result result = pts.h_t0min(turnRep3, turnRep4, vals.ht0, vals.slope, vals.alpha, pts.reinit_interval(turnRep3, turnRep4));
    System.out.println(result.h_t0min);
    System.out.println(result.theta_star);
    System.out.println(result.e.fi);
    System.out.println(result.e.gi);
    
    IPolygon p5 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON ((601393.8609586384 129308.3352145373, 601386.9327165736 129311.03452962362, 601379.2291888517 129293.53916847681, 601374.3532134495 129283.22767950434, 601368.0346282 129285.9536027778, 601377.7441714606 129308.66778316157, 601371.5720679228 129311.35937718416, 601374.4492891183 129320.03744756372, 601383.5914274431 129316.60334485126, 601385.8189535341 129322.58982121115, 601397.3242963976 129317.8336755299, 601393.8609586384 129308.3352145373))");
    IPolygon p6 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON ((601376.434639757 129283.2866709346, 601369.7599901359 129286.1207944395, 601376.2667794337 129304.44688186521, 601377.965937795 129309.33153550573, 601372.6519528106 129310.91699784098, 601375.0109723865 129319.3974715326, 601381.8533598916 129316.83477954452, 601384.4010608828 129315.95612352566, 601384.5301432277 129316.06721684137, 601385.9183598927 129321.0592651448, 601398.0811185343 129317.34119900773, 601398.2923065725 129317.20296331422, 601396.6836583051 129312.25906583045, 601396.4991118249 129311.60746502322, 601395.7431921208 129309.92098309714, 601393.0530589732 129303.94791756378, 601384.6775624597 129306.85775132495, 601382.8627687959 129301.45208578116, 601380.2473477669 129293.72951394776, 601376.434639757 129283.2866709346))");

    TurnRep turnRep5 = pts.poly_to_turn_rep(p5);
    TurnRep turnRep6 = pts.poly_to_turn_rep(p6);
    vals = pts.init_vals(turnRep5, turnRep6);
    pts.init_events(turnRep5, turnRep6);
//    result = pts.h_t0min(turnRep5, turnRep6, vals.ht0, vals.slope, vals.alpha, 0);
//    System.out.println(result.h_t0min);
    result = pts.h_t0min(turnRep5, turnRep6, vals.ht0, vals.slope, vals.alpha, pts.reinit_interval(turnRep5, turnRep6));
    System.out.println(result.h_t0min);
    
    IPolygon p7 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 601375.7482019763 129283.48234950138, 601369.1620646537 129286.51648838643, 601376.2186011001 129304.63799717242, 601378.0643056133 129309.46918286277, 601372.800554859 129311.21419152388, 601375.414268838 129319.61966037631, 601382.1762540007 129316.85177062867, 601384.6962961367 129315.89667672024, 601384.8286702893 129316.00382642982, 601386.3668135327 129320.95173580002, 601398.411903976 129316.8685373252, 601398.6188268135 129316.72399517294, 601396.8618044513 129311.8308628431, 601396.6576899149 129311.18512429588, 601395.8512504939 129309.52220777777, 601392.9821959463 129303.63299270101, 601384.6982688423 129306.79410423039, 601382.7212681291 129301.44563115918, 601379.8741273369 129293.80545235297, 601375.7482019763 129283.48234950138 ))");
    turnRep5 = pts.poly_to_turn_rep(p5);
    TurnRep turnRep7 = pts.poly_to_turn_rep(p7);
    vals = pts.init_vals(turnRep5, turnRep7);
    pts.init_events(turnRep5, turnRep7);
//    result = pts.h_t0min(turnRep5, turnRep6, vals.ht0, vals.slope, vals.alpha, 0);
//    System.out.println(result.h_t0min);
    result = pts.h_t0min(turnRep5, turnRep7, vals.ht0, vals.slope, vals.alpha, pts.reinit_interval(turnRep5, turnRep7));
    System.out.println(result.h_t0min);
    
    IPolygon p8 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 601363.8728868965 129291.64431080446, 601360.2676399174 129297.9360035676, 601376.5267036424 129308.60496012802, 601380.8323911293 129311.469927072, 601377.556649411 129315.94448218118, 601384.5611046376 129321.27561849263, 601388.4640270092 129315.09881883931, 601389.9628946633 129312.85912857136, 601390.1328758748 129312.86963401169, 601394.2557242012 129316.008075257, 601401.699387685 129305.69553010502, 601401.7841111356 129305.4577667113, 601397.514734459 129302.49090062547, 601396.9739227165 129302.08326883755, 601395.3524741941 129301.19639760941, 601389.5994863887 129298.06311506085, 601384.6848582306 129305.4429866337, 601379.9721809679 129302.23278897128, 601373.2214561302 129297.6604529114, 601363.8728868965 129291.64431080446 ))");
    turnRep5 = pts.poly_to_turn_rep(p5);
    TurnRep turnRep8 = pts.poly_to_turn_rep(p8);
    vals = pts.init_vals(turnRep5, turnRep8);
    pts.init_events(turnRep5, turnRep8);
//    result = pts.h_t0min(turnRep5, turnRep6, vals.ht0, vals.slope, vals.alpha, 0);
//    System.out.println(result.h_t0min);
    result = pts.h_t0min(turnRep5, turnRep8, vals.ht0, vals.slope, vals.alpha, 1);
    System.out.println(result.h_t0min);
    
    turnRep5 = pts.poly_to_turn_rep(p5);
    TurnRep turnRep9 = pts.poly_to_turn_rep(p5);
    vals = pts.init_vals(turnRep5, turnRep9);
    pts.init_events(turnRep5, turnRep9);
//    result = pts.h_t0min(turnRep5, turnRep6, vals.ht0, vals.slope, vals.alpha, 0);
//    System.out.println(result.h_t0min);
    result = pts.h_t0min(turnRep5, turnRep9, vals.ht0, vals.slope, vals.alpha, 1);
    System.out.println(result.h_t0min);

    turnRep5 = pts.poly_to_turn_rep(p5);
    turnRep3 = pts.poly_to_turn_rep(p3);
    vals = pts.init_vals(turnRep5, turnRep3);
    pts.init_events(turnRep5, turnRep3);
//    result = pts.h_t0min(turnRep5, turnRep6, vals.ht0, vals.slope, vals.alpha, 0);
//    System.out.println(result.h_t0min);
    result = pts.h_t0min(turnRep5, turnRep3, vals.ht0, vals.slope, vals.alpha, pts.reinit_interval(turnRep5, turnRep3));
    System.out.println(result.h_t0min);

  }
}
