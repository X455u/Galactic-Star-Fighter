package fi.gsf.objects;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import fi.gsf.Camera;

public class GameObject {
	
	/**
	 * x-coordinate in world coordinates.
	 */
	private double x;
	
	/**
	 * y-coordinate in world coordinates.
	 */
	private double y;
	
	/**
	 * The rotation of an object. RADIANS
	 */
	private double angle;
	
	/**
	 * The image of the object.
	 */
	private Image image;
	
	
	/**
	 * Create a new object with ref as the location for the image of the object.
	 * 
	 * @param ref
	 * @throws SlickException 
	 */
	public GameObject(String ref) throws SlickException {
		this.image = new Image(ref);
		this.x = 0;
		this.y = 0;		
		this.angle = 0;
	}
	
	/**
	 * Create a new object with img as the image for the object.
	 * 
	 * @param img
	 */
	public GameObject(Image img) {
		this.image = img;
		this.x = 0;
		this.y = 0;
		this.angle = 0;
	}
	
	public GameObject() {
		this.image = null;
		this.x = 0;
		this.y = 0;		
		this.angle = 0;
	}
	
	
	/**
	 * Move the object.
	 * 
	 * @param forward
	 * @param sideways
	 */
	public void move(double forward, double sideways) {
		this.x = this.x + forward * Math.cos(angle) + sideways * Math.cos(angle - Math.PI / 2);
		this.y = this.y + forward * Math.sin(angle) + sideways * Math.sin(angle - Math.PI / 2);
	}
	
	/**
	 * Move the object.
	 * 
	 * @param forward
	 */
	public void move(double forward) {
		this.move(forward, 0);
	}
	
	/**
	 * Move the object in relation to the x- and y-axis.
	 * 
	 * @param x
	 * @param y
	 */
	public void translate(double x, double y) {
		this.x += x;
		this.y += y;
	}

	/**
	 * Position the object at new coordinates.
	 * 
	 * @param x
	 * @param y
	 */
	public void position(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Turn the object.
	 * 
	 * @param angle How much to turn the object in RADIANS.
	 */
	public void turn(double angle) {
		this.angle += angle;
	}
	
	/**
	 * Turn the object.
	 * 
	 * @param angle How much to turn the object in DEGREES.
	 */
	public void turnInDegrees(double angle) {
		this.angle += Math.toRadians(angle);
	}
	
	/**
	 * Rotate the object to the specific angle.
	 * 
	 * @param angle The new angle for the object in RADIANS.
	 */
	public void rotateTo(double angle) {
		this.angle = angle;
	}
	
	/**
	 * Rotate the object to the specific angle.
	 * 
	 * @param angle The new angle for the object in DEGREES.
	 */
	public void rotateToInDegrees(double angle) {
		this.angle = Math.toRadians(angle);
	}
	
	/**
	 * Make the object point at the given coordinates.
	 * 
	 * @param x
	 * @param y
	 */
	public void point(double x, double y) {
		this.angle = Math.atan2(y - this.y, x - this.x);
	}
	
	/**
	 * Make the object point at the given object.
	 * 
	 * @param obj
	 */
	public void point(GameObject obj) {
		this.point(obj.x, obj.y);
	}
	
	/**
	 * 
	 * @return x-coordinate of the object.
	 */
	public double getX() {
		return this.x;
	}
	
	/**
	 * 
	 * @return y- coordinate of the object.
	 */
	public double getY() {
		return this.y;
	}
	
	/**
	 * Get the angle of the object in RADIANS.
	 * @return Rotation of the object.
	 */
	public double getAngle() {
		return this.angle;
	}
	
	/**
	 * Get the angle of the object in RADIANS.
	 * @return Rotation of the object.
	 */
	public double getAngleInDegrees() {
		return Math.toDegrees(this.angle);
	}
	
	/**
	 * Draw the object.
	 * 
	 * @param camera
	 */
	public void draw(Camera camera) {
		if (this.image != null) {
			this.image.setRotation((float) - this.getAngleInDegrees());
			this.image.drawCentered((float) camera.getScreenX(this.x), (float) camera.getScreenY(this.y));
		} else {
			System.err.println("The object (class: "+ this.getClass().getName() +") lacks an image.");
		}
	}
	
	/**
	 * Paint the object with a new Image.
	 * 
	 * @param img
	 */
	public void paint(Image img) {
		this.image = img;
	}
	
	/**
	 * Returns the image of the object.
	 * 
	 * @return
	 */
	public Image getImage() {
		return this.image;
	}
	
	/**
	 * Returns the color of the object at some specific world coordinates.
	 * 
	 * @param worldX
	 * @param worldY
	 * @return Returns null if it's not on the image.
	 */
	public Color pickColor(int worldX, int worldY) {
		if (this.image != null) {
			this.image.setRotation((float) - this.getAngleInDegrees());
			int x = (int) ((worldX - this.x) * Math.cos(angle) + (worldY - this.y) * Math.sin(angle) + this.image.getWidth() / 2);
			int y = (int) ((worldX - this.x) * Math.sin(angle) - (worldY - this.y) * Math.cos(angle) + this.image.getHeight() / 2);
			if (x >= 0 && y >= 0 && x < this.image.getWidth() && y < this.image.getHeight()) {
				return this.image.getColor(x, y);
			}
		}
		return null;
	}
	
	/**
	 * Checks if the specific coordinates are on the object.
	 * Uses pickColor and checks the alpha level of the color.
	 * 
	 * @param worldX
	 * @param worldY
	 * @return
	 */
	public boolean overlaps(int worldX, int worldY) {
		Color c = this.pickColor(worldX, worldY);
		return c != null && c.a != 0;
	}
	
}
