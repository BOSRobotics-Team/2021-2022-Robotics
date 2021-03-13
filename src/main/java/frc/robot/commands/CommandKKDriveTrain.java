// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.*;
import frc.robot.subsystems.DriveTrain.DriveMode;

public class CommandKKDriveTrain extends CommandDriveTrain {

    private int _lastPOV = -1;

    private boolean scalingOn = false;
    private double scaling = 0.5;

    private double lastX = 0.0;
    private double lastY = 0.0;
    
    public CommandKKDriveTrain(DriveTrain driveTrain, XboxController controller) {
        super(driveTrain, controller);

    }

    // Called just before this Command runs the first time
    @Override
    public void initialize() {
        super.initialize();

        lastX = lastY = 0.0;
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    public void execute() {

        int pov = m_controller.getPOV();
        if (pov != _lastPOV)
        {
            if (pov == 0) {
                scaling += 0.1;
                if (scaling > 1.0)
                    scaling = 1.0;

                m_driveTrain.setDriveScaling(scalingOn ? scaling : 1.0);
            }
            else if (pov == 180) {
                scaling -= 0.1;
                if (scaling < 0.1)
                    scaling = 0.1;

                m_driveTrain.setDriveScaling(scalingOn ? scaling : 1.0);
            }
        }    
        _lastPOV = pov;

        boolean leftStickDown = m_controller.getStickButton(Hand.kLeft);
        if (!_wasLeftStickDown && leftStickDown) {
            scalingOn = !scalingOn;
            m_driveTrain.setDriveScaling(scalingOn ? scaling : 1.0);
        }
        _wasLeftStickDown = leftStickDown;

        double yLeftStick = -m_controller.getY(Hand.kLeft) * m_driveTrain.getDriveScaling();
        double y = (yLeftStick + lastY) / 2.0;

        lastY = yLeftStick;

        if (m_driveTrain.getDriveMode() == DriveMode.ARCADE) {
            double xRightStick = m_controller.getX(Hand.kRight) * m_driveTrain.getDriveScaling();
            double x = (xRightStick + lastX) / 2.0;
            lastX = xRightStick;

            m_driveTrain.driveArcade(y, x, m_driveTrain.getUseSquares());
        } else if (m_driveTrain.getDriveMode() == DriveMode.TANK) {
            double yRightStick = -m_controller.getY(Hand.kRight) * m_driveTrain.getDriveScaling();
            double x = (yRightStick + lastX) / 2.0;
            lastX = yRightStick;

            m_driveTrain.driveTank(y, x);
        } else if (m_driveTrain.getDriveMode() == DriveMode.CURVATURE) {
            double xRightStick = m_controller.getX(Hand.kRight) * m_driveTrain.getDriveScaling();
            boolean btnRightStick = m_controller.getStickButton(Hand.kRight);

            double x = (xRightStick + lastX) / 2.0;
            lastX = xRightStick;

            m_driveTrain.driveCurvature(y, x, btnRightStick);
        }

//        m_driveTrain.setOutput(m_controller);

        //m_driveTrain.logPeriodic();
    }

    // Called once after isFinished returns true
    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        m_driveTrain.setDriveScaling(1.0);
    }

    // Make this return true when this Command no longer needs to run execute()
   @Override
   public boolean isFinished() {
       return super.isFinished();
    }   
}