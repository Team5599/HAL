/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.ColorSensorV3;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //Declaring Objects
    //Motors For Moving Left Side Of Robot
  Spark leftOne = new Spark(1);
  Spark leftTwo = new Spark(5);

    //Motors For Moving Right Side Of Robot
  Spark rightOne = new Spark(0);
  Spark rightTwo = new Spark(6);

    //Motor For Intake
  Spark intake = new Spark(4);

    //Falcon 500 Motor Example
  //TalonFX motor = new TalonFX(0);
    //Example Code To Move It
  //motor.set(ControlMode.PercentOutput, 1);

    //Port Object
  I2C.Port sensor_port = I2C.Port.kOnboard;

    //Color Sensor Example
  ColorSensorV3 sensor = new ColorSensorV3(sensor_port);

    //Grouping Motors To Respctive Sides Of Robot
  SpeedControllerGroup leftDrive = new SpeedControllerGroup(leftOne, leftTwo);
  SpeedControllerGroup rightDrive = new SpeedControllerGroup(rightOne, rightTwo);

    //Drive
  DifferentialDrive driveTrain = new DifferentialDrive(leftDrive, rightDrive);

    //Controller Objects
  XBoxController controller = new XBoxController(0);

    //Objects For Limelight (Gets Values)
  NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
  NetworkTableEntry tx = table.getEntry("tx");
  NetworkTableEntry ty = table.getEntry("ty");
  NetworkTableEntry ta = table.getEntry("ta");

  /**
   * This function is run when the robot is first started up and should be
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
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
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
    double x = tx.getDouble(0.0);
    
    //"Magic Button" (Auto Aligns Robot To Top (Outer) Port)
    if (controller.getXButton()) {
      if (x < -1) driveTrain.tankDrive(-0.5, 0.5); 
      else if (x > 8) driveTrain.tankDrive(0.5, -0.5);
    } else {
      driveTrain.tankDrive(-(controller.getLeftThumbstickY()), -(controller.getRightThumbstickY()));
    }

    if (controller.getYButton()) {
      if (sensor.getRed() > sensor.getGreen()) System.out.println("Red?");
      else if (sensor.getBlue() > sensor.getGreen()) System.out.println("Blue?");
      else if ((sensor.getRed() > sensor.getBlue()) & (sensor.getGreen() > sensor.getBlue())) System.out.println("Yellow?");
      else if ((sensor.getGreen() > sensor.getRed()) & (sensor.getGreen() > sensor.getBlue())) System.out.println("Green");
    }
    
    //Intake Control
    if (controller.getAButton()) intake.set(-0.5);
    else if (controller.getBButton()) intake.set(0.5);
    else intake.set(0);
    
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
