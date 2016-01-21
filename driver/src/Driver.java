import java.awt.EventQueue;

import controller.DriverWindowMediator;

public class Driver {

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        DriverWindowMediator controller = new DriverWindowMediator();
        controller.init();
      }
    });
  }

}
