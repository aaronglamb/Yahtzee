package model;

/**
 * Represent the immutable player identity for a Player object in 
 * a Yahtzee game.  I'm not going to lie, the only reason for this
 * class is to fulfill the requirement to have one immutable class.
 * I had originally planned for the Score to be immutable, but I 
 * found that to be a rather cumbersome idea.  So, here it is, my 
 * one immutable and consequently completely frivolous class.
 * @author Aaron Lamb
 * @version 2.0.1 2/8/2012
 */
public class PlayerIdentity {
	private String name;			// The Player name
	private Strategy strategy;		// The Player strategy
	
	/**
	 * Create a new PlayerIdentity object
	 * @param name the name of the player
	 * @param strategy the strategy for the player
	 */
	public PlayerIdentity(String name, Strategy strategy) {
		this.name = name;
		this.strategy = strategy;
	}
	
	/**
	 * Getter for the name
	 * @return the name of the player
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Getter for the strategy
	 * @return the strategy of the player
	 */
	public Strategy getStrategy() {
		return strategy;
	}
}