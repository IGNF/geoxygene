package fr.ign.cogit.geoxygene.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class CollectionsUtilTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testGetEditDistance2Lists() {
    List<Integer> list1 = new ArrayList<Integer>();
    list1.add(1);
    list1.add(2);
    list1.add(3);
    List<Integer> list2 = new ArrayList<Integer>();
    list2.add(1);
    list2.add(2);
    list2.add(3);
    List<Integer> list3 = new ArrayList<Integer>();
    list3.add(2);
    list3.add(2);
    list3.add(3);
    List<Integer> list4 = new ArrayList<Integer>();
    list4.add(2);
    list4.add(1);
    list4.add(3);
    List<Integer> list5 = new ArrayList<Integer>();
    list5.add(2);
    list5.add(1);
    list5.add(2);
    System.out.println(CollectionsUtil.getEditDistance2Lists(list1, list2));
    System.out.println(CollectionsUtil.getEditDistance2Lists(list1, list3));
    System.out.println(CollectionsUtil.getEditDistance2Lists(list1, list4));
    System.out.println(CollectionsUtil.getEditDistance2Lists(list1, list5));
    Assert.assertTrue(CollectionsUtil.getEditDistance2Lists(list1, list2) == 0);
    Assert.assertTrue(CollectionsUtil.getEditDistance2Lists(list1, list3) == 1);
    Assert.assertTrue(CollectionsUtil.getEditDistance2Lists(list1, list4) == 2);
    Assert.assertTrue(CollectionsUtil.getEditDistance2Lists(list1, list5) == 2);
  }

}
