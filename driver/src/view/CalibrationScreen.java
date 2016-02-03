package view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class CalibrationScreen extends JFrame {

  private static final long serialVersionUID = 1669113969770992986L;
  protected Point targetLocation;
  protected JLabel target;
  protected int width = 100, height = 100;
  protected double[][] scalers = {
    {0.15, 0.15},
    {0.85, 0.15},
    {0.15, 0.85},
    {0.85, 0.85},
    {0.5, 0.5}
  };
  protected int index = 0;

  public CalibrationScreen() {
    this.setResizable(false);
    this.setUndecorated(true);
    this.setSize(CalibrationScreen.getScreenSize());
  }

  @Override
  public void setVisible(boolean visible) {
    JLabel label = new JLabel("Click the targets on the screen", SwingConstants.CENTER);
    label.setFont(new Font("Serif", Font.BOLD, 22));
    this.add(label);
    try {
      Image targetPicture = ImageIO.read(new File("res/target.png")).getScaledInstance(width, height,
          Image.SCALE_SMOOTH);
      target = new JLabel(new ImageIcon(targetPicture));
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.setLayout(null);
    this.add(target);
    this.nextTarget();
    super.setVisible(visible);
    this.setAlwaysOnTop(visible);
  }

  public Point getTargetLocation() {
    Point centerTarget = new Point((int) (targetLocation.getX() + width / 2), (int) (targetLocation.getY() + width / 2));
    return centerTarget;
  }

  public void nextTarget() {
    Dimension screenSize = CalibrationScreen.getScreenSize();
    double maxX = screenSize.getWidth();
    double maxY = screenSize.getHeight();
    double[] scaler = scalers[index++];
    index %= 5;
    targetLocation = new Point((int) (maxX * scaler[0]), (int) (maxY * scaler[1]));
    this.target.setLocation(targetLocation);
    Dimension size = target.getPreferredSize();
    target.setBounds((int)targetLocation.getX(), (int)targetLocation.getY(), size.width, size.height);
  }

  public static Dimension getScreenSize() {
    return Toolkit.getDefaultToolkit().getScreenSize();
  }

}
