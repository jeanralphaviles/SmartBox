package model.platform;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import model.cursor.Cursor;

public abstract class PlatformReader implements Runnable {

  private boolean running = false;
  private String port;
  private ArrayList<Set<Cursor>> registeredCursors = new ArrayList<Set<Cursor>>();
  private Point[] blobs = new Point[Blob.values().length];
  private final Object runLock = new Object();
  private final Object portLock = new Object();

  public PlatformReader() {
    for (int i = 0; i < Blob.values().length; ++i) {
      registeredCursors.add(new HashSet<Cursor>());
      blobs[i] = new Point(1023, 1023);
    }
  }

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

  public void setPort(String port) {
    synchronized (portLock) {
      this.port = port;
    }
  }

  protected String getPort() {
    synchronized (portLock) {
      return this.port;
    }
  }

  public void registerCursor(Cursor cursor, Blob blob) {
    synchronized (registeredCursors) {
      registeredCursors.get(blob.ordinal()).add(cursor);
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
      for (int i = 0; i < registeredCursors.size(); ++i) {
        for (Cursor cursor : registeredCursors.get(i)) {
          cursor.alert();
        }
      }
    }
  }

  protected void writeBlob(Blob blob, Point coordinates) {
    Point old;
    synchronized (blobs) {
      old = blobs[blob.ordinal()];
      blobs[blob.ordinal()] = coordinates;
    }
    if (old.equals(coordinates)) {
      // Only notify if the coordinates changed.
      notifyCursors(blob);
    }
  }

  public Point readBlob(Blob blob) {
    synchronized (blobs) {
      return blobs[blob.ordinal()];
    }
  }

}
