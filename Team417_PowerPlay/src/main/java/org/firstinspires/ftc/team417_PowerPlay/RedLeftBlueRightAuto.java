package org.firstinspires.ftc.team417_PowerPlay;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.team417_PowerPlay.drive.SampleMecanumDrive;

// CHANGED NAME OF AUTO TO BETTER REFLECT WHAT IT DOES
@Autonomous (name="Red left || BLue right")
public class RedLeftBlueRightAuto extends LinearOpMode {

    // Coordinates for various tiles referenced on page 46 of:
    // https://www.firstinspires.org/sites/default/files/uploads/resource_library/ftc/game-manual-part-2-traditional.pdf
    // Robot's position starts at (0, 0) - all tile coordinates are in relation to this starting position
    private static final double Y_POS_AWAY_FROM_WAll = 8;
    private static final double X_POS_1 = 33;
    private static final double Y_POS_E = 20;
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);



        // CHANGED STARTS OF TRAJECT2 AND TRAJECT3 TO BE THE ENDS OF TRAJECT1 AND TRAJECT2
        //Red Left Blue Right Location 1
        Trajectory traject1 = drive.trajectoryBuilder(new Pose2d())
                .forward(Y_POS_AWAY_FROM_WAll)
                .build();
        Trajectory traject2 = drive.trajectoryBuilder(traject1.end())
                .strafeLeft(X_POS_1)
                .build();

        Trajectory traject3 = drive.trajectoryBuilder(traject2.end())
                .forward(Y_POS_E)
                .build();

        waitForStart();

        if (isStopRequested()) return;

        drive.followTrajectory(traject1);
        drive.followTrajectory(traject2);
        drive.followTrajectory(traject3);

    }
}
