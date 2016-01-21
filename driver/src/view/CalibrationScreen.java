package view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class CalibrationScreen extends JFrame {

  private static final long serialVersionUID = 1669113969770992986L;

  public CalibrationScreen() {
    this.setResizable(false);
    this.setUndecorated(true);
    this.setSize(CalibrationScreen.getScreenSize());
  }

  @Override
  public void setVisible(boolean visible) {
    JLabel label = new JLabel("Click three non colinear points on the screen", SwingConstants.CENTER);
    label.setFont(new Font("Serif", Font.BOLD, 22));
    this.add(label);
    super.setVisible(visible);
    this.setAlwaysOnTop(visible);
  }

  public static Dimension getScreenSize() {
    return Toolkit.getDefaultToolkit().getScreenSize();
  }

}
