package fi.gsf.objects;

import org.newdawn.slick.SlickException;

import fi.gsf.systems.ProjectileSystem;

public class Turret extends GameObject {

	public Turret(WeaponType type) throws SlickException {
		super();
		this.type = type;
		this.reloadTime = 0;
		this.paint(type.getImage());
		this.barrelLength = type.getImage().getWidth() / 2;
	}
	

	/** Type of the turret. */
	private WeaponType type;
	
	/** How many milliseconds left until the turret is ready to fire again. */
	private int reloadTime;
	
	/** Where to place the bullet when shooting. */
	private int barrelLength;
	
//	/** Is the turret currently in use? */
//	private boolean inUse = true;
	

	public WeaponType getType() {
		return this.type;
	}
	
	/**
	 * Call this method when you shoot with a turret. Otherwise reloading won't work.
	 * @param friendly 
	 * @param velocityY 
	 * @param velocityX 
	 * @param projectiles 
	 */
	public void shoot(ProjectileSystem projectiles, double velocityX, double velocityY, int side) {
		double angle = this.getAngle() + 2 * (Math.random() - 0.5) * type.getSpread();
		projectiles.addProjectile(this.getX() + this.barrelLength * Math.cos(this.getAngle()), this.getY() + this.barrelLength * Math.sin(this.getAngle()), velocityX + type.getShotVelocity() * Math.cos(angle), velocityY + type.getShotVelocity() * Math.sin(angle), type.getShotAcceleration(), type.getShotType(), type.getShotDamage(), side);
		
		this.reloadTime = type.getReloadTime();
	}
	
	public boolean isReloaded() {
//		return (this.shotTime + this.type.getReloadTime() >= GSFGame.getTimeElapsed());
		return this.reloadTime == 0;
	}
	
	public void update(int delta) {
		this.reloadTime = Math.max(0, reloadTime - delta);
	}


	
}
