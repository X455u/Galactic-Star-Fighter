package fi.gsf.systems;

import org.newdawn.slick.Image;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleEmitter;
import org.newdawn.slick.particles.ParticleSystem;

import fi.gsf.Camera;
import fi.gsf.objects.Spacecraft;


public class IonEmitter implements ParticleEmitter {
	
	/** The spacecraft emitting the particles */
	private Spacecraft ship;
	/** The camera of the game */
	private Camera camera;
	
	/** The previous x-coordinate of the camera. */
	private double cameraX;
	/** The previous y-coordinate of the camera. */
	private double cameraY;
	/** How much the camera coordinate has changed in relation to the x-axis. */ 
	private double cameraDx;
	/** How much the camera coordinate has changed in relation to the y-axis. */ 
	private double cameraDy;
	
	/** The particle emission rate */
	private int interval = 50;
	/** Time til the next particle */
	private int timer;
	/** The size of the initial particles */
	private float size = 40;
	


	public IonEmitter(Spacecraft craft, Camera cam, int size) {
		this.ship = craft;
		this.camera = cam;
		this.size = size;
	}

	
	/**
	 * Update the ParticleSystem.
	 */
	public void update(ParticleSystem system, int delta) {
		timer -= delta;
		if (timer <= 0) {
			timer = interval;
			Particle p = system.getNewParticle(this, 1000);
			p.setColor(0.5f, 0.5f, 1, 0.5f);
			p.setPosition( (int) camera.getScreenX(ship.getX()), (int) camera.getScreenY(ship.getY()) );
			p.setSize(size);
			double v = 0.3;
			float vx = (float) (ship.getXVelocity() * 0.7 - v * Math.cos(ship.getAngle()) );
			float vy = (float) (- ship.getYVelocity() * 0.7 + v * Math.sin(ship.getAngle()) );
			p.setVelocity(vx,vy);
		}
		this.cameraDx = this.cameraX - this.camera.getX();
		this.cameraDy = this.camera.getY() - this.cameraY;
		this.cameraX = this.camera.getX();
		this.cameraY = this.camera.getY();
	}

	/**
	 * Update the particles.
	 */
	public void updateParticle(Particle particle, int delta) {
		if (particle.getLife() > 600) {
			particle.adjustSize(0.07f * delta);
		} else {
			particle.adjustSize(-0.04f * delta * (size / 40.0f));
		}
		float c = 0.002f * delta;
		particle.adjustColor(c/8, c/8, 0,-c/2);
		particle.adjustPosition( (float) this.cameraDx, (float) this.cameraDy);
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#completed()
	 */
	public boolean completed() {
		return false;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#useAdditive()
	 */
	public boolean useAdditive() {
		return false;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#getImage()
	 */
	public Image getImage() {
		return null;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#usePoints(org.newdawn.slick.particles.ParticleSystem)
	 */
	public boolean usePoints(ParticleSystem system) {
		return false;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#isOriented()
	 */
	public boolean isOriented() {
		return false;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#wrapUp()
	 */
	public void wrapUp() {
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#resetState()
	 */
	public void resetState() {
	}
}
