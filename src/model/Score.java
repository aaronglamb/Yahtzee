package model;

/**
 * A <code>Score</code> object represents a single score in a
 * Yahtzee game, including a hand and a numeric score.
 * @author Aaron Lamb
 * @version 2.0.1 2/6/2012
 */
public class Score {
	private Hands hand;
	private int score;
	private boolean scoreTaken;
	
	/**
	 * Construct a new score from the given hand
	 * @param hand the hand for which to construct a Score
	 */
	public Score(Hands hand) {
		this.hand = hand;
		this.scoreTaken = false;
	}
	
	/**
	 * Returns the hand for this score
	 * @return the hand for this score
	 */
	public Hands getHand() {
		return hand;
	}
	
	/**
	 * Returns the score for this hand
	 * @return the score for this hand
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * Returns whether the score has been taken or not
	 * @return true if the score has been taken, false if not
	 */
	public boolean getScoreTaken() {
		return scoreTaken;
	}
	
	/**
	 * Set the score for this Score object and marks the score taken
	 * @param score the score to record
	 */
	public void setScore(int score) {
		this.score = score;
		this.scoreTaken = true;
	}
	
	/**
	 * Returns a string representation of the Score
	 */
	@Override
	public String toString() {
		return hand.toString() + "\t" + Integer.toString(score);
	}
}