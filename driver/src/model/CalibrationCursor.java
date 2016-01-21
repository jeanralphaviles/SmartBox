package model;

import java.awt.AWTException;
import java.awt.Point;
import java.util.ArrayList;

import gnu.io.NRSerialPort;

public class CalibrationCursor extends Cursor {

  private int numClicks;
  private Point mousePosition;
  private ArrayList<Point> cameraPoints = new ArrayList<Point>();

  public CalibrationCursor(NRSerialPort connection, int numClicks) throws AWTException {
    super(connection);
    this.numClicks = numClicks;
  }

  @Override
  public void mouseMove(int x, int y) {
    super.mouseMove(x, y);
    this.mousePosition = new Point(x, y);
  }

  @Override
  public void pressMouse() {
    super.pressMouse();
    if (--numClicks == 0) {
      this.end();
    }
    cameraPoints.add(this.mousePosition);
  }

  @Override
  public void releaseMouse() {
    super.releaseMouse();
  }

  public ArrayList<Point> getCameraPoints() {
    return this.cameraPoints;
  }

}
