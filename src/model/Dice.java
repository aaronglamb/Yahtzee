package model;

import java.util.Observable;
import java.util.Random;

/**
 * A <code>Dice</code> object consists of a rack of five dice for 
 * use in a Yahtzee game.  Since a Yahtzee game can only have
 * one set of five dice, this class implements the Singleton
 * design pattern.
 * @author Aaron Lamb
 * @version 2.0.1  2/2/2012
 */
public class Dice extends Observable {
	// The single instance of the dice class
	private static Dice instance;
	
	/**
	 * The <code>Dice</code> object implements the Singleton
	 * design pattern, and can therefore have only one instance
	 * of the class.  This method returns the instance.
	 * @return the single instance of the <code>Dice</code> class
	 */
	public static Dice getInstance() {
		if (instance == null)
			instance = new Dice();
		return instance;
	}
	
	// The number of dice in the race (5 for a regular Yahtzee game)
	private static final int numDice = 5;
	
	private DiceStatus status;
	private int[] dice;
	private boolean[] holds;
	private int rolls;
	private Random r;
	
	// Private constructor enforces the Singleton design pattern
	private Dice() {
		status = DiceStatus.READY;
		dice = new int[numDice];
		holds = new boolean[numDice];
		r = new Random();
		roll(); // We won't expect the GUI to be able to display zeros, so we'll
				// get some initial numbers for the dice
	}
	
	/**
	 * Rolls the dice.  If any dice are currently "held" they
	 * will not be rolled.  
	 * @throws IndexOutOfBoundsException if the dice have been 
	 * rolled three times already
	 */
	public void roll() {
		status = DiceStatus.ROLLING;
		if (rolls < 3) {
			for (int i = 0; i < numDice; i++) {
				if (!holds[i])
					dice[i] = r.nextInt(6) + 1;
			}
			rolls++;
			if (rolls > 2)
				status = DiceStatus.OUT_OF_ROLLS;
			setChanged();
			notifyObservers();
			
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	/**
	 * "Toggles" the current die between held or not held
	 * @param die the die to be toggled
	 */
	public void toggleDie(int die) {
		holds[die] = (holds[die]) ? false : true;
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Gets whether the current die is held or not
	 * @param die the die in question (zero indexed)
	 * @return true if the die is held, otherwise false
	 */
	public boolean isHeld(int die) {
		return holds[die];
	}
	
	/**
	 * Gets the current configuration (the current roll) of the dice
	 * @return the current roll
	 */
	public int[] getRoll() {
		return dice.clone();
	}
	
	/**
	 * Getter for the number of rolls in the current turn
	 * @return the number of rolls for the current turn
	 */
	public int getNumRolls() {
		return rolls;
	}
	
	/**
	 * Getter for the current status of the Dice, either
	 * READY, ROLLING, or OUT_OF_ROLLS.
	 * @return the current status for the dice
	 */
	public DiceStatus getStatus() {
		return status;
	}
	
	/**
	 * Resets the dice for the next turn
	 */
	public void reset() {
		holds = new boolean[numDice];
		rolls = 0;
		status = DiceStatus.READY;
		setChanged();
		notifyObservers();
	}
}