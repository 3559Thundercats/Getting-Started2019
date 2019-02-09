/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  
  private double driveSpeed = 1;

  //right side controllers
  private int canDeviceID1 = 10;
  private int canDeviceID2 = 11;
  private int canDeviceID3 = 12;

  //left side controllers
  private int canDeviceID4 = 13;
  private int canDeviceID5 = 14;
  private int canDeviceID6 = 15;

  private CANSparkMax motor1 = new CANSparkMax( canDeviceID1, MotorType.kBrushless);
  private CANSparkMax motor2 = new CANSparkMax( canDeviceID2, MotorType.kBrushless);
  private CANSparkMax motor3 = new CANSparkMax( canDeviceID3, MotorType.kBrushless);
  private SpeedControllerGroup spdc_right = new SpeedControllerGroup(motor1, motor2, motor3);

  private CANSparkMax motor4 = new CANSparkMax( canDeviceID4, MotorType.kBrushless);
  private CANSparkMax motor5 = new CANSparkMax( canDeviceID5, MotorType.kBrushless);
  private CANSparkMax motor6 = new CANSparkMax( canDeviceID6, MotorType.kBrushless);
  private SpeedControllerGroup spdc_left = new SpeedControllerGroup(motor4, motor5, motor6);

  private final DifferentialDrive
  robotDrive = new DifferentialDrive(spdc_left, spdc_right);
  private final Joystick stick1 = new Joystick(0);

  private final Timer timer = new Timer();

  public double getLeftstick() {
    return stick1.getRawAxis(5);
    }
  
  public double getRightstick() {
    return stick1.getRawAxis(1);
  }
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    
  }
  

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    robotDrive.tankDrive(getOptimalDriveSpeed(getLeftstick()*driveSpeed) , getOptimalDriveSpeed(getRightstick()*driveSpeed), false);
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public void myDrive(Double leftInput, Double rightInput) {
   
    motor1.set(leftInput);
    motor2.set(leftInput);
    motor3.set(leftInput);

    motor4.set(rightInput);
    motor5.set(rightInput);
    motor6.set(rightInput);
  
  }

  // x = Joystick value
  public double getOptimalDriveSpeed(Double x) { 
    //Jay
    /*if(x>0){
      return (1/(1-(x-0.03))/25) + 0.25;
      }else{
        x=-x;
        return (-1)*(1/(1-(x-0.03))/25) + 0.25;
      }*/
      
    //J@c0b
    if(x>0){
      return ((-43.4967)*(Math.pow(x,1.7))+(40.485)*(Math.pow(x,1.8))+(4.01411)*(x));
      }else{
        x=-x;
        return (-1)*((-43.4967)*(Math.pow(x,1.7))+(40.485)*(Math.pow(x,1.8))+(4.01411)*(x));
      }
  }
}
