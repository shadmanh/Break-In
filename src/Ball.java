/*Ball.java
 *Shadman Hassan
 *
 * This class is used in the game BreakIn. Each tennis ball used in the game is an object of this class. Each ball has
 * many fields that state its location, size and which direction it is going in at what speed(x,y velocities and
 * directions). It also has methods that handle collision detection. The ball bounces back according to which object
 * was hit (window,brick,paddle) from which direction. Among many setter and accessor methods, a ball also has a
 * variable recording its current state (it can be dormant on the paddle or moving around the screen) . the ball can
 * move and draw itself.
 */

import java.awt.*;
import java.util.*;

public class Ball{
    private int cenx,ceny,xvel, yvel,radius,resx,resy,horDir,vertDir; //variables for the ball's center
    //coordinates, x and y velocity variables for radius, width and height of the window
    //and which vertical direction it's going and which horizontal direction its going
    private Bar paddle; //paddle used in the game
    private boolean onPaddle; //status of ball stating whether it is dormant on paddle or not
    private boolean rumble; //flag for if the screen is currently shaking or not
    private boolean dead = false; //flag for if the player has failed to hit the ball back or not
    private boolean comboEnd = false; //flag for if the current combo has ended or not (ends when ball touches paddle)
    private Random rand = new Random();

    public Ball(int xResolution, int yResolution, Bar pad){
        //The ball's constructor requires the width of the window and the window's location
        //relative to the screen
        radius = 8;
        xvel = rand.nextInt(5)+3;
        yvel = rand.nextInt(5)+3;
        resx = xResolution;
        resy = yResolution;
        horDir = 1;
        vertDir = 1;
        paddle = pad;
        cenx = paddle.getCenX();
        ceny = (int)(paddle.getCenY()-paddle.getRect().getHeight()/2-radius);
        onPaddle = true;
        rumble = false;
    }
    public void draw(Graphics g){
        //Draws the bar on the screen as a rectangle
        g.setColor(new Color(80, 221, 38));
        g.fillOval(cenx-radius,ceny-radius,radius*2,radius*2);
    }
    public ArrayList<Brick> move(ArrayList<Brick> bricks){
        //The ball moves on its own in the direction it is currently on. Some methods are also called to check if the
        //ball has collided with anything and if a change in direction is necessary.
        rumble = false;
        if (onPaddle) {
            cenx = paddle.getCenX();
            ceny = (int)(paddle.getCenY()-paddle.getRect().getHeight()/2-radius);
        }
        else{
            cenx += xvel * horDir;
            ceny += yvel * vertDir;
            cenx = Math.max(cenx, radius);
            cenx = Math.min(cenx, resx - radius);
            ceny = Math.max(ceny, radius);
            windowCollision();
            paddleCollision();
            for (Brick b : bricks) {
                if (brickCollision(b)) {
                    bricks.remove(b);
                    rumble = true;
                    break;
                }
            }
        }
        return bricks;
    }
    public void windowCollision(){
    //Method checks for collision with the window borders and with the paddle.
    //If a collision occurs, the ball bounces off the object it hit.
        if (cenx-radius==0 || cenx+radius==resx){
            horDir *= -1;
            rumble = true;
        }
        else if (ceny-radius==0){
            vertDir = 1;
            rumble = true;
        }
        else if (ceny-radius>=resy){
            dead = true;
        }
    }
    public void paddleCollision(){
    //Method checks for collision between ball and paddle and changes the velocity of the ball depending on
    //where it hit the paddle. The paddle collision looks like:
    //   ____________
    //  /            \
    // /--------------\
        comboEnd = false;
        if (paddle.getRect().contains(cenx,ceny) || paddle.getRect().contains(cenx,ceny-radius)){
            vertDir = -1;
            paddle.setSwingingTrue();
            int diagonalLength = (int)paddle.getRect().getWidth()/3; //length of each diagonal portion
            //of paddle (each diagonal length make up 1/3 of the paddle (it is 1/3 of the width wide)
            if (cenx<(paddle.getRect().getX()+diagonalLength)){ //if the ball hits the left side of the paddle
                horDir = -1;
                int distFromEdge = (int)(cenx-paddle.getRect().getX()); //distance the ball is from the edge of the paddle
                xvel = (diagonalLength-distFromEdge)/3;
                yvel = distFromEdge/3;
            }
            else if (cenx>paddle.getRect().getX()+diagonalLength*2){ //if the ball hits the right side of the paddle
                horDir = 1;
                int distFromEdge = (int)(paddle.getRect().getX()+paddle.getRect().getWidth()-cenx);
                xvel = (diagonalLength-distFromEdge)/3;
                yvel = distFromEdge/3;
            }
            comboEnd = true;
            yvel = Math.max(yvel,1);//minimum velocities are set so that the ball isn't stuck in a horizontal path
        }
    }
    public boolean brickCollision(Brick b){
        //Method checks for collisions between ball and each brick remaining on screen. I needed assistance for this
        //part and I got help from
        //http://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection

        double distX = Math.abs(cenx-(b.getX()+b.getWidth()/2));
        double distY = Math.abs(ceny-(b.getY()+b.getHeight()/2));
        if (distX>(b.getWidth()/2+radius) || distY>(b.getHeight()/2+radius)){
            return false; //if the ball is too far away to even touch the brick
        }
        if (distX<=(b.getWidth()/2+radius) && cenx>=b.getX() && cenx<=(b.getX()+b.getWidth())){
            vertDir*=-1; //if the ball touches the top/bottom side of the brick
            return true;
        }
        if (distY<=(b.getHeight()/2+radius) && ceny>=b.getY() && ceny<=(b.getY()+b.getHeight())){
            horDir*=-1; //if the ball touches the left/right side of the brick
            return true;
        }
        if (Math.hypot(distX-b.getWidth()/2,distY-b.getHeight()/2)<=radius){
            horDir*=-1;
            return true;
        }
        return false;
    }
    public void setOnPaddleFalse(){onPaddle=false;}//Setter method that changes the state of the ball making it move
    public void setOnPaddleTrue(){onPaddle=true;}//Setter method that changes the state of the ball making it idle
//---------------------------------------------------ACCESSOR METHODS-------------------------------------------------
//Methods that return the field specified in the method name
    public int getVertDir(){return vertDir;}
    public int getHorDir(){return horDir;}
    public boolean getOnPaddle(){return onPaddle;}
    public boolean getRumble(){return rumble;}
    public boolean getDead(){return dead;}
    public boolean getComboEnd(){return comboEnd;}
}