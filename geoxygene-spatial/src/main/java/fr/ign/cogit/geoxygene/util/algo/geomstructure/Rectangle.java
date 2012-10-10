package fr.ign.cogit.geoxygene.util.algo.geomstructure;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public class Rectangle {
  public double xmin ;
  public double xmax;
  public double ymin;
  public double ymax;

  public Rectangle() {
  }

  public static Rectangle boundingRectangle(ILineString L) {
      IDirectPositionList listepoints = L.coord();
      IDirectPosition point;
      Rectangle R = new Rectangle();
      R.xmin = listepoints.get(0).getX();
      R.xmax = listepoints.get(0).getX();
      R.ymin = listepoints.get(0).getY();
      R.ymax = listepoints.get(0).getY();

      for (int i=1;i<listepoints.size();i++) {
          point = listepoints.get(i);
          if ( point.getX() < R.xmin ) R.xmin = point.getX();
          if ( point.getX() > R.xmax ) R.xmax = point.getX();
          if ( point.getY() < R.ymin ) R.ymin = point.getY();
          if ( point.getY() > R.ymax ) R.ymax = point.getY();
      }
      return R;
  }

  public Rectangle dilates(double dilatation) {
      Rectangle R = new Rectangle();
      R.xmin = this.xmin - dilatation;
      R.xmax = this.xmax + dilatation;
      R.ymin = this.ymin - dilatation;
      R.ymax = this.ymax + dilatation;
      return R;
  }


  public boolean intersects(Rectangle R) {
      boolean intersecteX = false;
      boolean intersecteY = false;
      if (R.xmin < this.xmin && R.xmax > this.xmin ) intersecteX = true;
      if (R.xmin > this.xmin && R.xmin < this.xmax ) intersecteX = true;
      if (R.ymin < this.ymin && R.ymax > this.ymin ) intersecteY = true;
      if (R.ymin > this.ymin && R.ymin < this.ymax ) intersecteY = true;
      return (intersecteX && intersecteY );
  }
  
  public double getDiameter(){
    double dx = this.xmax - this.xmin;
    double dy = this.ymax - this.ymin;
    return Math.sqrt(dx*dx+dy*dy);
  }
}



