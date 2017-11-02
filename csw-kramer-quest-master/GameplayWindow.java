import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
/**
 * Displays a window with a demo components of the game inside
 * including example stats, the user's name, and text graphics
 * of an example room.
 * 
 * Interesting Variable Only Dictionary:
 * room             String holding the ASCII text for the current room
 * levelNum         What stage the player is on
 * actionMessage    message displayed at bottom of screen to notify user
 */
public class GameplayWindow extends JFrame
{
    public static final int CANVAS_WIDTH  = 750;//Sets size of window
    public static final int CANVAS_HEIGHT = 450;

    private GameDisplay canvas;
    public String room;

    /**
     * All these are stats that are ideally accessed through other objects
     * like a Player object or something
     */
    private String pName;
    private int levelNum; 
    private int health;
    private int stamina;
    private int attack;
    private int wDurability;
    private int roomHeight;
    private Player play;
    private Room currRoom;
    private Stage space;
    private ArrayList<Item> inventory;

    private String actionMessage = "";

    /**
     * Sets variables based on what's passed in and sets up
     * the window's components and layout. Pretty standard.
     */
    public void displayWindow(Player player, Stage board) {
        levelNum = board.getCurrRoom();//Sets variables
        room = board.toString();
        currRoom = board.getRoom(levelNum);
        pName = player.getName();
        health = player.getHealth();
        stamina = player.getStamina();
        attack = player.getAttack();
        wDurability = player.getDur();
        roomHeight = currRoom.getHeight();
        play = player;
        space = board;
        inventory = play.getInventory();

        canvas = new GameDisplay();    // Construct the drawing canvas
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        // Set the Drawing JPanel as the JFrame's content-pane
        Container cp = getContentPane();
        cp.add(canvas);

        setDefaultCloseOperation(EXIT_ON_CLOSE);   // Handle the CLOSE button
        pack();              // Either pack() the components; or setSize()
        setTitle("\"Game Board\"");  //JFrame sets the title of outer frame
        setVisible(true);    //Displays window

        addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent evt) {
                    switch(evt.getKeyCode()) {
                        case KeyEvent.VK_W:
                        moveAction("up");
                        break;
                        case KeyEvent.VK_S:
                        moveAction("down");
                        break;
                        case KeyEvent.VK_A:
                        moveAction("left");
                        break;
                        case KeyEvent.VK_D:
                        moveAction("right");
                        break;
                        case KeyEvent.VK_I:
                        InventoryWindow iW = new InventoryWindow();
                        iW.displayWindow(play.getInventory(), play, space);
                        dispose();
                        break;
                    }
                }
            });
    }
    
    /**
     * Takes in a string holding the desired move direction and then finds the result
     * of the move and carries out any actions associated with that move.
     * 
     * @param direction desired move direction in string format "up," "down," "left," or "right."
     */
    private void moveAction(String direction) {
        int action = 0;
       
        if (direction.equals("up")) {
            action = play.moveUp();
        } else if (direction.equals("down")) {
            action = play.moveDown();
        } else if (direction.equals("left")) {
            action = play.moveLeft();
        } else if (direction.equals("right")) {
            action = play.moveRight();
        }
        
        do {
            if (action == 1) {
                currRoom = play.getRoom();
                refreshWindow("You moved "+direction+"!", play, space);
            } else if(action == 2) {
                levelNum++;
                space.setCurrRoom(levelNum);
                currRoom = space.getRoom(levelNum);
                currRoom.addPlayer(1,1);
                play.setRoom(currRoom);
                play.setPos(1,1);
                refreshWindow("You moved to a new room!", play, space);
            } else if(action == 3) {
                play.addItem(randomItem());
                refreshWindow("You got " +inventory.get(inventory.size() - 1).getName() +".", play, space);
            } else if(action == 0) {
                action = play.moveUp();
                refreshWindow("You can't move there!", play, space);
            } else {
                
                refreshWindow("You enter combat!", play, space);
                CombatWindow cW = new CombatWindow();
                cW.displayWindow(play, new Gremlin(), space);
                dispose();
            }
        } while (action < 0);
        
        repaint();
    }

    public Item randomItem() {
        int value = (int) (Math.random() * 100 + 1);
        if (value <= 80 && value >= 1) {
            return new Bread();
        } else if (value <= 100 && value >= 81) {
            return new Axe();
        } else {
            return new Bread();    
        }
    }

    /**
     * Simply updates variables with what's passed in and
     * repaints the window. VOILA!
     */
    public void refreshWindow(String useMessage, Player player, Stage board) {
        levelNum = board.getCurrRoom();//Sets variables
        room = board.toString();
        currRoom = board.getRoom(levelNum);
        pName = player.getName();
        health = player.getHealth();
        stamina = player.getStamina();
        attack = player.getAttack();
        wDurability = player.getDur();
        roomHeight = currRoom.getHeight();
        play = player;
        space = board;
        inventory = play.getInventory();

        canvas.repaint();
    }

    /*
     * centerStringX finds the x coordinate needed to center a String in the window.
     */
    private int centerStringX(String text, int frameWidth, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = frameWidth/2 - textWidth/2;
        return textX;
    }

    /*
     * Panel inside frame that holds game graphics
     * and stats for user
     */
    private class GameDisplay extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);     // paint base background
            setBackground(Color.BLACK);  // set background color for this JPanel

            g.setColor(Color.WHITE);//Displays username and score at top of screen
            g.setFont(new Font("Monospaced", Font.PLAIN, 24));
            String playerInfo = pName + " - Level " + levelNum;
            int x = centerStringX(playerInfo, CANVAS_WIDTH, g);
            g.drawString(playerInfo, x, 30);

            g.setFont(new Font("Monospaced", Font.PLAIN, 24)); //Displays gameboard
            int yCor = 60; //Starting Y coordinate
            int inc = room.length()/roomHeight; //Size of each row chunk to be displayed
            x = centerStringX(room.substring(0,inc), CANVAS_WIDTH, g);
            for (int i = 1; i <= roomHeight; i++) {//Splits room into rows and displays them
                g.drawString(room.substring((i-1)*inc,i*inc), x, yCor);
                yCor += 25;
            }

            //Shows desired action message to notify user about
            //whatever just happened
            g.setFont(new Font("Monospaced", Font.PLAIN, 14));
            x = centerStringX(actionMessage, CANVAS_WIDTH, g);
            g.drawString(actionMessage, x, CANVAS_HEIGHT - 60);

            g.setFont(new Font("Monospaced", Font.PLAIN, 20));
            g.setColor(Color.YELLOW); //Displays important stats at bottom of screen
            String line1Vars = "HP:"+health+"  Stamina:"+stamina+"    Attack:"+attack+"   Weapon Strength:"+wDurability;
            x = centerStringX(line1Vars, CANVAS_WIDTH, g);
            g.drawString(line1Vars, x, CANVAS_HEIGHT - 20);
        }
    }
}