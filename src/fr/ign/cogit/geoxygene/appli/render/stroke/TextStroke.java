/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package fr.ign.cogit.geoxygene.appli.render.stroke;

import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

/**
 * Text Strokes are used to draw text symbolizers along a path. The path is
 * flattened using the static FLATNESS attribute.
 * <p>
 * This class is a modified version of Jerry Huxtable's TextStroke available at
 * <a
 * href=" URL#http://www.jhlabs.com/java/java2d/strokes/TextStroke.java ">http
 * ://www.jhlabs.com/java/java2d/strokes/TextStroke.java</a>
 */
public class TextStroke implements Stroke {
  /**
   * The text to draw.
   */
  private String text;
  /**
   * The font used to draw the text.
   */
  private Font font;
  /**
   * The flatness parameter used to approximate the curve.
   */
  private static final float FLATNESS = 1;
  /**
   * If true, stretch the text of the entire path.
   */
  private boolean stretchToFit = false;
  /**
   * If true, repeat the text over the path.
   */
  private boolean repeat = false;
  /**
   * If true, allow partial text, otherwise, only allow for the entire text to
   * be drawn.
   */
  private boolean partial = false;
  /**
   * Construct a text stroke.
   * <p>
   * By default, stretch to fit and repeat are set to false.
   * @param text the text to draw
   * @param font the font used to draw the text
   */
  public TextStroke(String text, Font font) {
    this(text, font, false, false, false);
  }
  /**
   * Construct a text stroke.
   * @param text the text to draw
   * @param font the font used to draw the text
   * @param stretchToFit true if the text should be stretched on the entire
   *          geometry
   * @param repeat true if the text should be repeat along the geometry
   */
  public TextStroke(String text, Font font, boolean stretchToFit, boolean repeat, boolean partial) {
    this.text = text;
    this.font = font;
    this.stretchToFit = stretchToFit;
    this.repeat = repeat;
    this.partial = partial;
  }
  @Override
  public Shape createStrokedShape(Shape shape) {
    // the result is a general path reflecting the given path
    GeneralPath result = new GeneralPath();
    if (this.text.isEmpty()) {
      // no text, nothing to draw, return an empty general path
      return result;
    }
    // create a font render context with anti aliasing and fractional metrics
    FontRenderContext frc = new FontRenderContext(null, true, true);
    // create a glyph vector representing the text in the given font
    GlyphVector glyphVector = this.font.createGlyphVector(frc, this.text);
    int numberOfGlyphs = glyphVector.getNumGlyphs();
    if (numberOfGlyphs == 0) {
      // no glyph, nothing to draw, return an empty general path
      // shouldn't happen since we already test if the text was empty
      return result;
    }
    GeneralPath currentShape = new GeneralPath();
    // the transform we will use for creating the transformed shapes
    AffineTransform transform = new AffineTransform();
    int currentCharIndex = 0;
    // factor used to stretch the space between characters if stretch to fit is
    // true. It is computed using the actual length of the shape divided by the
    // width of the glyph vector.
    float factor = this.stretchToFit ? TextStroke.length(shape)
        / (float) glyphVector.getLogicalBounds().getWidth() : 1.0f;
    PathIterator it = new FlatteningPathIterator(shape.getPathIterator(null),
        TextStroke.FLATNESS);
    float moveX = 0f, moveY = 0f, lastX = 0f, lastY = 0f, next = 0f, nextAdvance = 0f;
    // while there are still characters and there are still path segments
    while (currentCharIndex < numberOfGlyphs && !it.isDone()) {
      float coords[] = new float[6];
      int segmentType = it.currentSegment(coords);
      switch( segmentType ){
        case PathIterator.SEG_MOVETO:
          // store the new coordinates
          moveX = lastX = coords[0];
          moveY = lastY = coords[1];
          currentShape.moveTo(moveX, moveY);
          // half the advance of the current glyph
          next = nextAdvance = glyphVector.getGlyphMetrics(currentCharIndex)
              .getAdvance() * 0.5f;
          break;
        case PathIterator.SEG_CLOSE:
          // close the path: in this case, the coords is empty so we put the
          // last move as the new point...
          coords[0] = moveX;
          coords[1] = moveY;
          // ... and treat the segment type as a lineTo segment
        case PathIterator.SEG_LINETO:
          float currentX = coords[0];
          float currentY = coords[1];
          float dx = currentX - lastX;
          float dy = currentY - lastY;
          // the size of the current segment. Here we do not use the
          // Point2D.distance method since we actually need the dx and dy
          // variables
          float segmentLength = (float) Math.sqrt(dx * dx + dy * dy);
          // if the segment length is larger or equal to half the advance of the
          // current glyph, i.e. if there is place to draw the current glyph
          if (segmentLength >= next) {
            float ratio = 1.0f / segmentLength;
            float angle = (float) Math.atan2(dy, dx);
            // while there is more characters and place on the current segment
            // to draw them
            while (currentCharIndex < numberOfGlyphs && segmentLength >= next) {
              Shape currentGlyph = glyphVector
                  .getGlyphOutline(currentCharIndex);
              Point2D currentPosition = glyphVector
                  .getGlyphPosition(currentCharIndex);
              float px = (float) currentPosition.getX();
              float py = (float) currentPosition.getY();
              float x = lastX + next * dx * ratio;
              float y = lastY + next * dy * ratio;
              float currentAdvance = nextAdvance;
              // if there is more characters, the next advance is half the
              // advance of the next character; otherwise 0
              nextAdvance = this.repeat
                  || currentCharIndex < numberOfGlyphs - 1 ? glyphVector
                  .getGlyphMetrics((currentCharIndex + 1) % numberOfGlyphs)
                  .getAdvance() * 0.5f : 0;
              transform.setToTranslation(x, y);
              transform.rotate(angle);
              transform.translate(-px - currentAdvance, -py);
              // append the current glyph to the result without connecting it to
              // the previous one
              Shape transformedShape = transform.createTransformedShape(currentGlyph);
              currentShape.append(transformedShape, false);
              next += (currentAdvance + nextAdvance) * factor;
              currentCharIndex++;
              if (this.repeat) {
                if (currentCharIndex >= numberOfGlyphs) {
                  result.append(currentShape, false);
                  currentShape = new GeneralPath();
                }
                // if repeat, cycle over the length of the text/glyph vector
                currentCharIndex %= numberOfGlyphs;
              }
            }
          }
          next -= segmentLength;
          lastX = currentX;
          lastY = currentY;
          break;
      }
      it.next(); // next segment
    }
    if (currentCharIndex == numberOfGlyphs || this.partial) {
      result.append(currentShape, false);
    }
    return result;
  }
  /**
   * Compute the length of the given shape.
   * @param shape a path
   * @return the length of the given path
   */
  private static float length(Shape shape) {
    float pathLength = 0f; // the accumulated length
    PathIterator it = new FlatteningPathIterator(shape.getPathIterator(null),
        TextStroke.FLATNESS);
    float moveX = 0, moveY = 0, lastX = 0, lastY = 0;
    while (!it.isDone()) {
      float coords[] = new float[6];
      int segmentType = it.currentSegment(coords);
      switch(segmentType){
        case PathIterator.SEG_MOVETO:
          // store the new coordinates
          moveX = lastX = coords[0];
          moveY = lastY = coords[1];
          break;
        case PathIterator.SEG_CLOSE:
          // close the path: in this case, the coords is empty so we put the
          // last move as the new point...
          coords[0] = moveX;
          coords[1] = moveY;
          // ... treat the segment type as a lineTo segment
        case PathIterator.SEG_LINETO:
          pathLength += Point2D.distance(lastX, lastY, coords[0], coords[1]);
          lastX = coords[0];
          lastY = coords[1];
          break;
      }
      it.next(); // next segment
    }
    return pathLength;
  }
}
