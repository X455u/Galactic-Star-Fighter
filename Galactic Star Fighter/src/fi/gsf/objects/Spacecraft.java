package fi.gsf.objects;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.SlickException;

import fi.gsf.Camera;
import fi.gsf.GSFGame;
import fi.gsf.systems.ProjectileSystem;

/**
 * The spacecraft of the player.
 * 
 * @author Kasper Hellstr�m
 * @edited Niclas Lindgren
 *
 */
public class Spacecraft extends SpaceObject {
	
	/** The acceleration of the ship in relation to the x-axis (m/s^2) */
	private double accelerationX;
	/** The acceleration of the ship in relation to the y-axis (m/s^2) */
	private double accelerationY;
	/** The velocity of the ship in relation to the x-axis (m/s) */
	private double velocityX;
	/** The velocity of the ship in relation to the y-axis (m/s) */
	private double velocityY;
	
	/** The weight of the ship (kg). Affects the acceleration of the ship. */
	private int weight = 5000; //kg
	/** The thrust of the ship engine (N). Affects the acceleration of the ship. */
	private int engineThrust = 1200000; //N
	
	/** Array of turrets on this ship */
	private Turret[] turrets;
	
	/** Number of turrets */
	private int numberOfTurrets = 2;
		
	/** Turret placement in format: distance in pixels from middle of the ship, angle. */
	private double turretPosition[][] = {{31.5, 0.7 * Math.PI}, {31.5, - 0.7 * Math.PI}};
	
	/** The projectile system taking care of the projectiles in the game. */
	private ProjectileSystem projectiles;
	
	/** The maximum velocity the ship can get. */
	private final double maxVelocity = 200; // m/s
	
	/**
	 *  Create a new Spacecraft object.
	 *  
	 * @param ref Path of intended Image for the craft. 
	 * @throws SlickException 
	 */
	public Spacecraft(String ref, ProjectileSystem projectiles) throws SlickException {
		super(ref, 5000, 5000, SpaceObject.FRIENDLY);
		this.accelerationX = 0;
		this.accelerationY = 0;
		this.velocityX = 0;
		this.velocityY = 0;
		this.turrets = new Turret[this.numberOfTurrets];
		this.projectiles = projectiles;
	}
	
	/**
	 * Create a new Spacecraft object.
	 */
	public Spacecraft(ProjectileSystem projectiles) {
		super(5000, 5000, Spacecraft.FRIENDLY);
		this.accelerationX = 0;
		this.accelerationY = 0;
		this.velocityX = 0;
		this.velocityY = 0;
		this.turrets = new Turret[this.numberOfTurrets];
		this.projectiles = projectiles;
	}
	
	/**
	 * update the acceleration, velocity, position and rotation of the ship.
	 * 
	 * @param up
	 * @param down
	 * @param left
	 * @param right
	 * @param delta Time passed since last update (in milliseconds)
	 */
	public void update(boolean up, boolean down, boolean left, boolean right, int delta) {
		super.update(delta);
		//update acceleration
		this.accelerationX = this.engineThrust / this.weight * ( (right? 1 : 0) + (left? -1 : 0) );
		this.accelerationY = this.engineThrust / this.weight * ( (up? 1 : 0) + (down? -1 : 0) );
		
		//limit diagonal acceleration
		if (this.accelerationX != 0) {
			this.accelerationY *= 0.707106781; // 1/sqrt(2)
		}
		if (this.accelerationY != 0) {
			this.accelerationX *= 0.707106781; // 1/sqrt(2)
		}
		
		//update velocity
		this.velocityX += this.accelerationX * delta / 1000.0;
		this.velocityY += this.accelerationY * delta / 1000.0;
		
		//limit the velocity if it's above max
		if (Math.hypot(this.velocityX, this.velocityY) > this.maxVelocity) {
			double angle = Math.atan2(this.velocityY, this.velocityX);
			this.velocityX = this.maxVelocity * Math.cos(angle);
			this.velocityY = this.maxVelocity * Math.sin(angle);
		}
		
		//slow down the ship
		double percent = 0.50; // 50% in a second
		this.velocityX *= Math.pow(percent, delta / 1000.0);
		this.velocityY *= Math.pow(percent, delta / 1000.0);
		
		
		//update position
		double x = this.getX() + this.velocityX * GSFGame.getPixelRatio() * delta / 1000.0;
		double y = this.getY() + this.velocityY * GSFGame.getPixelRatio() * delta / 1000.0;
		
		x = Math.min(GSFGame.getWorldWidth(), Math.max(- GSFGame.getWorldWidth(), x));
		y = Math.min(GSFGame.getWorldHeight(), Math.max(- GSFGame.getWorldHeight(), y));
		
		this.position(x, y);
		
		//update rotation
		this.rotateToInDegrees(90 - 75 * this.velocityX / maxVelocity);
		
		//update turrets
		for (int i = 0; i < turrets.length; i++) {
			Turret turret = turrets[i];
			if (turret != null){
				turret.update(delta);
				turret.position(	this.getX() + this.turretPosition[i][0] * Math.cos(this.getAngle() + this.turretPosition[i][1]),
										this.getY() + this.turretPosition[i][0] * Math.sin(this.getAngle() + this.turretPosition[i][1]));
				//turrets are rotated just before they are drawn
			}
		}
		
		//Check hits on player
		projectiles.checkObject(this);
		
		
	}
	
	/**
	 * Called when the player tries to shoot.
	 */
	public void shoot() {
		for (int i = 0; i < turrets.length; i++) {
			Turret turret = turrets[i];
			if (turret != null){
				if (turret.isReloaded()){
//					WeaponType type = turret.getType();
//					double angle = turret.getAngle() + 2 * (Math.random() - 0.5) * type.getSpread();
//					this.projectiles.addProjectile(turret.getX(), turret.getY(), this.velocityX + type.getShotVelocity() * Math.cos(angle), this.velocityY + type.getShotVelocity() * Math.sin(angle), type.getShotAcceleration(), type.getShotType(), type.getShotDamage(), SpaceObject.FRIENDLY);
					turret.shoot(this.projectiles, this.velocityX, this.velocityY, SpaceObject.FRIENDLY);
				}
			}
		}
	}
	
	/**
	 * Get the velocity in pixels per millisecond.
	 * 
	 * @return The velocity of the spacecraft in relation to the x-axis.
	 */
	public double getXVelocity() {
		return this.velocityX* GSFGame.getPixelRatio() / 1000.0;
	}
	
	/**
	 * Get the velocity in pixels per millisecond.
	 * 
	 * @return The velocity of the spacecraft in relation to the y-axis.
	 */
	public double getYVelocity() {
		return this.velocityY* GSFGame.getPixelRatio() / 1000.0;
	}
	
	/** Get some info of the acceleration, velocity and position of the spacecraft. */
	public String getInfo() {
		return "Spacecraft info:\naccx: " + this.accelerationX + "\naccy: " + this.accelerationY + "\nvelx: " + this.velocityX + "\nvely: " + this.velocityY + "\nposx: " + this.getX() + "\nposy: " + this.getY() + "\narmor: " + this.getArmor() + "\nshield: " + this.getShield();
	}
	
	/**
	 * Set a turret in a turret slot on the ship.
	 *
	 * @param turret
	 * @param number
	 */
	public void setTurrets(Turret turret, int number) {
		if (number >= 0 && number < this.numberOfTurrets) {
			this.turrets[number] = turret;
		}
	}
	
	/**
	 * Get a turret from a slot.
	 * 
	 * @param number slot
	 * @return The turret
	 */
	public Turret getTurret(int number) {
		if (number >= 0 && number < this.numberOfTurrets) {
			return this.turrets[number];
		}
		return null;
	}
	
	
	@Override
	public void draw(Camera camera) {
		//draw spacecraft
		super.draw(camera);
		
		//rotate turrets and draw them
		for (int i = 0; i < turrets.length; i++) {
			Turret turret = turrets[i];
			if (turret != null) {
				if (Mouse.isInsideWindow()) {
					int mouseX = camera.getWorldX(Mouse.getX());
					int mouseY = camera.getWorldY(GSFGame.getScreenHeight() - Mouse.getY());
					turret.point(mouseX, mouseY);
				} else {
					turret.rotateTo(0.5 * Math.PI);
				}
				turret.draw(camera);
			}
		}
	}
	
}
