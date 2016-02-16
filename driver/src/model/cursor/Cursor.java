package model.cursor;

import java.awt.Point;

import model.Calibration;
import model.platform.Blob;
import model.platform.PlatformReader;

public abstract class Cursor implements Runnable {
  private Calibration calibration;
  private PlatformReader reader;
  private Blob blob;
  private boolean running = false;
  private final Object calibrationLock = new Object();
  private final Object runLock = new Object();

  public Cursor(PlatformReader reader, Blob blob, Calibration calibration) {
    this.reader = reader;
    this.blob = blob;
    this.calibration = calibration;
  }

  public void run() {
    this.begin();
    this.reader.registerCursor(this, getBlob());
    while (isRunning()) {
      try {
        synchronized (this) {
          wait();
        }
      } catch (InterruptedException e) {
      }
      if (isRunning()) {
        Point mouse = this.reader.readBlob(this.blob);
        this.handleMouseUpdate(mouse);
      }
    }
    this.reader.unregisterCursor(this);
  }

  public synchronized void alert() {
    notify();
  }

  protected abstract void handleMouseUpdate(Point mouse);

  public void setCalibration(Calibration calibration) {
    synchronized (calibrationLock) {
      this.calibration = calibration;
    }
  }

  protected void begin() {
    synchronized (runLock) {
      this.running = true;
    }
  }

  public void stop() {
    synchronized (runLock) {
      this.running = false;
    }
    this.alert();
  }

  protected boolean isRunning() {
    synchronized (runLock) {
      return this.running;
    }
  }

  protected Calibration getCalibration() {
    synchronized (calibrationLock) {
      return this.calibration;
    }
  }

  protected Blob getBlob() {
    return blob;
  }

}
