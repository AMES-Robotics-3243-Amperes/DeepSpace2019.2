package frc.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogOutput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Spark;
import com.ctre.phoenix.ILoopable;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;

public class MotorController {

    WPI_TalonSRX driveM1 = new WPI_TalonSRX(2); // right
    WPI_TalonSRX driveM2 = new WPI_TalonSRX(3); // left
    WPI_TalonSRX driveM3 = new WPI_TalonSRX(1); // right
    WPI_TalonSRX driveM4 = new WPI_TalonSRX(4); // left

    Float[] alter = new Float[2];
    AnalogInput Darty = new AnalogInput(0);
    Spark collect = new Spark(3);

    public void setDart(Boolean paid, Boolean laid) {
        // push = push out & suck is pull in :D
        if (Darty.getValue() > 50 && Darty.getValue() < 3900) {
            if (!paid && laid) {
                collect.set(0.6);
            } else if (!laid && paid) {
                collect.set(-0.6);
            } else if (Darty.getValue() > 3900) {
                collect.set(-0.6);
            } else {
                collect.set(0);
            }
        }

    }

    public void setDartMotor() {

        System.out.println("Move 1: " + Darty.getValue());
        System.out.print("");
        System.out.println("Move 2: " + Darty.getValue());

    }

    // For Vision 2019
    public void setVision(boolean value, double x, double v, double area) {
        float KpSteering = 0.00002f;
        float KpSteering2 = 0.025f;
        float KpDistance = 0.0001f;
        float KpDistance2 = 0.08f;
        float min_command = 0.05f;
        float refArea = 4.50f;

        if (value == true) { // value reads the activation button from the joystick
            double heading_error = (float) x;
            double steering_adjust = 0.0d;
            double distance_error = (float) area;
            double distance_adjust = 0.0d;
            double maxDistAdjust = 0.8d;
            double maxAngAdjust = 1.0d;

            if (v == 0.0)
            // We don't see the target, seek for the target by spinning in place at a safe
            // speed.
            {
                steering_adjust = 0.25f;
            }

            else if (x > 0 && v != 0)
            // We do see the target, execute aiming code
            {
                steering_adjust = (KpSteering * Math.pow(heading_error, 3) + KpSteering2 * heading_error) - min_command;
            } else if (x < 0 && v != 0) {
                steering_adjust = (KpSteering * Math.pow(heading_error, 3) + KpSteering2 * heading_error) + min_command;
            }

            if (v == 0.0)
            // We don't see the target, seek for the target by spinning in place at a safe
            // speed.
            {
                distance_adjust = 0.0f;
            }
            if (area != 0) {

                distance_error = refArea - distance_error;
                distance_adjust = KpDistance * Math.pow(distance_error, 3) + KpDistance2 * distance_error;
            }

            distance_adjust = Math.tanh(distance_adjust) * maxDistAdjust;
            steering_adjust = Math.tanh(steering_adjust) * maxAngAdjust;
            driveM1.set(alter[0] + steering_adjust + distance_adjust);
            driveM2.set(alter[1] + steering_adjust - distance_adjust);
            driveM3.follow(driveM1);
            driveM4.follow(driveM2);

        }

    }

}
