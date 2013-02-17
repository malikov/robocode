package RAM;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import static robocode.util.Utils.normalRelativeAngle;
import java.lang.Math;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Tanstaafl - a robot by  Team Alice
 */
public class Tanstaafl extends AdvancedRobot
{

	//this will be our target
	Enemy target;
	//this will be our firepower at any given time
	double firepower;
	//for angles and shit
	double PI = Math.PI;
	//current relative direction
	int direction;

	/**
	 * run: Tanstaafl's default behavior
	 */
	public void run() {
		
		// Initialization of the robot should be put here
		target = new Enemy();
		target.distance = 12398713;

		//independent pieces of robot
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		// Robot main loop
		while(true) {
			scan();
			firepower();
			fire(firepower);
			movement();
			execute();
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		//turn perpindicular to the enemy
		//this turns relative to the scanned enemy, not THE enemy
		//consider whether we want this to be for the target instead
		double turnAmt = 0.0;

		turnAmt = normalRelativeAngleDegrees(90 - (getHeading() - e.getHeading()));
		turnRight(turnAmt);
		//implement some sort of targeting method
		double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
		setTurnGunRightRadians(normalRelativeAngle(absoluteBearing - getGunHeadingRadians()));

		target.bearing = e.getBearingRadians();
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		// move forward or backwards

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

	public void firepower(){
		firepower = 400/target.distance;
	}

	public void scan(){
		turnRadarRightRadians(PI*2);	
	}

	public void movement(){

		//reverse direction every 20 ticks
	//	if(getTime()%20==0){
	//		direction*= -1;
			setAhead(250);
	//	}
		//circle around our target
		setTurnRightRadians(target.bearing + (PI/2));
	}

}
								
class Enemy{
	//use this to store information about our enemy
	//kinda like copying snippetbot's idea, but it's a very good one!

	String name;
	public double distance;
	public double bearing;
}
									
