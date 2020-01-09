/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/


package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
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
import edu.wpi.first.wpilibj.Spark;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {  

  DigitalInput topLimitSwitch = new DigitalInput(1);
  DigitalInput bottomLimitSwitch = new DigitalInput(0);
  public double upperEncoderLimit = 156;
  public double middleRocketPOS = 68.6;
  public double elevatorEncoderPos = 0;
  public double safetyLimitFactor = 50;
  

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private Timer autoDriveTimer = new Timer();
  public double speedModifier = 0.75;
  public double rampRate = 0.50;
  public double driveLoopCount = 0;
  public boolean autoDriveForward = false;
  public double loopCounter = 0;
  public double elevatorSpeed = 1;
  private CANEncoder elevatorEncoder;
  private CANEncoder driveEncoder;
  double elevatorHome = 0;

  //right side controllers
  private int canDeviceID1 = 16;
  private int canDeviceID3 = 17;
  private int canDeviceID2 = 18;

  //left side controllers
  private int canDeviceID4 = 19;
  private int canDeviceID5 = 20;
  private int canDeviceID6 = 21;

  // elevator ID
  private int canDeviceID7 = 12;

  //Drive motors
  private CANSparkMax motor1 = new CANSparkMax( canDeviceID1, MotorType.kBrushless);
  private CANSparkMax motor2 = new CANSparkMax( canDeviceID2, MotorType.kBrushless);
  private CANSparkMax motor3 = new CANSparkMax( canDeviceID3, MotorType.kBrushless);
  private SpeedControllerGroup spdc_right = new SpeedControllerGroup(motor1, motor2, motor3);

  private CANSparkMax motor4 = new CANSparkMax( canDeviceID4, MotorType.kBrushless);
  private CANSparkMax motor5 = new CANSparkMax( canDeviceID5, MotorType.kBrushless);
  private CANSparkMax motor6 = new CANSparkMax( canDeviceID6, MotorType.kBrushless);
  private SpeedControllerGroup spdc_left = new SpeedControllerGroup(motor4, motor5, motor6);

  //elevator motor
  private CANSparkMax elevator = new CANSparkMax(canDeviceID7, MotorType.kBrushless);

  //claw Lift/lower (clift)
  private Spark clift = new Spark(0);
   boolean clawUp = false;
  private Timer clawTimer = new Timer();

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


  CameraServer cServer = CameraServer.getInstance();
  CameraServer cServer2 = CameraServer.getInstance();
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
    //m_oi = new OI();
   
    cServer.startAutomaticCapture(0);
     cServer2.startAutomaticCapture(1);
    // cServer3.startAutomaticCapture(2);
    // cServer4.startAutomaticCapture(3);
    c.setClosedLoopControl(true);
    
    // elevatorEncoder.setPosition(10);
    elevatorEncoder = elevator.getEncoder();
    driveEncoder = motor2.getEncoder();

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
    autoDriveTimer.start();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {

    loopCounter++;
    myRobotDrive( -stick1.getRawAxis(1), -stick1.getRawAxis(5) );
  
  // auto forward at the beginning of the match 
    if(stick1.getRawButton(8)){
      autoDriveTimer.reset();
      autoDriveForward = true;
   }
    
    //if( autoDriveForward ) {
      
    //}

    while ( autoDriveForward == true && autoDriveTimer.get() < 0.8) {
      //autoDriveForward = true;
      spdc_right.set(-0.8);
      spdc_left.set(0.8);
    }
    while ( autoDriveForward == true && autoDriveTimer.get() < 1.5) {
      //autoDriveForward= true;
      spdc_right.set(-0.422);
      spdc_left.set(0.422);
    }

    autoDriveForward = false;

    if (!autoDriveForward) {
      myRobotDrive( -stick1.getRawAxis(1), -stick1.getRawAxis(5) );
    }

    if(stick1.getRawButton(8)) {

      if(driveEncoder.getPosition() <= 80 ) { 
        spdc_right.set(-0.8);
        spdc_left.set(0.8);
      }
      

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
   
    loopCounter++;

    
    
    
    myRobotDrive( -stick1.getRawAxis(1), -stick1.getRawAxis(5) );
  
    clawControl();  
  
    elevatorControl();

    precisionDrive();
      
  }//end teleopPeriodic()



  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public void clawControl(){

    //Claw Lift/Lower
    if ( stick2.getRawButton(5) ) {
      //left bumper - lifts claw
      clawTimer.start();
      while( clawTimer.get() < .5 ){
        //close
        clawA.set(true);
        clawB.set(false);
      }
      clawTimer.reset();
      while( clawTimer.get() < .7 ){
        clift.set(0.7);
      }
      clawUp = true;
      //Open Claw
      clawA.set(false);
      clawB.set(true);
    }
    else if ( stick2.getRawButton(6) ) {
      // right bumper - lowers claw
      clift.set(-0.5);
      clawA.set(true);
      clawB.set(false);
      clawUp = false;
    }
    else {
      clift.set(0.0);
      clawTimer.reset();
    }

    if(clawUp && !stick2.getRawButton(5) ) {
      clift.set(0.12);
    }

    //Claw Open/Close
    if( stick2.getRawButton(2) ){
      //Open - Button A, Green
      clawA.set(false);
      clawB.set(true);
   }
    if(  stick2.getRawButton(1) ) {
      //Close - Button B, Red
      clawA.set(true);
      clawB.set(false);
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
    // Drive Train speed limiter
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
      rampRate = 0.5;
      motor1.setOpenLoopRampRate(rampRate);
      motor2.setOpenLoopRampRate(rampRate);
      motor3.setOpenLoopRampRate(rampRate);
      motor4.setOpenLoopRampRate(rampRate);
      motor5.setOpenLoopRampRate(rampRate);
      motor6.setOpenLoopRampRate(rampRate);
    } 


    if( stick2.getRawButton(3) && bottomLimitSwitch.get() && elevatorEncoderPos > 0) {
      //down
      elevator.set(.80 );

      // limit approach safety
      if(elevatorEncoderPos < elevatorHome + 25) {
        elevator.set(.2);
      }
      else{
        elevator.set(.80);
      }
    }
    else if( stick2.getRawButton(4) && topLimitSwitch.get() && elevatorEncoderPos < 133 && !clawUp) {
      // up
      elevator.set(-1);

      // limit approach safety
      if(elevatorEncoderPos > elevatorHome + upperEncoderLimit - safetyLimitFactor){
        elevator.set(-.25);
      }
      else{
        elevatorSpeed = -1;
      }
    }
    else if(stick2.getRawButton(8)){
      
      if( elevatorEncoderPos < middleRocketPOS - 8) {
        elevator.set(-0.6);
      }
      else if(elevatorEncoderPos > middleRocketPOS + 8
      ) {
        elevator.set(0.6);
      }
      else {
        elevator.set(-0.02);
          
      }
    }  
    else {
      //hold robot in place
      elevator.set(-0.02);
    }

    if(!bottomLimitSwitch.get()) {
       elevatorEncoder.setPosition(0);
       elevatorHome = elevatorEncoderPos;
    }

  }//end elevatorControl()



//robot D-pad driving
  public void precisionDrive(){

    //D-pad POV values measured in degrees
    SmartDashboard.putNumber( "POV", stick1.getPOV() );
    while( stick1.getPOV() == 0 ){
      if((elevatorEncoder.getPosition())>80) {
        spdc_left.set(.12);
        spdc_right.set(-.12);
      }else{
      spdc_left.set( .16 );
      spdc_right.set( -.16 );
      }
    }
    while( stick1.getPOV() == 180 ){
      if((elevatorEncoder.getPosition())>80) {
        spdc_left.set(-.12);
        spdc_right.set(.12);
      }
      else{
      spdc_left.set( -.16 );
      spdc_right.set( .16 );}
    }
    while( stick1.getPOV() == 90 ){
      if((elevatorEncoder.getPosition())>80) {
        spdc_left.set(.12);
        spdc_right.set(.12);
      }
      else{
      spdc_left.set( .16 );
      spdc_right.set(.16 );}
    }
    while( stick1.getPOV() == 270 ){
      if((elevatorEncoder.getPosition())>80) {
        spdc_left.set(-.12);
        spdc_right.set(-.12);
      }
      else{
      spdc_left.set( -.16 );
      spdc_right.set( -.16 );
      }
    }

  }//end precisionDrive()

  
//begin drive code
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
        

        /*   //old section
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
        */  //old section
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

      if(elevatorEncoderPos < 40){
        spdc_left.set( leftSpeed );
        spdc_right.set( -rightSpeed );
      } 
      else if(elevatorSpeed < 80) {
        spdc_left.set( leftSpeed / 3 );
        spdc_right.set( -rightSpeed / 3 );
      }
      else {
        spdc_left.set( leftSpeed / 8 );
        spdc_right.set( -rightSpeed / 8 );
      }

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

  //possible manual drive centre
  /*if(stick1.getRawButton(12)){
    double stick1.getRawAxis(1)=0;
    double stick1.getRawAxis(5)=0;
  }*/
}
