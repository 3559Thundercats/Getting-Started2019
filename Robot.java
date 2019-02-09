/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.kauailabs.*;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends IterativeRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

//navx Gyro necessities 
public Robot() {

  try{
    ahrs = new AHRS(SPI.Port.kMXP); 
  }catch (RuntimeException ex) {
    DriverStation.reportError("Error instantiating navX-MXP:  " + ex.getMessage(), true);
  }
}
// Displays Gyro input info on SmartDashboard for viewing
public void operatorControl() {
  while (isOperatorControl() && isEnabled()) {
      
      Timer.delay(0.020);		/* wait for one motor update time period (50Hz)     */
      
      boolean zero_yaw_pressed = stick1.getTrigger();
      if ( zero_yaw_pressed ) {
          ahrs.zeroYaw();
      }

      /* Display 6-axis Processed Angle Data                                      */
      SmartDashboard.putBoolean(  "IMU_Connected",        ahrs.isConnected());
      SmartDashboard.putBoolean(  "IMU_IsCalibrating",    ahrs.isCalibrating());
      SmartDashboard.putNumber(   "IMU_Yaw",              ahrs.getYaw());
      SmartDashboard.putNumber(   "IMU_Pitch",            ahrs.getPitch());
      SmartDashboard.putNumber(   "IMU_Roll",             ahrs.getRoll());
      
      /* Display tilt-corrected, Magnetometer-based heading (requires             */
      /* magnetometer calibration to be useful)                                   */
      
      SmartDashboard.putNumber(   "IMU_CompassHeading",   ahrs.getCompassHeading());
      
      /* Display 9-axis Heading (requires magnetometer calibration to be useful)  */
      SmartDashboard.putNumber(   "IMU_FusedHeading",     ahrs.getFusedHeading());

      /* These functions are compatible w/the WPI Gyro Class, providing a simple  */
      /* path for upgrading from the Kit-of-Parts gyro to the navx-MXP            */
      
      SmartDashboard.putNumber(   "IMU_TotalYaw",         ahrs.getAngle());
      SmartDashboard.putNumber(   "IMU_YawRateDPS",       ahrs.getRate());

      /* Display Processed Acceleration Data (Linear Acceleration, Motion Detect) */
      
      SmartDashboard.putNumber(   "IMU_Accel_X",          ahrs.getWorldLinearAccelX());
      SmartDashboard.putNumber(   "IMU_Accel_Y",          ahrs.getWorldLinearAccelY());
      SmartDashboard.putBoolean(  "IMU_IsMoving",         ahrs.isMoving());
      SmartDashboard.putBoolean(  "IMU_IsRotating",       ahrs.isRotating());

      /* Display estimates of velocity/displacement.  Note that these values are  */
      /* not expected to be accurate enough for estimating robot position on a    */
      /* FIRST FRC Robotics Field, due to accelerometer noise and the compounding */
      /* of these errors due to single (velocity) integration and especially      */
      /* double (displacement) integration.                                       */
      
      SmartDashboard.putNumber(   "Velocity_X",           ahrs.getVelocityX());
      SmartDashboard.putNumber(   "Velocity_Y",           ahrs.getVelocityY());
      SmartDashboard.putNumber(   "Displacement_X",       ahrs.getDisplacementX());
      SmartDashboard.putNumber(   "Displacement_Y",       ahrs.getDisplacementY());
      
      /* Display Raw Gyro/Accelerometer/Magnetometer Values                       */
      /* NOTE:  These values are not normally necessary, but are made available   */
      /* for advanced users.  Before using this data, please consider whether     */
      /* the processed data (see above) will suit your needs.                     */
      
      SmartDashboard.putNumber(   "RawGyro_X",            ahrs.getRawGyroX());
      SmartDashboard.putNumber(   "RawGyro_Y",            ahrs.getRawGyroY());
      SmartDashboard.putNumber(   "RawGyro_Z",            ahrs.getRawGyroZ());
      SmartDashboard.putNumber(   "RawAccel_X",           ahrs.getRawAccelX());
      SmartDashboard.putNumber(   "RawAccel_Y",           ahrs.getRawAccelY());
      SmartDashboard.putNumber(   "RawAccel_Z",           ahrs.getRawAccelZ());
      SmartDashboard.putNumber(   "RawMag_X",             ahrs.getRawMagX());
      SmartDashboard.putNumber(   "RawMag_Y",             ahrs.getRawMagY());
      SmartDashboard.putNumber(   "RawMag_Z",             ahrs.getRawMagZ());
      SmartDashboard.putNumber(   "IMU_Temp_C",           ahrs.getTempC());
      
      /* Omnimount Yaw Axis Information                                           */
      /* For more info, see http://navx-mxp.kauailabs.com/installation/omnimount  */
      AHRS.BoardYawAxis yaw_axis = ahrs.getBoardYawAxis();
      SmartDashboard.putString(   "YawAxisDirection",     yaw_axis.up ? "Up" : "Down" );
      SmartDashboard.putNumber(   "YawAxis",              yaw_axis.board_axis.getValue() );
      
      /* Sensor Board Information                                                 */
      SmartDashboard.putString(   "FirmwareVersion",      ahrs.getFirmwareVersion());
      
      /* Quaternion Data                                                          */
      /* Quaternions are fascinating, and are the most compact representation of  */
      /* orientation data.  All of the Yaw, Pitch and Roll Values can be derived  */
      /* from the Quaternions.  If interested in motion processing, knowledge of  */
      /* Quaternions is highly recommended.                                       */
      SmartDashboard.putNumber(   "QuaternionW",          ahrs.getQuaternionW());
      SmartDashboard.putNumber(   "QuaternionX",          ahrs.getQuaternionX());
      SmartDashboard.putNumber(   "QuaternionY",          ahrs.getQuaternionY());
      SmartDashboard.putNumber(   "QuaternionZ",          ahrs.getQuaternionZ());
      
      /* Connectivity Debugging Support                                           */
      SmartDashboard.putNumber(   "IMU_Byte_Count",       ahrs.getByteCount());
      SmartDashboard.putNumber(   "IMU_Update_Count",     ahrs.getUpdateCount());
  }
}

/**
 * This function is run when the robot is first started up and should be
 * used for any initialization code.
 */

   /* This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
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
    m_autoSelected = m_chooser.getSelected();
    // autoSelected = SmartDashboard.getString("Auto Selector",
    // defaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
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
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
