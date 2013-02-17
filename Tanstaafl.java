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
	
	//number of time I miss
	int missCount = 0;
	
	//gun aim
	double gunAim;

	/**
	 * run: Tanstaafl's default behavior
	 */
	public void run() {
		
		// Initialization of the robot should be put here
		target = new Enemy();
		
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
			//fire(firepower);
			execute();
		}
	}
	

	public void setGunTurn(String direction, double degrees){
		if(direction.equalsIgnoreCase("R")){
			turnGunRight(degrees);
		}else{
			turnGunLeft(degrees);
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
		
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10)
			fire(firepower);
	}
	

	/*
		my bullet hit another robot
	*/
	public void onBulletHit(BulletHitEvent event){
		/*
			As long as I'm not hit I'll keep on shooting, if hit move and shoot otherwise stay steady
		*/		
		out.println(event.getName());
		
		Bullet bulletHit = event.getBullet();
		String rName = event.getName();
		
	}
		

	/*
		when my bullet hits another bullet
	*/
	public void onBulletHitBullet(BulletHitBulletEvent event){
		/*
			Then I could be right in front of the person, or in crossfire with someone elese check my position
		*/
		
	}
	
	/*
		Missing opponent need to recalibrate aim
	*/
	public void onBulletMissed(BulletMissedEvent event) {
		
		Bullet bullet = event.getBullet();
		
		/*
			readjust current heading either by scanning left or right
			
		*/
		double currentHeading = getGunHeadingRadians();
		double bulletHead = bullet.getHeading();
		
		double newHeading = getHeading() - getGunHeading() + bullet.getHeadingRadians();
		String direction = "R";
		
		this.setGunTurn(direction,newHeading);
		
		
		/*
		double bulletHeadRadians = bullet.getHeadingRadians();
		double bulletPower = bullet.getPower();
		double bulletVel = bullet.getVelocity();
		boolean isActive = bullet.isActive();
		
		double bulletX = bullet.getX();
		double bulletY = bullet.getY();
		*/
		
		/*
			by default turn right then keep turning right, if found a hit then stop until I miss again.
		*/
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
	}	

	public void firepower(){
		firepower = 400/target.distance;
	}

	public void scan(){
		turnRadarRightRadians(PI*2);	
	}

	public void movement(){
		//reverse direction every 20 ticks
		if(getTime()%5==0){
			direction*= -1;
			setAhead(direction*300);
		}

/**		//too close to wall
		double hypotenuse = 0.0;
		double turnAmtDegrees = 0.0;
		if(FIELD_WIDTH-getX() < 150 ){
				turnRight(PI/2);
		} else if (getX() < 150){
				turnRight(PI/2);
		}else if (FIELD_HEIGHT-getY() < 150){
				turnRight(PI/2);
		}else if (getY() < 150){
				turnRight(PI/2);

		}  */
		//circle around our target
		setTurnRightRadians(target.bearing + (PI/2));
		
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
	
	public Move nextMove;
	
	public Enemy(){
		distance = 12398713;		
		bearing = 0.0;
		nextMove = new Move();
	}
	
	public Move getNextMove(){
		return this.nextMove;
	}
}

class Move{
	public double x;
	public double y;
	
	public Move(){
		x= 0.0;
		y= 0.0;	
	}
	
}
									
																
