package fr.ign.cogit.geoxygene.matching.dst.operators;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

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
      Map<ByteArrayWrapper, Float> map = new HashMap<ByteArrayWrapper, Float>();
      for (Pair<byte[], Float> i : conditionnedlist) {
        Float f = map.get(new ByteArrayWrapper(i.getFirst()));
        if (f == null) {
          map.put(new ByteArrayWrapper(i.getFirst()), i.getSecond());
        } else {
          map.put(new ByteArrayWrapper(i.getFirst()), i.getSecond() + f);
        }
      }
      Assert.equals(1048597, map.size());
      
      // Convert to List
      // conditionnedlist = new ArrayList<Pair<byte[], Float>>(map.values());
      conditionnedlist = new ArrayList<Pair<byte[], Float>>();
      for (Iterator<Entry<ByteArrayWrapper, Float>> itr = map.entrySet().iterator(); itr.hasNext();) {
        Map.Entry<ByteArrayWrapper, Float> entrySet = (Map.Entry<ByteArrayWrapper, Float>) itr.next();
        conditionnedlist.add(new Pair<byte[], Float>(entrySet.getKey().getData(), entrySet.getValue()));
      }
      Assert.equals(1048597, conditionnedlist.size());
      
      long end = System.currentTimeMillis();
      float time = ((float) (end-begin)) / 1000f;
      
      System.out.println("Time = " + time + " ? 259");
      
      oos.close();
    
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
  public final class ByteArrayWrapper {
      
    private final byte[] data;

    public ByteArrayWrapper(byte[] data) {
      if (data == null) {
        throw new NullPointerException();
      }
      this.data = data;
    }
      
    public byte[] getData() {
      return data;
    }

    @Override
    public boolean equals(Object other) {
      if (!(other instanceof ByteArrayWrapper)) {
        return false;
      }
      return Arrays.equals(data, ((ByteArrayWrapper)other).data);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(data);
    }
  }

}
