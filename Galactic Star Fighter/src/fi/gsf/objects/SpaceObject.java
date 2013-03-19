package fi.gsf.objects;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import fi.gsf.Camera;

public abstract class SpaceObject extends GameObject {
	
	/** On the player's side. */
	public static final int FRIENDLY = 0;
	
	/** On the enemy's side. */
	public static final int ENEMY = 1;
	
	/** Neither on the player's nor the enemy's side. */
	public static final int NEUTRAL = 2;
	
	/** How long the damage flash lasts. */
	private static final int DAMAGE_FLASH_DURATION = 200;
	
	/** How long the fading away takes for a destoryed object. */
	private static final int DESTORYED_FADE_DURATION = 500;
	
	/** Master Image of shield. Radius 100 px. */
	private static Image shieldImage;
	
	//Load shield image
	static {
		try {
			shieldImage = new Image("images/other/blue shield.png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	/** The maximum amount of armor of the spaceship. */
	private int maxArmor;
	
	/** The current amount of armor of the spaceship. */
	private int armor;
	
	/** The maximum amount of shield of the spaceship. */
	private int maxShield;
	
	/** The current amount of shield of the spaceship. */
	private int shield;
	
	/** The radius of the shield in pixels. */
	private int shieldRadius;
	
	/** The side of the spaceship. Friendly, enemy, neutral. */
	private int side;
	
	/** Tells how much the object should be colored when damaged and how much should be faded when it's destroyed. */
	private int flashAndFade;
	
	/** True if the spaceship is destroyed. */
	private boolean isDestroyed;
	
	/** True if the spaceship can be deleted. */
	private boolean isDeletable;
	
		
	/**
	 * Creates a new space object.
	 * @param ref
	 * @param armor
	 * @param shield
	 * @param side
	 * @throws SlickException
	 */
	public SpaceObject(String ref, int armor, int shield, int side) throws SlickException {
		super(ref);
		this.maxArmor = this.armor = armor;
		this.maxShield = this.shield = shield;
		this.side = side;
		Image img = this.getImage();
		this.shieldRadius = (int) Math.hypot( img.getHeight() / 2, img.getWidth() / 2); 
	}
	
	/**
	 * Creates a new space object.
	 * @param img
	 * @param armor
	 * @param shield
	 * @param side
	 */
	public SpaceObject(Image img, int armor, int shield, int side) {
		super(img);
		this.maxArmor = this.armor = armor;
		this.maxShield = this.shield = shield;
		this.side = side;
		this.shieldRadius = (int) Math.hypot( img.getHeight() / 2, img.getWidth() / 2); 
	}
	
	/**
	 * Creates a new space object.
	 * @param armor
	 * @param shield
	 * @param side
	 */
	public SpaceObject(int armor, int shield, int side) {
		super();
		this.maxArmor = this.armor = armor;
		this.maxShield = this.shield = shield;
		this.side = side;
		this.shieldRadius = 0; 
	}
	
	/**
	 * Creates a dummy enemy with one armor and no image;
	 */
	public SpaceObject() {
		this(1, 0, SpaceObject.ENEMY);
	}
	
	
	/**
	 * Updates the flashing and fading of an object. Not necessary if the object doesn't flash/fade.
	 * 
	 * @param delta
	 */
	public void update(int delta) {
		this.flashAndFade = Math.max(0, this.flashAndFade - delta);
		if (this.isDestroyed && this.flashAndFade == 0) {
			this.isDeletable = true;
		}
	}
	
	/**
	 * Draw the object using fading and flashing.
	 * 
	 * @param camera
	 */
	public void draw(Camera camera) {
		if (this.flashAndFade != 0) {
			Image img = this.getImage();
			if (img != null) {
				if (this.isDestroyed) {
					img.setAlpha((float) this.flashAndFade / DESTORYED_FADE_DURATION);
					super.draw(camera);
				} else {
					super.draw(camera);
					if (this.shield > 0) {
						shieldImage.setAlpha( (float) this.flashAndFade / DAMAGE_FLASH_DURATION);
						shieldImage.draw((float) camera.getScreenX(this.getX()) - this.shieldRadius, (float) camera.getScreenY(this.getY()) - this.shieldRadius, this.shieldRadius / 100.0f);
						shieldImage.setAlpha(1.0f);
					} else {
						img.setAlpha((float) this.flashAndFade / DAMAGE_FLASH_DURATION);
						img.draw((float) camera.getScreenX(this.getX()) - img.getWidth() / 2, (float) camera.getScreenY(this.getY()) - img.getHeight() / 2, Color.red);
					}
				}
				img.setAlpha(1.0f);
			}
		} else {
			super.draw(camera);
		}
	}
	
	public int getMaxArmor() {
		return this.maxArmor;
	}
	
	public int getArmor() {
		return this.armor;
	}
	
	public  int getMaxShield() {
		return this.maxShield;
	}
	
	public int getShield() {
		return this.shield;
	}
	
	public int getSide() {
		return this.side;
	}
	
	public int getShieldRadius() {
		return this.shieldRadius;
	}
	
	public void damage(int damage) {
		if (this.shield >= damage) {
			this.shield -= damage;
			this.flashAndFade = DAMAGE_FLASH_DURATION;
		} else if (this.shield > 0) {
			this.armor -= damage - this.shield;
			this.shield = 0;
			this.flashAndFade = DAMAGE_FLASH_DURATION;
		} else {
			this.armor -= damage;
			this.flashAndFade = DAMAGE_FLASH_DURATION;
		}
		
		if (this.armor <= 0) {
			this.armor = 0;
			this.isDestroyed = true;
			this.flashAndFade = DESTORYED_FADE_DURATION;
		}
	}
	
	
	public boolean isDestroyed() {
		return this.isDestroyed;
	}
	
	public boolean isDeletable() {
		return this.isDeletable;
	}
	
	public void updateArmor(int points) {
		this.armor += points;
		this.armor = Math.max(this.maxArmor, this.armor);
	}
	
	public void updateShield(int points) {
		this.shield += points;
		this.armor = Math.max(this.maxShield, this.shield);
	}
	
	public void setMaxShield(int shield) {
		this.maxShield = shield;
	}
	
	public void setMaxArmor(int armor) {
		this.maxArmor = armor;
	}
	
	public void setShield(int shield) {
		this.shield = shield;
	}
	
	public void setArmor(int armor) {
		this.armor = armor;
	}
	
	public void setShieldRadius(int radius) {
		this.shieldRadius = radius;
	}
	
	/**
	 * Does the given coordinates of a bullet overlap the object or its shield.
	 * 
	 * @param worldX
	 * @param worldY
	 * @return
	 */
	public boolean bulletOverlaps(int worldX, int worldY) {
		if (this.shield > 0) {
			if (Math.hypot(this.getX() - worldX, this.getY() - worldY) <= this.shieldRadius) {
				return true;
			}
			return false;
		}
		return this.overlaps(worldX, worldY);
	}
	
	@Override
	public void paint(Image img) {
		super.paint(img);
		this.shieldRadius = (int) Math.hypot( img.getHeight() / 2, img.getWidth() / 2); 
	}
	
	
}
