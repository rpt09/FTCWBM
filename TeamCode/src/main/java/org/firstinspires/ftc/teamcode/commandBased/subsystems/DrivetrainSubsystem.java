package org.firstinspires.ftc.teamcode.commandBased.subsystems;

import com.ThermalEquilibrium.homeostasis.Controllers.Feedback.AngleController;
import com.ThermalEquilibrium.homeostasis.Filters.FilterAlgorithms.KalmanFilter;
import com.ThermalEquilibrium.homeostasis.Parameters.PIDCoefficientsEx;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.lynx.LynxNackException;
import com.qualcomm.hardware.lynx.commands.core.LynxGetADCCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetADCResponse;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.classes.DeadzonePID;
import org.firstinspires.ftc.teamcode.classes.PIDOpenClosed;

public class DrivetrainSubsystem extends SubsystemBase {

    //DRIVE MOTORS
    private DcMotor m_fL;
    private DcMotor m_fR;
    private DcMotor m_rL;
    private DcMotor m_rR;

    //DRIVE VARIABLES
    private boolean fieldCentric = false;
    private double totalSpeed = 0.5;
    private double strafeMultiplier = 1;
    private double turnMultiplier = 1;
    private double forwardMultiplier = 1;
    private double heading;

    //TURNING VARIABLES
    private PIDCoefficientsEx turningCoeffs;
    private DeadzonePID turningPID;
    private AngleController turningController;
    private PIDOpenClosed turnPID;
    private double turningPIDDeadzone = 0.25;

    private final MecanumDrive drive;
    private final LocalizerSubsystem localizerSubsystem;
    private LynxModule chub;

    public DrivetrainSubsystem(final HardwareMap hwMap) {
        Motor fL = new Motor(hwMap, "fL", Motor.GoBILDA.RPM_312);
        Motor fR = new Motor(hwMap, "fR", Motor.GoBILDA.RPM_312);
        Motor rL = new Motor(hwMap, "rL", Motor.GoBILDA.RPM_312);
        Motor rR = new Motor(hwMap, "rR", Motor.GoBILDA.RPM_312);

        m_fL = fL.motor;
        m_fR = fR.motor;
        m_rL = rL.motor;
        m_rR = rR.motor;

        m_fL.setDirection(DcMotorSimple.Direction.REVERSE);
        m_rL.setDirection(DcMotorSimple.Direction.REVERSE);
        m_fR.setDirection(DcMotorSimple.Direction.REVERSE);
        m_rR.setDirection(DcMotorSimple.Direction.REVERSE);

        m_fL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        m_fR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        m_rL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        m_rR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        m_fL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        m_fR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        m_rL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        m_rR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        drive = new MecanumDrive(fL, fR, rL, rR);

        //TURNING PID
        turningCoeffs = new PIDCoefficientsEx(2.5, 0.4, 0.4, 0.25, 2, 0.5);
        turningPID = new DeadzonePID(turningCoeffs, Math.toRadians(turningPIDDeadzone));
        turningController = new AngleController(turningPID);
        turnPID = new PIDOpenClosed(turningController, 0.2);

        localizerSubsystem = new LocalizerSubsystem(hwMap);

        chub = hwMap.getAll(LynxModule.class).get(0); //better ways to do this
    }

    public void periodic() {
        heading = localizerSubsystem.getHeading();
    }


    public void setSpeedMultipliers(double strafeMultiplier, double forwardMultiplier, double turnMultiplier) {
        this.strafeMultiplier = strafeMultiplier;
        this.forwardMultiplier = forwardMultiplier;
        this.turnMultiplier = turnMultiplier;
    }

    public void fieldCentric(double strafeSpeed, double forwardSpeed, double turnSpeed) {
        drive.driveFieldCentric(
                strafeSpeed * strafeMultiplier,
                forwardSpeed * forwardMultiplier,
                turnSpeed * turnMultiplier,
                heading
        );
    }

    public void robotCentric(double strafeSpeed, double forwardSpeed, double turnSpeed) {
        drive.driveRobotCentric(
                strafeSpeed * strafeMultiplier,
                forwardSpeed * forwardMultiplier,
                turnSpeed * turnMultiplier
        );
    }

    public void pointCentric() {
        Vector2d vector = new Vector2d();
        vector.rotated(10);
    }

    public double getTurnAmount(double stick) {
        return turnPID.calculate(stick, Math.toRadians(localizerSubsystem.getHeading()));
    }

    public double getHeading() {
        return heading;
    }

    public double getParallelEncoder() {
        return localizerSubsystem.getParallelEncoder();
    }

    public double getPerpendicularEncoder() {
        return localizerSubsystem.getPerpendicularEncoder();
    }

    public double servoCurrent() {
        LynxGetADCCommand command = new LynxGetADCCommand(chub, LynxGetADCCommand.Channel.SERVO_CURRENT, LynxGetADCCommand.Mode.ENGINEERING);
        try {
            LynxGetADCResponse response = command.sendReceive();
            return response.getValue();
        } catch (InterruptedException|RuntimeException|LynxNackException e) {
            return 0;
        }
    }
}
