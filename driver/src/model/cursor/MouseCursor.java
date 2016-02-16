package model.cursor;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

import model.Calibration;
import model.platform.Blob;
import model.platform.PlatformReader;

public class MouseCursor extends Cursor {
  private boolean seen = false;
  private Robot robot;

  public MouseCursor(PlatformReader reader, Blob blob, Calibration calibration) {
    super(reader, blob, calibration);
    try {
      this.robot = new Robot();
    } catch (AWTException e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  @Override
  protected void handleMouseUpdate(Point mouse) {
    if (!validPoint(mouse)) {
      if (seen) {
        this.releaseMouse();
        seen = false;
      }
      return;
    }
    this.moveMouse(mouse);
    if (!seen) {
      this.pressMouse();
      seen = true;
    }
  }

  protected boolean validPoint(Point mouse) {
    return (int) mouse.getX() != 1023 && (int) mouse.getY() != 1023;
  }

  protected void moveMouse(Point mouse) {
    if (this.getCalibration() != null) {
      mouse = this.getCalibration().translatePoint(mouse);
    }
    int x = (int) mouse.getX();
    int y = (int) mouse.getY();
    this.robot.mouseMove(x, y);
  }

  protected void pressMouse() {
    this.getRobot().mousePress(InputEvent.BUTTON1_MASK);
  }

  protected void releaseMouse() {
    this.getRobot().mouseRelease(InputEvent.BUTTON1_MASK);
  }

  protected Robot getRobot() {
    return this.robot;
  }

}
