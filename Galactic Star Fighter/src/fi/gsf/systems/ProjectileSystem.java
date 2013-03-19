package fi.gsf.systems;

import java.util.ArrayList;
import java.util.Iterator;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import fi.gsf.Camera;
import fi.gsf.GSFGame;
import fi.gsf.objects.SpaceObject;

public class ProjectileSystem {
	
	/** Regular (space) bullet. */
	public static final int TYPE_BULLET = 0;
	
	/** Plasma. */
	public static final int TYPE_PLASMA = 1;
	
	/** List of all the projectiles. */
	private ArrayList<Projectile> projectiles;
	
	/** Image of bullet. */
	private Image bulletImage;
	
	/**
	 * Create a new projectile system.
	 * 
	 * @param bulletImage
	 */
	public ProjectileSystem(Image bulletImage) {
		this.projectiles = new ArrayList<Projectile>();
		this.bulletImage = bulletImage;
	}
	
	/**
	 * Add a new projectile to the system.
	 * 
	 * @param x
	 * @param y
	 * @param direction
	 * @param velocity
	 * @param acceleration
	 * @param type
	 * @param damage
	 * @param side
	 */
	public void addProjectile(double x, double y, double velocityX, double velocityY, double acceleration, int type, int damage, int side) {
		projectiles.add(new Projectile(x, y, velocityX, velocityY, acceleration, type, damage, side));
	}
	
	/**
	 * Update all projectiles.
	 * @param delta
	 */
	public void update(int delta) {
		Iterator<Projectile> iterator = projectiles.iterator();
		while (iterator.hasNext()) {
			Projectile projectile = iterator.next();
			projectile.update(delta);
			if (projectile.isDeletable()) {
				iterator.remove();
			}
		}
	}
	
	/**
	 * Render projectiles.
	 * @param camera
	 */
	public void render(Camera camera) {
		for (Projectile projectile : projectiles) {
			projectile.render(camera);
		}
	}
	
	/**
	 * Check if one of the projectiles overlap the object.
	 * If a projectile overlaps the object the projectile is deleted
	 * and damage is done to the object.
	 * 
	 * @param object
	 */
	public void checkObject(SpaceObject object) {
		Iterator<Projectile> iterator = this.projectiles.iterator();
		while (iterator.hasNext()) {
			Projectile p = iterator.next();
			if (p.side != object.getSide() && object.bulletOverlaps(p.getX(), p.getY())) {
				object.damage(p.damage);
				iterator.remove();
			}
		}
	}
	
	/**
	 * Returns the amount of projectiles in the system.
	 * @return
	 */
	public int getSize() {
		return this.projectiles.size();
	}
	
	
	public class Projectile {
		
		private double x;
		private double y;
		
		private double velocityX;
		private double velocityY;
		
		/** How much the velocity drops during a second. 0.5 will drop the velocity to half in one second. */
		private double retardationFactor;
		
		private int type;
		
		private int damage;
		
		private int side;
				
		/**
		 * Create a new projectile.
		 * 
		 * @param x
		 * @param y
		 * @param direction Angle in radians.
		 * @param velocityX
		 * @param velocityY
		 * @param retardation How much the velocity drops during a second. 0.5 will drop the velocity to half in one second.
		 * @param type
		 * @param damage
		 * @param side
		 */
		public Projectile(double x, double y, double velocityX, double velocityY, double retardation, int type, int damage, int side) {
			this.x = x;
			this.y = y;
			this.velocityX = velocityX;
			this.velocityY = velocityY;
			this.retardationFactor = retardation;
			this.type = type;
			this.damage = damage;
			this.side = side;
		}

		
		/**
		 * Update the velocity and position of the projectile.
		 * 
		 * @param delta
		 */
		public void update(int delta) {
			//update velocity
			this.velocityX *= Math.pow(this.retardationFactor,  delta / 1000.0);
			this.velocityY *= Math.pow(this.retardationFactor,  delta / 1000.0);
			
			//update position
			this.x += this.velocityX * GSFGame.getPixelRatio() * delta / 1000.0;
			this.y += this.velocityY * GSFGame.getPixelRatio() * delta / 1000.0;
		}
		
		/**
		 * Render a projectile.
		 */
		public void render(Camera camera) {
			double angle = - Math.atan2(velocityY, velocityX); //IMPORTANT! MINUS!!!
			
			if (this.type == ProjectileSystem.TYPE_BULLET) {
				float aboutHalfOfMaximumDamageOfASingleBullet = 50;
				Color color = new Color(255 , (int)(200 * Math.pow(0.5 , damage / aboutHalfOfMaximumDamageOfASingleBullet)), 0);
//				Color color = Color.orange;
				double v = Math.hypot(velocityX, velocityY);
				float alpha = 1.0f;
				if (v < 50) {
					alpha = (float) Math.max(0, 1.0 + (v - 50) / 50);
				}
				bulletImage.setRotation((float) Math.toDegrees(angle));
				bulletImage.setAlpha(alpha);
				bulletImage.draw((float)camera.getScreenX(this.x) - bulletImage.getWidth() / 2, (float)camera.getScreenY(this.y) - bulletImage.getHeight() / 2, color);
			} else if (this.type == ProjectileSystem.TYPE_PLASMA) {
				//TODO Draw plasma projetile.
			}
		}
		
		public int getX() {
			return (int) this.x;
		}
		
		public int getY() {
			return (int) this.y;
		}
		
		/**
		 * Can the projectile be deleted?
		 * 
		 * @return returns true if the projectile is outside of the world.
		 */
		public boolean isDeletable() {
			return Math.abs(this.x) >= GSFGame.getWorldWidth() + 50 || Math.abs(this.y) >= GSFGame.getWorldHeight() + 50 || Math.hypot(velocityX, velocityY) < 5 ;
		}
	}
	
}
