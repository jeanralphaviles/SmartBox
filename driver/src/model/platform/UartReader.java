package model.platform;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;

import gnu.io.NRSerialPort;

public class UartReader extends PlatformReader {
  private DataInputStream stream;
  private final int baud = 9600;

  @Override
  public void run() {
    this.begin();
    NRSerialPort serialPort = new NRSerialPort(this.getPort(), baud);
    serialPort.connect();
    stream = new DataInputStream(serialPort.getInputStream());
    int[] coordinates = new int[2];
    char[] num = new char[64];
    while (this.isRunning()) {
      char c = this.read();
      int ascii = c - '0';
      if (ascii >= 0 && ascii < Blob.values().length) {
        // Found a potential blob
        Blob currentBlob = Blob.fromInt(ascii);
        if (this.read() == ':') {
          // Confirmed blob, ignore next space character
          this.read();
          // Read two integers separated by space
          for (int i = 0; i < 2; ++i) {
            int j = 0;
            do {
              c = this.read();
              num[j++] = c;
            } while (Character.isDigit(c));
            try {
              coordinates[i] = Integer.parseInt(new String(num).trim());
            } catch (NumberFormatException e) {
              coordinates[0] = 1023;
              coordinates[1] = 1023;
              break;
            }
          }
          int x = coordinates[0], y = coordinates[1];
          this.writeBlob(currentBlob, new Point(x, y));
        }
      }
    }
    try {
      this.stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    serialPort.disconnect();
  }

  private char read() {
    try {
      while (stream.available() == 0) {
        Thread.sleep(1);
      }
      return (char) stream.readByte();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
    return '\0';
  }
;
}
