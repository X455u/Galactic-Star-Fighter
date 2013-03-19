package fi.gsf.objects.enemies;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import fi.gsf.Camera;
import fi.gsf.GSFGame;
import fi.gsf.objects.SpaceObject;
import fi.gsf.objects.Spacecraft;

public class Swarmer extends SpaceObject {
	
	/** Master Image of a Swarmer. */
	private static Image swarmerImage;
	
	/** The maximum acceleration of a swarmer */
	private static final int MAX_ACCELERATION = 100;
	
	/** The maximum velocity of a swarmer */
	private static final int MAX_VELOCITY = 150;
	
	/** How much space a swarmer should take in the group. */
	private static final int SWARMER_AREA = 700;
	
	/** The preferred distance between swarmers. */
	private static final int SWARMER_DISTANCE = 10;
	
	/** The preferred distance from the ship. */
	private static final int DISTANCE_FROM_SHIP = 75;
	
	/** The radius of the swarmer. */
	private static final int SWARMER_RADIUS = 8;
	
	/** The maximum range of the swarmer's laser. */
	private static final int MAX_RANGE = 200;
	
	/** The damage caused by the laser. One shot. */
	private static final int DAMAGE = 1;
	
	/** The duration of the laser. How long you will see the laser on the screen. */
	private static final int LASER_DURATION = 50;
	
	/** The time it takes for the swarmer to reload the laser. */
	private static final int RELOAD_TIME = 500;
	
	//Load Swarmer Image
	static {
		try {
			swarmerImage = new Image("images/enemies/swarmer.png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	/** The acceleration of the ship in relation to the x-axis (m/s^2) */
	private double accelerationX;
	/** The acceleration of the ship in relation to the y-axis (m/s^2) */
	private double accelerationY;
	/** The velocity of the ship in relation to the x-axis (m/s) */
	private double velocityX;
	/** The velocity of the ship in relation to the y-axis (m/s) */
	private double velocityY;
	
	/** True if the swarmer uses its laser. */
	private boolean shootsLaser;
	/** How many milliseconds it takes until the laser has reloaded. 0 if ready to shoot. */
	private int reload;
	/** The x-coordinate of where the laser hits. */
	private int laserX;
	/** The y-coordinate of where the laser hits. */
	private int laserY;
	
	
	/** Create a new Swarmer at the world cooridnate (x,y). */
	public Swarmer(double x, double y) {
		super(Swarmer.swarmerImage, 1, 0, SpaceObject.ENEMY);
		this.position(x, y);
	}
	
	
	public void update(int delta, Spacecraft ship, double groupX, double groupY, ArrayList<Swarmer> swarmers) {
		super.update(delta);
		
		double shipX = ship.getX();
		double shipY = ship.getY();
		
		this.accelerationX = 0;
		this.accelerationY = 0;
		double angle = 0; //temporary variable
		
		if (!this.isDestroyed()) {
			final double preferredMaxDistFromGroup = Math.sqrt(SWARMER_AREA * swarmers.size() / Math.PI);
			
			//Acceleration towards the group center. Acceleration ~ r
			double acceleration =  MAX_ACCELERATION * Math.hypot(groupX - this.getX(), groupY - this.getY()) / preferredMaxDistFromGroup;
			
			angle = Math.atan2(groupY - this.getY(), groupX - this.getX());
			this.accelerationX += Math.cos(angle) * acceleration;
			this.accelerationY += Math.sin(angle) * acceleration;
			
			//Acceleration away from other swarmers. Acceleration ~ 1/r^2
			for (Swarmer s : swarmers) {
				if (s != this) {
					acceleration = MAX_ACCELERATION * Math.pow(SWARMER_DISTANCE, 2) / (Math.pow(s.getX() - this.getX(), 2) + Math.pow(s.getY() - this.getY(), 2));
					
					angle = Math.atan2(this.getY() - s.getY(), this.getX() - s.getX());
					this.accelerationX += Math.cos(angle) * acceleration;
					this.accelerationY += Math.sin(angle) * acceleration;
				}
			}
			
			//Acceleration towards the ship/player
			double distance = Math.hypot(shipX - this.getX(), shipY - this.getY());
			acceleration =  MAX_ACCELERATION * ( distance / DISTANCE_FROM_SHIP - Math.pow(DISTANCE_FROM_SHIP / distance, 2));
			
			angle = Math.atan2(shipY - this.getY(), shipX - this.getX());
			this.accelerationX += Math.cos(angle) * acceleration;
			this.accelerationY += Math.sin(angle) * acceleration;
			
			
			//limit the acceleration
			if (Math.hypot(this.accelerationX, this.accelerationY) > MAX_ACCELERATION) {
				angle = Math.atan2(this.accelerationY, this.accelerationX);
				this.accelerationX = MAX_ACCELERATION * Math.cos(angle);
				this.accelerationY = MAX_ACCELERATION * Math.sin(angle);
			}
		}
		
		//update velocity
		//slow down the ship
		double percent = 0.50; // 50% in a second
		this.velocityX *= Math.pow(percent, delta / 1000.0);
		this.velocityY *= Math.pow(percent, delta / 1000.0);
		
		this.velocityX += this.accelerationX * delta / 1000.0;
		this.velocityY += this.accelerationY * delta / 1000.0;
		
		//Limit the velocity
		angle = Math.atan2(this.velocityY, this.velocityX);
		if (Math.hypot(this.velocityX, this.velocityY) > MAX_VELOCITY) {
			this.velocityX = MAX_VELOCITY * Math.cos(angle);
			this.velocityY = MAX_VELOCITY * Math.sin(angle);
		}
		this.rotateTo(angle);
		
		//update position
		double x = this.getX() + this.velocityX * GSFGame.getPixelRatio() * delta / 1000.0;
		double y = this.getY() + this.velocityY * GSFGame.getPixelRatio() * delta / 1000.0;
		
		this.position(x, y);
		
		//bounce from shield if the shield is active
		if (ship.getShield() > 0 && Math.hypot(shipX - x, shipY - y) < ship.getShieldRadius() + SWARMER_RADIUS) {
			//position ship outside the shield
			x = shipX + (ship.getShieldRadius() + SWARMER_RADIUS) * Math.cos(Math.atan2(shipY - y, shipX - x) + Math.PI);
			y = shipY + (ship.getShieldRadius() + SWARMER_RADIUS) * Math.sin(Math.atan2(shipY - y, shipX - x) + Math.PI);
			this.position(x, y);
			//flash the shield
			ship.damage(0);
			//calculate new angle
			angle = 2 * Math.atan2(y - shipX, x - shipX) + Math.PI - angle;
			//calculate new velocity
			double velocity = Math.hypot(this.velocityX, this.velocityY);
			this.velocityX = velocity * Math.cos(angle);
			this.velocityY = velocity * Math.sin(angle);
		}
		
		//shoot with the laser
		if (this.reload < RELOAD_TIME - LASER_DURATION) {
			this.shootsLaser = false;
		}
		this.reload = Math.max(0, this.reload - delta);
		
		double distance = Math.hypot(shipX - this.getX(), shipY - this.getY());
		
		if (!this.isDestroyed() && this.reload == 0 && distance < MAX_RANGE) {
			this.shootsLaser = true;
			this.reload = RELOAD_TIME;
			ship.damage(DAMAGE);
		}

		//find a spot where to shoot the laser
		if (this.shootsLaser) {
			angle = Math.atan2(y - shipY, x - shipX);
			if (ship.getShield() > 0) {
				this.laserX = (int) (shipX + ship.getShieldRadius() * Math.cos(angle));
				this.laserY = (int) (shipY + ship.getShieldRadius() * Math.sin(angle));
			} else {
//				this.laserX = (int) shipX;
//				this.laserY = (int) shipY;
				for (int r = 5; r <= ship.getShieldRadius(); r += 5) {
					if (!ship.overlaps((int) (shipX + r * Math.cos(angle)), (int) (shipY + r * Math.sin(angle)))) {
						this.laserX = (int) (shipX + (r - 5) * Math.cos(angle));
						this.laserY = (int) (shipY + (r - 5) * Math.sin(angle));
						break;
					}
				}
			}
		}
		
	}
	
	public void draw(Camera camera, Graphics g) {
		if (this.shootsLaser) {
			double angle = Math.atan2(velocityY, velocityX);
			g.setColor(Color.red);
			g.drawLine((float) camera.getScreenX(this.getX() + SWARMER_RADIUS * Math.cos(angle)),
							(float) camera.getScreenY(this.getY() + SWARMER_RADIUS * Math.sin(angle)),
							camera.getScreenX(this.laserX),
							camera.getScreenY(this.laserY));
		}
		super.draw(camera);
	}
	
	@Override
	public boolean overlaps(int x, int y) {
		return Math.hypot(x - this.getX(), y - this.getY()) <= SWARMER_RADIUS;
	}
	
}
