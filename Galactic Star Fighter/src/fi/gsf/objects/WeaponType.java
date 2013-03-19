package fi.gsf.objects;

import org.newdawn.slick.Image;

import fi.gsf.systems.ProjectileSystem;

public class WeaponType {
	
	/** The image of the gun. */
	private Image image;
	
	/**
	 * Damage done by one shot.
	 */
	private int shotDamage;
	
	/**
	 * How long it takes for this weapon to reload itself. milliseconds
	 */
	private int reloadTime;
	
	/**
	 * How much the direction of the shot fired may differ from targeted direction. RADIANS
	 */
	private double spread;
	
	/**
	 * Velocity of the shot fired. m/s
	 */
	private double shotVelocity;
	
	/**
	 * Acceleration of the shot. (Negative value slows the shot down) m/s^2
	 */
	private double shotAcceleration;
	
	/**
	 * Type of the shot fired.
	 */
	private int shotType;
	
	/**
	 * How well the shot homes in to enemy targets. (0 for non-homing shots)
	 */
	private double shotHoming;
	
	/**
	 * Weight of the weapon.
	 */
	private int weight;
	
	public WeaponType(Image img, int shotDamage, int reloadTime, double spread, double shotSpeed,
								double shotRetardation, int shotType, double shotHoming, int weight) {
		this.image = img;
		this.shotDamage = shotDamage;
		this.reloadTime = reloadTime;
		this.spread = spread;
		this.shotVelocity = shotSpeed;
		this.shotAcceleration = shotRetardation;
		this.shotType = shotType;
		this.shotHoming = shotHoming;
		this.weight = weight;
	}
	
	public WeaponType(Image img) {
//		this(img, 50, 100, 0, 300, 0.1, ProjectileSystem.TYPE_BULLET, 0, 250); //fading and color test bullet
//		this(img, 5, 10, 0, 50, 10, ProjectileSystem.TYPE_BULLET, 0, 250); //accelerating bullets
		this(img, 5, 100, 0.10, 300, 1, ProjectileSystem.TYPE_BULLET, 0, 250);
	}

	
	public Image getImage() {
		return this.image;
	}
	
	public int getShotDamage() {
		return shotDamage;
	}

	public int getReloadTime() {
		return reloadTime;
	}

	public double getSpread() {
		return spread;
	}

	public double getShotVelocity() {
		return shotVelocity;
	}

	public double getShotAcceleration() {
		return shotAcceleration;
	}

	public int getShotType() {
		return shotType;
	}

	public double getShotHoming() {
		return shotHoming;
	}

	public int getWeight() {
		return weight;
	}
}
