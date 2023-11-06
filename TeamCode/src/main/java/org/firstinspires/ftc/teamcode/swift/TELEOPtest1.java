package org.firstinspires.ftc.teamcode.swift;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

@TeleOp
public class TELEOPtest1 extends LinearOpMode {
    static final double INCREMENT = 0.01;     // amount to ramp motor each CYCLE_MS cycle
    static final int CYCLE_MS = 50;     // period of each cycle
    static final double MAX_FWD = 1.0;     // Maximum FWD power applied to motor
    static final double MAX_REV = -1.0;     // Maximum REV power applied to motor
    private Servo leftGripper;
    private Servo rightGripper;
    private Servo launcherServo = null;

    @Override
    public void runOpMode() {

        waitForStart();

        // Define class members
        DcMotor motor;
        double power = 0;
        boolean rampUp = true;
        //Define variables for arm, wrist, and gripper
        Servo wristServo, leftGripper, rightGripper;
        DcMotor armMotor;
        //CRServo contServo;
        float leftY, rightY;
        double armPosition, gripPosition, contPower;
        double MIN_POSITION = 0, MAX_POSITION = 1;
        // called when init button is  pressed.


        armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        wristServo = hardwareMap.servo.get("wristServo");
        launcherServo = hardwareMap.get(Servo.class, "launcherServo");
        leftGripper = hardwareMap.get(Servo.class, "leftGripper");
        rightGripper = hardwareMap.get(Servo.class, "rightGripper");
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        leftY = gamepad2.left_stick_y * -1;
        rightY = gamepad2.right_stick_y * -1;
        armMotor.setPower(Range.clip(leftY, -1.0, 1.0));
        telemetry.addData("sticks", "  left=" + leftY + "  right=" + rightY);

        // check the gamepad buttons and if pressed, increment the appropriate position
        // variable to change the servo location.


        telemetry.addData("Mode", "waiting");

        while (opModeIsActive()) {
            telemetry.addData("Mode", "running");
            // check to see if we need to move the servo.
            if (gamepad1.left_bumper) {
                // move to 0 degrees.
                launcherServo.setPosition(0.3);
            } else if (gamepad1.right_bumper || gamepad1.left_bumper) {
                // move to 90 degrees.
                launcherServo.setPosition(1);
            }
            telemetry.addData("Servo Position", launcherServo.getPosition());
            telemetry.addData("Status", "Running");
            telemetry.update();

            // Declare Motors

            double y = gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = -gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = -gamepad1.right_stick_x;

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            //start Asher
            // move arm down on A button if not already at lowest position.
            if (gamepad2.y) {
                // Move servos in opposite directions when "y" is pressed
                leftGripper.setPosition(1); // Adjust the position value as needed
                rightGripper.setPosition(0); // Adjust the position value as needed
            } else if (gamepad2.x) {
                // Return servos to the center position when "x" is pressed
                leftGripper.setPosition(0.9); // Adjust the position value for the center position
                rightGripper.setPosition(0.1); // Adjust the position value for the center position
            }
                if (gamepad2.b) {
                    // Move servo in opposite directions when "y" is pressed
                    wristServo.setPosition(.4 ); // Adjust the position value as needed

                } else if (gamepad2.a) {
                    // Return servos to the center position when "x" is pressed
                    wristServo.setPosition(0); // Adjust the position value for the center position

                    telemetry.update();
                }
            }
        }
    }


