package model;

import java.util.Observable;

/**
 * A <code>Player</code> object represents a player in a Yahtzee game.
 * The player has a name, a Strategy object, a ScoreCard object, and 
 * a cumulative score. The <code>Player</code> object extends Observabele.
 * @author Aaron Lamb
 * @version 2.0.1 2/2/2012
 */
public class Player extends Observable {
	private PlayerIdentity identity;
	private ScoreCard sc;
	private int score;
	private Boolean first;
	
	/**
	 * The constructor for a Player object
	 * @param name The player's name
	 * @param strategy The Strategy object for the player
	 */
	public Player(String name, Strategy strategy) {
		if (strategy == null)
			strategy = new HumanStrategy();
		this.identity = new PlayerIdentity(name, strategy);
		this.sc = new ScoreCard();
		this.score = 0;
		this.first = false;
	}
	
	/**
	 * Initiates a turn for the player.  This method must be
	 * called at the start of a turn by the Game object, or 
	 * the Strategy will not take the appropriate turn.
	 */
	public void takeTurn() {
		identity.getStrategy().takeTurn();
	}
	
	/**
	 * Records the score for the given hand.
	 * @param hand The hand to score
	 * @param score The score for the hand
	 * @return true if the score is recorded, false if it cannot be
	 * recorded (if the score is already taken it cannot be taken
	 * a second time).
	 * @throws IllegalArgumentException if the hand passed is null
	 */
	public Boolean takeScore(Hands hand, int score) {
		if (hand == null)
			throw new IllegalArgumentException();
		return sc.setScore(hand, score);
	}
	
	/**
	 * Increments the Player's cumulative score. The final score
	 * for the most recent game should be passed to this method
	 * in order to track cumulative scores.
	 * @param score
	 */
	public void incrementScore(int score) {
		this.score += score;
	}
	
	/**
	 * Resets the scorecard for the player.  Should be called when
	 * starting a new game.
	 */
	public void resetScoreCard() {
		sc = new ScoreCard();
	}

	/**
	 * Resets the cumulative score to 0.  Should be called
	 * when resetting the game.
	 */
	public void resetScore() {
		score = 0;
	}
	
	/**
	 * Marks a player as the first player in a game
	 */
	public void markFirst() {
		first = true;
	}
	
	/**
	 * Returns whether a player is the first playing in the game
	 * (true) or not (false)
	 * @return true if the player is the first player in the game
	 */
	public Boolean isFirst() {
		return first;
	}
	
	/**
	 * Getter for the Player's ScoreCard.
	 * @return a deep clone of the Player's ScoreCard
	 */
	public ScoreCard getScoreCard() {
		return sc;
	}
	
	/**
	 * Getter for the Player's name
	 * @return the Player's name
	 */
	public String getName() {
		return identity.getName();
	}
	
	/**
	 * Getter for the Player's cumulative score
	 * @return the Player's cumulative score
	 */
	public int getScore() { 
		return score;
	}
	
	/**
	 * Getter for the string representation of the 
	 * current Player's Strategy object
	 * @return the strategy name for the player
	 */
	public String getStrategyName() {
		return identity.getStrategy().getName();
	}
	
	/**
	 * Check for Player equality. Based on Player name, cumulative
	 * score, and score card.
	 */
	@Override
	public boolean equals(Object other) {
		if (other != null && other.getClass() == getClass()) {
			Player o = (Player) other;
			ScoreCard osc = o.getScoreCard();
			for (Hands h : Hands.values()) {
				if (osc.getScore(h) != sc.getScore(h))
					return false;
			}
			return o.getName().equals(identity.getName()) && o.getStrategyName().equals(getStrategyName()) 
					&& o.getScore() == score && o.isFirst().equals(isFirst());
		}
		return false;		
	}
}