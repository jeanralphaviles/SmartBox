package model;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;

import view.CalibrationScreen;

public class Calibration {
  private double ax, ay, bx, by, dx, dy;

  public Calibration(ArrayList<Point> screenPoints, ArrayList<Point> cameraPoints) {
    if (screenPoints.size() != cameraPoints.size()) {
      throw new RuntimeException("Must calibrate with same number of points.");
    }
    if (screenPoints.size() < 3 || cameraPoints.size() < 3) {
      throw new RuntimeException("Calibration must be done with at least 3 points.");
    }
    findCoefficients(screenPoints, cameraPoints);
  }

  // Algorithm from http://www.ti.com/lit/an/slyt277/slyt277.pdf
  public void findCoefficients(ArrayList<Point> screenPoints, ArrayList<Point> cameraPoints) {
    double x1 = 0, x2 = 0, x3 = 0, y1 = 0, y2 = 0, y3 = 0, delta, dx1, dx2, dx3, dy1, dy2, dy3, a = 0, b = 0, c = 0,
        d = 0, e = 0;
    Dimension screenCoordinates = CalibrationScreen.getScreenSize();
    int numPoints = screenPoints.size();
    for (int i = 0; i < numPoints; ++i) {
      // Because of the way the algorithm works, we have to 'invert' the screen coordinates.
      double tempX = screenPoints.get(i).getX();
      double tempY = screenCoordinates.getHeight() - screenPoints.get(i).getY();
      // screenPoints.set(i, new Point((int) tempX, (int) tempY));
      x1 += screenPoints.get(i).getX() * cameraPoints.get(i).getX();
      x2 += screenPoints.get(i).getX() * cameraPoints.get(i).getY();
      x3 += screenPoints.get(i).getX();
      y1 += screenPoints.get(i).getY() * cameraPoints.get(i).getX();
      y2 += screenPoints.get(i).getY() * cameraPoints.get(i).getY();
      y3 += screenPoints.get(i).getY();
      a += cameraPoints.get(i).getX() * cameraPoints.get(i).getX();
      b += cameraPoints.get(i).getY() * cameraPoints.get(i).getY();
      c += cameraPoints.get(i).getX() * cameraPoints.get(i).getY();
      d += cameraPoints.get(i).getX();
      e += cameraPoints.get(i).getY();
    }
    delta = numPoints * (a * b - c * c) + 2 * c * d * e - a * e * e - b * d * d;
    dx1 = numPoints * (x1 * b - x2 * c) + e * (x2 * d - x1 * e) + x3 * (c * e - b * d);
    dx2 = numPoints * (x2 * a - x1 * c) + d * (x1 * e - x2 * d) + x3 * (c * d - a * e);
    dx3 = x3 * (a * b - c * c) + x1 * (c * e - b * d) + x2 * (c * d - a * e);
    dy1 = numPoints * (y1 * b - y2 * c) + e * (y2 * d - y1 * e) + y3 * (c * e - b * d);
    dy2 = numPoints * (y2 * a - y1 * c) + d * (y1 * e - y2 * d) + y3 * (c * d - a * e);
    dy3 = y3 * (a * b - c * c) + y1 * (c * e - b * d) + y2 * (c * d - a * e);
    ax = dx1 / delta;
    ay = dy1 / delta;
    bx = dx2 / delta;
    by = dy2 / delta;
    dx = dx3 / delta;
    dy = dy3 / delta;
  }

  public Point translatePoint(Point cameraPoint) {
    int screenX = (int) (ax * cameraPoint.getX() + bx * cameraPoint.getY() + dx);
    int screenY = (int) (ay * cameraPoint.getX() + by * cameraPoint.getY() + dy);
    System.out.println(cameraPoint.toString() + " => " + new Point(screenX, screenY));
    return new Point(screenX, screenY);
  }

}
