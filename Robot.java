/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.vision.*;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import com.revrobotics.CANEncoder;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {  

  DigitalInput topLimitSwitch = new DigitalInput(0);
  DigitalInput bottomLimitSwitch = new DigitalInput(1);

 private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  public double speedModifier = 0.6;
 
  public double number = 1;
  public double loopCounter = 0;
  public double elevatorSpeed = .3;
  private CANEncoder m_encoder1;

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
  private SpeedControllerGroup spdc_right = new SpeedControllerGroup(motor1, motor2);

  private CANSparkMax motor4 = new CANSparkMax( canDeviceID4, MotorType.kBrushless);
  private CANSparkMax motor5 = new CANSparkMax( canDeviceID5, MotorType.kBrushless);
  private CANSparkMax motor6 = new CANSparkMax( canDeviceID6, MotorType.kBrushless);
  private SpeedControllerGroup spdc_left = new SpeedControllerGroup(motor4, motor5);

  Compressor c = new Compressor(0);
  
  Solenoid clawA = new Solenoid(1);
  Solenoid clawB=new Solenoid(2);

  private final DifferentialDrive
  robotDrive = new DifferentialDrive(spdc_left, spdc_right);

  public double leftSpeed = spdc_left.get();
  public double rightSpeed = spdc_right.get();
 
  private final Joystick stick1 = new Joystick(0);

  Double axisLx = 0.0;
  Double axisLy = 0.0;
  Double axisRx = 0.0;
  Double axisRy = 0.0;


  CameraServer cServer = CameraServer.getInstance();
  CameraServer cServer2 = CameraServer.getInstance();
  //CameraServer cServer3 = CameraServer.getInstance();
  //CameraServer cServer4 = CameraServer.getInstance();

  //private final Timer timer = new Timer();
  

 public void refreshJoystickAxes(){
     axisLx = stick1.getRawAxis(0);
     axisLy = stick1.getRawAxis(1);
     axisRx = stick1.getRawAxis(4);
     axisRy = stick1.getRawAxis(5);
 }

  public double getLeftstick() {
    return stick1.getRawAxis(1);
    }
  
  public double getRightstick() {
    return stick1.getRawAxis(5);
  }
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    //m_oi = new OI();
     cServer.startAutomaticCapture(0);
     cServer2.startAutomaticCapture(1);
    // cServer3.startAutomaticCapture(2);
    // cServer4.startAutomaticCapture(3);
    c.setClosedLoopControl(true);

    m_encoder1 = motor3.getEncoder();

      m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    SmartDashboard.putNumber("Speed Modifier", speedModifier );

    
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
   
     SmartDashboard.putNumber("Left Stick", getLeftstick() );
      SmartDashboard.putNumber("Right Stick", getRightstick() );
      SmartDashboard.putNumber("encoder Position", m_encoder1.getPosition());
    
      loopCounter++;

      refreshJoystickAxes();

    double elevatorh = 0;
      if(m_encoder1.getPosition() >= elevatorh) {
        robotDrive.tankDrive(0.5*(-getOptimalDriveSpeed(getLeftstick())), 0.5*(-getOptimalDriveSpeed (getRightstick())),false);
      }else {
      robotDrive.tankDrive(-getOptimalDriveSpeed(getLeftstick()),-getOptimalDriveSpeed (getRightstick()),false);
      }

      
      //robotDrive.curvatureDrive( -axisLy, axisLx, true);
      /*
      if( Math.abs(axis1x) > .07 || Math.abs(axis1y) >.07 ){
        robotDrive.arcadeDrive(-getLeftstick(),stick1.getRawAxis(0));
      }
      if( Math.abs(axis2x) > .07 || Math.abs(axis2y) >.07 ){
        robotDrive.arcadeDrive(-getRightstick()*.4,stick1.getRawAxis(4)*.4);
      }
      */
      
      //elevator control
      elevatorSpeed = elevatorSpeed * 1.02;
      if( stick1.getRawButton(4)) {
        motor3.set(elevatorSpeed);
      }else if( stick1.getRawButton(3)) {
        motor3.set(-elevatorSpeed);
      }else if(topLimitSwitch.get()) {
        motor3.set(-0.02);
      }else if(bottomLimitSwitch.get()) {
        motor3.set(-0.02);
      }else {
        motor3.set(-0.02);
      }
     
      if( stick1.getRawButton(1) ){
        //button A green
        clawA.set(true);
        clawB.set(false);
     }
      if(  stick1.getRawButton(2) ) {
        //button B red
        clawA.set(false);
        clawB.set(true);
      }

      //D-pad POV values measured in degrees
      SmartDashboard.putNumber( "POV", stick1.getPOV() );
      if( stick1.getPOV() == 0 ){
        robotDrive.tankDrive(.2,.2,false);
      }
      if( stick1.getPOV() == 180 ){
        robotDrive.tankDrive(-.2,-.2,false);
      }
      if( stick1.getPOV() == 90 ){
        robotDrive.tankDrive(.1,-.1,false);
      }
      if( stick1.getPOV() == 270 ){
        robotDrive.tankDrive(-.1,.1,false);
      }
    
     SmartDashboard.putNumber( "Left Speed", leftSpeed);
      SmartDashboard.putNumber( "Right Speed", rightSpeed);
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }


  
  public double getOptimalDriveSpeed(Double joystickValue) { 
    double x = joystickValue;
/*
    SmartDashboard.putNumber( "stickValue", x );
    if( Math.abs(x) < .07 ){
      return 0;
    }
  
    return .8 * x;
*/

      //Smokinghalo8 Made dis :>
  //  return x;
    
    //J@c0b

    if(x>0){
      return ((-43.4967)*(Math.pow(x,1.7))+(40.485)*(Math.pow(x,1.8))+(4.01411)*(x));
      }else{
        x=-x;
        return (-1)*((-43.4967)*(Math.pow(x,1.7))+(40.485)*(Math.pow(x,1.8))+(4.01411)*(x));
      }
    
  }
}
