import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.golden.gamedev.*;
import com.golden.gamedev.object.CollisionManager;
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.SpriteGroup;
import com.golden.gamedev.object.background.ImageBackground;
import com.golden.gamedev.object.sprite.AdvanceSprite;
import com.golden.gamedev.object.collision.*;

public class game extends Game{
	ImageBackground start, prologue, l1s1, l1s2, l1s3, l2s1, l2s2, l2s3, l3s1, l3s2, l3s3,
		l4s1, l4s2, l4s3, l5s1, l5s2, l5s3, l6s1, l6s2, l6s3, l7s1, l7s2, l7s3, l8s1, l8s2, l8s3,
		l9s1, l9s2, l9s3, l10s1, l10s2, l10s3, epilogue, credits;
	int level, jump_state, screen, cooldown, enemies_deactivated;
	final int DIR = 0, WALK = 1, JUMP = 2;
	boolean jump_height, frequency, move_speed, pause, dev_mode, released;
	AdvanceSprite simple_being;
	Sprite pwr_height, pwr_freq, pwr_speed, enemy;
	SpriteGroup GROUP_player, GROUP_enemy, GROUP_powerup;
	CollisionManager enemy_player, player_powerup;
	Font eras;
	
	public void initResources(){
		//Initialize backgrounds
		start = new ImageBackground(getImage("res/start.png"));
		prologue = new ImageBackground(getImage("res/prologue.png"));
		epilogue = new ImageBackground(getImage("res/epilogue.png"));
		credits = new ImageBackground(getImage("res/credits.png"));
		
		l1s1 = new ImageBackground(getImage("res/l1s1.png"));
		l1s2 = new ImageBackground(getImage("res/l1s2.png"));
		l1s3 = new ImageBackground(getImage("res/l1s3.png"));
		
		l2s1 = new ImageBackground(getImage("res/l2s1.png"));
		l2s2 = new ImageBackground(getImage("res/l2s2.png"));
		l2s3 = new ImageBackground(getImage("res/l2s3.png"));
		
		l3s1 = new ImageBackground(getImage("res/l3s1.png"));
		l3s2 = new ImageBackground(getImage("res/l3s2.png"));
		l3s3 = new ImageBackground(getImage("res/l3s3.png"));
		
		l4s1 = new ImageBackground(getImage("res/l4s1.png"));
		l4s2 = new ImageBackground(getImage("res/l4s2.png"));
		l4s3 = new ImageBackground(getImage("res/l4s3.png"));
		
		l5s1 = new ImageBackground(getImage("res/l5s1.png"));
		l5s2 = new ImageBackground(getImage("res/l5s2.png"));
		l5s3 = new ImageBackground(getImage("res/l5s3.png"));
		
		l6s1 = new ImageBackground(getImage("res/l6s1.png"));
		l6s2 = new ImageBackground(getImage("res/l6s2.png"));
		l6s3 = new ImageBackground(getImage("res/l6s3.png"));
		
		l7s1 = new ImageBackground(getImage("res/l7s1.png"));
		l7s2 = new ImageBackground(getImage("res/l7s2.png"));
		l7s3 = new ImageBackground(getImage("res/l7s3.png"));
		
		l8s1 = new ImageBackground(getImage("res/l8s1.png"));
		l8s2 = new ImageBackground(getImage("res/l8s2.png"));
		l8s3 = new ImageBackground(getImage("res/l8s3.png"));
		
		l9s1 = new ImageBackground(getImage("res/l9s1.png"));
		l9s2 = new ImageBackground(getImage("res/l9s2.png"));
		l9s3 = new ImageBackground(getImage("res/l9s3.png"));
		
		l10s1 = new ImageBackground(getImage("res/l10s1.png"));
		l10s2 = new ImageBackground(getImage("res/l10s2.png"));
		l10s3 = new ImageBackground(getImage("res/l10s3.png"));
		
		BufferedImage[] simple_being_array = {getImage("res/simple_being_walk.png"), 
				getImage("res/simple_being_jump.png")};
		
		try {
			eras = Font.createFont(Font.TRUETYPE_FONT, new File("res/ErasMediumITC.ttf")).deriveFont(20f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/ErasMediumITC.ttf")));
		} catch (FontFormatException | IOException e) {}
		
		//Initialize player
		simple_being = new AdvanceSprite(simple_being_array, 10, 350);
		simple_being.setAnimate(true);
		simple_being.setStatus(WALK);
		
		//Initialize powerups
		pwr_height = new Sprite(getImage("res/jump_height.png"), 150, 350);
		pwr_height.setActive(false);
		pwr_freq = new Sprite(getImage("res/jump_freq.png"), 150, 350);
		pwr_freq.setActive(false);
		pwr_speed = new Sprite(getImage("res/move_speed.png"), 150, 350);
		pwr_speed.setActive(false);
		
		//Initialize sprite groups and collision managers
		GROUP_player = new SpriteGroup("The Player");
		GROUP_enemy = new SpriteGroup("The Enemies");
		GROUP_powerup = new SpriteGroup("The Powerups");
		GROUP_player.add(simple_being);
		GROUP_enemy.add(new Sprite(getImage("res/gnome.png"), 300, 386));
		GROUP_enemy.add(new Sprite(getImage("res/gnome.png"), 600, 386));
		enemy = GROUP_enemy.getActiveSprite();
		enemy_player = new EnemyCollision();
		enemy_player.setCollisionGroup(GROUP_player, GROUP_enemy);
		player_powerup = new PowerupCollision();
		player_powerup.setCollisionGroup(GROUP_player, GROUP_powerup);
		
		//Initialize variables
		level = -1;
		jump_state = 0;
		screen = 0;
		cooldown = 0;
		jump_height = false;
		frequency = false;
		move_speed = false;
		pause = false;
		released = true;
		enemies_deactivated = 0;
		
		//Dev mode toggle:
		dev_mode = true;
	}
	public void update(long elapsedTime){
		GROUP_player.update(elapsedTime);
		GROUP_enemy.update(elapsedTime);
		GROUP_powerup.update(elapsedTime);
		/*if(enemies_deactivated  < 2){
			enemy = GROUP_enemy.getActiveSprite();
		}*/
				
		if(level != -2147483647 && level <= 0 && bsInput.isKeyPressed(KeyEvent.VK_ENTER)){
			level++;
		}
		if(level == 2147483647 && bsInput.isKeyPressed(KeyEvent.VK_ENTER)){
			level = -2147483647;
		}
		if(level > 0 && pause == false){
			//Movement
			if(bsInput.isKeyDown(KeyEvent.VK_A)){
				if(move_speed == true){
					simple_being.moveX(-4);
				}
				else{
					simple_being.moveX(-2);
				}
			}
			if(bsInput.isKeyDown(KeyEvent.VK_D)){
				if(move_speed == true){
					simple_being.moveX(4);
				}
				else{
					simple_being.moveX(2);
				}
			}
			
			//Enemy movement
			if(enemies_deactivated < 2 && level <= 10){
				enemy.moveX(-1);
				if(enemy.getX() < -100){
					enemy.setActive(false);
					enemy = GROUP_enemy.getActiveSprite();
					enemies_deactivated ++;
					System.out.println(enemies_deactivated);
				}
			}
			
			//Jump ability
			if(cooldown == 0 && jump_state == 0 && bsInput.isKeyPressed(KeyEvent.VK_SPACE)){
				playSound("res/jump.wav");
				jump_state = 1;
				animationChanged(simple_being.getStatus(), JUMP);
			}
			if(jump_height == false){
				if(jump_state == 1 && simple_being.getY() == 250){
					jump_state = 2;
				}
			}
			if(jump_height == true){
				if(jump_state == 1 && simple_being.getY() == 150){
					jump_state = 2;
				}
			}
			if(jump_state == 2 && simple_being.getY() == 350){
				jump_state = 0;
				animationChanged(simple_being.getStatus(), WALK);
				if(frequency == true)
					cooldown = 200;
				else
					cooldown = 400;
			}
			if(jump_state == 1){
				simple_being.moveY(-2);
			}
			if(jump_state == 2){
				simple_being.moveY(2);
			}
			if(cooldown > 0){
				cooldown --;
			}
			
			//Borders
			if(simple_being.getX() < 0){
				simple_being.setX(0);
			}
			if(simple_being.getX() > getWidth() && screen != 2){
				simple_being.setX(0);
				screen ++;
				
				GROUP_enemy.clear();
				if(level == 1 && screen == 1){
					GROUP_enemy.add(new Sprite(getImage("res/gnome.png"), 300, 386));
					GROUP_enemy.add(new Sprite(getImage("res/gnome.png"), 600, 386));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 1 && screen == 2){
					GROUP_enemy.add(new Sprite(getImage("res/gnome.png"), 300, 386));
					GROUP_enemy.add(new Sprite(getImage("res/gnome.png"), 600, 386));
					enemy = GROUP_enemy.getActiveSprite();
				}
				
				if(level == 2 && screen == 1){
					GROUP_enemy.add(new Sprite(getImage("res/gnome.png"), 300, 386));
					GROUP_enemy.add(new Sprite(getImage("res/gnome.png"), 600, 386));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 2 && screen == 2){
					GROUP_enemy.add(new Sprite(getImage("res/gnome.png"), 300, 386));
					GROUP_enemy.add(new Sprite(getImage("res/gnome.png"), 600, 386));
					enemy = GROUP_enemy.getActiveSprite();
				}
				
				if(level == 3 && screen == 1){
					GROUP_enemy.add(new Sprite(getImage("res/troll.png"), 300, 330));
					GROUP_enemy.add(new Sprite(getImage("res/troll.png"), 600, 330));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 3 && screen == 2){
					GROUP_enemy.add(new Sprite(getImage("res/troll.png"), 300, 330));
					GROUP_enemy.add(new Sprite(getImage("res/troll.png"), 600, 330));
					enemy = GROUP_enemy.getActiveSprite();
				}
				
				if(level == 4 && screen == 1){
					GROUP_enemy.add(new Sprite(getImage("res/troll.png"), 300, 330));
					GROUP_enemy.add(new Sprite(getImage("res/troll.png"), 600, 330));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 4 && screen == 2){
					GROUP_enemy.add(new Sprite(getImage("res/troll.png"), 300, 330));
					GROUP_enemy.add(new Sprite(getImage("res/troll.png"), 600, 330));
					enemy = GROUP_enemy.getActiveSprite();
				}
				
				if(level == 5 && screen == 1){
					GROUP_enemy.add(new Sprite(getImage("res/mummy.png"), 300, 350));
					GROUP_enemy.add(new Sprite(getImage("res/mummy.png"), 600, 350));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 5 && screen == 2){
					GROUP_enemy.add(new Sprite(getImage("res/mummy.png"), 300, 350));
					GROUP_enemy.add(new Sprite(getImage("res/mummy.png"), 600, 350));
					enemy = GROUP_enemy.getActiveSprite();
				}
				
				if(level == 6 && screen == 1){
					GROUP_enemy.add(new Sprite(getImage("res/mummy.png"), 300, 350));
					GROUP_enemy.add(new Sprite(getImage("res/mummy.png"), 600, 350));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 6 && screen == 2){
					GROUP_enemy.add(new Sprite(getImage("res/mummy.png"), 300, 350));
					GROUP_enemy.add(new Sprite(getImage("res/mummy.png"), 600, 350));
					enemy = GROUP_enemy.getActiveSprite();
				}
				
				if(level == 7 && screen == 1){
					GROUP_enemy.add(new Sprite(getImage("res/spirit.png"), 300, 289));
					GROUP_enemy.add(new Sprite(getImage("res/spirit.png"), 600, 289));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 7 && screen == 2){
					GROUP_enemy.add(new Sprite(getImage("res/spirit.png"), 300, 289));
					GROUP_enemy.add(new Sprite(getImage("res/spirit.png"), 600, 289));
					enemy = GROUP_enemy.getActiveSprite();
				}
				
				if(level == 8 && screen == 1){
					GROUP_enemy.add(new Sprite(getImage("res/spirit.png"), 300, 289));
					GROUP_enemy.add(new Sprite(getImage("res/spirit.png"), 600, 289));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 8 && screen == 2){
					GROUP_enemy.add(new Sprite(getImage("res/spirit.png"), 300, 289));
					GROUP_enemy.add(new Sprite(getImage("res/spirit.png"), 600, 289));
					enemy = GROUP_enemy.getActiveSprite();
				}
				
				if(level == 9 && screen == 1){
					GROUP_enemy.add(new Sprite(getImage("res/scribble.png"), 300, 350));
					GROUP_enemy.add(new Sprite(getImage("res/scribble.png"), 600, 350));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 9 && screen == 2){
					GROUP_enemy.add(new Sprite(getImage("res/scribble.png"), 300, 350));
					GROUP_enemy.add(new Sprite(getImage("res/scribble.png"), 600, 350));
					enemy = GROUP_enemy.getActiveSprite();
				}
				
				if(level == 10 && screen == 1){
					GROUP_enemy.add(new Sprite(getImage("res/scribble.png"), 300, 350));
					GROUP_enemy.add(new Sprite(getImage("res/scribble.png"), 600, 350));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 10 && screen == 2){
					GROUP_enemy.add(new Sprite(getImage("res/scribble.png"), 300, 350));
					GROUP_enemy.add(new Sprite(getImage("res/scribble.png"), 600, 350));
					enemy = GROUP_enemy.getActiveSprite();
				}
				
				enemies_deactivated = 0;
			}
			if((simple_being.getX() > getWidth() && screen == 2 && level < 10) || 
					(bsInput.isKeyPressed(KeyEvent.VK_NUMPAD0) && dev_mode)){
				simple_being.setX(0);
				level ++;
				screen = 0;
				GROUP_powerup.clear();
				GROUP_enemy.clear();
				
				if(level == 2 && screen == 0){
					GROUP_enemy.add(new Sprite(getImage("res/gnome.png"), 300, 386));
					GROUP_enemy.add(new Sprite(getImage("res/gnome.png"), 600, 386));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 3 && screen == 0){
					GROUP_enemy.add(new Sprite(getImage("res/troll.png"), 300, 330));
					GROUP_enemy.add(new Sprite(getImage("res/troll.png"), 600, 330));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 4 && screen == 0){
					GROUP_enemy.add(new Sprite(getImage("res/troll.png"), 300, 330));
					GROUP_enemy.add(new Sprite(getImage("res/troll.png"), 600, 330));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 5 && screen == 0){
					GROUP_enemy.add(new Sprite(getImage("res/mummy.png"), 300, 350));
					GROUP_enemy.add(new Sprite(getImage("res/mummy.png"), 600, 350));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 6 && screen == 0){
					GROUP_enemy.add(new Sprite(getImage("res/mummy.png"), 300, 350));
					GROUP_enemy.add(new Sprite(getImage("res/mummy.png"), 600, 350));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 7 && screen == 0){
					GROUP_enemy.add(new Sprite(getImage("res/spirit.png"), 300, 289));
					GROUP_enemy.add(new Sprite(getImage("res/spirit.png"), 600, 289));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 8 && screen == 0){
					GROUP_enemy.add(new Sprite(getImage("res/spirit.png"), 300, 289));
					GROUP_enemy.add(new Sprite(getImage("res/spirit.png"), 600, 289));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 9 && screen == 0){
					GROUP_enemy.add(new Sprite(getImage("res/scribble.png"), 300, 350));
					GROUP_enemy.add(new Sprite(getImage("res/scribble.png"), 600, 350));
					enemy = GROUP_enemy.getActiveSprite();
				}
				if(level == 10 && screen == 0){
					GROUP_enemy.add(new Sprite(getImage("res/scribble.png"), 300, 350));
					GROUP_enemy.add(new Sprite(getImage("res/scribble.png"), 600, 350));
					enemy = GROUP_enemy.getActiveSprite();
				}
				enemies_deactivated = 0;
				
				if(level == 3 && screen == 0){
					pwr_height.setActive(true);
					GROUP_powerup.add(pwr_height);
				}
				if(level == 5 && screen == 0){
					pwr_freq.setActive(true);
					GROUP_powerup.add(pwr_freq);
				}
				if(level == 7 && screen == 0){
					pwr_speed.setActive(true);
					GROUP_powerup.add(pwr_speed);
				}
			}
			if(simple_being.getX() > getWidth() && screen == 2 && level == 10){
				simple_being.setActive(false);
				level = 2147483647;
				screen = 0;
			}
			
			//Death
			enemy_player.checkCollision();
			if(simple_being.isActive() == false && level != 2147483647){
				playSound("res/death.wav");
				screen = 0;
				simple_being.setX(10);
				simple_being.setActive(true);
			}
			
			//Powerups
			player_powerup.checkCollision();
			if(level == 3 && pwr_height.isActive() == false && jump_state == 0){
				jump_height = true;
				GROUP_powerup.clear();
			}
			if(level == 5 && pwr_freq.isActive() == false){
				frequency = true;
				GROUP_powerup.clear();
			}
			if(level == 7 && pwr_speed.isActive() == false){
				move_speed = true;
				GROUP_powerup.clear();
			}
			
			//Pause
			if(bsInput.isKeyPressed(KeyEvent.VK_ESCAPE) && released){
				pause = true;
				released = false;
				System.out.println("paused");
			}
			
			//Dev mode tools
			if(dev_mode){
				if(bsInput.isKeyPressed(KeyEvent.VK_NUMPAD1)){
					jump_height = true;
				}
				if(bsInput.isKeyPressed(KeyEvent.VK_NUMPAD2)){
					frequency = true;
				}
				if(bsInput.isKeyPressed(KeyEvent.VK_NUMPAD3)){
					move_speed = true;
				}
			}
		}
		if(bsInput.isKeyReleased(KeyEvent.VK_ESCAPE)){
			released = true;
		}
		if(level > 0 && pause == true && released){
			if(bsInput.isKeyPressed(KeyEvent.VK_ESCAPE)){
				pause = false;
				System.out.println("unpaused");
			}
		}
	}
	
	public void render(Graphics2D g){
		g.setFont(eras);
		//Render start screen
		if(level == -1){
			start.render(g);
		}
		//Render prologue
		else if(level == 0){
			prologue.render(g);
		}
		//Render epilogue
		else if(level == 2147483647){
			epilogue.render(g);
			GROUP_enemy.setActive(false);
		}
		else if(level == -2147483647){
			credits.render(g);
		}
		//Render the levels
		else{
			if(level == 1){
				if(screen == 0){
					l1s1.render(g);
				}
				if(screen == 1){
					l1s2.render(g);
				}
				if(screen == 2){
					l1s3.render(g);
				}
				GROUP_enemy.render(g);
			}
			if(level == 2){
				if(screen == 0){
					l2s1.render(g);
				}
				if(screen == 1){
					l2s2.render(g);
				}
				if(screen == 2){
					l2s3.render(g);
				}
				GROUP_enemy.render(g);
			}
			if(level == 3){
				if(screen == 0){
					l3s1.render(g);
				}
				if(screen == 1){
					l3s2.render(g);
				}
				if(screen == 2){
					l3s3.render(g);
				}
				GROUP_enemy.render(g);
			}
			if(level == 4){
				if(screen == 0){
					l4s1.render(g);
				}
				if(screen == 1){
					l4s2.render(g);
				}
				if(screen == 2){
					l4s3.render(g);
				}
				GROUP_enemy.render(g);
			}
			if(level == 5){
				if(screen == 0){
					l5s1.render(g);
				}
				if(screen == 1){
					l5s2.render(g);
				}
				if(screen == 2){
					l5s3.render(g);
				}
				GROUP_enemy.render(g);
			}
			if(level == 6){
				if(screen == 0){
					l6s1.render(g);
				}
				if(screen == 1){
					l6s2.render(g);
				}
				if(screen == 2){
					l6s3.render(g);
				}
				GROUP_enemy.render(g);
			}
			if(level == 7){
				if(screen == 0){
					l7s1.render(g);
				}
				if(screen == 1){
					l7s2.render(g);
				}
				if(screen == 2){
					l7s3.render(g);
				}
				GROUP_enemy.render(g);
			}
			if(level == 8){
				if(screen == 0){
					l8s1.render(g);
				}
				if(screen == 1){
					l8s2.render(g);
				}
				if(screen == 2){
					l8s3.render(g);
				}
				GROUP_enemy.render(g);
			}
			if(level == 9){
				if(screen == 0){
					l9s1.render(g);
				}
				if(screen == 1){
					l9s2.render(g);
				}
				if(screen == 2){
					l9s3.render(g);
				}
				GROUP_enemy.render(g);
			}
			if(level == 10){
				if(screen == 0){
					l10s1.render(g);
				}
				if(screen == 1){
					l10s2.render(g);
				}
				if(screen == 2){
					l10s3.render(g);
				}
				GROUP_enemy.render(g);
			}
			GROUP_player.render(g);
			GROUP_powerup.render(g);
			
			//HUD Items
			if(cooldown == 0){
				g.drawString("Jump Cooldown: 0", 10, 20);
			}
			if(300 < cooldown && cooldown <= 400){
				g.drawString("Jump Cooldown: [][][][]", 10, 20);
			}
			if(200 < cooldown && cooldown <= 300){
				g.drawString("Jump Cooldown: [][][]", 10, 20);
				
			}
			if(100 < cooldown && cooldown <= 200){
				g.drawString("Jump Cooldown: [][]", 10, 20);
			}
			if(0 < cooldown && cooldown <= 100){
				g.drawString("Jump Cooldown: []", 10, 20);
			}
			if(jump_height == true){
				g.drawImage(getImage("res/jump_height.png"), 10, 40, null);
			}
			if(frequency == true){
				g.drawImage(getImage("res/jump_freq.png"), 61, 40, null);
			}
			if(move_speed == true){
				g.drawImage(getImage("res/move_speed.png"), 112, 40, null);
			}
			if(pause == true){
				g.drawString("Paused. Press ESCAPE to resume.", 10, 250);
			}
		}
	}
	
	protected void animationChanged(int oldStat, int status){
		switch(status){
		case WALK:
			simple_being.setAnimationFrame(0, 0);
			break;
		case JUMP:
			simple_being.setAnimationFrame(1, 1);
			break;
		}
	}
	
	public static void main(String args[]){
		GameLoader jump = new GameLoader();
		jump.setup(new game(), new Dimension(800, 500), false);
		jump.start();
	}
}

//Enemy-player collision group
class EnemyCollision extends BasicCollisionGroup{
	public EnemyCollision(){
		pixelPerfectCollision = true;
	}
	public void collided(Sprite player, Sprite enemy){
		player.setActive(false);
	}
}

//Player-powerup collision group
class PowerupCollision extends BasicCollisionGroup{
	public PowerupCollision(){
		pixelPerfectCollision = true;
	}
	public void collided(Sprite player, Sprite powerup){
		powerup.setActive(false);
	}
}
