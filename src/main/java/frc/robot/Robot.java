package frc.robot;

import javax.sound.sampled.BooleanControl;

//import com.sun.tools.javac.resources.version;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import com.ctre.phoenix.motorcontrol.ControlMode;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.j
 */
public class Robot extends TimedRobot {
  final String defaultAuto = "Default";
  final String customAuto = "My Auto";
  String autoSelected;
  SendableChooser<String> chooser = new SendableChooser<>();
  InputManager IM = new InputManager();
  MotorController MC = new MotorController();
  NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
  NetworkTableEntry camMode;
  NetworkTableEntry pipeline;
  NetworkTableEntry tx;
  NetworkTableEntry ty;
  NetworkTableEntry ta;
  NetworkTableEntry tv;
  NetworkTableEntry da;
  Double[] forward = new Double[2];
  Double[] left = new Double[2];
  Double[] right = new Double[2];
  long startTime = 0;

  final boolean compBot = true; // false is practice

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {

    camMode = table.getEntry("camMode");
    pipeline = table.getEntry("pipeline");
    tx = table.getEntry("tx");
    ty = table.getEntry("ty");
    tv = table.getEntry("tv");
    ta = table.getEntry("ta");
    
    camMode.setFlags(1);
    pipeline.setNumber(0);
    MC.setMotorControllers(compBot);
  }

  public void teleAuto() {
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    double v = tv.getDouble(0.0);
    double area = ta.getDouble(0.0);
    double encodeLefty = MC.leftE.getDistance();
    double encodeRighty = MC.rightE.getDistance();
    if (IM.getLimeVision()){
      pipeline.setNumber(0);
    } else{
        pipeline.setNumber(7);
      }

    // post to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);
    SmartDashboard.putNumber("Limelight Pipeline", (double) pipeline.getNumber(-1));
    SmartDashboard.putNumber("See Light?", v);
    SmartDashboard.putNumber("Left Encoder", encodeLefty);
    SmartDashboard.putNumber("Right Encoder", encodeRighty);
    SmartDashboard.putBoolean("Turbo Mode", IM.getToggleTurbo());
    SmartDashboard.putBoolean("Cargo Mode", IM.cargoDepositToggle);
    SmartDashboard.putBoolean("Cargo Start", IM.getCargoStart());
    
    MC.setVision(IM.getOrade(), x, v, area);
    //MC.setDart(IM.getPaid(), IM.getLaid(), IM.getPaidUpFront(), IM.getLaidUpFront());
    MC.setLift(IM.getLift());
    MC.setRotate(IM.getRotateConveyor());
    //MC.setCarMotor(IM.getoutPoke(), IM.getinPoke());
    MC.drive(IM.drivingJoysticks(), IM.getOrade(), IM.turbo(), IM.getToggleTurbo());
    MC.setBelt(IM.getBelter(), IM.getBeltee());
    
    IM.cargoDepositToggle = MC.FIFTYcent(IM.getCargoStart(), IM.cargoDepositToggle);

    MC.setGlow(IM.getGlow()); //for the lights under the robot
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    startTime = 0;
    if (chooser.getSelected() == defaultAuto) {
    }
  }

  /**
   * This function is called periodically during autonomous
   */
  @Override
  public void autonomousPeriodic() {

    if(startTime == 0){
      startTime = System.currentTimeMillis();
    }
    long timeNow = System.currentTimeMillis();
    timeNow = timeNow - startTime;
    if(timeNow <= 900){
      MC.driveM1.set(ControlMode.PercentOutput, -0.9);
      MC.driveM2.set(ControlMode.PercentOutput, 0.9);
      System.out.println(timeNow);
    } else {
      teleAuto();
    }
  }

  /**
   * This function is called periodically during operator control
   */
  @Override
  public void teleopInit() {
    IM.cargoDepositToggle = false;

  }

  public void teleopPeriodic() {
    teleAuto();
    int darty = MC.darty.getValue();
    int darty2 = MC.darty2.getValue();
    int darty3 = MC.darty3.getValue();
    SmartDashboard.putNumber("Back Dart Value", darty);
    SmartDashboard.putNumber("Right Dart Value", darty2);
    SmartDashboard.putNumber("Left Dart Value", darty3);
    MC.setDart(IM.getPaid(), IM.getLaid(), IM.getPaidUpFront(), IM.getLaidUpFront(), IM.getGlow());
  }

  /**
   * This function is called periodically during test mode
   */
  @Override
  public void testPeriodic() {

  }
}
