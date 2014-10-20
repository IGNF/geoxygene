package fr.ign.cogit.cartagen.spatialanalysis.network.railways;

import java.awt.Color;

import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public class ParallelStroke {

  private Stroke stroke;
  private int position = 0;
  private ILineString parallelGeom;
  private ParallelismEnding start, end;

  public Stroke getStroke() {
    return stroke;
  }

  public void setStroke(Stroke stroke) {
    this.stroke = stroke;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public ILineString getParallelGeom() {
    return parallelGeom;
  }

  public void setParallelGeom(ILineString parallelGeom) {
    this.parallelGeom = parallelGeom;
  }

  public ParallelismEnding getStart() {
    return start;
  }

  public void setStart(ParallelismEnding start) {
    this.start = start;
  }

  public ParallelismEnding getEnd() {
    return end;
  }

  public void setEnd(ParallelismEnding end) {
    this.end = end;
  }

  public ParallelStroke(Stroke stroke, int position, ILineString parallelGeom,
      ParallelismEnding start, ParallelismEnding end) {
    super();
    this.stroke = stroke;
    this.position = position;
    this.parallelGeom = parallelGeom;
    this.start = start;
    this.end = end;
  }

  /**
   * Returns a color to display the position of the stroke in the parallel
   * strokes group. Left strokes are green and right strokes are blue. Strokes
   * are 'darker' when closer to the central stroke of the group. Colors have
   * been chosen using the ColorBrewer.
   * @return
   */
  public Color getColor() {
    if (position == -1)
      return new Color(0, 109, 44);
    if (position == -2)
      return new Color(44, 162, 95);
    if (position == -3)
      return new Color(102, 194, 164);
    if (position == -4)
      return new Color(178, 226, 226);
    if (position == -5)
      return new Color(237, 248, 251);
    if (position == 1)
      return new Color(8, 81, 156);
    if (position == 2)
      return new Color(49, 130, 189);
    if (position == 3)
      return new Color(107, 174, 214);
    if (position == 4)
      return new Color(189, 215, 231);
    if (position == 5)
      return new Color(239, 243, 255);
    return Color.WHITE;
  }
}
