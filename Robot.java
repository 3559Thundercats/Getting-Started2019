/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */



public class Robot extends TimedRobot {
  Spark leftDrivef = new Spark(1);
  Spark leftDriveb = new Spark(2);
  private SpeedControllerGroup sc_left = new SpeedControllerGroup(leftDrivef, leftDriveb);
  Spark rightDrivef = new Spark(3);  
  Spark rightDriveb = new Spark(4);
  private SpeedControllerGroup sc_right= new SpeedControllerGroup(rightDrivef, rightDriveb);

  private final DifferentialDrive 
    robotDrive = new DifferentialDrive(sc_left, sc_right);
  private final Joystick stick1 = new Joystick(0);
  Button lshoulder1 = new JoystickButton(stick1, 5);
  Button rshoulder1 = new JoystickButton(stick1, 6);

  private final Timer timer = new Timer();

  public double getLeftstick() {
  return stick1.getRawAxis(5);
}
public double getRightstick() {
  return stick1.getRawAxis(1);
}

public void drive(double left, double right) {
  robotDrive.tankDrive(speedModifier*left, speedModifier*right);
}

public void modifySpeed(double newspeedModifier) {
  this.speedModifier = newspeedModifier;
}
private double speedModifier = 0.7;

/**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
  }

  /**
   * This function is run once each time the robot enters autonomous mode.
   */
  @Override
  public void autonomousInit() {
    timer.reset();
    timer.start();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    // Drive for 2 seconds
    if (timer.get() < 2.0) {
      robotDrive.arcadeDrive(0.5, 0.0); // drive forwards half speed
    } else {
      robotDrive.stopMotor(); // stop robot
    }
  }

  /**
   * This function is called once each time the robot enters teleoperated mode.
   */
  @Override
  public void teleopInit() {
  }

  /**
   * This function is called periodically during teleoperated mode.
   */
  @Override
  public void teleopPeriodic() {
    robotDrive.tankDrive(getLeftstick(), getRightstick());

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
