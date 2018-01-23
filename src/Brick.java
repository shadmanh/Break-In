/*Brick.java
 *Shadman Hassan
 *
 * This class is used in the game BreakIn. Each brick in the game is an object of the Brick class. Each brick holds
 * basic information that are taken from its constructor's parameters, such as its dimensions, position and colour.
 * Each brick can draw itself as well.
 */





import java.awt.*;
import java.util.*;
import java.awt.geom.*;

public class Brick extends Rectangle{
    private int cenx,ceny; //variables for the bar's center coordinates
    private Random rand;
    private Color col; //colour of brick

    public Brick(int px, int py, int w, int h, Color colour){
        //A brick's constructor creates a brick with ___ width and _____ height
        super(px,py,w,h);
        cenx = x+width/2;
        ceny = y+height/2;
        rand = new Random();
        col = colour;
    }
    public void draw(Graphics g,int xOffset,int yOffset){
        //Draws the bar on the screen as a rectangle, factoring in any offsets that are input
        g.setColor(col);
        g.fillRect(x+xOffset,y+yOffset,width,height);
    }
}