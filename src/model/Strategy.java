package model;

/**
 * A <code>Strategy</code> object should have a name, which
 * can identify it to a user, and a takeTurn method.
 * The takeTurn method MUST call the notifyPlaying(false) method
 * of the Game object in order to allow the game to continue.
 * 
 * In addition, the Strategy should a) reset the dice by calling
 * the reset() method of the Dice object, and b) call the nextTurn() 
 * or showWinner() (whichever is appropriate) method of the Game
 * object.  In order to support animation, the takeTurn method 
 * should run in a new thread.
 * @author Aaron Lamb
 * @version 2.0.1 2/6/2012
 */
public interface Strategy {
	public void takeTurn();
	public String getName();
}