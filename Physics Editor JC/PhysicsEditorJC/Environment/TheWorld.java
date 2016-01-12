package Environment;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Landscapes.Field;
import Objects.BouncyBall;
import Objects.RandomClass;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class TheWorld
 extends JPanel //a "JPanel" is a window where we can see things
 implements Runnable,//"Runnable" means this program keeps track of time
 KeyListener//"KeyListener" means this program listens for user input.
 {
 JFrame mainFrame;
 private boolean running = true;
 public static int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
 public static int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
 private boolean rightHeld = false;
 private boolean leftHeld = false;
 private boolean upHeld = false;
 //private boolean downHeld=false;
 public static double gravity = -1.0;
 public static double wind;
 public static double accelerationVertical = 1.5;
 public static double accelerationHorizontal = 0.4;
 public static int seaLevel = SCREEN_HEIGHT - 150;//50 pixels above the bottom of the window
 
 /**
  * The following fields are all the objects that are visible in the environment
  * @param args
  */

 private ArrayList<Object> objects;
 private Landscape landscape;
 private BouncyBall ball;
 //private BouncyBall ball2;
 private RandomClass randomObject;

 public static void main(String[] args)
 {
	 new TheWorld();
 }
 
 public TheWorld() //set up just the program window and add the content from this environment
 {
	 mainFrame = new JFrame("The World");//creates a window called "The World"
	 mainFrame.setBounds(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);//makes the window as large as the computer screen
	 mainFrame.setVisible(true);//makes the window appear on the screen
	 mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//tells the whole program to "Quit" when this main window is closed 
	 mainFrame.add(this);//adds everything in "TheWorld" to this window
	 setFocusable(true);
	 addKeyListener(this);//makes "TheWorld" sensitive to input from the keyboard
	 landscape = new Field(SCREEN_WIDTH, SCREEN_HEIGHT);
	 ball = new BouncyBall(); 
	 //ball2 = new BouncyBall();
	 randomObject = new RandomClass();
	 objects = new ArrayList<Object>();
	 objects.add(ball);
	 //objects.add(ball2);
	 objects.add(randomObject);
	 run();
 }
 
 @Override
 public void paintComponent(Graphics g) //the method that paints everything you can see in the window
 {
 /**
  * BufferedImage is like a canvas that we paint on backstage while the 
  * audience (the user) is looking at what is one the stage. Once we
  * are finished painting the BufferedImage, we put it on the screen all at once
  * so that the audience can just see the finished product
  * they aren't literally watching the picture be created in front of their faces
  */
	 BufferedImage screen = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);//The buffered image must be exactly the same size as the screen
	 Graphics2D g2 = screen.createGraphics();//Graphics 2D is the tools we use to paint with
	 Color skyColor = new Color(20,20,20);
	 //51,153,255
	 //56,56,56
	 g2.setColor(skyColor);
	 g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
	 g2.drawImage(landscape.getAppearance(),0,0,null);
	 ball.draw(g2);
	 randomObject.draw(g2);
	 g2.setColor(Color.black);
	 g.drawImage(screen, 0, 0, null);
	 super.paintComponent(g2);//paints any normal Java.Swing components
 }
 
 @Override
 public void run() 
 {
	 while (running)
	 {
		 long currentTime = System.currentTimeMillis();//get the current time
		 applyControls();
		 applyPhysics();
		 update(getGraphics());//paint all of the objects
		 long timeTaken = System.currentTimeMillis() - currentTime;//figure out how long it took to paint everything
		 int pauseThisLength = (int)(33-timeTaken);//determine how long the animation should pause before the next frame is shown
		 if(pauseThisLength < 0) 
			 pauseThisLength = 1;//never have a negative pause
		 pause(pauseThisLength);//pause
	 }
 }
 
 private void applyControls() 
 {
	 if(rightHeld)
		 randomObject.setVx(randomObject.getVx()+accelerationHorizontal); 
	 if(leftHeld)
		 randomObject.setVx(randomObject.getVx()-accelerationHorizontal);
	 if(upHeld)
		 randomObject.setVy(randomObject.getVy()-accelerationVertical);
 }

private void applyPhysics() 
 {
	for(Object thing:objects)
	{
		thing.updatePosition(landscape.getLandscapeHeightAt(thing.getX()+((thing.getWidth()-1)/2), thing.getElevation()));
		thing.setVy(thing.getVy()-gravity);
		int groundUnderObject=landscape.getLandscapeHeightAt(thing.getX()+((thing.getWidth()-1)/2),thing.getElevation());//gets where the land is
		double dydx=landscape.getDyDxAt(thing.getX(), thing.getY());//gets the slope of the land
		if(thing.getY()>=groundUnderObject)//figures out if the ground has been hit
		{
			thing.setY(groundUnderObject);
			thing.hitGround(groundUnderObject, dydx);//tells the object to react to having hit ground
		}
	}
 }

private synchronized void pause(int time)
 {
	 try 
	 {
		 wait(time);//wait
	 } 
	 catch (InterruptedException e) 
	 {
		 e.printStackTrace();//prints an error whenever one occurs
	 }
 }
 
 @Override
 public synchronized void keyPressed(KeyEvent arg0) 
 { 
	 if(arg0.getKeyCode()==KeyEvent.VK_RIGHT) 
		 rightHeld=true;
	 if(arg0.getKeyCode()==KeyEvent.VK_LEFT) 
		 leftHeld=true;
	 if(arg0.getKeyCode()==KeyEvent.VK_UP) 
		 upHeld=true;
//	 if(arg0.getKeyCode()==KeyEvent.VK_DOWN) 
//		 downHeld=true;
 }

 @Override
 public synchronized void keyReleased(KeyEvent arg0) 
 {
	 if(arg0.getKeyCode()==KeyEvent.VK_RIGHT) 
		 rightHeld=false;
	 if(arg0.getKeyCode()==KeyEvent.VK_LEFT) 
		 leftHeld=false;
	 if(arg0.getKeyCode()==KeyEvent.VK_UP) 
		 upHeld=false;
 }

 @Override
 public synchronized void keyTyped(KeyEvent arg0) 
 {
	 
 }
}