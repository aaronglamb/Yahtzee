package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;

/**
 * A <code>Game</code> object represents an instance of a Yahtzee game.
 * The game object implements the Singleton design pattern, and as such,
 * can only be instantiated one time. This particular design will not work
 * for a multi-game environment, such as a web environment where more than
 * one instance of the game would need to be instantiated, but it does add
 * a layer of security in a single-instance environment.
 * 
 * A <code>Game</code> object includes a queue of <code>Player</code>s, and a
 * game <code>Status</code>.
 * @author Aaron Lamb
 * @version 2.0.1 2/6/2012
 */
public class Game extends Observable {
	private static Game instance;
	
	/**
	 * Returns the current instance of the Game object
	 * @return the current instance of the Game object
	 */
	public static Game getInstance() {
		if (instance == null)
			instance = new Game();
		return instance;
	}
	
	private Status status;				// The current Status of the Game
	private Queue<Player> players; 		// The LIFO queue of Players in the game
										// (determines turn order)
	
	private int strategyPlaySpeed;		// The play speed (rather, timeout) for the 
										// Stategy game play
	private Boolean strategyPlaying;	// A boolean conditional that lets us know
										// if another strategy is playing its turn
	
	// Private constructor implements Singleton pattern
	private Game() {
		players = new LinkedList<Player>();
		setStatus(Status.UNINITIALIZED);
		strategyPlaying = false;
		strategyPlaySpeed = 500;
	}
	
	/**
	 * Gets the current Status of the Game
	 * @return the current Status of the Game
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * Set the current Status of the Game, notifies observers
	 * @param status the Status to set
	 * @throws IllegalArgumentException if status is null
	 */
	public void setStatus(Status status) {
		if (status == null)
			throw new IllegalArgumentException();
		this.status = status;
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Adds a new Player to the Game, notifies observers
	 * This should not be called when the game status is GAME_IN_PROGRESS, 
	 * or it will not add the Player and return null. Players cannot be 
	 * added in the middle of a game.
	 * @param p the Player to add to the Game
	 * @return true if the Player is added
	 * @throws IllegalArgumentException if the player is null
	 */
	public Boolean addPlayer(Player p) {
		if (p == null) 
			throw new IllegalArgumentException();
		if (status.equals(Status.GAME_IN_PROGRESS))
			return false;
		if (players.size() == 0)
			p.markFirst();
		players.add(p);
		
		setChanged();
		notifyObservers();
		
		return true;		
	}
	
	/**
	 * Returns (by value) a list of Players.
	 * The Players returned are in order of current turn, with the
	 * Player at the front of the list the current Player.
	 * @return a list of the Players in the Game, in order of current turn
	 */
	public List<Player> getPlayers() {
		List<Player> output = new LinkedList<Player>(players);
		return output;
	}
	
	/**
	 * Records the passed score for the passed hand for the current
	 * Player, and notifies the observers.
	 * @param hand the hand to score
	 * @param score the score for the hand
	 * @return true if the score can be taken, false if it cannot be
	 * taken (i.e. if it has already been taken)
	 * @throws IllegalArgumentException if hand is null
	 */
	public Boolean takeScore(Hands hand, int score) {
		if (hand.equals(null))
			throw new IllegalArgumentException();
		if (players.peek().getScoreCard().getScore(hand).getScoreTaken()) 
			return false;
		players.peek().takeScore(hand, score);
		players.add(players.remove());
		
		setChanged();
		notifyObservers();
		
		return true;
	}
	
	/**
	 * Returns whether the Strategy is currently playing a turn
	 * @return true if the Strategy is currently playing
	 */
	public Boolean getStrategyPlaying() {
		return strategyPlaying;
	}
	
	/**
	 * Notify the Game object that the Strategy has either begun
	 * playing their turn (true) or finished (false).  This must
	 * be called by the Strategy (even the HumanStrategy) when the
	 * turn has begun and finished.  (The HumanStrategy may simply
	 * call false initially, signifying that it's done.)
	 * @param flag
	 */
	public void notifyPlaying(Boolean flag) {
		strategyPlaying = flag;
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Sets the strategy play speed for the Strategy object.  This will
	 * set the play speed to the opposite of 100% of the value - i.e., if
	 * you pass 100, the timeout will be 0ms, and the play will be very fast.
	 * @param speed
	 */
	public void setStrategyPlaySpeed(int speed) {
		strategyPlaySpeed = 1000 - (speed * 10);
	}
	
	/**
	 * Returns the play speed (rather the timeout in milliseconds) for the Strategy
	 * @return the timeout (in ms) for the Strategy
	 */
	public int getStrategyPlaySpeed() {
		return strategyPlaySpeed;
	}
	
	/**
	 * Resets the scores for the Players for the current game and 
	 * notifies observers.
	 */
	public void resetScores() {
		while (!players.peek().isFirst()) 
			players.add(players.remove());
		for (Player p : players)
			p.resetScoreCard();
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Resets the current game by removing all the Players and resetting
	 * the Game Status.  Notifies observers.
	 */
	public void resetGame() {
		players = new LinkedList<Player>();
		status = Status.UNINITIALIZED;
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Takes the turn for the next Player. This MUST be called after recording
	 * a score in order to ensure Strategy functionality. Additionally, it must
	 * be called to start a game. 
	 */
	public synchronized void nextTurn() {
		if (players != null) {
			while (strategyPlaying) { }		// This is an ugly way to handle concurrancy,
			players.peek().takeTurn();  	// but I don't really have the tools yet.
											// I'm not pleased with using such a resource hog
											// as this CPU melting while loop, but it's the only
											// only dirty tool I have at the moment. 
											// I hope to come back to this after a bit more study.
		}
	}
	
	/**
	 * Checks if the Game is over by check to see if all the Player's ScoreCards
	 * are full. 
	 * @return true if Game is over, false if not
	 */
	public Boolean isOver() {
		if (status.equals(Status.UNINITIALIZED)) 
			return false;
		for (Player p : players) {
			if (!p.getScoreCard().isFull())
				return false;
		}
		status = Status.INITIALIZED;
		return true;
	}
	
	/**
	 * Records the scores from the current game, incrementing the cumulative score
	 */
	public void recordScores() {
		for (Player p : players) {
			ScoreCard sc = p.getScoreCard();
			int score = sc.getUpperTotal() + sc.getUpperBonus() + sc.getLowerTotal() + sc.getYahtzeeBonus();
			p.incrementScore(score);
		}
	}
	
	/**
	 * Rotates the queue of Players so the winner is at the head of the queue
	 * Notifies observers
	 */
	public void showWinner() {
		if (!isOver())
			return;
		int finalScore;
		ScoreCard sc;
		int maxScore = -1;
		for (Player p : players) {
			sc = p.getScoreCard();
			finalScore = sc.getUpperTotal() + sc.getUpperBonus() + sc.getLowerTotal() + sc.getYahtzeeBonus();
			if (finalScore > maxScore)
				maxScore = finalScore;
		}
		sc = players.peek().getScoreCard();
		finalScore = sc.getUpperTotal() + sc.getUpperBonus() + sc.getLowerTotal() + sc.getYahtzeeBonus();
		while (finalScore != maxScore) {
			players.add(players.remove());
			sc = players.peek().getScoreCard();
			finalScore = sc.getUpperTotal() + sc.getUpperBonus() + sc.getLowerTotal() + sc.getYahtzeeBonus();
		}
		
		setChanged();
		notifyObservers();
	}
}