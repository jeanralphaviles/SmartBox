package view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class OSXCalibrationScreen extends CalibrationScreen {
  private static final long serialVersionUID = -6594520138147208306L;

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
    com.apple.eawt.FullScreenUtilities.setWindowCanFullScreen(this,true);
    com.apple.eawt.Application.getApplication().requestToggleFullScreen(this);
  }

}
