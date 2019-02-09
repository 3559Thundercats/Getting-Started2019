package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
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
 * functions corresponding to each mode, as described in the TimedRobot
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends IterativeRobot {

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private double driveSpeed = 1;

  //right side controllers
  private int canDeviceID1 = 10;
  private int canDeviceID2 = 11;
  private int canDeviceID3 = 12;

public class Robot extends TimedRobot {

  Spark leftDrivef = new Spark(1);
  Spark leftDriveb = new Spark(2);
  private SpeedControllerGroup sc_left = new SpeedControllerGroup(leftDrivef, leftDriveb);
  Spark rightDrivef = new Spark(3);  
  Spark rightDriveb = new Spark(4);
  private SpeedControllerGroup sc_right= new SpeedControllerGroup(rightDrivef, rightDriveb);

  private static final int deviceID = 1;
  private CANSparkMax neo_motor;
  double driveSpeed = 1;
  private CANEncoder m_encoder;

  private final DifferentialDrive 
    robotDrive = new DifferentialDrive(sc_left, sc_right);
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
  //Button lshoulder1 = new JoystickButton(stick1, 5);
  //Button rshoulder1 = new JoystickButton(stick1, 6);

  private final Timer timer = new Timer();

  public double getLeftstick() {
  return stick1.getRawAxis(5);
    return stick1.getRawAxis(5);
    }

  public double getRightstick() {
    return stick1.getRawAxis(1);
  }


}
public double getRightstick() {
  return stick1.getRawAxis(1);
}

/**
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    neo_motor = new CANSparkMax(deviceID, MotorType.kBrushless);
    m_encoder = neo_motor.getEncoder();
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  /**
   * This function is run once each time the robot enters autonomous mode.
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void autonomousInit() {
    timer.reset();
    timer.start();
  public void robotPeriodic() {
  }

  /**
   * This function is called periodically during autonomous.
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
  public void autonomousPeriodic() {
    // Drive for 2 seconds
    if (timer.get() < 2.0) {
      robotDrive.arcadeDrive(0.5, 0.0); // drive forwards half speed
    } else {
      robotDrive.stopMotor(); // stop robot
    }
  public void autonomousInit() {
    timer.start();
    timer.reset();

    m_autoSelected = m_chooser.getSelected();
    // autoSelected = SmartDashboard.getString("Auto Selector",
    // defaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called once each time the robot enters teleoperated mode.
   * This function is called periodically during autonomous.
   */
  @Override
  public void teleopInit() {
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /**
   * This function is called periodically during teleoperated mode.
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    robotDrive.tankDrive(getLeftstick()*driveSpeed , getRightstick()*driveSpeed );
        if(m_encoder.getPosition() >= 1000 ) {
      neo_motor.set(0.90);
    }else {
      neo_motor.set(0.75);
    }

    SmartDashboard.putNumber("Encoder Position", m_encoder.getPosition());
    SmartDashboard.putNumber("Encoder Velocity", m_encoder.getVelocity());
  }

    //robotDrive.tankDrive(getOptimalDriveSpeed(getLeftstick()*driveSpeed) , getOptimalDriveSpeed(getRightstick()*driveSpeed), false);
    robotDrive.tankDrive(getOptimalDriveSpeed(getLeftstick()*driveSpeed) , getOptimalDriveSpeed(getRightstick()*driveSpeed), false);
    /*motor1.set(.7);
      motor2.set(.7);
      motor3.set(.7);
      motor4.set(-.7);
      motor5.set(-.7);
      motor6.set(-.7);*/
  }
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
  /*public double getOptimalDriveSpeed (double x) {
     return (1) / (1-(x-0.3)) - 1;  
  }*/

  public double getOptimalDriveSpeed (double x) {
    if(x>0){
    return ((-43.4967)*(Math.pow(x,1.7))+(40.485)*(Math.pow(x,1.8))+(4.01411)*(x));
    }else{
      x=-x;
      return (-1)*((-43.4967)*(Math.pow(x,1.7))+(40.485)*(Math.pow(x,1.8))+(4.01411)*(x));
    }
    //return (((-43.4967)*(x)^1.7)+((40.485)*(x)^1.8)+((4.01411)*x));
    //return (x*x) + (-0.7*x) + (0.3);
    /*when x>.1 y=x^3-bx^2+b
      when -.1<=x<=.1 y=0
      when x<.1 y=x^3+bx^2-b*/
  }


}
