package model;

/**
 * Enumerates the possible hands in a Yahtzee game.
 * @author Aaron Lamb
 * @version 2.0.1 2/6/2012
 */
public enum Hands {
	ONES, 
	TWOS, 
	THREES, 
	FOURS, 
	FIVES, 
	SIXES, 
	
	THREE_OF_A_KIND, 
	FOUR_OF_A_KIND, 
	FULL_HOUSE, 
	SMALL_STRAIGHT, 
	LARGE_STRAIGHT, 
	YAHTZEE, 
	
	CHANCE;
	
	/**
	 * Returns a string representation of the value
	 * to be output to the user in a UI
	 */
	@Override
	public String toString() {
		String s = super.toString();
		s = s.replace('_', ' ');
		return s.substring(0, 1) + s.substring(1).toLowerCase();
	}
}