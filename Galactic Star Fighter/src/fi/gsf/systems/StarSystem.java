package fi.gsf.systems;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import fi.gsf.Camera;
import fi.gsf.GSFGame;

public class StarSystem {
	
	/** The master image of a star */
	private Image image;
	
	/** The velocity of the nearest/closest stars. */
	private float velocity;
	
	/** The amount of stars */
	private int amount;
	
	/** A list of all the stars. */
	private ArrayList<Star> stars;
	
	
	public StarSystem(String ref) throws SlickException {
		this(ref, 0.2f);
	}
	
	public StarSystem(String ref, float velocity) throws SlickException {
		this(ref, velocity, 500);
	}
	
	public StarSystem(String ref, float velocity, int amount) throws SlickException {
		this.image = new Image(ref);
		
		this.velocity = Math.abs(velocity);
		this.amount = Math.abs(amount);
		
		this.stars = new ArrayList<Star>(this.amount);
		
		for (int i = 0; i < this.amount; i++) {
			//weighted rng that produces numbers between 0.3 and 0.8, so that it's more likely for them to be smaller.
			float scale = (float) (- Math.log(0.489682 - 0.34* Math.random()) / 2.38);
			float x = (float) (2 * (Math.random() - 0.5) * ( (GSFGame.getWorldWidth() - GSFGame.getScreenWidth() / 2) * scale + GSFGame.getScreenWidth() / 2));
			float y = (float) (2 * (Math.random() - 0.5) * ( GSFGame.getScreenHeight() / 2 + (GSFGame.getWorldHeight() - GSFGame.getScreenHeight() / 2) * scale));
			Color color = new Color((float) (1 - 0.3 * Math.random()), (float) (1 - 0.3 * Math.random()), (float) (1 - 0.3 * Math.random()));
			color = color.darker(0.8f - scale);
			this.stars.add(new Star(x, y, scale, color));
		}
	}
	
	
	public void update(int delta) {
		for (Star star : stars) {
			star.update(delta);
		}
	}
	
	public void render(Camera camera) {
		for (Star star : stars) {
			star.render(camera);
		}
	}
	

	private class Star {
		
		private float x;
		private float y;
		
		private float scale;
		
		private Color color;
				
		
		public Star (float x, float y, float scale, Color color) {
			this.x = x;
			this.y = y;
			this.scale = scale;
			this.color = color;
		}
		
		public void render(Camera camera) {
			image.draw((float) (GSFGame.getScreenWidth() / 2 + this.x - camera.getX() * scale), (float) (GSFGame.getScreenHeight() / 2 - this.y + camera.getY() * scale), (float) scale, color);
		}

		public void update(int delta) {
			this.y -= velocity * this.scale * delta;
			
			if (this.y < - GSFGame.getScreenHeight() / 2 + (- GSFGame.getWorldHeight() + GSFGame.getScreenHeight() / 2) * this.scale) {
				//replace, resize and recolor
				this.scale = (float) (- Math.log(0.489682 - 0.34* Math.random()) / 2.38);
				this.x = (float) (2 * (Math.random() - 0.5) * ( (GSFGame.getWorldWidth() - GSFGame.getScreenWidth() / 2) * this.scale + GSFGame.getScreenWidth() / 2));
				this.y = GSFGame.getScreenHeight() / 2 + (GSFGame.getWorldHeight() - GSFGame.getScreenHeight() / 2) * this.scale + 10;
				this.color = new Color((float) (1 - 0.3 * Math.random()), (float) (1 - 0.3 * Math.random()), (float) (1 - 0.3 * Math.random()));
				this.color = this.color.darker(0.8f - scale);
			}
		}
		
		
	}
	
}
