package bagel;

import java.util.ArrayList;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *  Main class to be extended for game projects.
 *  Creates the window, handles {@link Input} and {@link Group} objects, 
 *    and manages the life cycle of the game (initialization and game loop). 
 */
public abstract class Game 
{
    /**
	 * default width of game canvas. may change if desired.
	 */
	public int windowWidth = 800;
	
	/**
	 * default height of game canvas. may change if desired.
	 */
	public int windowHeight = 600;

    public String windowTitle = "";

    // list of groups, each of which contains a list of sprites 
    //   (to stay organized)
    public ArrayList<Group> groupList;
    
    /**
     * timestamp for start of previous game loop; used to calculate {@link #deltaTime}
     */
	public long previousTime;

	/**
	 * amount of time that has passed since the last iteration of the game loop
	 */
	public double deltaTime; 

	/**
	 * amount of time that has passed since game started
	 */
	public double elapsedTime; 

    // FPS analyze data
    public int countFPS;
    public double timerFPS;

    public JFrame window;
    public JPanel canvas;

    public Input input;

    /**
     * Initialize objects used in this game.
     * This method must be overridden by the specific game extending this class.
     */
	public abstract void initialize();

	/**
	 * Update objects used in this game.
	 * Called 60 times per second when possible.
	 * This method must be overridden by the specific game extending this class. 
	 * @param dt amount of time that has passed since the last iteration of the game loop
	 */
	public abstract void update(double dt);

    public Game()
    {

    }

    public void setWindowTitle(String title)
    {
        windowTitle = title;
    }

    public void setWindowSize(int width, int height)
    {
        windowWidth = width;
        windowHeight = height;
    }

    public void run()
    {
        // create the window
        window = new JFrame();
        window.setTitle(windowTitle);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // set the size of the content area
        window.getContentPane().setPreferredSize(
            new Dimension(windowWidth, windowHeight) );
        window.pack();
        window.setVisible(true);

        groupList = new ArrayList<Group>();

        canvas = new JPanel()
        {
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);

                // draw all sprites, in all groups, in the group list.
                for (Group group : groupList)
                {
                    // update all sprites within the group
                    // g.update( 1.0 / 60.0 );
                    for ( Sprite sprite : group.getSpriteList() )
                    {
                        // if destroy function was called,
                        //  remove sprite from the group that contains it.
                        // if (s.destroySignal == true)
                        //     g.removeSprite(s);
                            
                        sprite.draw(g);
                    }
                }
            }
        };

        input = new Input(window);

        window.add(canvas);
        // must call after adding components
        window.revalidate();

        elapsedTime = 0;
        previousTime = System.currentTimeMillis();

        initialize();

        int targetFPS = 60;
        int loopDuration = 1000 / targetFPS;

        while (true) 
        {
            if (System.currentTimeMillis() - previousTime > loopDuration) 
            {
                window.repaint();

                // calculate time passed
                long currentTime = System.currentTimeMillis();
                deltaTime = (currentTime - previousTime) / 1000.0;
                previousTime = currentTime;
                elapsedTime += deltaTime;

                // FPS calculations
                countFPS++;
                timerFPS += deltaTime;

                // FPS check
                if (timerFPS >= 1.0)
                {
                    System.out.println("FPS : " + countFPS);
                    countFPS = 0;
                    timerFPS = 0;
                }

                // process input
                input.update();

                // update game state
                update(deltaTime);
            }
            else
            {
                try 
                { 
                    Thread.sleep(1); 
                }
                catch (Exception ex)
                { 
                    ex.printStackTrace(); 
                }
            }
        }

    }

    // methods for interacting with groups
    
    /**
     * Create a new group, and add it to the list of all groups.
     * @param groupName the name of the group
     * @return the group that was created
     */
    public Group createGroup(String groupName)
    {
        Group g = new Group(groupName);
        groupList.add(g);
        return g;
    }
    
    /**
     * Get the group with the given name from the list of all groups.
     * @param groupName the name of the group
     * @return the group with the given name
     */
    public Group getGroup(String groupName)
    {
        for (Group g : groupList)
        {
            if ( g.getName().equals(groupName) )
                return g;
        }
        
        // if this line is reached, there is no group with that name
        
        // option 1: print message in Java console
        // System.out.println("There is no group with the name: " + groupName);

        // option 2: print message as an error (red font)
        // System.err.println("There is no group with the name: " + groupName);

        // option 3: print error and stop program (throw Exception)
        throw new RuntimeException("There is no group with the name: " + groupName);
    }
    
    /**
     * Add a sprite to the group with the given name.
     * @param sprite sprite to be added
     * @param groupName name of the group to add the sprite to
     */
    public void addSpriteToGroup(Sprite sprite, String groupName)
    {
        getGroup( groupName ).addSprite( sprite );
    }
    
    /**
     * Remove a sprite from the group with the given name
     * @param sprite the sprite to be removed
     * @param groupName name of the group that the sprite is in
     */
    public void removeSpriteFromGroup(Sprite sprite, String groupName)
    {
        getGroup( groupName ).removeSprite( sprite );
    }
    
    /**
     * Get the list of sprites in the group with the given name;
     *   useful in for-loops, when interacting with a specific type of sprite.
     * @param groupName the name of the group
     * @return the list of sprites in that group
     */
    public ArrayList<Sprite> getGroupSpriteList(String groupName)
    {
        return getGroup( groupName ).getSpriteList();
    }
    
    /**
     * Return the number of sprites in the group with the given name
     * @param groupName the name of the group
     * @return the number of sprites in the group's sprite list
     */
    public int getGroupSpriteCount(String groupName)
    {
        return getGroup( groupName ).getSpriteCount();
    }
}
