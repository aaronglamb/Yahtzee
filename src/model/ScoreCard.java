package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;

/**
 * A Score Card keeps score for a certain player.  The ScoreCard object
 * implements the Iterator design pattern.
 * @author Aaron Lamb
 * @version 1.5.0 2011
 */
public class ScoreCard extends Observable implements Iterable<Score> {
	private ArrayList<Score> card;
	private boolean yahtzee;
	private int yahtzeeBonus;
	private int yahtzeeBonusTurnNumber;
	private int upperBonus;
	private int count;
	
	/**
	 * Constructs a new ScoreCard object
	 */
	public ScoreCard() {
		card = new ArrayList<Score>();
		card.ensureCapacity(13);
		for (Hands hand : Hands.values())
			card.add(new Score(hand));
		card.trimToSize();
	}
	
	/**
	 * Sets the score corresponding to the given hand to the given score
	 * If the score is already taken this will return false and do nothing
	 * @param hand the hand for which to record the score
	 * @param score the score to record for the corresponding hand
	 * @return true if the score is recorded, false if it cannot be
	 */
	public boolean setScore(Hands hand, int score) {
		for (Score s : card) {
			if (s.getHand() == hand && s.getScoreTaken() != true) { 
				s.setScore(score);
				count++;
				if (hand == Hands.YAHTZEE)
					yahtzee = true;
				if (upperBonus != 35) {
					if (getUpperTotal() > 62)
						upperBonus = 35;
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the Score object for the corresponding hand
	 * @param hand the hand for which to retrieve the Score
	 * @return the Score object corresponding to the passed hand
	 */
	public Score getScore(Hands hand) {
		for (Score s : card) {
			if (s.getHand().equals(hand))
				return s;
		}
		return null;
	}
	
	/**
	 * Returns the total of the upper six elements on the ScoreCard:
	 * Ones, Twos, Threes, Fours, Fives, Sixes.  This does NOT add the 
	 * upper bonus if it is applicable.  Call getUpperBonus() for that.
	 * @return the score for the first six categories on a Yahtzee score card
	 */
	public int getUpperTotal() {
		int sum = 0;
		for (int i = 0; i < 6; i++) 
			sum += card.get(i).getScore();
		return sum;
	}
	
	/**
	 * Returns the total of the lower seven elements on the ScoreCard:
	 * Three of a Kind, Four of a Kind, Full House, Small Straight, 
	 * Large Straight, Yahtzee, Chance.  This does NOT add any Yahtzee
	 * bonus that may be applicable.  Call getYahtzeeBonus() for that.
	 * @return the score for the last seven categories on a Yahtzee score card
	 */
	public int getLowerTotal() {
		int sum = 0;
		for (int i = 6; i < 13; i++)
			sum += card.get(i).getScore();
		return sum;
	}
	
	/**
	 * Returns the Upper Bonus value: 35 if the bonus can be taken (if the
	 * upper total is >= 63) or 0 if it cannot be taken.
	 * @return the upper bonus value
	 */
	public int getUpperBonus() {
		return upperBonus;
	}
	
	/**
	 * Returns the Yahtzee Bonus value: +100 points for every additional 
	 * Yahtzee rolled after the first Yahtzee has been scored.
	 * @return the yahtzee bonus value
	 */
	public int getYahtzeeBonus() {
		return yahtzeeBonus;
	}
	
	/**
	 * Returns whether or not a Yahtzee has been scored
	 * @return true if a Yahtzee has been scored, false if not
	 */
	public boolean yahtzee() {
		return yahtzee;
	}
	
	/**
	 * Takes the Yahtzee bonus by incrementing the value of the Yahtzee bonus
	 * by 100 if the count != yahtzeeBonusTurnNumber.  Generally, the yahtzee bonus
	 * cannot be taken more than once each turn: if this turn is the same as when
	 * the last bonus was taken then the bonus will NOT be taken.  Therefore, you
	 * may call the Yahtzee bonus everytime a Yahtzee is rolled and yahtzee() is true.
	 */
	public void takeYahtzeeBonus() {
		if (yahtzee && count != yahtzeeBonusTurnNumber) {
			yahtzeeBonus += 100;
			yahtzeeBonusTurnNumber = count;
		}
	}
	
	/**
	 * Checks whether the ScoreCard is full
	 * @return true if the ScoreCard is full, false otherwise
	 */
	public boolean isFull() {
		return count == 13;
	}
	
	/**
	 * Returns a String representation of the ScoreCard
	 */
	public String toString() {
		String st = "";
		for (Score s : card) {
			st += s + "\n";
		}
		if (upperBonus > 0)
			st += "Upper Bonus: " + upperBonus + "\n";
		if (yahtzeeBonus > 0)
			st += "Yahtzee Bonus: " + yahtzeeBonus + "\n";
		return st;
	}

	@Override
	public Iterator<Score> iterator() {
		return card.iterator();
	}
}