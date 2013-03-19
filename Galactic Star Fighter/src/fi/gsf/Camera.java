package fi.gsf;

public class Camera {
	
	/** The x-coordinate of the camera in world coordinates. */
	private double x;
	/** The y-coordinate of the camera in world coordinates. */
	private double y;
	
	/**
	 * Create a new camera with the world coordinates (0, 0).
	 */
	public Camera() {
		this(0, 0);
	}
	
	/**
	 * Create a new camera with the world coordinates (x, y).
	 * 
	 * @param x The x-coordinate of the camera. (world coordinates)
	 * @param y The y-coordinate of the camera. (world coordinates)
	 */
	public Camera(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 *  Give the camera a new position in world coordinates.
	 *  
	 * @param x
	 * @param y
	 */
	public void position(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Returns the x-coordinate of the camera.
	 * @return
	 */
	public double getX() {
		return this.x;
	}
	
	/**
	 * Returns the y-coordinate of the camera.
	 * @return
	 */
	public double getY() {
		return this.y;
	}
	
	/**
	 * Converts the world x-coordinate to a screen x-coordinate.
	 * @param x
	 * @return
	 */
	public double getScreenX(double x) {
		return GSFGame.getScreenWidth() / 2 - this.x + x;
	}
	
	/**
	 * Converts the world x-coordinate to a screen x-coordinate.
	 * @param x
	 * @return
	 */
	public int getScreenX(int x) {
		return (int) (GSFGame.getScreenWidth() / 2 - this.x + x);
	}

	/**
	 * Converts the world y-coordinate to a screen y-coordinate.
	 * @param y
	 * @return
	 */
	public double getScreenY(double y) {
		return GSFGame.getScreenHeight() / 2 + this.y - y;
	}
	
	/**
	 * Converts the world y-coordinate to a screen y-coordinate.
	 * @param y
	 * @return
	 */
	public int getScreenY(int y) {
		return (int) (GSFGame.getScreenHeight() / 2 + this.y - y);
	}
	
	/**
	 * Converts the screen x-coordinate to a world x-coordinate.
	 * @param x
	 * @return
	 */
	public double getWorldX(double x) {
		return this.x + x - GSFGame.getScreenWidth() / 2;
	}
	
	/**
	 * Converts the screen x-coordinate to a world x-coordinate.
	 * @param x
	 * @return
	 */
	public int getWorldX(int x) {
		return (int) (this.x + x - GSFGame.getScreenWidth() / 2);
	}
	
	/**
	 * Converts the screen y-coordinate to a world y-coordinate.
	 * @param y
	 * @return
	 */
	public double getWorldY(double y) {
		return this.y - y + GSFGame.getScreenHeight() / 2;
	}
	
	/**
	 * Converts the screen y-coordinate to a world y-coordinate.
	 * @param y
	 * @return
	 */
	public int getWorldY(int y) {
		return (int) (this.y - y + GSFGame.getScreenHeight() / 2);
	}
}
