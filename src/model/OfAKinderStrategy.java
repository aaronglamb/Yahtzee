package model;

import java.util.HashSet;
import java.util.Set;

/**
 * The OfAKinderStrategy implements the Strategy interface.
 * The OfAKinderStrategy is a computer strategy that takes a Yahtzee turn
 * by rolling the dice and holding die values that occur 2 or more times,
 * rerolling the rest.  For example, if the initial hand is [2, 4, 3, 1, 4], 
 * the strategy keeps the 4s and rerolls the other. If the next roll is
 * [1, 4, 1, 5, 4], the strategy keeps the 1s and 4s and rerolls the 5. 
 * At the end of all rolls, the strategy places the hand into the category 
 * that gives the maximum score for that hand.
 * @author Aaron Lamb
 * @version 2.0.1 2/6/2012
 */
public class OfAKinderStrategy implements Strategy {
	private final String name = "Of a Kinder";
	Set<Integer> toHold;
	
	/**
	 * Constructs a new OfAKinderStrategy
	 */
	public OfAKinderStrategy() { }
	
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
		toHold = new HashSet<Integer>();
		int[] roll = Dice.getInstance().getRoll();
		for (int i = 0; i < roll.length; i++) {
			for (int j = i + 1; j < roll.length; j++) {
				if (roll[i] == roll[j]) {
					toHold.add(i);
					toHold.add(j);
				}
			}
		}
		for (int i : toHold) {
			if (!Dice.getInstance().isHeld(i))
				Dice.getInstance().toggleDie(i);
		}
	}

	// Records a score for the strategy. See the class documentation
	// for specific information
	private void takeScore() {
		int[] roll = Dice.getInstance().getRoll();
		ScoreCard sc = Game.getInstance().getPlayers().get(0).getScoreCard();
		int max = -1;
		Hands maxHand = null;
		for (Hands h : Hands.values()) {
			int score = ScoreValidator.getInstance().scoreHand(h,  roll);
			if (!sc.getScore(h).getScoreTaken() && score > max) {
				max = score;
				maxHand = h;
			}
		}
		if (maxHand != null) {
			Game.getInstance().takeScore(maxHand, ScoreValidator.getInstance().scoreHand(maxHand, roll));
		}
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