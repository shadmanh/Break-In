/*Bar.java
 *Shadman Hassan
 *
 * This class is used in the game BreakIn. The paddle in the game is an object of the Bar class. Each bar's fields
 * consist of the bar's size, position and the speed it moves at. It also has a variable that holds what state it is in,
 * whether its standing still or moving in a direction. Another field holds whether the player is currently swinging
 * or not, all of these helping choose the correct sprite to be displayed in the game. A bar has many accessor and
 * setter methods and can move and draw itself.
 */

import java.awt.*;
import java.awt.MouseInfo;

public class Bar{
    private int cenx,ceny,len,height,speed,resx;//variables for the bar's
    //center coordinates, length, height, movement speed and the width of the window
    private Rectangle rect; //rectangle covering bar, used for collision detection
    private String status; //the current status of the player (dormant, moving left, or moving right)
    private boolean swinging; //flag for if player is swinging or not

    public Bar(int xResolution){
        //The bar's constructor requires the width of the window and the window's location
        //relative to the screen
        cenx = 400;
        ceny = 900;
        height = 20;
        len = 100;
        speed = 10;
        resx = xResolution;
        rect = new Rectangle(cenx-len/2,ceny-height/2,len,height);
        status = "standing";
        swinging = false;
    }
    public void draw(Graphics g){
        //Draws the bar on the screen as a rectangle
        g.setColor(new Color(255,121,29,150));
        g.fillRect(cenx-len/2,ceny-height/2,len,height);
    }
    public void move(Point offset){
        //The bar is moved and controlled by the mouse's coordinates. The window's x,y position at the time is input
        //as a parameter which is then used to determine the mouse's coordinates relative to the game
        Point mouse = MouseInfo.getPointerInfo().getLocation(); //mouse x,y relative to screen
        int newx = mouse.x-offset.x;
        int newy = mouse.y-offset.y;
        if (newx<cenx && cenx-len/2>0){
            cenx -= Math.min(speed,cenx-newx);
            cenx = Math.max(cenx,len/2);
            status = "runningLeft";
        }
        else if (cenx<newx && cenx+len/2<resx){
            cenx += Math.min(speed,newx-cenx);
            cenx = Math.min(cenx,resx-len/2);
            status = "runningRight";
        }
        else{
            status = "standing";
        }
        rect.setLocation(cenx-len/2,ceny-height/2);
    }
//-----------------------------------------------------SETTER METHODS-------------------------------------------------
//Methods that are called to set the values of certain fields
    public void setSwingingTrue(){swinging = true;}
    public void setSwingingFalse(){swinging = false;}
//---------------------------------------------------ACCESSOR METHODS-------------------------------------------------
//Methods that return the field specified in the method name
    public Rectangle getRect(){return rect;}
    public int getCenX(){return cenx;}
    public int getCenY(){return ceny;}
    public String getStatus(){return status;}
    public boolean getSwinging(){return swinging;}
}