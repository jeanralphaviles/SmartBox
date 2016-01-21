package model;

import java.awt.AWTException;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Set;

import gnu.io.NRSerialPort;

public class DriverModel {
  
  private String port = "";
  private final int baudRate = 9600;
  private Cursor cursor;
  private Calibration calibration = null;
  
  public void setPort(String port) {
    if (this.port.equals(port) == false) {
      this.port = port;
      this.disableCursor();
    }
  }

  public Set<String> availableSerialPorts() {
    return NRSerialPort.getAvailableSerialPorts();
  }

  public boolean enableCursor() {
    disableCursor();
    try {
      cursor = new Cursor(new NRSerialPort(this.port, this.baudRate));
      this.cursor.setCalibration(this.calibration);
      cursor.start();
      return true;
    } catch (AWTException e) {
      e.printStackTrace();
      return false;
    }
  }

  public void disableCursor() {
    if (cursor != null) {
      cursor.end();
    }
  }

  public void calibrate(ArrayList<Point> screenPoints) {
    ArrayList<Point> cameraPoints = ((CalibrationCursor)this.cursor).getCameraPoints();
    this.calibration = new Calibration(screenPoints, cameraPoints);
    this.enableCursor();
    System.out.print("Calibrated");
  }

  public void beginCalibration(int numClicks) {
    this.disableCursor();
    try {
      this.cursor = new CalibrationCursor(new NRSerialPort(this.port, this.baudRate), numClicks);
      this.cursor.start();
    } catch (AWTException e) {
      e.printStackTrace();
    }
  }

}
