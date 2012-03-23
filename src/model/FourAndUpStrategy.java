package model;

/**
 * The FourAndUpStrategy implements the Strategy interface.
 * The FourAndUpStrategy is a computer strategy that takes a Yahtzee turn
 * by rolling the dice and holding die values that are greater than or equal
 * to four. 
 * At the end of all rolls, the strategy places the hand into the category 
 * that gives the maximum score for that hand. 
 * @author Aaron Lamb
 * @version 2.0.1 2/6/2012
 */
public class FourAndUpStrategy implements Strategy {
	private final String name = "Four and Up";
	
	/**
	 * Constructs a new FourAndUpStrategy
	 */
	public FourAndUpStrategy() { }
	
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
			if (roll[i] > 3 && !Dice.getInstance().isHeld(i))
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
			//Dice.getInstance().reset();
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