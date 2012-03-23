package model;

/**
 * The HumanStrategy implements the Strategy interface, and allows
 * human play for a Yahtzee game implementing the Strategy design
 * pattern for computer strategies.  When the HumanStrategy is 
 * called on to take a turn, it does nothing, thereby allowing the
 * UI to wait for user input.
 * @author Aaron Lamb 
 * @version 2.0.1 2/5/2012
 */
public class HumanStrategy implements Strategy {
	private final String name = "Human"; 	// The strategy name
	
	/**
	 * Does nothing.
	 */
	public void takeTurn() { 
		Game.getInstance().notifyPlaying(false);
	}
	
	/**
	 * Return the name of the strategy.
	 */
	public String getName() { return name; }
}