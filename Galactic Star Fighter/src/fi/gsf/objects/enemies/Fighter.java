package fi.gsf.objects.enemies;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import fi.gsf.Camera;
import fi.gsf.GSFGame;
import fi.gsf.objects.SpaceObject;
import fi.gsf.objects.Spacecraft;
import fi.gsf.systems.ProjectileSystem;

public class Fighter extends SpaceObject {
	
	/** Master Image of a fighter. */
	private static Image fighterImage;
	
//	/** Master Image of a fighter shot. */
//	private static Image shotImage;
	
	/** The maximum acceleration of a fighter. */
	private static final int MAX_ACCELERATION = 100;
	
	/** The maximum velocity of a fighter. */
	private static final int MAX_VELOCITY = 150;
	
	/** The maximum turning speed of a fighter (radians per second). */
	private static final double TURN_SPEED = 0.5 * Math.PI;
	
	/** The distance from the ship when the fighter begins attacking. */
	private static final int ATTACK_DIST = 600;
	
	/** The distance from the ship when the fighter begins retreating. */
	private static final int RETREAT_DIST = 250;
	
	/** The maximum range of the fighter's weapon. */
	private static final int MAX_RANGE = 1000;
	
	/** The damage caused by one shot. */
	private static final int DAMAGE = 10;
	
	/** The amount of shots in a burst. */
	private static final int BURST_SHOTS = 5;
	
	/** The cooldown time between shots in a burst. */
	private static final int SHOT_COOLDOWN = 50;
	
	/** Time between bursts. */
	private static final int RELOAD_TIME = 1000;
	
	/** Shoots when player ship is inside this arc. In radians and to both directions (PI is whole circle). */
	private static final double FIRING_ARC = 0.1;

	/** Where the shots spawn when shooting. Offset from center straight forward. */
	private static final double BARREL_LENGTH = 10;
	
	/** Velocity for shots. */
	private static final double SHOT_VELOCITY = 200;

	/** Acceleration for shots. */
	private static final double SHOT_RETARDATION = 1;
	
	/** What kind of shot the fighter shoot. */
	private static final int SHOT_TYPE = ProjectileSystem.TYPE_BULLET;
	
	//Load fighter Image
	static {
		try {
			fighterImage = new Image("images/enemies/fighter_temp.png").getScaledCopy(0.4f);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
//	//Load fighter shot Image
//	static {
//		try {
//			shotImage = new Image("images/other/particle.tga");
//		} catch (SlickException e) {
//			e.printStackTrace();
//		}
//	}
	
	/** The acceleration of the ship in relation to the x-axis (m/s^2) */
	private double accelerationX;
	/** The acceleration of the ship in relation to the y-axis (m/s^2) */
	private double accelerationY;
	/** The velocity of the ship in relation to the x-axis (m/s) */
	private double velocityX;
	/** The velocity of the ship in relation to the y-axis (m/s) */
	private double velocityY;
	
	/** True if the fighter is attacking, else it's retreating. */
	private boolean isAttacking;	
	/** Amount of shots left to fire in on-going burst. */
	private int shots;	
	/** Milliseconds left until the burst weapon is ready for another shot. */
	private int shotCooldown;
	/** Milliseconds left until the burst weapon is ready for another burst. */
	private int reload;
	/** The shots of a fighter */
	private ProjectileSystem projectiles;
	
	
	/** Create a new fighter at the world coordinate (x,y). */
	public Fighter(double x, double y, ProjectileSystem projectiles) {
		super(Fighter.fighterImage, 1, 0, SpaceObject.ENEMY);
		this.position(x, y);
		this.projectiles = projectiles;
		this.isAttacking = true;
	}
	
	
	public void update(int delta, Spacecraft ship) {
		super.update(delta);
		
		double shipX = ship.getX();
		double shipY = ship.getY();
		
		this.accelerationX = 0;
		this.accelerationY = 0;
		double angle = 0; //temporary variable
		
		if (!this.isDestroyed()) {
			//Determine attack or retreat mode
			if (isAttacking && Math.hypot(this.getX() -  shipX, this.getY() - shipY) < RETREAT_DIST) isAttacking = false;
			if (!isAttacking && Math.hypot(this.getX() -  shipX, this.getY() - shipY) > ATTACK_DIST) isAttacking = true;
			
			//Acceleration
			if (isAttacking) angle = Math.atan2(shipY - this.getY(), shipX - this.getX());
			else angle = Math.atan2(this.getY() - shipY, this.getX() - shipX);
			this.slowTurnTo(angle, TURN_SPEED, delta);
			this.accelerationX += Math.cos(this.getAngle()) * MAX_ACCELERATION;
			this.accelerationY += Math.sin(this.getAngle()) * MAX_ACCELERATION;
		}
		
		//update velocity
		//slow down the ship
		double percent = 0.50; // 50% in a second
		this.velocityX *= Math.pow(percent, delta / 1000.0);
		this.velocityY *= Math.pow(percent, delta / 1000.0);
		
		this.velocityX += this.accelerationX * delta / 1000.0;
		this.velocityY += this.accelerationY * delta / 1000.0;
		
		//Limit the velocity
		if (Math.hypot(this.velocityX, this.velocityY) > MAX_VELOCITY) {
			this.velocityX = MAX_VELOCITY * Math.cos(angle);
			this.velocityY = MAX_VELOCITY * Math.sin(angle);
		}
		
		//Update position
		double x = this.getX() + this.velocityX * GSFGame.getPixelRatio() * delta / 1000.0;
		double y = this.getY() + this.velocityY * GSFGame.getPixelRatio() * delta / 1000.0;
		
		this.position(x, y);
		
		//Update reload and cooldown time
		this.reload = Math.max(0, this.reload - delta);
		this.shotCooldown = Math.max(0, this.shotCooldown - delta);
		
		//Begin firing a burst
		angle = Math.atan2(shipY - y, shipX - x);
		double distance = Math.hypot(shipX - this.getX(), shipY - this.getY());
		if (!this.isDestroyed() && this.reload == 0 && distance < MAX_RANGE && isAttacking && this.angleBetween(angle, this.getAngle()) < FIRING_ARC) {
			this.shots = BURST_SHOTS;
			this.reload = RELOAD_TIME;
		}

		//Shoot a shot of a burst
		if (this.shots > 0 && this.shotCooldown == 0) {
			projectiles.addProjectile(this.getX() + BARREL_LENGTH * Math.cos(this.getAngle()), this.getY() + BARREL_LENGTH * Math.sin(this.getAngle()), SHOT_VELOCITY * Math.cos(this.getAngle()), SHOT_VELOCITY * Math.sin(this.getAngle()), SHOT_RETARDATION, SHOT_TYPE, DAMAGE, SpaceObject.ENEMY);
			this.shots = this.shots - 1;
			this.shotCooldown = SHOT_COOLDOWN;
		}
		
	}
	
	public void draw(Camera camera, Graphics g) {
		super.draw(camera);
	}
	
}
