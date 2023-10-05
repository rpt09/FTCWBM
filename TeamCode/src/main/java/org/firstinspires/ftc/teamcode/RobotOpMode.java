package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.internal.ui.GamepadUser;

import java.util.concurrent.TimeUnit;

public abstract class RobotOpMode extends OpMode {


    // Used in the endTime parameter in moveRobot()
    public static long STOP_NEVER = Long.MAX_VALUE;
    DcMotor leftFrontDrive, leftBackDrive, rightFrontDrive, rightBackDrive;
    ElapsedTime elapsedTime;


    @Override
    public void init() {
        leftFrontDrive = hardwareMap.get(DcMotor.class, "fl_drv");
        leftBackDrive = hardwareMap.get(DcMotor.class, "bl_drv");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "fr_drv");
        rightBackDrive = hardwareMap.get(DcMotor.class, "br_drv");
        elapsedTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);
    }

    @Override
    public final void loop() {
        robotloop();
        telemetry.update();
    }

    public abstract void robotloop();

    public boolean moveRobot(double axial, double lateral, double yaw) {
        return moveRobot(axial, lateral, yaw, STOP_NEVER);
    }

    /**
     * Sets the power of the robot's drive motors according to the parameters
     *
     * @param axial FORWARD AND BACKWARD
     * @param lateral STRAFING, SIDE TO SIDE
     * @param yaw ROTATION
     * @param endTime the nanoTime that the robot should stop doing the move action
     * @return if the nanoTime of elapsedTime has <strong>NOT</strong> exceeded endTime.
     */
    public boolean moveRobot(double axial, double lateral, double yaw, long endTime) {

        double max;
        if(elapsedTime.now(TimeUnit.NANOSECONDS) >= endTime) {
            resetDriveMotors();
            return false;
        }

        double leftFrontPower  = axial + lateral + yaw;
        double rightFrontPower = axial - lateral - yaw;
        double leftBackPower   = axial - lateral + yaw;
        double rightBackPower  = axial + lateral - yaw;

        max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower  /= max;
            rightFrontPower /= max;
            leftBackPower   /= max;
            rightBackPower  /= max;
        }
        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);
        return true;
    }

    public void resetDriveMotors() {
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    public void gamePadMoveRobot() {
        if(gamepad1 == null) {
            return;
        }

        double axial   = -gamepad1.left_stick_y;
        double lateral =  gamepad1.left_stick_x;
        double yaw     =  gamepad1.right_stick_x;
        // sets the power of the motors accordingly
        moveRobot(axial, lateral, yaw, Long.MAX_VALUE);
    }
}

