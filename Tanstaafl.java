package RAM;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Tanstaafl - a robot by (your name here)
 */
public class Tanstaafl extends Robot
{
	/**
	 * run: Tanstaafl's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		// Robot main loop
		while(true) {
			// Replace the next 4 lines with any behavior you would like
			ahead(100);
			turnGunRight(360);
			back(100);
			turnGunRight(360);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		fire(3);
		if(e.getBearing() > -90 && e.getBearing() <= 90) {
			ahead(40);
		} else {
			back(40);
		}
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		double turnAmt = 0.0;
		
		turnAmt = normalRelativeAngleDegrees(90 - (getHeading() - e.getHeading()));
		turnRight(turnAmt);
		
		ahead(40);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		if(e.getBearing() > -90 && e.getBearing() <= 90) {
			back(40);
		} else {
			ahead(40);
		}
	}	


}
								
									