package fi.gsf.systems;

import java.util.ArrayList;
import java.util.Iterator;

import org.newdawn.slick.Graphics;

import fi.gsf.Camera;
import fi.gsf.GSFGame;
import fi.gsf.objects.Spacecraft;
import fi.gsf.objects.enemies.Swarmer;

/**
 * The system that takes care of the updating and rendering of the enemies.
 * 
 * @author X455u
 */
public class EnemySystem {
	
	/** How far from the world edge the enemies should respawn.
	 * 
	 * Enemy_y = GSFGame.GetWorldHeight() + RESPAWN_LINE;.
	 */
	private final int RESPAWN_LINE = 100;
	
	/** The player's spacecraft. */
	private Spacecraft player;
	
	/** The game's projectile system. */
	private ProjectileSystem projectiles;
	
	/** The swarmers. */
	private ArrayList<Swarmer> swarmers;
	
	/** Create a new enemy system. */
	public EnemySystem(Spacecraft spaceship, ProjectileSystem projectiles) {
		this.player = spaceship;
		this.projectiles = projectiles;
		
		this.swarmers = new ArrayList<Swarmer>();
	}
	
	/**
	 * Respawn Swarmers in a group outside the world.
	 * @param amount
	 */
	public void respawnSwarmers(int amount) {
		int swarmerArea = 300;
		//radius of group
		for (int i = 0; i < amount; i++) {
			double angle = 2 * Math.PI * Math.random(); 
			double radius = Math.sqrt(swarmerArea * amount / Math.PI) * Math.random();
			this.swarmers.add(new Swarmer((int) Math.cos(angle) * radius, GSFGame.getWorldHeight() + RESPAWN_LINE + radius + (int) Math.sin(angle) * radius));
		}
	}
	
	
	/**
	 * Update all enemies.
	 * @param delta
	 */
	public void update(int delta) {
		
		//calculate group center
		double groupX = 0;
		double groupY = 0;
		for (Swarmer s : swarmers) {
			groupX += s.getX();
			groupY += s.getY();
		}
		groupX /= swarmers.size();
		groupY /= swarmers.size();
		
		//update swarmers
		Iterator<Swarmer> iterator = swarmers.iterator();
		while (iterator.hasNext()) {
			Swarmer s = iterator.next();
			s.update(delta, player, groupX, groupY, swarmers);
			projectiles.checkObject(s);
			if (s.isDeletable()) {
				iterator.remove();
			}
		}
	}
	
	/**
	 * Render all enemies.
	 * 
	 * @param camera
	 * @param g 
	 */
	public void render(Camera camera, Graphics g) {
		
		//draw swarmers
		for (Swarmer s : swarmers) {
			s.draw(camera, g);
		}
		
	}
	
}
