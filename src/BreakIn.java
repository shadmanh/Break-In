/*BreakIn.java
 *Shadman Hassan
 *
 * This is a game about breaking in. Legit. The player controls a character trying to break into a random house on the
 * street with nothing more than a racket and tennis ball, destroying everything in his path by doing so. The aim of the
 * game is to destroy every brick in the level to move on to the next and try to earn as many points as possible. Each
 * brick is only worth one point, but if the player makes the ball hit multiple bricks before the ball hits the paddle
 * again, a multiplier will be factored into the current streak and the player will earn even more points. If the player
 * beats the final level or loses all of their lives, the game ends and their final score is displayed. The game plays
 * a random soundtrack each time the game is started and there are sound effects that play when the ball makes impact
 * with anything. If the player is able to hit the ball back or scores points, the character has a chance to make a
 * random remark/comment. Good luck Breaking In!
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.*;
import java.io.*;
import java.applet.*;
import javax.sound.sampled.AudioSystem;

public class BreakIn extends JFrame implements ActionListener{
    GamePanel game; //Panel where game is played
    javax.swing.Timer myTimer;
    int resx = 1246; //window's horizontal pixel size
    int resy = 980; //window's vertical pixel size
    public BreakIn(){
        //constructs the game BreakIn and adds itself to GamePanel
        super("Break-In");
        setSize(resx,resy);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);

        myTimer = new javax.swing.Timer(10,this);

        game = new GamePanel(this);
        add(game);
    }
    public static void main(String[]args){
        BreakIn frame = new BreakIn();
    }
    public void start(){
        myTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent evt){
        //When an event occurs, the everything on the panel redraws itself and
        //all objects that are supposed to move in the game move
        game.repaint();
        game.move();
        game.playSound();
    }
    //-----------------------ACCESSOR METHODS-----------------------------------
//Returns the field value that is in the method name when the method is called
    public int getResX(){return resx;}
    public int getResY(){return resy;}
}
class GamePanel extends JPanel implements ActionListener,MouseListener,ImageObserver{
    private BreakIn mainFrame; //The object that controls the window, timer and redraws everything every frame
    private Random rand = new Random();
    private Bar paddle; //The paddle the player controls
    private Ball tBall; //The ball the player hits
    private int ballsLeft; //Number of lives the plyer has left
    private Image menuBg,helpScreen,bg1,bg2,playerStanding; //Menu background,help screen image,level 1 & 2's background
    //image (copyright of Michal Jez), player's dormant sprite
    private Image[] runningSprites = new Image[6]; //Array of running sprite images. The sprites are from:
    //http://luiscastanon.com/images/folio_tennis1.jpg
    private Image[] swingSprites = new Image[4]; //Array of swinging sprites
    private double runningIndex,swingingIndex; //index in array running/swinging sprites which signifies which sprite
    //should be drawn at the current frame
    private AudioClip music; //background music
    private AudioClip [] sounds = new AudioClip[5]; //array of sound effects for when the ball collides
    private AudioClip [] voiceSounds = new AudioClip[4]; //array of audio of comments made by the character when a event
    //in the game occurs (E.g. when the player hits a brick and scores points)
    private ArrayList<Brick> bricks = new ArrayList<Brick>(); //ArrayList of bricks currently left in level
    private Color [] brickColList; //array of colours used as a legend to figure out which brick is what colour
    private boolean rumble; //flag for if the screen should have a small shake or not.
    private int totalScore = 0; //total score the player has accumulated
    private int curScore = 0; //current score of the player in the current combo
    private int scorePerBrick = 10; //score accumulated per brick
    private int multiplier = 0; //current score multiplier(for every brick hit before it touches the paddle, the
    //multiplier increases by 1. Once the paddle reflects the ball, the current score X the multiplier is added to the
    //total score.
    private boolean gameOver = false; //flag for if the player has lost all their lives or completed the final level
    private String state = "menu"; //signifies what state the game is currently in (could be menu, game, etc.)
    private int level = 1; //holds what level the player is currently on. To move to the next level, they have to
    //demolish all of the bricks on the current level
    private JButton playButton;

    public GamePanel(BreakIn f){
        //Constructs the game panel by adding the play button, all the game's objects (ball, paddle, etc), adding
        //default values to variables and adding different listeners
        mainFrame = f;
        setLayout(null);
        playButton = new JButton("Play");
        playButton.addActionListener(this);
        playButton.setBounds(500,450,220,100);
        add(playButton);
        paddle = new Bar(mainFrame.getResX()); //constructs paddle, ball, and all bricks
        tBall = new Ball(mainFrame.getResX(),mainFrame.getResY(),paddle);
        ballsLeft = 5;
        runningIndex = 0;
        this.addMouseListener(this);
        rumble = false;
        brickColList = new Color []{new Color(6,255,2),new Color(133,133,133),new Color(194,3,0),new Color(254,124,0),
        new Color(51,231,219),new Color(238,231,13)};
    }

    @Override
    public void addNotify(){
        super.addNotify();
        requestFocus();
        mainFrame.start();
        /*for (int x=100; x+50<mainFrame.getResX()-100;x+=50){
            for (int y=100; y+20<500; y+=20){ //adds bricks to ArrayList of all bricks
                bricks.add(new Brick(x,y,50,20));
            }
        }*/
        buildLevel(level);
        menuBg = new ImageIcon("menu.png").getImage();
        helpScreen = new ImageIcon("helpScreen.png").getImage();
        bg1 = new ImageIcon("bg1.png").getImage();
        bg2 = new ImageIcon("bg2.png").getImage();
        playerStanding = new ImageIcon("sprites/standing.png").getImage();
        for (int i=1; i<(runningSprites.length+1); i++){ //loads running sprites
            Image runSprite = new ImageIcon("sprites/running"+i+".png").getImage();
            runningSprites[i-1] = runSprite;
        }
        for (int i=1; i<(swingSprites.length+1); i++){ //loads swinging sprites
            Image swingSprite = new ImageIcon("sprites/swing"+i+".png").getImage();
            swingSprites[i-1] = swingSprite;
        }
        for (int i=1; i<sounds.length+1; i++){ //loads sound effects
            AudioClip sound = Applet.newAudioClip(getClass().getResource("sounds/hit"+i+".wav"));
            sounds[i-1] = sound;
        }
        for (int i=1; i<voiceSounds.length+1; i++){ //loads voice sounds/comments made by character
            AudioClip voiceSound = Applet.newAudioClip(getClass().getResource("sounds/voice"+i+".wav"));
            voiceSounds[i-1] = voiceSound;
        }
        int songNum = rand.nextInt(3)+1; //chooses random song from folder
        music = Applet.newAudioClip(getClass().getResource("sounds/song"+songNum+".wav"));
        music.loop(); //loops background music
    }
    
    @Override
    public void paintComponent(Graphics g) {
        //draws everything on the screen
        if (gameOver) {
            g.setColor(Color.black);
            g.fillRect(0, 0, mainFrame.getResX(), mainFrame.getResY());
            int fontSize = 195;
            g.setFont(new Font("TimesRoman",Font.BOLD,fontSize));
            g.setColor(Color.red);
            g.drawString("GAME OVER",0,500);
            fontSize = 100;
            g.setFont(new Font("TimesRoman",Font.BOLD,fontSize));
            g.drawString("Final Score: "+totalScore,0,700);
        }
        else {
            if (state.equals("menu")){
                g.drawImage(menuBg,0,0,this);
            }
            else if (state.equals("help")){
                g.drawImage(helpScreen,0,0,this);
            }
            else if (state.equals("game")){
                Image bg = level == 1 ? bg1 : bg2; //picks appropriate background depending on what level it is
                int rumbleXOffset = calcRumble(10); //number the screen's x,y coordinates should offset by resembling a
                int rumbleYOffset = calcRumble(10); //rumble effect
                g.drawImage(bg, rumbleXOffset, rumbleYOffset, this);
                paddle.draw(g);
                for (Brick b : bricks) {
                    b.draw(g, rumbleXOffset, rumbleYOffset);
                }
                tBall.draw(g);
                drawLives(g);
                drawPlayer(g);
                drawScore(g);
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent evt){
        //if player clicks play, the help screen before the game pops up
        state = "help";
        remove(playButton);
    }
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton()==1 && gameOver==false && state.equals("game")){ //while the ball is dormant, clicking the
            tBall.setOnPaddleFalse(); //first mouse button launches the ball and starts the game
        }
        else if (e.getButton()==1 && state.equals("help")){ //clicking on the help screen starts the game
            state = "game";
        }
    }
    @Override
    public boolean imageUpdate(Image image, int i, int i1, int i2, int i3, int i4) {
        return false;
    }
    //Method that needs to be overridden since ImageObserver is implemented
    public void buildLevel(int levelNum){
    //Gets brick layout information from level text file and adds all bricks of the level to the current level's brick ArrayList
    	try{
	        Scanner levelText = new Scanner(new BufferedReader(new FileReader("level"+levelNum+".txt"))); //text file full of levels
	        int numRows = Integer.parseInt(levelText.nextLine()); //number of rows of bricks in level
	        int numColumns = 31; //number of columns of bricks, as well as each brick's width and height
	        int brickWidth = 40;
	        int brickHeight = 20;
	        String row; //row the level creator is currently on
	        for (int y=0; y<numRows; y++){
	            row = levelText.nextLine();
	            for (int x=0; x<numColumns; x++){
	                if (row.charAt(x)!=' '){
                        int brickColIndex = Integer.parseInt(row.substring(x,x+1)); //the integer in the current spot
                        //of the  test file has a colour in the corresponding integer index of the brickColList array
	                    bricks.add(new Brick(x*brickWidth,y*brickHeight,brickWidth,brickHeight,brickColList[brickColIndex]));
	                }
	            }
	        }
	        levelText.close();
    	}
    	catch(IOException ex){
    		System.out.println(ex);
    	}
    }
    public void move(){
        //A method that calls the specific methods in the game required to
        //make everything move
        if (gameOver==false && state.equals("game")){
            Point offset = getLocationOnScreen(); //frame x,y on monitor screen
            paddle.move(offset);
            int firstSize = bricks.size();
            bricks = tBall.move(bricks);
            int secondSize = bricks.size();
            if (tBall.getComboEnd()) { //once the ball touches the paddle, the current combo ends and the total score
                totalScore += curScore * multiplier; //is updated
                curScore = 0;
                multiplier = 0;
                int chance = rand.nextInt(5); //if the player hits the ball back or demolishes a brick, there's a 20%
                if (chance == 0) { //chance that the character will make a random comment
                    playVoiceSound();
                }
            }
            rumble = tBall.getRumble();
            if (secondSize < firstSize) { //if the ball has demolished a brick
                curScore += scorePerBrick;
                multiplier += 1;
                int chance = rand.nextInt(5);
                if (chance == 0) {
                    playVoiceSound();
                }
                if (bricks.size()==0){ //once the level is complete, the new level is loaded
                    totalScore += curScore * multiplier; //is updated
                    curScore = 0;
                    multiplier = 0;
                    if (level == 1) {
                        level = 2;
                        buildLevel(level);
                        tBall.setOnPaddleTrue();
                    }
                    else{ //if the player has beat the game
                        rumble = false;
                        gameOver = true;
                    }
                }
            }
            if (tBall.getDead()) { //if the ball goes off screen, a live is lost and the current combo ends
                totalScore += curScore * multiplier;
                curScore = 0;
                multiplier = 0;
                ballsLeft -= 1;
                tBall = ballsLeft > 0 ? new Ball(mainFrame.getResX(), mainFrame.getResY(), paddle) : null;
            }
            if (ballsLeft == 0) {
                gameOver = true;
            }
        }
    }
    public void playSound(){
    //Plays a random sound signifying the impact of the ball on an object
        if (rumble){
            int index = rand.nextInt(sounds.length);
            sounds[index].play();
        }
    }
    public void playVoiceSound(){
    //plays a random comment made by the character, signifying that they hit a block or striked back the ball
        int index = rand.nextInt(voiceSounds.length);
        voiceSounds[index].play();
    }
    public int calcRumble(int range){
    //calculates how much the items on the screen should shake (offset by) if the ball hits something
        if (rumble) {
            return -range + rand.nextInt(range * 2 + 1);
        }
        return 0;
    }
    public void drawLives(Graphics g){
    //Draws the number of lives the player has left in the form of tennis balls at the bottom left corner
    	for (int i=0; i<ballsLeft; i++){ //draws the number of lives left
            g.setColor(new Color(80, 221, 38));
            g.fillOval(10,mainFrame.getResY()-125-20*i,16,16);
        }
    }
    public void drawPlayer(Graphics g){
    	//Draws the sprite of the player, whether the player is swinging left,right, running left/right or standing still.
        String status = paddle.getStatus();
        if (paddle.getSwinging()){
            Image sprite = swingSprites[(int) swingingIndex];
            if (tBall.getHorDir()==-1) { //player swinging left
                g.drawImage(sprite, paddle.getCenX() - sprite.getWidth(this) / 2,
                        paddle.getCenY() - (int) paddle.getRect().getHeight() / 2, this);
            }
            else{ //player swinging right (same sprite negatively scaled to flip it)
                g.drawImage(sprite, paddle.getCenX() - sprite.getWidth(this) / 2 + sprite.getWidth(this),
                        paddle.getCenY() - (int) paddle.getRect().getHeight() / 2,
                        -sprite.getWidth(this), sprite.getHeight(this), this);
            }
            swingingIndex += 0.25;
            if (swingingIndex >= swingSprites.length) {
            	swingingIndex = 0;
            	paddle.setSwingingFalse();
        	}
        }
        else {
            swingingIndex = 0;
            if (status.equals("standing")) {
                runningIndex = 0;
                g.drawImage(playerStanding, paddle.getCenX() - playerStanding.getWidth(this) / 2,
                        paddle.getCenY()-(int)paddle.getRect().getHeight()/2, this);
            } else if (status.equals("runningRight")) {
                Image sprite = runningSprites[(int) runningIndex];
                g.drawImage(sprite, paddle.getCenX() - sprite.getWidth(this) / 2,
                        paddle.getCenY()-(int)paddle.getRect().getHeight()/2, this);
                runningIndex = runningIndex + 0.25 == runningSprites.length ? 0 : runningIndex + 0.25;
            } else if (status.equals("runningLeft")) {
                Image sprite = runningSprites[(int) runningIndex];
                //reverses the sprite by negatively scaling it and draws it
                g.drawImage(sprite, paddle.getCenX() - sprite.getWidth(this) / 2 + sprite.getWidth(this),
                        paddle.getCenY()-(int)paddle.getRect().getHeight()/2,
                        -sprite.getWidth(this), sprite.getHeight(this), this);
                runningIndex = runningIndex + 0.25 == runningSprites.length ? 0 : runningIndex + 0.25;
            }
        }
    }
    public void drawScore(Graphics g){
        //draws the score on the screen
        int fontSize = 40;
        g.setFont(new Font("TimesRoman",Font.BOLD,fontSize));
        g.setColor(Color.magenta);
        g.drawString("Score: "+totalScore,0,30);
        if (curScore>0) {
            g.drawString("Combo: " + curScore + " X" + multiplier, 900, 30);
        }
    }
}