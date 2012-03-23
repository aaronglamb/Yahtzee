package model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The ScoreValidator class validates and scores hands for a Yahtzee
 * dice game.  The ScoreValidator implements the Singleton design pattern
 * in order to achieve an abstract final (static) top level class.
 * @author Aaron Lamb
 * @version 2.0.1 2/6/2012
 */
public class ScoreValidator {
	private static ScoreValidator instance;
	
	/**
	 * Returns the instance of the ScoreValidator
	 * @return the instance of the ScoreValidator
	 */
	public static ScoreValidator getInstance() {
		if (instance == null)
			instance = new ScoreValidator();
		return instance;
	}
	
	// Private constructor enforces the Singleton pattern
	private ScoreValidator() { }
	
	/**
	 * Scores the given roll for the given hand
	 * @param hand The hand to score
	 * @param roll The roll of dice to score
	 * @return the score for this hand and roll
	 */
	public int scoreHand(Hands hand, int[] roll) {
		// First, sort the array to get the dice in numerical order
		Arrays.sort(roll);
		
		// Use the corresponding helper method
		if (hand == Hands.THREE_OF_A_KIND || hand == Hands.FOUR_OF_A_KIND 
				|| hand == Hands.YAHTZEE || hand == Hands.FULL_HOUSE)
			return scoreOfAKind(hand, roll);
		else if (hand == Hands.SMALL_STRAIGHT || hand == Hands.LARGE_STRAIGHT) 
			return scoreStraight(hand, roll);
		else
			return scoreX(hand, roll);
	}
	
	// Scores hands that count occurances of one number of dice
	// i.e. ONES, TWOS, THREES, FOURS, FIVES, SIXES, CHANCE
	//
	// In Yahtzee, the number categories are scored the value of 
	// the number of occurances of the category, i.e. if a player
	// rolled three sixes for the sixes category, this would score 
	// as 18. No sixes would score 0.  The CHANCE category is simply
	// the sum of all the dice.
	private int scoreX(Hands hand, int[] roll) {
		// If the hand is CHANCE, return the sum of the dice
		if (hand == Hands.CHANCE) {
			int sum = 0;
			for (int i = 0; i < roll.length; i++)
				sum += roll[i];
			return sum;
		}
		
		// Otherwise, count the occurance of the number in question
		int[] diceOccurances = new int[6];
		for (int i = 0; i < roll.length; i++) 
			diceOccurances[roll[i] - 1]++;
		int n = 0;
		if (hand == Hands.ONES)
			n = 1;
		if (hand == Hands.TWOS)
			n = 2;
		if (hand == Hands.THREES)
			n = 3;
		if (hand == Hands.FOURS) 
			n = 4;
		if (hand == Hands.FIVES)
			n = 5;
		if (hand == Hands.SIXES)
			n = 6;
		
		// Multiply the value of the die by the number of occurances
		// to get the total score and return
		return n * diceOccurances[n - 1];
	}
	
	// Scores hands that are "of a kind" in nature: Three of a Kind,
	// Four of a Kind, Full House, Yahtzee.  
	//
	// In Yahtzee, the Three and Four of a Kind categories score simply
	// the sum of all the dice.  The Full House category scores 25, and
	// a Yahtzee scores 50.  For subsequent Yahtzees after the first is 
	// scored, a 100 point bonus is accrued (this functionality is
	// implemented elsewhere, this method simply scores a hand).
	private int scoreOfAKind(Hands hand, int[] roll) {
		// Get the sum of all dice
		int sum = 0;
		for (int i = 0; i < roll.length; i++)
			sum += roll[i];
		
		// Get the number of occurances of each number
		int[] diceOccurances = new int[6];
		for (int i = 0; i < roll.length; i++) 
			diceOccurances[roll[i] - 1]++;
		
		// Look for numbers of occurances that satisfy the respective categories.
		// If such an occurance is found, return the proper score
		Set<Integer> counts = new HashSet<Integer>();
		for (int i = 0; i < diceOccurances.length; i++) {
			if (diceOccurances[i] > 0)
				counts.add(diceOccurances[i]);
		}
		if (hand == Hands.FULL_HOUSE) {
			if (counts.contains(3) && counts.contains(2))
				return 25;
		} else if (hand == Hands.THREE_OF_A_KIND && counts.contains(3))
			return sum;
		else if (hand == Hands.FOUR_OF_A_KIND && counts.contains(4))
			return sum;
		else if (hand == Hands.YAHTZEE && counts.contains(5))
			return 50;
		
		// Otherwise, return 0
		return 0;
	}
	
	// Scores for hands that are straights: Small Straight or Large Straight
	//
	// In Yahtzee a Small Straight (a straight of 4 dice) is worth 30 points,
	// whereas a Large Straight (a straight of 5 dice) is worth 40 points.
	private int scoreStraight(Hands hand, int[] roll) {
		// First we need to get the unique dice
		Set<Integer> dice = new HashSet<Integer>();
		for (int i : roll)
			dice.add(i);
				
		// If the size of the set is less than 4, we don't have any kind of
		// straight, so we'll immediately return 0.
		if (dice.size() < 4)
			return 0; 
		
		// Now we can assert that there are at least 4 unique dice.  We'll 
		// get these into a sorted array:
		Integer[] sortedDice = dice.toArray(new Integer[0]);
		Arrays.sort(sortedDice);
		
		// Now we can count the gaps in the array: a "gap" is going to be
		// a case where the two adjacent members of the array have a difference
		// of greater than 1
		int gaps = 0;
		for (int i = 1; i < sortedDice.length; i++) {
			if (sortedDice[i] - sortedDice[i - 1] > 1)
				gaps++;
		}
		
		// Now we can score the hands
		// The large straight is easy: 
		if (hand.equals(Hands.LARGE_STRAIGHT) && sortedDice.length == 5 && gaps == 0)
			return 40;
		
		// The small straight is more complex.
		// Remember, we're only here if sortedDice.length >= 4 (we returned 0 if < 4 earlier)
		// Therefore, anything with no gaps is a small straight
		// If there is a gap then we'll only check the case where there are 5 dice 
		// (since a gap with 4 won't be a straight). In that case, we have a small
		// straight if a) there is only one gap and b) that gap is either between
		// dice 1 and 2 or dice 4 and 5:
		if (hand.equals(Hands.SMALL_STRAIGHT)) {
			if (gaps == 0)
				return 30;
			if (sortedDice.length == 5 && gaps == 1) {
				if (sortedDice[1] - sortedDice[0] > 1 || sortedDice[sortedDice.length - 1] - sortedDice[sortedDice.length - 2] > 1)
					return 30;
			}
		}
		
		// If we haven't returned anything, we don't have a straight
		return 0;
	}
}