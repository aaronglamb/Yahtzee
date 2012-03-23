package model;

import java.util.Random;

/**
 * The RandomStrategy implements the Strategy interface.
 * The RandomStrategy is a computer strategy that takes a Yahtzee turn
 * by rolling the dice and randomly choosing which dice to reroll each turn, 
 * then chooses a random category to assign the hand.
 * @author Aaron Lamb
 * @version 2.0.1 2/6/2012
 */
public class RandomStrategy implements Strategy {
	private final String name = "Random";
	private Random r;
	
	/**
	 * Constructs a new RandomStrategy
	 */
	public RandomStrategy() { 
		r = new Random();
	}
	
	/**
	 * Take a turn for the computer player
	 */
	@Override
	public void takeTurn() {
		// Notify the Game object that a computer turn
		// is in progress
		Game.getInstance().notifyPlaying(true);
		
		// Create a new Thread for the turn
		StrategyTurn turn = new StrategyTurn();
		Thread t = new Thread(turn);
		
		// Take the turn
		t.start();
	}
	
	// Holds the dice for the strategy. See the class documentation
	// for specifics
	private void holdDice() {
		int[] roll = Dice.getInstance().getRoll();
		for (int i = 0; i < roll.length; i++) {
			if (r.nextBoolean())
				Dice.getInstance().toggleDie(i);
		}
	}
	
	// Records a score for the strategy. See the class documentation
	// for specific information
	private void takeScore() {
		// Retrieve the roll and the score card
		int[] roll = Dice.getInstance().getRoll();
		ScoreCard sc = Game.getInstance().getPlayers().get(0).getScoreCard();
		
		// Choose a preliminary hand at random
		Hands hand = Hands.values()[r.nextInt(Hands.values().length)];
		
		// Check if the score for the random hand is taken; keep choosing
		// random hands until a score is available
		while (sc.getScore(hand).getScoreTaken())
			hand = Hands.values()[r.nextInt(Hands.values().length)];
		
		//Dice.getInstance().reset();
		Game.getInstance().takeScore(hand, ScoreValidator.getInstance().scoreHand(hand, roll));	
	}

	/**
	 * Returns the name of the strategy
	 */
	@Override
	public String getName() {
		return name;
	}
	
	// Creates a thread to take the computer turn
	private class StrategyTurn implements Runnable {
		@Override
		public void run() {
			int timeout = Game.getInstance().getStrategyPlaySpeed();
			try {
				while (Dice.getInstance().getNumRolls() < 3) {
					Dice.getInstance().roll();
					Thread.sleep(timeout);
					holdDice();
					Thread.sleep(timeout);
				}
				takeScore();
				Game.getInstance().notifyPlaying(false);
				Dice.getInstance().reset();
				if (!Game.getInstance().isOver())
					Game.getInstance().nextTurn();
				else
					Game.getInstance().showWinner();
			} catch (Exception ex) { }			
		}
	}
}