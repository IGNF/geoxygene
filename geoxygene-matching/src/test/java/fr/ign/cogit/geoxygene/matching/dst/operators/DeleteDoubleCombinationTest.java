package fr.ign.cogit.geoxygene.matching.dst.operators;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;
import org.math.plot.utils.Array;

import com.vividsolutions.jts.util.Assert;

import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

/**
 * 
 *
 */
public class DeleteDoubleCombinationTest {
  
  @SuppressWarnings("unchecked")
  @Test
  public void testDeleteDouble1() {
    
    try {
      
      FileInputStream fos = new FileInputStream("E:\\Workspace\\geoxygene\\geoxygene-matching\\src\\test\\resources\\datakernel\\deleteset1.tmp");
      ObjectInputStream oos = new ObjectInputStream(fos);
      List<Pair<byte[], Float>> conditionnedlist = (List<Pair<byte[], Float>>) oos.readObject();
      System.out.println(conditionnedlist.size());
      Assert.equals(1572924, conditionnedlist.size());
      
      long begin = System.currentTimeMillis();
      
      // Convert to Map
      Map<Integer,Pair<byte[], Float>> map = new HashMap<Integer,Pair<byte[], Float>>();
      int cpt = 0;
      for (Pair<byte[], Float> i : conditionnedlist) { 
        map.put(cpt,i);
        cpt++;
      }
      Assert.equals(1572924, map.size());
      
      // Delete doublon
      Set<String> mySet = new HashSet<String>();
      for (Iterator<Entry<Integer, Pair<byte[], Float>>> itr = map.entrySet().iterator(); itr.hasNext();) {
          Map.Entry<Integer, Pair<byte[], Float>> entrySet = (Map.Entry<Integer, Pair<byte[], Float>>) itr.next();
          
          byte[] value = entrySet.getValue().getFirst();
          if (!mySet.add(Arrays.toString(value))) {
            
            int key = -1;
            for (Map.Entry entry: map.entrySet()) {
              if (Arrays.toString(value).equals(Arrays.toString(((Pair<byte[], Float>)entry.getValue()).getFirst()))) {
                  key = (int) entry.getKey();
                  break; // breaking because its one to one map
              }
            }
            if (key != -1) {
              map.put(key, new Pair(entrySet.getValue().getFirst(), 
                  map.get(key).getSecond() + entrySet.getValue().getSecond()));
            }

            // 
            itr.remove();               
          }
      }
      Assert.equals(1048597, map.size());
      
      // Convert to List
      conditionnedlist = new ArrayList<Pair<byte[], Float>>(map.values());
      Assert.equals(1048597, conditionnedlist.size());
      
      /*List<Pair<byte[], Float>> toremove = new ArrayList<Pair<byte[], Float>>();
      for (int i = 0; i < conditionnedlist.size() - 1; i++) {
        Pair<byte[], Float> pair = conditionnedlist.get(i);
        Pair<byte[], Float> pair2 = conditionnedlist.get(i + 1);
        if (Arrays.equals(pair.getFirst(), pair2.getFirst())) {
          pair2.setSecond(pair2.getSecond() + pair.getSecond());
          toremove.add(pair);
        }
      }
      conditionnedlist.removeAll(toremove);*/
      
      // System.out.println(conditionnedlist.size());
      Assert.equals(1048597, map.size());
      
      long end = System.currentTimeMillis();
      float time = ((float) (end-begin)) / 1000f;
      
      System.out.println("Time = " + time + " ? 259");
      
      oos.close();
    
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
  

}
