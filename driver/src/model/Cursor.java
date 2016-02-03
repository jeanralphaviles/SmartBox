package model;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.DataInputStream;
import java.io.IOException;

import gnu.io.NRSerialPort;

public class Cursor extends Thread {
  private NRSerialPort connection;
  private Robot control;
  private boolean running = false;
  private DataInputStream input;
  private Calibration calibration;
  // some calibration data

  public Cursor(NRSerialPort connection) throws AWTException {
    this.connection = connection;
    this.control = new Robot();
  }

  public void mouseMove(int x, int y) {
    // Translate X, Y with calibration
    if (this.getCalibration() != null) {
      Point translated = this.calibration.translatePoint(new Point(x, y));
      x = (int) (translated.getX());
      y = (int) (translated.getY());
    }
    this.control.mouseMove(x, y);
  }

  public void pressMouse() {
    this.control.mousePress(InputEvent.BUTTON1_DOWN_MASK);
  }

  public void releaseMouse() {
    this.control.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
  }

  public synchronized void setCalibration(Calibration calibration) {
    this.calibration = calibration;
  }

  public synchronized Calibration getCalibration() {
    return this.calibration;
  }

  @Override
  public void run() {
    this.setRunning(true);
    this.connection.connect();
    boolean seen = false;
    this.input = new DataInputStream(this.connection.getInputStream());
    int[] coordinates = new int[2];
    while (this.isRunning()) {
      char c = (char) this.readChar();
      if (c == '0') {
        c = (char) this.readChar();
        if (c == ':') {
          // Next two integers are coordinates
          c = this.readChar(); // Ignore space
          for (int i = 0; i < 2; ++i) {
            StringBuilder num = new StringBuilder();
            do {
              c = this.readChar();
              num.append(c);
            } while (c <= '9' && c >= '0');
            try {
              coordinates[i] = Integer.parseInt(num.toString().trim());
            } catch (NumberFormatException e) {
              coordinates[0] = 1023;
              coordinates[1] = 1023;
              break;
            }
          }
          int x = coordinates[0], y = coordinates[1];
          if (x == 1023 && y == 1023) {
            if (seen == true) {
              this.releaseMouse();
            }
            seen = false;
          } else {
            this.mouseMove(coordinates[0], coordinates[1]);
            if (seen == false) {
              this.pressMouse();
            }
            seen = true;
          }
        }
      }
    }
    try {
      this.input.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    connection.disconnect();
  }

  public void end() {
    this.setRunning(false);
  }

  protected synchronized void setRunning(boolean running) {
    this.running = running;
  }

  protected synchronized boolean isRunning() {
    return this.running;
  }

  protected char readChar() {
    try {
      while (input.available() == 0) {
      }
      return (char) input.readByte();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return '\0';
  }

}
