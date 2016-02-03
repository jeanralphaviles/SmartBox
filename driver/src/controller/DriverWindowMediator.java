package controller;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;

import model.DriverModel;

import view.CalibrationScreen;
import view.DriverWindow;

public class DriverWindowMediator {
  private DriverModel model;
  private CalibrationScreen calibrationScreen;
  private DriverWindow view;
  private JButton calibrateButton;
  private JButton enableButton;
  private JButton refreshButton;
  private JComboBox<String> portPicker;
  private boolean enabled;

  public DriverWindowMediator() {
    view = new DriverWindow();
    model = new DriverModel();
    enabled = false;
  }

  public void init() {
    view.init(this);
    view.setVisible(true);
    this.refresh();
  }

  public void registerPortPicker(JComboBox<String> portPicker) {
    this.portPicker = portPicker;
    this.portPicker.addItem("Select a Serial Port");
    this.portPicker.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        DriverWindowMediator.this.changePort();
      }
    });
  }

  public void registerRefreshButton(JButton refreshButton) {
    this.refreshButton = refreshButton;
    this.refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        DriverWindowMediator.this.refresh();
      }
    });
  }

  public void registerCalibrateButton(JButton calibrateButton) {
    this.calibrateButton = calibrateButton;
    this.calibrateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        DriverWindowMediator.this.calibrate();
      }
    });
  }

  public void registerEnableButton(JButton enableButton) {
    this.enableButton = enableButton;
    this.enableButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        DriverWindowMediator.this.enable();
      }
    });
  }

  protected void changePort() {
    String newPort = (String) this.portPicker.getSelectedItem();
    if (newPort != null && newPort.equals("Select a Serial Port") == false) {
      this.model.setPort(newPort);
      this.enableButton.setEnabled(true);
      this.calibrateButton.setEnabled(true);
    } else {
      this.enableButton.setEnabled(false);
      // this.calibrateButton.setEnabled(false);
    }
  }

  protected void refresh() {
    String currentlySelected = (String) this.portPicker.getSelectedItem();
    this.portPicker.removeAllItems();
    this.portPicker.addItem("Select a Serial Port");
    this.portPicker.setSelectedIndex(0);
    Iterator<String> availableSerialPorts = this.model.availableSerialPorts().iterator();
    while (availableSerialPorts.hasNext()) {
      String port = availableSerialPorts.next();
      this.portPicker.addItem(port);
      if (currentlySelected.equals(port)) {
        this.portPicker.setSelectedItem(port);
      }
    }
  }

  protected void calibrate() {
    this.calibrationScreen = new CalibrationScreen();
    this.model.beginCalibration(5);
    this.calibrationScreen.addMouseListener(new MouseListener() {
      private ArrayList<Point> points = new ArrayList<Point>();

      @Override
      public void mouseClicked(MouseEvent e) {
      }

      @Override
      public void mousePressed(MouseEvent e) {
        points.add(calibrationScreen.getTargetLocation());
        calibrationScreen.nextTarget();
        if (points.size() > 4) {
          calibrationScreen.setVisible(false);
          DriverWindowMediator.this.model.calibrate(points);
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }
    });
    calibrationScreen.setVisible(true);
  }

  protected void enable() {
    if (enabled) {
      this.model.disableCursor();
      enabled = false;
      this.enableButton.setText("Enable");
    } else {
      if (this.model.enableCursor()) {
        this.enableButton.setText("Disable");
        enabled = true;
      }
    }
  }

}
