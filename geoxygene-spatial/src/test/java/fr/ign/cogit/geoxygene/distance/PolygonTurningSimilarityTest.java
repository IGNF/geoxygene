package fr.ign.cogit.geoxygene.distance;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.util.conversion.ParseException;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

/**
 * Test for Polygon Turning Similarity. Expected values are computed using the C code provided by Eugene K. Ressler truncated to 10^-12.
 * @author Julien Perret
 * 
 */
public class PolygonTurningSimilarityTest {
  @Test
  public void testH_t0min() throws ParseException {
    PolygonTurningSimilarity pts = new PolygonTurningSimilarity();
    IPolygon p1 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 281 476, 281 739, 476 739, 476 476, 281 476 ))");
    IPolygon p2 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 264 487, 264 752, 464 752, 464 487, 264 487 ))");
    TurnRep turnRep1 = PolygonTurningSimilarity.poly_to_turn_rep(p1);
    TurnRep turnRep2 = PolygonTurningSimilarity.poly_to_turn_rep(p2);
    InitVals vals = PolygonTurningSimilarity.init_vals(turnRep1, turnRep2);
    pts.init_events(turnRep1, turnRep2);
    Result result = pts.h_t0min(turnRep1, turnRep2, vals.ht0, vals.slope, vals.alpha, 0);
    double metric = Math.sqrt(result.h_t0min);
    System.out.println("Metric " + metric);
    double expected = 0.103296672227;
    double tolerance = Math.pow(10, -12);
    Assert.assertTrue("Metric is not inside the tolerance", metric >= expected - tolerance && metric <= expected + tolerance);
    
    IPolygon p3 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 587 455, 594 763, 840 756, 841 571, 703 618, 702 436, 587 455 ))");
    IPolygon p4 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 587 455, 584 774, 826 773, 852 672, 850 593, 711 598, 710 448, 587 455 ))");
    TurnRep turnRep3 = PolygonTurningSimilarity.poly_to_turn_rep(p3);
    TurnRep turnRep4 = PolygonTurningSimilarity.poly_to_turn_rep(p4);
    vals = PolygonTurningSimilarity.init_vals(turnRep3, turnRep4);
    pts.init_events(turnRep3, turnRep4);
    result = pts.h_t0min(turnRep3, turnRep4, vals.ht0, vals.slope, vals.alpha, PolygonTurningSimilarity.reinit_interval(turnRep3, turnRep4));
    metric = Math.sqrt(result.h_t0min);
    System.out.println("Metric " + metric);
    expected = 0.4592845355155;
    Assert.assertTrue("Metric is not inside the tolerance", metric >= expected - tolerance && metric <= expected + tolerance);
    
    IPolygon p5 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON ((601393.8609586384 129308.3352145373, 601386.9327165736 129311.03452962362, 601379.2291888517 129293.53916847681, 601374.3532134495 129283.22767950434, 601368.0346282 129285.9536027778, 601377.7441714606 129308.66778316157, 601371.5720679228 129311.35937718416, 601374.4492891183 129320.03744756372, 601383.5914274431 129316.60334485126, 601385.8189535341 129322.58982121115, 601397.3242963976 129317.8336755299, 601393.8609586384 129308.3352145373))");
    IPolygon p6 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON ((601376.434639757 129283.2866709346, 601369.7599901359 129286.1207944395, 601376.2667794337 129304.44688186521, 601377.965937795 129309.33153550573, 601372.6519528106 129310.91699784098, 601375.0109723865 129319.3974715326, 601381.8533598916 129316.83477954452, 601384.4010608828 129315.95612352566, 601384.5301432277 129316.06721684137, 601385.9183598927 129321.0592651448, 601398.0811185343 129317.34119900773, 601398.2923065725 129317.20296331422, 601396.6836583051 129312.25906583045, 601396.4991118249 129311.60746502322, 601395.7431921208 129309.92098309714, 601393.0530589732 129303.94791756378, 601384.6775624597 129306.85775132495, 601382.8627687959 129301.45208578116, 601380.2473477669 129293.72951394776, 601376.434639757 129283.2866709346))");

    TurnRep turnRep5 = PolygonTurningSimilarity.poly_to_turn_rep(p5);
    TurnRep turnRep6 = PolygonTurningSimilarity.poly_to_turn_rep(p6);
    vals = PolygonTurningSimilarity.init_vals(turnRep5, turnRep6);
    pts.init_events(turnRep5, turnRep6);
    result = pts.h_t0min(turnRep5, turnRep6, vals.ht0, vals.slope, vals.alpha, PolygonTurningSimilarity.reinit_interval(turnRep5, turnRep6));
    metric = Math.sqrt(result.h_t0min);
    expected = 1.278248716738;
    System.out.println("Metric " + metric);
    Assert.assertTrue("Metric is not inside the tolerance", metric >= expected - tolerance && metric <= expected + tolerance);
    
    IPolygon p7 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 601375.7482019763 129283.48234950138, 601369.1620646537 129286.51648838643, 601376.2186011001 129304.63799717242, 601378.0643056133 129309.46918286277, 601372.800554859 129311.21419152388, 601375.414268838 129319.61966037631, 601382.1762540007 129316.85177062867, 601384.6962961367 129315.89667672024, 601384.8286702893 129316.00382642982, 601386.3668135327 129320.95173580002, 601398.411903976 129316.8685373252, 601398.6188268135 129316.72399517294, 601396.8618044513 129311.8308628431, 601396.6576899149 129311.18512429588, 601395.8512504939 129309.52220777777, 601392.9821959463 129303.63299270101, 601384.6982688423 129306.79410423039, 601382.7212681291 129301.44563115918, 601379.8741273369 129293.80545235297, 601375.7482019763 129283.48234950138 ))");
    turnRep5 = PolygonTurningSimilarity.poly_to_turn_rep(p5);
    TurnRep turnRep7 = PolygonTurningSimilarity.poly_to_turn_rep(p7);
    vals = PolygonTurningSimilarity.init_vals(turnRep5, turnRep7);
    pts.init_events(turnRep5, turnRep7);
    result = pts.h_t0min(turnRep5, turnRep7, vals.ht0, vals.slope, vals.alpha, PolygonTurningSimilarity.reinit_interval(turnRep5, turnRep7));
    metric = Math.sqrt(result.h_t0min);
    System.out.println("Metric " + metric);
    expected = 1.278248716738;
    Assert.assertTrue("Metric is not inside the tolerance", metric >= expected - tolerance && metric <= expected + tolerance);
    
    IPolygon p8 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 601363.8728868965 129291.64431080446, 601360.2676399174 129297.9360035676, 601376.5267036424 129308.60496012802, 601380.8323911293 129311.469927072, 601377.556649411 129315.94448218118, 601384.5611046376 129321.27561849263, 601388.4640270092 129315.09881883931, 601389.9628946633 129312.85912857136, 601390.1328758748 129312.86963401169, 601394.2557242012 129316.008075257, 601401.699387685 129305.69553010502, 601401.7841111356 129305.4577667113, 601397.514734459 129302.49090062547, 601396.9739227165 129302.08326883755, 601395.3524741941 129301.19639760941, 601389.5994863887 129298.06311506085, 601384.6848582306 129305.4429866337, 601379.9721809679 129302.23278897128, 601373.2214561302 129297.6604529114, 601363.8728868965 129291.64431080446 ))");
    turnRep5 = PolygonTurningSimilarity.poly_to_turn_rep(p5);
    TurnRep turnRep8 = PolygonTurningSimilarity.poly_to_turn_rep(p8);
    vals = PolygonTurningSimilarity.init_vals(turnRep5, turnRep8);
    pts.init_events(turnRep5, turnRep8);
    result = pts.h_t0min(turnRep5, turnRep8, vals.ht0, vals.slope, vals.alpha, 1);
    metric = Math.sqrt(result.h_t0min);
    System.out.println("Metric " + metric);
    expected = 1.278248716738;
    Assert.assertTrue("Metric is not inside the tolerance", metric >= expected - tolerance && metric <= expected + tolerance);
    
    turnRep5 = PolygonTurningSimilarity.poly_to_turn_rep(p5);
    TurnRep turnRep9 = PolygonTurningSimilarity.poly_to_turn_rep(p5);
    vals = PolygonTurningSimilarity.init_vals(turnRep5, turnRep9);
    pts.init_events(turnRep5, turnRep9);
    result = pts.h_t0min(turnRep5, turnRep9, vals.ht0, vals.slope, vals.alpha, 0);
    metric = Math.sqrt(result.h_t0min);
    System.out.println("Metric " + metric);
    expected = 0;
    Assert.assertTrue("Metric is not inside the tolerance", metric >= expected - tolerance && metric <= expected + tolerance);

    turnRep5 = PolygonTurningSimilarity.poly_to_turn_rep(p5);
    turnRep3 = PolygonTurningSimilarity.poly_to_turn_rep(p3);
    vals = PolygonTurningSimilarity.init_vals(turnRep5, turnRep3);
    pts.init_events(turnRep5, turnRep3);
    result = pts.h_t0min(turnRep5, turnRep3, vals.ht0, vals.slope, vals.alpha, PolygonTurningSimilarity.reinit_interval(turnRep5, turnRep3));
    metric = Math.sqrt(result.h_t0min);
    System.out.println("Metric " + metric);
    expected = 1.114847311262;
    Assert.assertTrue("Metric is not inside the tolerance", metric >= expected - tolerance && metric <= expected + tolerance);

    IPolygon p9 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 601441.4342498202 129330.69657678947, 601446.998416316 129329.0932190586, 601449.1831561449 129328.56154042977, 601449.4239255186 129328.47355729576, 601446.1744461419 129318.8455110395, 601446.3354634352 129318.7268552612, 601446.2480645102 129318.4160969522, 601444.9140533174 129314.11455620779, 601444.9154823524 129313.9445552182, 601441.849962729 129303.84801817672, 601446.7374564287 129301.83894184312, 601447.1595804879 129301.59247076798, 601438.9401906154 129282.36191591695, 601439.0213717661 129282.22258751713, 601438.9427964882 129282.05191403016, 601430.4842341916 129261.52924717129, 601424.8154361504 129263.68176943673, 601422.7989391891 129264.42487904291, 601417.5917633481 129266.39126696048, 601414.0401404502 129267.7215200353, 601406.645786504 129270.50958968962, 601407.5194432089 129272.46708254764, 601407.3686786046 129272.55582228936, 601404.2982586696 129273.75010931466, 601403.4151011254 129274.11271486295, 601397.6710047945 129276.89465367264, 601397.3999815691 129277.01238497236, 601402.7502421663 129291.33844894147, 601402.8989053082 129291.4997107085, 601405.21910244 129297.45966702249, 601405.8117352534 129299.53480663798, 601409.4266079227 129313.28623961232, 601409.1354996384 129313.41380279842, 601403.9821113935 129314.93060816215, 601406.3165885096 129323.9509171835, 601408.6251172735 129322.97024385617, 601409.7725635185 129328.05027536441, 601410.7570614663 129332.28887298472, 601412.8158097795 129340.79682446027, 601419.3889365415 129339.33195582371, 601421.4831669095 129338.85952058077, 601427.2844034912 129337.60818134565, 601426.082505308 129330.67755117128, 601423.6518071081 129321.20639911946, 601428.7578825627 129319.36917254709, 601432.5596015446 129318.0310218174, 601437.033205798 129316.47850169893, 601441.4342498202 129330.69657678947 ))");
    IPolygon p10 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 601496.9180560536 129268.00810636792, 601494.5038260942 129261.33730178747, 601484.1844175747 129239.69890113699, 601481.3530904751 129238.62502124581, 601475.298889816 129241.41435137606, 601469.0541747754 129244.26208500398, 601468.8333222392 129244.36023634033, 601482.2744770828 129274.16550037418, 601487.400522088 129285.41944836792, 601492.5898828557 129283.19289406705, 601493.2711740419 129285.4287918759, 601494.1958375102 129288.45679594702, 601495.3982462198 129291.75715528426, 601497.2350697607 129290.97253371406, 601504.2604134453 129288.06135905905, 601502.2268739924 129282.5038406056, 601495.3796062177 129285.6465300086, 601495.0272367765 129284.73349828579, 601494.4042675473 129282.69810603885, 601493.8858368036 129282.50373370059, 601492.4236661588 129279.17118835212, 601490.6087896713 129274.96561139246, 601489.1965374751 129271.64348624379, 601489.0391348533 129271.33213935515, 601490.7262027715 129270.5162564548, 601496.9180560536 129268.00810636792 ))");
    turnRep9 = PolygonTurningSimilarity.poly_to_turn_rep(p9);
    TurnRep turnRep10 = PolygonTurningSimilarity.poly_to_turn_rep(p10);
    vals = PolygonTurningSimilarity.init_vals(turnRep9, turnRep10);
    pts.init_events(turnRep9, turnRep10);
    result = pts.h_t0min(turnRep9, turnRep10, vals.ht0, vals.slope, vals.alpha, PolygonTurningSimilarity.reinit_interval(turnRep9, turnRep10));
    metric = Math.sqrt(result.h_t0min);
    System.out.println("Metric " + metric);
    expected = 0.955753214440;
    Assert.assertTrue("Metric is not inside the tolerance", metric >= expected - tolerance && metric <= expected + tolerance);
    metric = PolygonTurningSimilarity.getTurningSimilarity(p9, p10);
    System.out.println("Metric " + metric);
    Assert.assertTrue("Metric is not inside the tolerance", metric >= expected - tolerance && metric <= expected + tolerance);
    
    IPolygon p11 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 601357.5738392449 129322.59158131861, 601355.9793853059 129317.71710788687, 601345.8492194316 129295.52517669284, 601341.5598823554 129297.37604050254, 601348.7863323218 129315.30395463118, 601345.0757914833 129316.92992195868, 601346.6890099152 129321.1987461102, 601350.6426040507 129319.5733796331, 601352.5210949532 129324.5215995266, 601357.5738392449 129322.59158131861 ))");
    IPolygon p12 = (IPolygon) WktGeOxygene.makeGeOxygene("POLYGON (( 601342.7299481705 129297.62451890176, 601346.7202307567 129308.35887420032, 601347.6580082099 129311.01695845657, 601347.8723044028 129311.6988115165, 601348.5488929017 129313.30462025681, 601349.3527145957 129315.24152353872, 601349.526336206 129316.00304084715, 601345.8349601787 129317.30211851967, 601347.4879833439 129321.72634831614, 601350.9914504165 129320.17567235854, 601351.8159437274 129323.22283408903, 601352.2827730416 129324.79687748937, 601352.2336091029 129325.88654754496, 601352.1019210897 129326.08545605018, 601357.8098765129 129324.03326825026, 601357.7712192327 129323.87293113176, 601355.6869985467 129318.39499700525, 601353.8687575728 129313.39933480648, 601351.8241184078 129307.97173676496, 601347.337425436 129295.61308507819, 601342.7299481705 129297.62451890176 ))");
    TurnRep turnRep11 = PolygonTurningSimilarity.poly_to_turn_rep(p11);
    TurnRep turnRep12 = PolygonTurningSimilarity.poly_to_turn_rep(p12);
    vals = PolygonTurningSimilarity.init_vals(turnRep11, turnRep12);
    pts.init_events(turnRep11, turnRep12);
    result = pts.h_t0min(turnRep11, turnRep12, vals.ht0, vals.slope, vals.alpha, PolygonTurningSimilarity.reinit_interval(turnRep11, turnRep12));
    metric = Math.sqrt(result.h_t0min);
    System.out.println("Metric " + metric);
    expected = 1.040626430966;
    Assert.assertTrue("Metric is not inside the tolerance", metric >= expected - tolerance && metric <= expected + tolerance);
    metric = PolygonTurningSimilarity.getTurningSimilarity(p11, p12);
    System.out.println("Metric " + metric);
    Assert.assertTrue("Metric is not inside the tolerance", metric >= expected - tolerance && metric <= expected + tolerance);
  }
}
