/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.lang.reflect.Array;
import java.util.Arrays;
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
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class TestRobot extends TimedRobot {  

  private static final int IMG_WIDTH = 320;
  private static final int IMG_HEIGHT = 240;  

//	private VisionThread visionThread;
  private double centerX = 0.0;
  private double centerY = 0.0;
  private Relay lightRing = new Relay(0);
  private boolean lightOn = false;
  private Timer ledTimer = new Timer();

	
	private final Object imgLock = new Object();
  
 // CameraServer cServer = CameraServer.getInstance();
  // vision inputs

  DigitalInput leftLineSensor = new DigitalInput(3);
  DigitalInput midLeftLineSensor = new DigitalInput(4);
  DigitalInput midLineSensor = new DigitalInput(5);
  DigitalInput midRightLineSensor = new DigitalInput(6);
  DigitalInput rightLineSensor = new DigitalInput(7);
  

  DigitalInput topLimitSwitch = new DigitalInput(0);
  DigitalInput bottomLimitSwitch = new DigitalInput(1);
  public double upperEncoderLimit = 156;
  public double middleRocketPOS = 80.6;
  public double elevatorEncoderPos = 0;
  public double safetyLimitFactor = 50;
  

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  public double speedModifier = 0.6;
  public double rampRate = 0.35;
  public double driveLoopCount = 0;
  public boolean autoDriveForward = true;
  public double loopCounter = 0;
  public double elevatorSpeed = .5;
  private CANEncoder elevatorEncoder;
  double elevatorHome = 0;

  //right side controllers
  private int canDeviceID1 = 13;
  private int canDeviceID3 = 14;
  private int canDeviceID2 = 15;

  //left side controllers
  private int canDeviceID4 = 10;
  private int canDeviceID5 = 11;
  private int canDeviceID6 = 12;

  // elevator ID
  private int canDeviceID7 = 16;

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

  //elevator motor
  private CANSparkMax elevator = new CANSparkMax(canDeviceID7, MotorType.kBrushless);

  //claw Lift/lower (clift)
  private Spark clift = new Spark(0);

  //Pneumatics
  Compressor c = new Compressor(0);
  Solenoid clawA = new Solenoid(1);
  Solenoid clawB=new Solenoid(2);

  public double leftSpeed = 0;
  public double rightSpeed = 0;
 
  private final Joystick stick1 = new Joystick(0);
  private final Joystick stick2 = new Joystick(1);

  Double axisLx = 0.0;
  Double axisLy = 0.0;
  Double axisRx = 0.0;
  Double axisRy = 0.0;



 // CameraServer cServer2 = CameraServer.getInstance();
  //CameraServer cServer3 = CameraServer.getInstance();
  //CameraServer cServer4 = CameraServer.getInstance();

  //private final Timer timer = new Timer();
  


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
    
   // cServer.startAutomaticCapture(0);
     
    

   //  cServer2.startAutomaticCapture(0);
    // cServer3.startAutomaticCapture(2);
    // cServer4.startAutomaticCapture(3);
    c.setClosedLoopControl(true);
    
    // elevatorEncoder.setPosition(10);
    elevatorEncoder = elevator.getEncoder();

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    SmartDashboard.putNumber("Speed Modifier", speedModifier );
    SmartDashboard.putNumber("Upper Encoder Limit", upperEncoderLimit );
    
    motor1.setIdleMode(IdleMode.kCoast);
    motor2.setIdleMode(IdleMode.kCoast);
    motor3.setIdleMode(IdleMode.kCoast);
    motor4.setIdleMode(IdleMode.kCoast);
    motor5.setIdleMode(IdleMode.kCoast);
    motor6.setIdleMode(IdleMode.kCoast);

    motor1.setOpenLoopRampRate(rampRate);
    motor2.setOpenLoopRampRate(rampRate);
    motor3.setOpenLoopRampRate(rampRate);
    motor4.setOpenLoopRampRate(rampRate);
    motor5.setOpenLoopRampRate(rampRate);
    motor6.setOpenLoopRampRate(rampRate);
    
    // elevator
    elevator.setOpenLoopRampRate(0.5);


    
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
      autoDriveForward = true;
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {

    loopCounter++;

    // auto forward at the beginning of the match
    if( autoDriveForward) {
      spdc_right.set(-0.8);
      spdc_left.set(0.8);
    }
    if ( getLeftstick() > 0.5 || getLeftstick() < -0.5 || loopCounter > 65 ) {
      autoDriveForward = false;
    }
    if (!autoDriveForward) {
      myRobotDrive( -stick1.getRawAxis(1), -stick1.getRawAxis(5) );
    }

    clawControl();  
     
    //elevatorControl();

    precisionDrive();
    
  }
  

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    SmartDashboard.putBoolean("line sensor", leftLineSensor.get());
   
    loopCounter++;

    
    if( stick2.getRawButton( 8 ) && ledTimer.get() > .2 ){
      ledTimer.reset();
      if ( lightOn ){
        //light is on - turn off
        lightOn = false;
        lightRing.set( Relay.Value.kOff );
      }
      else{
        //light is off - turn on
        lightOn = true;
        lightRing.set( Relay.Value.kForward );
      }
    }
  
    
    myRobotDrive( -stick1.getRawAxis(1), -stick1.getRawAxis(5) );
  
    clawControl();  
  
    elevatorControl();

    precisionDrive();

    lineDrive();

  }//end teleopPeriodic()

  public void lineDrive(){

    // NOTE TO SELF: Check to see if sensors are tripped by dropped hatch panels!

    while(stick1.getRawButton(8)) {

     // outer block - right sensor side
        if(rightLineSensor.get() == true){//turn robot right until it hits the midright sensor
            while(midRightLineSensor.get() != true){
              spdc_left.set(0.6);
              spdc_right.set(-0.3);
            }
        }
        //inner block right sensor side - gets the robot to turn until it bounces between the mid left sensor and mid sensor
        else if(midRightLineSensor.get() == true && midLineSensor.get() != true) {//turn robot right SLOWLY until it hits the mid sensor
          spdc_left.set(0.65);
          spdc_right.set(-0.5);
        }
        else if(midRightLineSensor.get() == true && midLineSensor.get() == true) {// continue to turn: guiding between mid left and mid sensors
          spdc_left.set(0.6);
          spdc_right.set(-0.5);
        }
        else if(midLineSensor.get() == true) {// if only the mid sensor is true: arc right until mid left sensor and mid sensor are hit
          spdc_left.set(0.6);
          spdc_right.set(-0.4);
        }

        // outer block - left sensor side
        else if(leftLineSensor.get() == true){// turn robot left until it hits the midleft sensor
            while(midLeftLineSensor.get() != true) {
              spdc_left.set(-0.3);
              spdc_right.set(0.6);
            }
        }
        // inner block left sensor side - bounces between mid and left to get to destination
        else if(midLeftLineSensor.get() == true && midLineSensor.get() == true) {//continue to arc right until mid sensor is NOT hit
          spdc_left.set(0.6);
          spdc_right.set(-0.5);  
        }
        else if(midLeftLineSensor.get() == true && midLineSensor.get() != true) {//arc robot left until mid line sensor is hit
          spdc_left.set(0.5);
          spdc_right.set(-0.6);
          }

        // error block - all three mid sensors are hit or all five sensors are hit - stops robot
        else if(midLineSensor.get() == true && midLeftLineSensor.get() == true && midRightLineSensor.get() == true || rightLineSensor.get() == true && midRightLineSensor.get() == true && midLineSensor.get() == true && midLeftLineSensor.get() == true && leftLineSensor.get() == true){
          spdc_left.set(0);
          spdc_right.set(0);
        }

        // robot reads no sensors: slow turn right
        else {
          spdc_left.set(0.55);
          spdc_right.set(-0.3);
        }
    }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public void clawControl(){

    //Claw Lift/Lower
    if ( stick2.getRawButton(5) ) {
      //left bumper
      clift.set(0.5);
    }
    else if ( stick2.getRawButton(6) ) {
      // right bumper
      clift.set(-0.5);
    }
    else {
      clift.set(0.0);
    }

    //Claw Open/Close
    if( stick2.getRawButton(1) ){
      //Open - Button A, Green
      clawA.set(true);
      clawB.set(false);
   }
    if(  stick2.getRawButton(2) ) {
      //Close - Button B, Red
      clawA.set(false);
      clawB.set(true);
    }

  }//end clawControl()



  public void elevatorControl(){

    SmartDashboard.putNumber("encoder Position", elevatorEncoder.getPosition());
    SmartDashboard.putBoolean("upper limit", topLimitSwitch.get());
    SmartDashboard.putBoolean("lower limit", bottomLimitSwitch.get());
    SmartDashboard.putNumber("Elevator Speed", elevatorSpeed );
    SmartDashboard.putBoolean("button 8", stick2.getRawButton(8));
    

    elevatorEncoderPos = Math.abs(elevatorEncoder.getPosition());
    SmartDashboard.putNumber("elevator encoder position", elevatorEncoderPos);

    if( elevatorEncoderPos > 60 ){
      rampRate = 2;
      motor1.setOpenLoopRampRate(rampRate);
      motor2.setOpenLoopRampRate(rampRate);
      motor3.setOpenLoopRampRate(rampRate);
      motor4.setOpenLoopRampRate(rampRate);
      motor5.setOpenLoopRampRate(rampRate);
      motor6.setOpenLoopRampRate(rampRate);
    }
    else{
      rampRate = .35;
    }


    if( stick2.getRawButton(3) && bottomLimitSwitch.get()) {
      //down
      elevator.set(elevatorSpeed );

      // limit approach safety
      if(elevatorEncoderPos < elevatorHome + safetyLimitFactor) {
        elevator.set(.15);
      }
      else{
        elevator.set(.5);
      }
    }else if( stick2.getRawButton(4) && topLimitSwitch.get()) {
      // up
      elevator.set(-0.15);

      // limit approach safety
      if(elevatorEncoderPos < elevatorHome + upperEncoderLimit - safetyLimitFactor){
        elevator.set(-.5);
      }
      else{
        elevatorSpeed = -.5;
      }
    }
    else if(stick1.getRawButton(1)){
      
      if( elevatorEncoderPos < middleRocketPOS - 5) {
        elevator.set(-0.5);
      }
      if(elevatorEncoderPos > middleRocketPOS + 5) {
        elevator.set(0.5);
      }
      else {
        elevator.set(-0.02);
          
      }
    } 
    else {
      elevator.set(-0.02);
    }

    if(!bottomLimitSwitch.get()) {
       elevatorEncoder.setPosition(0);
       elevatorHome = elevatorEncoderPos;
    }

  }//end elevatorControl()



  public void precisionDrive(){

    //D-pad POV values measured in degrees
    SmartDashboard.putNumber( "POV", stick1.getPOV() );
    while( stick1.getPOV() == 0 ){
      spdc_left.set( .22 );
      spdc_right.set( -.22 );
    }
    while( stick1.getPOV() == 180 ){
      spdc_left.set( -.22 );
      spdc_right.set( .22 );
    }
    while( stick1.getPOV() == 90 ){
      spdc_left.set( .22 );
      spdc_right.set(.22 );
    }
    while( stick1.getPOV() == 270 ){
      spdc_left.set( -.22 );
      spdc_right.set( -.22 );
    }

  }//end precisionDrive()


  public void myRobotDrive( double leftStick, double rightStick ){

    SmartDashboard.putNumber("Left Stick", leftStick );
    SmartDashboard.putNumber("Right Stick", rightStick );

 
      driveLoopCount++;
      if( driveLoopCount < 2){
        leftSpeed = speedModifier * leftStick;
        rightSpeed = speedModifier * rightStick;
      }
      
      //balance joystick values for smooth robot steering
      if( leftStick > 0 && rightStick > 0 ){
        //bot is moving forward
        if( leftStick > rightStick ){
          if ( rightSpeed < leftSpeed * .4 ){
            rightSpeed = leftSpeed * .4;
          }
        }
        else{
          //right stick is greater
          if ( leftSpeed < rightSpeed *.4 ){
            leftSpeed = rightSpeed * .4;
          }
        }
      }
      else if( leftStick > .05 && rightStick < 0 ||
          leftStick < 0 && rightStick > .05 ){
          //bot is rotating 
          leftSpeed = leftStick * .4;
          rightSpeed = rightStick * .4;
      }
      else{
        //bot is moving backward
        if( leftStick < rightStick ){
          if ( rightSpeed > leftSpeed * .4 ){
            rightSpeed = leftSpeed * .4;
          }
        }
        else{
          //right stick is less
          if ( leftSpeed > rightSpeed *.4 ){
            leftSpeed = rightSpeed * .4;
          }
        }
      }
      // End of Balance Code
      
      if( leftStick < .9 || rightStick < .9 ){
        //reset the loop count time delay
        driveLoopCount = 0;
      }
      if( driveLoopCount > 2 ){
         rightSpeed = rightSpeed * 1.03;
         leftSpeed = leftSpeed * 1.03;
      }

      spdc_left.set( leftSpeed );
      spdc_right.set( -rightSpeed );

      SmartDashboard.putNumber( "Left Speed", leftSpeed );
      SmartDashboard.putNumber( "Right Speed", rightSpeed );
    
  }//end myRobotDrive()



  public void myRobotDrive2( double leftStick, double rightStick ){

      driveLoopCount++;
      if( driveLoopCount < 2){
        leftSpeed = speedModifier * leftStick;
        rightSpeed = speedModifier * rightStick;
      }
      
      //balance joystick values for smooth robot steering
      if( leftStick > 0 && rightStick > 0 ){
        //bot is moving forward
        if( leftStick > rightStick ){
          if ( rightSpeed < leftSpeed * .4 ){
            rightSpeed = leftSpeed * .4;
          }
        }
        else{
          //right stick is greater
          if ( leftSpeed < rightSpeed *.4 ){
            leftSpeed = rightSpeed * .4;
          }
        }
      }
      else if( leftStick > .05 && rightStick < 0 ||
          leftStick < 0 && rightStick > .05 ){
          //bot is rotating 
          leftSpeed = leftStick * .4;
          rightSpeed = rightStick * .4;
      }
      else{
        //bot is moving backward
        if( leftStick < rightStick ){
          if ( rightSpeed > leftSpeed * .4 ){
            rightSpeed = leftSpeed * .4;
          }
        }
        else{
          //right stick is less
          if ( leftSpeed > rightSpeed *.4 ){
            leftSpeed = rightSpeed * .4;
          }
        }
      
      // End of Balance Code
      
      
      if( leftStick < .9 || rightStick < .9 ){
        //reset the loop count time delay
        driveLoopCount = 0;
      }
      if( driveLoopCount > 2 ){
         rightSpeed = rightSpeed * 1.03;
         leftSpeed = leftSpeed * 1.03;
      }

      spdc_left.set( leftSpeed );
      spdc_right.set( -rightSpeed );

      SmartDashboard.putNumber( "Left Speed", leftSpeed );
      SmartDashboard.putNumber( "Right Speed", rightSpeed );
    }
  }
  
}
