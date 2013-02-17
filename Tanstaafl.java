package RAM;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import static robocode.util.Utils.normalRelativeAngle;
import java.lang.Math;
import java.util.Hashtable;
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
	//a list of our targets
	Hashtable targets = new Hashtable();
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
		Enemy scannedEnemy;
		if(targets.containsKey(e.getName())){
			//already have a record of this target
			scannedEnemy = targets.get(e.getName());
		} else {
			//know nothing about it yet
			scannedEnemy = new Enemy();
			targets.put(e.getName(), scannedEnemy);
		}

		//aboslute bearings to location of the guy
		double absoluteBearing = (getHeadingRadians() + e.getBearingRadians())%(2*PI);
		
		//fill out that enemy's instance variables
		scannedEnemy.name = e.getName();
		scannedEnemy.distance = e.getDistance();
		scannedEnemy.heading = e.getHeading();
		scannedEnemy.X = getX()+Math.sin(absoluteBearing)*e.getDistance();
		scannedEnemy.Y = getY()+Math.cos(absoluteBearing)*e.getDistance();
		scannedEnemy.timespotted = getTime(); //the last time we saw this guy

		//optionally could include code here for selecting closest target
		//but the contest is only 1v1...

		//implement some sort of targeting method
		setTurnGunRightRadians(normalRelativeAngle(absoluteBearing - getGunHeadingRadians()));

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
		//get frustrated
	}	

	public void firepower(){
		firepower = 400/target.distance;
	}

	public void scan(){
		turnRadarRightRadians(PI*2);	
	}

	public void movement(){
		//implement antigrav movement
		//the force on us from each axis
		double xforce = 0.0;
		double yforce = 0.0;
	
		double force;
		double ang;
		GravPoint p;
		Enemy currentEnemy; //always the first enemy since 1v1 only in the contest
		//could implement iterator here to go through each element in the hashtable, but no need
		currentEnemy = targets.get(target.getName());
		p = new GravPoint(currentEnemy.X,currentEnemy.Y,-1000); //set anti gravity
		ang = normalizeBearing(PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); //generate the square triangle for this enemy to generate force vectors
		//add these components tot he x and y forces acting on us
		xforce += Math.sin(ang)*force;
		yforce += Math.cos(ang)*force;

		//add force for the walls to dodge those assholes
		xforce += 5000/Math.pow(distance(getX(),getY(),FIELD_WIDTH,getY()),3); //increase the force as I get farther from right wall
		xforce -= 5000/Math.pow(distance(getX(),getY(),0,getY()),3); //decrease the force as I get close to left wall
		yforce += 5000/Math.pow(distance(getX(),getY(),getX(),FIELD_HEIGHT),3); //increase the force as I get farther from top
		yforce -= 5000/Math.pow(distance(getX(),getY(),getX(),0),3); //decrease the force as closer to top

		//now must go towards where the force is pressing on me
		goTo(getX()-xforce,getY()-yforce);

	}
	
	public void goTo(double x, double y){
		double distance = 20;
		double angle = Math.toDegrees(absoluteBearing(getX(),getY(),x,y); //get angle to desired location
		double radius = turnTo(angle);

		setAhead(distance*radius);
	}

	public double turnTo(double angle){
		
	}

	public double distance(double xa, double ya, double xb, double yb){
		double x = xb - xa;
		double y = yb - ya;
		return Math.sqrt(x*x + y*y);
	}

	//return the shortest angle to turn--same point on unit circle, smaller angle
	public double normalizeBearing(double ang){
		if(ang < PI){
			ang += 2*PI;
		}
		if(ang > PI) {
			ang -= 2*PI;
		}
		return ang;
	}
	//return the hypotenuse of a right triange given the two sides
	public double hyp(double a, double b){
		return(Math.sqrt(Math.pow(a,2) + Math.pow(b,2)));
	}

}
								
class Enemy{
	//use this to store information about our enemy
	//kinda like copying snippetbot's idea, but it's a very good one!

	public double distance;
	public double bearing;
	public String name;
	public double Y;
	public double X;
	public double timespotted;
	
	public String getName(){
		return name;
	}
	
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

//record the gravitational force of objects in the plane
class GravPoint{
	public double x,y,power;
	public GravPoint(double pX, double pY, double pPower){
		x = Px;
		y = Py;
		power = pPower;
	}
}
									
																
