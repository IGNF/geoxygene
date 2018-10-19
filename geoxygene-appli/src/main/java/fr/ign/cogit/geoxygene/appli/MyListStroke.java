package fr.ign.cogit.geoxygene.appli;
import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MyListStroke extends JPanel {

    private  JList list;

    DefaultListModel model;

    private static int counter = 0;

    public MyListStroke(int nbr) {
        
      setLayout(new BorderLayout());
      model = new DefaultListModel();
      list = new JList(model);
      list.setSize(200, 200);
      JScrollPane pane = new JScrollPane(list);
       
      for (int i = 0+1; i < nbr+1; i++){
          model.addElement("UserStyle " + i);
          counter++;
      }
        
      
      add(pane);

    }

    public  JList getList() {
        return list;
    }

    public void setList(JList list) {
        this.list = list;
    }

    public DefaultListModel getModel() {
        return model;
    }

    public void setModel(DefaultListModel model) {
        this.model = model;
    }

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        MyListStroke.counter = counter;
    }
    
    public void updateList(int nbr){
        counter=0;
        model.removeAllElements();
        for (int i = 0+1; i < nbr+1; i++){
            model.addElement("UserStyle " + i);
            counter++;
        }
        
    }

   


    
}
