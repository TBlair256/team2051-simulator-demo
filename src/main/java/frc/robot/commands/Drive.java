package frc.robot.commands;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Hardware;

/**
 * Simple command to drive the robot forward
 */
public class Drive extends CommandBase {
    private Hardware m_hardware;
    private DifferentialDrive m_drive;
    private double m_distance;
    private int m_targetLeftEncoder;
    private int m_targetRightEncoder;
    private boolean m_drivingForward;

    /**
     * Create a command
     * @param hardware The hardware to build
     * @param drive Differential drive to control on the robot
     * @param distance The total distance to go, in meters
     */
    public Drive(Hardware hardware, DifferentialDrive drive, double distance) {
        m_hardware = hardware;
        m_drive = drive;
        m_distance = distance;
        m_drivingForward = distance > 0;
    }

    @Override public void initialize() {
        m_targetLeftEncoder = m_hardware.leftEncoderCount() + distanceToEncoderCounts(m_distance);
        m_targetRightEncoder = m_hardware.rightEncoderCount() + distanceToEncoderCounts(m_distance);
    }

    @Override public void execute() {
        int leftEncoder = m_hardware.leftEncoderCount();
        int rightEncoder = m_hardware.rightEncoderCount();

        double speed = m_drivingForward ? 0.5 : -0.5;

        double leftSpeed = travelDone(leftEncoder, m_targetLeftEncoder) ? 0 : speed;
        double rightSpeed = travelDone(rightEncoder, m_targetRightEncoder) ? 0 : speed;
        m_drive.tankDrive(leftSpeed, rightSpeed);
    }

    @Override public void end(boolean interrupt) {
        m_drive.stopMotor();
    }

    @Override public boolean isFinished() {
        return 
        travelDone(m_hardware.leftEncoderCount(), m_targetLeftEncoder) &&
        travelDone(m_hardware.rightEncoderCount(), m_targetRightEncoder);
    }

    /**
     * Convert a distance (in meters) to encoder counts
     * @param distance
     * @return
     */
    private int distanceToEncoderCounts(double distance) {
        // distance / circumference is wheel rotations to go
        // then multiply by encoder ticks per rotation to get total encoder ticks
        return (int)(distance / Hardware.WHEEL_CIRCUMFERENCE_METERS * Hardware.ENCODER_TICKS_PER_REVOLUTION);
    }

    private boolean travelDone(int currentEncoder, int targetEncoder) {
        if (m_drivingForward) {
            return currentEncoder > targetEncoder;
        } else {
            return currentEncoder < targetEncoder;
        }
    }
}
