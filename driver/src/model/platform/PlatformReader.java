package model.platform;

import java.awt.Point;
import java.util.Set;

import gnu.io.NRSerialPort;

import model.cursor.Cursor;

public abstract class PlatformReader implements Runnable {

  private boolean running = false;
  private NRSerialPort port;
  @SuppressWarnings("unchecked")
  private Set<Cursor>[] registeredCursors = (Set<Cursor>[]) new Object[Blob.values().length];
  private Point[] blobs = new Point[Blob.values().length];
  private final Object runLock = new Object();
  private final Object portLock = new Object();

  public abstract void run();

  public void stop() {
    synchronized (runLock) {
      this.running = false;
    }
  }

  protected void begin() {
    synchronized (runLock) {
      this.running = true;
    }
  }

  protected boolean isRunning() {
    synchronized (runLock) {
      return this.running;
    }
  }

  public void setPort(NRSerialPort port) {
    synchronized (portLock) {
      this.port = port;
    }
  }

  protected NRSerialPort getPort() {
    synchronized (portLock) {
      return this.port;
    }
  }

  public void registerCursor(Cursor cursor, Blob blob) {
    synchronized (registeredCursors) {
      registeredCursors[blob.ordinal()].add(cursor);
    }
  }

  public void unregisterCursor(Cursor cursor) {
    synchronized (registeredCursors) {
      for (Set<Cursor> set : registeredCursors) {
        set.remove(cursor);
      }
    }
  }

  private void notifyCursors(Blob blob) {
    synchronized(registeredCursors) {
      for (Cursor cursor : registeredCursors[blob.ordinal()]) {
        cursor.notify();
      }
    }
  }

  protected void writeBlob(Blob blob, Point coordinates) {
    synchronized (blobs) {
      blobs[blob.ordinal()] = coordinates;
    }
    notifyCursors(blob);
  }

  public Point readBlob(Blob blob) {
    synchronized (blobs) {
      return blobs[blob.ordinal()];
    }
  }

}
