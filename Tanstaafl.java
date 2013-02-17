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

	double FIELD_HEIGHT = 0.0;
	double FIELD_WIDTH = 0.0;

	//this will be our target
	Enemy target;

	//this will be our firepower at any given time
	double firepower;

	//for angles and shit
	double PI = Math.PI;

	//current relative direction
	int direction = 1;

	/**
	 * run: Tanstaafl's default behavior
	 */
	public void run() {
		
		// Initialization of the robot should be put here
		target = new Enemy();
		target.distance = 12398713;

		FIELD_HEIGHT = getBattleFieldHeight();
		FIELD_WIDTH = getBattleFieldWidth();

		//independent pieces of robot
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		// Robot main loop
		while(true) {
			movement();
			scan();
			firepower();
			fire(firepower);
			execute();
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		//implement some sort of targeting method
		double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
		setTurnGunRightRadians(normalRelativeAngle(absoluteBearing - getGunHeadingRadians()));

		target.bearing = e.getBearingRadians();
		target.distance = e.getDistance();
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like

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
		if(getTime()%20==0){
			direction*= -1;
		}
		
		//too close to wall
		double hypotenuse = 0.0;
		double turnAmtDegrees = 0.0;
		if(FIELD_WIDTH-getX() < 50 ){
			if(getY() > FIELD_HEIGHT/2){
				setTurnRight(PI/4);
			} else {
				setTurnLeft(PI/4);
			}
			setAhead(-1*direction*250);
		} else if (getX() < 50){
			if(getY() < FIELD_HEIGHT/2){
				setTurnRight(PI/4);
			} else {
				turnLeft(PI/4);
			}
			setAhead(-1*direction*250);
		}else if (FIELD_HEIGHT-getY() < 50){
			if(getX() < FIELD_WIDTH/2){
				setTurnRight(PI/4);
			} else {
				setTurnLeft(PI/4);
			}	
			setAhead(-1*direction*250);
		}else if (getY() < 50){
			if(getX() > FIELD_WIDTH/2){
				setTurnRight(PI/4);
			} else {
				setTurnLeft(PI/4);
			}
			setAhead(-1*direction*250);
/**	//go to centre of map
			hypotenuse = hyp(Math.abs(getX()-FIELD_WIDTH/2),Math.abs(getY()-FIELD_HEIGHT/2));
			turnAmtDegrees = Math.acos(getY()-FIELD_HEIGHT/2);
			setTurnRight(turnAmtDegrees);
			setAhead(hypotenuse);

*/			
		} else {
			setAhead(direction*250);
			//circle around our target
			setTurnRightRadians(target.bearing + (PI/2));
		}
	}

	//return the hypotenuse of a right triange given the two sides
	public double hyp(double a, double b){
		return(Math.sqrt(Math.pow(a,2) + Math.pow(b,2)));
	}

}
								
class Enemy{
	//use this to store information about our enemy
	//kinda like copying snippetbot's idea, but it's a very good one!

	String name;
	public double distance;
	public double bearing;
}
									
