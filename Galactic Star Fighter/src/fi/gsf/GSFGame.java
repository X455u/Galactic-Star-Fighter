package fi.gsf;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.util.Log;

import fi.gsf.objects.*;
import fi.gsf.systems.EnemySystem;
import fi.gsf.systems.IonEmitter;
import fi.gsf.systems.ProjectileSystem;
import fi.gsf.systems.StarSystem;

public class GSFGame extends BasicGame {
	
	//GAME VARIABLES
	private static AppGameContainer app;
	
	private static double screenRatio = 16.0 / 10.0;
	private static double pixelRatio = 5.0; //pixels/meter
	
	private static int windowHeight = 800;
	private static int windowWidth = (int) (windowHeight * screenRatio);  //height: 800, ratio 16:10 => width: 1280
	
	private static int height = windowHeight;
	private static int width = (int) (height * screenRatio);  //height: 800, ratio 16:10 => width: 1280
	
	private static boolean fullscreen = false;
		
	private Camera camera;
	
	private static int worldHeight = 1000; // -1000 to 1000
	private static int worldWidth = (int) (worldHeight * screenRatio);
	
	
	
	private Spacecraft spacecraft;
	
	private EnemySystem enemies;
	
	private ProjectileSystem projectiles;

	private StarSystem stars;
	private ParticleSystem particles;
	
	
	
    public GSFGame() {
        super("Galactic Star Fighter");
    }
    
    @Override
    public void init(GameContainer container) throws SlickException {    	
    	camera = new Camera();
    	
    	stars = new StarSystem("images/other/star.png", 0.2f, 1000);

    	projectiles = new ProjectileSystem(new Image("images/other/star.png").getScaledCopy(0.5f));

    	spacecraft = new Spacecraft("images/ships/ship0.png", projectiles);
    	WeaponType weapontype1 = new WeaponType(new Image("images/guns/turret0.png").getScaledCopy(0.5f));
    	spacecraft.setTurrets(new Turret(weapontype1), 0);
    	spacecraft.setTurrets(new Turret(weapontype1), 1);
    	
    	particles = new ParticleSystem(new Image("images/other/particle.tga"));
    	particles.addEmitter(new IonEmitter(spacecraft, camera, 40));
    	
    	enemies = new EnemySystem(spacecraft, projectiles);
    	
    }

    @Override
    public void update(GameContainer container, int delta)
            throws SlickException {
    	
    	
    	spacecraft.update(keyDown(Input.KEY_W), keyDown(Input.KEY_S), keyDown(Input.KEY_A), keyDown(Input.KEY_D), delta);
    	
    	if (container.getInput().isMouseButtonDown(0)) {
    		spacecraft.shoot();
    	}
    	
    	if (container.getInput().isKeyPressed(Input.KEY_SPACE)) {
        	enemies.respawnSwarmers(40);
    	}
    	
    	camera.position( (worldWidth - width / 2) * spacecraft.getX() / worldWidth, (worldHeight - height / 2) * spacecraft.getY() / worldHeight);
    	
    	enemies.update(delta);
    	
    	stars.update(delta);
    	
    	particles.update(delta);
    	
    	projectiles.update(delta);
    	
    }

    @Override
    public void render(GameContainer container, Graphics g)
            throws SlickException {
    	
    	stars.render(camera);
    	
    	particles.render();
    	
    	spacecraft.draw(camera);
    	
    	enemies.render(camera, g);
    	
    	projectiles.render(camera);
        
    	g.setColor(Color.white);
//    	g.drawString("Camera x: " + camera.getX(), 50, 50);
//    	g.drawString("Camera y: " + camera.getY(), 50, 70);
//    	g.drawString(spacecraft.getInfo(), 50, 90);
//    	g.drawString("Projectiles: " + projectiles.getSize(), 50, 50);
//    	g.drawString("MouseX: " + Mouse.getX() + "\nMouseY: " + Mouse.getY(), 50, 70);
    	
    	//fun color pick test :P
//    	for (int x = -50; x < 50; x++) {
//    		for (int y = -50; y < 50; y++) {
//    			Color c = spacecraft.pickColor((int)spacecraft.getX() + x, (int)spacecraft.getY() + y);
//    			if (c != null && c.a != 0) {
//    				g.fillRect((float)camera.getScreenX(this.spacecraft.getX() + x), (float)camera.getScreenY(this.spacecraft.getY() + y), 1, 1);
//    			}
//    		}
//    	}
    	
    }
    
    @Override
    public void keyPressed(int key, char c) { 
        if (key == Input.KEY_ESCAPE) { 
            System.exit(0); 
        } 
        if (key == Input.KEY_F1) { 
            if (app != null) {
            	fullscreen = !fullscreen;
                try {
                	if (fullscreen) {
                		width = app.getScreenWidth();
                		height = app.getScreenHeight();
                	} else {
                		width = windowWidth;
                		height = windowHeight;
                	}
                    app.setDisplayMode(width, height, fullscreen);
//                    app.reinit(); 
                } catch (Exception e) { Log.error(e); } 
            } 
        } 
    } 
    
    
    public static int getWorldWidth() {
    	return worldWidth;
    }
    
    public static int getWorldHeight() {
    	return worldHeight;
    }

    public static int getScreenWidth() {
    	return width;
    }
    
    public static int getScreenHeight() {
    	return height;
    }
    
    public static double getPixelRatio() {
    	return pixelRatio;
    }
    
    private boolean keyDown(int key) {
    	return app.getInput().isKeyDown(key);
    }
    
    
    public static void main(String[] args) {
        try {
            app = new AppGameContainer(new GSFGame(), width, height, fullscreen);
//            app.setShowFPS(false);
//            app.setTargetFrameRate(100);
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

}