package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Dice;
import model.DiceStatus;
import model.FourAndUpStrategy;
import model.Game;
import model.Hands;
import model.HumanStrategy;
import model.OfAKinderStrategy;
import model.Player;
import model.RandomStrategy;
import model.Score;
import model.ScoreCard;
import model.ScoreValidator;
import model.Status;
import model.Strategy;
import model.UpperSectionerStrategy;

/**
 * This class represents the GUI for a Yahtzee game.
 * @author Aaron Lamb
 * @version 2.0.1 1/31/2012
 */
public class YahtzeeGUI {
	private JFrame frame;					// The main frame for the GUI
	private Game game;						// The Game that will be played
	private Dice dice;						// The Dice that will be rolled
	private DicePanel dicePanel;			// The display panel for the dice
	private PlayersPanel playersPanel;		// The display panel for the players
	private ControlPanel controlPanel;		// The display panel for the controls
	private PlayerScoreCardPanel scp;		// The display panel for the score cards
		
	/**
	 * Construct a new Yahtzee GUI window
	 */
	public YahtzeeGUI() {
		game = Game.getInstance();
		dice = Dice.getInstance();
		setupComponents();
		frame.setVisible(true);
	}
	
	// Set up the components for the GUI
	private void setupComponents() {
		frame = new JFrame("CSE331 final project: Yahtzee!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Set size and open center
		int width = 400;
		int height = 480;
		frame.setSize(width, height);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;
		frame.setLocation((screenWidth - width) / 2, (screenHeight - height) / 2);
		
		// Set up the main panel
		frame.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		// Set up the left panel
		// This will include the dice, the players, and the control
		JPanel left = new JPanel();
		left.setLayout(new BorderLayout());
		
		// The dice panel
		dicePanel = new DicePanel();
			
		// The players panel
		playersPanel = new PlayersPanel();
		
		// The control panel
		controlPanel = new ControlPanel();
		
		// Add the dice panel to the left panel
		left.add(dicePanel, BorderLayout.NORTH);
					
		// Add the players panel to the left panel
		left.add(playersPanel, BorderLayout.CENTER);
		
		// Add the control panel to the left panel
		left.add(controlPanel, BorderLayout.SOUTH);
		
		// Add the panels to the frame
		frame.add(left);
	}	
	
	// The DicePanel holds the controls for the Dice in the Yahtzee game.
	// The panel includes the five dice represented as buttons that can be 
	// toggled on (held) and off (not held), the roll button, and the 
	// count of rolls.
	//
	// The DicePanel observes both the Game object and the Dice object. When
	// the Game status is NOT_PLAYING the panel will not be enabled (the dice 
	// cannot be rolled or held, and there will be no count of rolls).  When 
	// the Game status is PLAYING the panel will be enabled.  When the Dice
	// status is READY and ROLLING the dice buttons will be enabled, but when
	// the Dice status is OUT_OF_ROLLS the dice will be enabled.  The roll button
	// will still be enabled, but the user will receive a message that they
	// are "out of rolls".
	@SuppressWarnings("serial")
	private class DicePanel extends JPanel {
		private JButton[] diceRack;			// The five dice buttons
		private ImageIcon[] diceImages;		// The images for use in the buttons
		private JButton rollButton;			// The button to roll the dice
		private JLabel rollCounter;			// The label for the rolls count
		private JPanel count;				// The panel for the rolls count
		
		/**
		 * Construtor for the DicePanel object.
		 */
		public DicePanel() {
			diceRack = new JButton[5];
			diceImages = new ImageIcon[6];
			rollButton = new JButton();
			count = new JPanel();
			
			setupControls();
		}
		
		// Initializes and places the controls on the panel.
		private void setupControls() {
			// Set up the dice images
			diceImages[0] = new ImageIcon(YahtzeeGUI.class.getResource("/1.jpg"));
			diceImages[1] = new ImageIcon(YahtzeeGUI.class.getResource("/2.jpg"));
			diceImages[2] = new ImageIcon(YahtzeeGUI.class.getResource("/3.jpg"));
			diceImages[3] = new ImageIcon(YahtzeeGUI.class.getResource("/4.jpg"));
			diceImages[4] = new ImageIcon(YahtzeeGUI.class.getResource("/5.jpg"));
			diceImages[5] = new ImageIcon(YahtzeeGUI.class.getResource("/6.jpg"));
			
			// Set up the dice as buttons each button the the rack 
			// of dice, set the image for the die, and add the listener
			// for the toggle action
			for (int i = 0; i < diceRack.length; i++) {
				Border b = new MatteBorder(0, 0, 4, 0, Color.RED);
				diceRack[i] = new JButton();
				diceRack[i].setIcon(diceImages[i]);
				diceRack[i].setName(Integer.toString(i));
				diceRack[i].setBorder(b);
				this.add(diceRack[i]);
				diceRack[i].addActionListener(toggleListener);
			}
			
			// Set up the roll counter
			count.setLayout(new BorderLayout());
			rollCounter = new JLabel(" ");
			rollCounter.setFont(new Font("Sans Serif", Font.BOLD, 22));
			rollCounter.setForeground(Color.GRAY);
			this.add(count);
			
			// Set up the roll button
			rollButton.setText("Roll Dice");
			rollButton.addActionListener(rollListener);
			
			// Set up the observer object for the Dice object
			// This observer will update the UI whenever the
			// Dice object notifies it of a change
			Observer diceObserver = new DiceObserver();
			dice.addObserver(diceObserver);
			
			// Set up the observer object for the Game object
			// This observer will update the UI whenever the
			// Game object notifies it of a change
			Observer gameObserver = new GameObserver();
			game.addObserver(gameObserver);
			
			// Disable the panel; it will be enabled when it detects
			// the game is ready for it.
			enablePanel(false);
		}
		
		/**
		 * Enables the Dice Panel and prepares it for a new game.
		 * This should be called when a new game is started (true) 
		 * and when a game is over (false)
		 * @param flag The boolean flag whether to enable the panel or not
		 */
		public void enablePanel(Boolean flag) {
			rollButton.setEnabled(flag);
			enableDice(false);
			count.add(rollCounter, BorderLayout.NORTH);
			count.add(rollButton, BorderLayout.SOUTH);
		}
		
		/**
		 * Enables the Dice buttons for holding the dice.  This should
		 * be called when before a new turn (true) and when a player is
		 * out of rolls (false)
		 * @param flag The boolean flag whether to enable the dice or not
		 */
		public void enableDice(Boolean flag) {
			for (JButton die : diceRack) {
				die.setEnabled(flag);
				die.setBorderPainted(false);
			}
		}
		
		/**
		 * Refreshes the dice panel, updating the die face as per 
		 * the current roll, setting the border for the dice that 
		 * are held, and updating the roll counter accordingly.
		 */
		public void refresh() {
			int[] roll = dice.getRoll();
			for (JButton die : diceRack) {
				die.removeActionListener(toggleListener);
				if (!game.getStrategyPlaying())
					die.addActionListener(toggleListener);
				die.setIcon(diceImages[roll[Integer.parseInt(die.getName())] - 1]);
				if (!dice.getStatus().equals(DiceStatus.OUT_OF_ROLLS)) {
					if (dice.isHeld(Integer.parseInt(die.getName()))) 
						die.setBorderPainted(true);
					else
						die.setBorderPainted(false);
				}
			}
			rollCounter.setText(" ");
			if (!dice.getStatus().equals(DiceStatus.READY)) 
				rollCounter.setText("Roll #" + dice.getNumRolls());
		}
		
		// This observer watches the Dice object.  When the Dice notify 
		// of a change, the observer object will call the update method.
		private class DiceObserver implements Observer {
			@Override
			public void update(Observable arg0, Object arg1) {
				DiceStatus status = dice.getStatus();
				if (status.equals(DiceStatus.ROLLING) || status.equals(DiceStatus.OUT_OF_ROLLS)) {
					if (game.getPlayers().get(0).getScoreCard().yahtzee()) {
						if (ScoreValidator.getInstance().scoreHand(Hands.YAHTZEE, dice.getRoll()) > 0)
							game.getPlayers().get(0).getScoreCard().takeYahtzeeBonus();
					}						
				}
				if (status.equals(DiceStatus.READY)) 
					enableDice(false);
				else
					enableDice(true);
				
				refresh();
			}
		}
		
		// This observer watches the Game object.  When the Game notifies
		// of a change, the observer object will call the update method.
		private class GameObserver implements Observer {
			@Override
			public void update(Observable arg0, Object arg1) {
				// If the game is in progress and a strategy is not playing their turn, enable
				if (game.getStatus().equals(Status.GAME_IN_PROGRESS) && !game.getStrategyPlaying())
					enablePanel(true);
				else
					enablePanel(false);
			}
		}
		
		// The action listener for the roll button: rolls the dice
		// when called, dispaying a message if it gets an error (i.e.
		// the dice cannot be rolled any longer.
		private ActionListener rollListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					dice.roll();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "You have already rolled three times!\nPlease score your hand.");
				}
			}
		};
		
		// The action listener for the dice buttons: if a die is clicked, 
		// this listener will call the Dice object's toggleDie method
		// on the particular die.
		private ActionListener toggleListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton source = (JButton) e.getSource();
				try {
					dice.toggleDie(Integer.parseInt(source.getName()));
				} catch (Exception ex) {
					System.out.println(ex);
				}
			}
		};
	}
	
	
	// The PlayersPanel holds the controls for the Players in the Yahtzee game. 
	// The players are listed in the panel with the player who's turn it is
	// currently listed first.  The players list of players includes the player's
	// name, their strategy type, their score thus far for the current game, and
	// their cumulative score this session.
	//
	// The PlayersPanel observes both the Game object and the Dice object.  When 
	// the GameObserver detects that a player has been added or removed, or a score 
	// has been recorded, or the action moves to a new player it will update the
	// players panel. Similarly, when the DiceObserver detects that the Dice have been
	// rolled, it will update the PlayerScoreCardPanel.
	@SuppressWarnings("serial")
	private class PlayersPanel extends JPanel {
		JPanel headerRow;				// The header row for the panel
		JPanel mainPlayerContent;		// The area where the player's list will display
		JButton addPlayer;				// The add player button
		
		/**
		 * Constructor for the PlayersPanel object
		 */
		public PlayersPanel() {
			headerRow = new JPanel();
			mainPlayerContent = new JPanel();
			addPlayer = new JButton();
			setupControls();
		}
		
		// Initializes and places the controls on the panel
		private void setupControls() {
			// Set up the main border and layout for the panel
			Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
			this.setBorder(BorderFactory.createTitledBorder(border, "Players (click for scorecard)"));
			//this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.setLayout(new BorderLayout());
			
			// Set up the header row and add it to the panel
			Border b = new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY);
			headerRow.setBorder(b);
			headerRow.setLayout(new GridLayout(1, 4, 5, 15));
			headerRow.add(new JLabel("Player"));
			headerRow.add(new JLabel("Type"));
			headerRow.add(new JLabel("Current Score"));
			headerRow.add(new JLabel("Total Score"));
			this.add(headerRow, BorderLayout.NORTH);
			
			// Set up the content panel and add it to the main panel
			mainPlayerContent.setLayout(new BoxLayout(mainPlayerContent, BoxLayout.Y_AXIS));
			this.add(mainPlayerContent, BorderLayout.CENTER);

			// Add any current Players to the PlayersPanel
			refreshPlayers();
			
			// Set up and add the Add Player button
			addPlayer.setText("Add Player");
			addPlayer.addActionListener(addPlayerListener);
			this.add(addPlayer, BorderLayout.SOUTH);
			
			// Set up the GameObserver object to observe the Game.
			// The Game object will notify this panel of changes;
			// when it does, the update method will be called
			GameObserver gameObserver = new GameObserver();
			game.addObserver(gameObserver);
			
			// Set up the DiceObserver object to observe the Dice.
			// The Dice object will notify this panel of changes;
			// when it does, the update method will be called
			DiceObserver diceObserver = new DiceObserver();
			dice.addObserver(diceObserver);
		}
		
		// Enables or disables the panel (specifically the addPlayer
		// button) depeding on the passed Boolean flag
		private void enablePanel(Boolean flag) {
			addPlayer.setEnabled(flag);
		}
		
		// Refreshes the current list of players on the mainPlayersContent panel
		private void refreshPlayers() {
			
			// Get the current list of Player objects from the Game object
			// The list of Players will be in order of turn, with the first
			// player on the list being the current player.
			List<Player> players = game.getPlayers();
			
			// Clear the panel
			mainPlayerContent.removeAll();
						
			// If there are players, display them. This is a fencepost: the first
			// player on the list is the current player, and will have a highlighted
			// background
			if (players.size() > 0) {
				
				// Fencepost: add the current player
				PlayerInfoPanel firstPlayer = new PlayerInfoPanel(players.get(0));
				if (game.isOver())
					firstPlayer.setBackground(Color.PINK);
				else
					firstPlayer.setBackground(Color.YELLOW);
				mainPlayerContent.add(firstPlayer);
				
				// Add the remaining players
				for (int i = 1; i < players.size(); i++) 
					mainPlayerContent.add(new PlayerInfoPanel(players.get(i)));
			}
			
			// Swap the PlayerScoreCardPanel for the current Player's
			// PlayerScoreCardPanel
			if (game.getStatus().equals(Status.GAME_IN_PROGRESS) || game.isOver()) {
				enablePanel(false);
				swapScoreCard(players.get(0));
			} else {
				enablePanel(true);
			}
			
			// Revalidate the panel
			mainPlayerContent.revalidate();
		}
		
		// This is the ActionListener for the addPlayer button.  When the button is
		// clicked, the listener will bring up a dialogue box prompting for the player's
		// name, followed by dialog box prompting for a strategy type chosen from a drop-
		// down list.  The listener then creates the appropriate Player object and adds
		// it to the Game instance.
		private ActionListener addPlayerListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// Prompt the user for the name
					String name = JOptionPane.showInputDialog(null, "Player name:");
					
					// Prompt the user for the player strategy type
					String strategy = "";
					if (name != null && name.length() != 0) {
						strategy = (String) JOptionPane.showInputDialog(
							null,						 	// The parent
					        "Player strategy:",				// The prompt text
					        "",								// The title
					        JOptionPane.QUESTION_MESSAGE, 	// The JOptionPane type
					        null, 							// The icon		// The values (below)
					        new String[] { "Human", "Random", "Of-a-Kinder", "Upper-Half", "Four-and-Up" },
					        null							// The selected value at load
					    );
					
						// Create the appropriate strategy (if strategy == null the user clicked
						// the cancel button, in this case simply return) and the create the appropriate
						// Player object and add it to the Game instance
						if (strategy == null) { return; } else if (strategy.length() != 0) {
							Strategy s = new HumanStrategy();
							if (strategy.trim().equals("Random")) { s = new RandomStrategy(); }
							else if (strategy.trim().equals("Of-a-Kinder")) { s = new OfAKinderStrategy(); }
							else if (strategy.trim().equals("Upper-Half")) { s = new UpperSectionerStrategy(); }
							else if (strategy.trim().equals("Four-and-Up")) { s = new FourAndUpStrategy(); }
							game.addPlayer(new Player(name.trim(), s));
						}
					} else { }
				} catch (Exception ex) { 	// Catch any problems
					JOptionPane.showMessageDialog(null, "Oops. Something went wrong - please try again.");
				}				
			}
		};	
		
		// This is the GameObserver object, which observes the Game object.
		// If the game object changes, the GameObserver.update method will
		// be called, and the refreshPlayers() method of the panel will be
		// called.
		private class GameObserver implements Observer {
			@Override 
			public void update(Observable arg0, Object arg1) {
				refreshPlayers();
			}
		}
		
		// This is the DiceObserver object, which observes Dice object.
		// If the Dice object changes (i.e. if the dice are rolles), the
		// update method of the DiceObserver will be called.
		private class DiceObserver implements Observer {
			@Override
			public void update(Observable arg0, Object arg1) {
				refreshPlayers();
			}
		}
	}
	
	// The PlayerInfoPanel holds an individual Player's information
	// in the PlayersPanel.  Each PlayerInfoPanel contains the Player's
	// name, strategy type, current running score for the current game,
	// and cumulative score for the session.
	@SuppressWarnings("serial")
	private class PlayerInfoPanel extends JPanel implements MouseListener {
		private Player p;				// The Player object this panel represents
		private JLabel nameField;		// The label for the name
		private JLabel currentScore;	// The label for the current score
		private JLabel cumScore;		// The label for the cumulative score
		private JLabel strategy;		// The label for the strategy type
		
		/**
		 * The constructor for a PlayerInfoPanel
		 * @param p the Player object the panel represents
		 */
		public PlayerInfoPanel(Player p) {
			this.p = p;

			// Retrieve the Player's score card, and tally their current score
			ScoreCard sc = p.getScoreCard();
			int score = sc.getUpperTotal() + sc.getUpperBonus() + sc.getLowerTotal() + sc.getYahtzeeBonus();
			
			// Update all fields with the current information 
			// from the Player object
			nameField = new JLabel(p.getName());
			currentScore = new JLabel(Integer.toString(score));
			cumScore = new JLabel(Integer.toString(p.getScore()));
			strategy = new JLabel(p.getStrategyName());
			
			// Call the setupControls method to place
			// the objects on the panel
			setupControls();
		}
		
		// Sets up the layout for the panel and adds the objects
		private void setupControls() {
			this.setLayout(new GridLayout(1, 4, 5, 15));
			
			this.add(nameField);
			this.add(strategy);
			this.add(currentScore);
			this.add(cumScore);
			
			addMouseListener(this);
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (!game.getStatus().equals(Status.UNINITIALIZED)) {
				swapScoreCard(p);
				scp.revalidate();
			}
		}

		// Not used
		@Override
		public void mouseEntered(MouseEvent arg0) { }

		// Not used
		@Override
		public void mouseExited(MouseEvent arg0) { }

		// Not used
		@Override
		public void mousePressed(MouseEvent arg0) { }

		// Not used
		@Override
		public void mouseReleased(MouseEvent arg0) { }		
	}
	
	// A PlayerScoreCardPanel holds an individual Player's ScoreCard for the current
	// game.  Each individual score is displayed as an inner ScorePanel.  Derived fields
	// (totals and bonuses) are displayed as inner TotalBonusPanels.  
	// The PlayerScoreCardPanel does not observe any objects, but is rather "swapped"
	// when an appropriate change is detected from other object Observers via the swapScoreCard()
	// method.
	@SuppressWarnings({ "serial" })
	private class PlayerScoreCardPanel extends JPanel {
		private Player p;		// The Player for which to display the ScoreCard
		private ScoreCard sc;	// The ScoreCard to display
		
		/**
		 * The constructor for the PlayerScoreCardPanel
		 * @param p The Player for which to display the ScoreCard
		 */
		public PlayerScoreCardPanel(Player p) {
			this.p = p;
			this.sc = p.getScoreCard();
			setupControls();
		}
		
		// Initializes and places the controls for the PlayerScoreCardPanel
		private void setupControls() {
			// Set up the main border and layout for the ScoreCard
			Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
			this.setBorder(BorderFactory.createTitledBorder(border, p.getName() + "'s scorecard"));
			this.setLayout(new GridLayout(10, 2, 10, 2));
			
			// Add all the individual score controls to the panel
			Iterator<Score> scItr = sc.iterator();
			List<ScorePanel> scores = new ArrayList<ScorePanel>();
			while (scItr.hasNext()) 
				scores.add(new ScorePanel(scItr.next()));
			for (int i = 0; i < scores.size() / 2; i++) {
				this.add(scores.get(i));
				this.add(scores.get(i + 6));
			}
			this.add(new TotalBonusPanel("", ""));
			this.add(scores.get(12));
			
			// Calculate the derived fields: the totals and the bonuses
			int up = sc.getUpperBonus();
			int yb = sc.getYahtzeeBonus();
			int ut = sc.getUpperTotal() + up;
			int lt = sc.getLowerTotal() + yb;
			String uBonus = (up > 0) ? Integer.toString(up) : "";
			String yBonus = (yb > 0) ? Integer.toString(yb) : "";
			String uTotal = (ut > 0) ? Integer.toString(ut) : "";
			String lTotal = (lt > 0) ? Integer.toString(lt) : "";
			String total = (ut + lt > 0) ? Integer.toString(ut + lt) : "";
			
			// Add the derived fields to the panel
			this.add(new TotalBonusPanel("Upper bonus", uBonus));
			this.add(new TotalBonusPanel("Yahtzee bonus", yBonus));
			this.add(new TotalBonusPanel("Upper Total", uTotal));
			this.add(new TotalBonusPanel("Lower Total", lTotal));
			this.add(new TotalBonusPanel("TOTAL SCORE: ", total));			
		}
		
		// The inner ScorePanel class creates a panel for a Score.
		// The Score provides funcionality: it displays the current
		// score the player has earned for a hand; if the dice have
		// been rolled, it displays the potential score for the current
		// roll and the given hand (if any); and if clicked it records
		// the score for the turn
		private class ScorePanel extends JPanel implements MouseListener {
			private Score s;			// The Score object to display
			private JLabel scoreName;	// The label to hold the name of the hand
			private JLabel score;		// The label to hold the score for the hand
			
			/**
			 * The constructor for the ScorePanel object
			 * @param s The Score object to display
			 */
			public ScorePanel(Score s) {
				this.s = s;
				scoreName = new JLabel();
				score = new JLabel();
				setupControls();
				addMouseListener(this);
			}
			
			// Initialized the controls and add them to the panel
			private void setupControls() {
				
				// Set the layout for the panel
				this.setLayout(new GridLayout(1, 2));
				
				// Set the text of the scoreName field to the string value of the hand
				scoreName.setText(s.getHand().toString());
				
				// Set the fields to opaque, so we can see their background color
				// and set the alignment
				scoreName.setOpaque(true);
				score.setOpaque(true);
				score.setHorizontalAlignment(JLabel.RIGHT);
				
				// If the score for this hand has not been recorded yet
				if (!s.getScoreTaken()) {	
					// If the dice have been rolled
					if (dice.getNumRolls() > 0 && game.getPlayers().get(0).equals(p)) {
						// Display the score for the hand for this current roll, if any
						int possible = ScoreValidator.getInstance().scoreHand(s.getHand(), dice.getRoll());		
						if (possible > 0) {
							scoreName.setForeground(Color.DARK_GRAY);
							score.setForeground(Color.DARK_GRAY);
							scoreName.setBackground(Color.LIGHT_GRAY);
							score.setBackground(Color.LIGHT_GRAY);
							score.setText(Integer.toString(possible));
						}
					}
				// Otherwise, the score has been taken, so display that score
				} else {
					score.setText(Integer.toString(s.getScore()));
				}
				
				// Add the labels to the panel
				this.add(scoreName);
				this.add(score);
			}
			
			// These are the overridden method to implement the MouseListener interface
			// mouseClicked handles any mouse clicks
			// A mouse click on a score will record that score and advance the game
			@Override
			public void mouseClicked(MouseEvent e) {
				// If this is the current players scorecard and a strategy is not currently playing
				if (game.getPlayers().get(0).equals(p) && !game.getStrategyPlaying()) { 
					// If the dice have not yet been rolled, alert the user
					if (dice.getStatus().equals(DiceStatus.READY)) { 
						JOptionPane.showMessageDialog(
								null, 
								"You must roll the dice first.", 
								"Please Roll", 
								JOptionPane.WARNING_MESSAGE
						);
					// If a score for that hand has already been taken, alert the user
					} else if (s.getScoreTaken()) {
						JOptionPane.showMessageDialog(
								null, 
								"You've already recorded a score for that hand. Please select another.", 
								"Score Recorded", JOptionPane.WARNING_MESSAGE
						);
					// Otherwise, check if the score is 0, if it is, request permission from the 
					// user to record a score for this category as 0. If permission is granted, record
					// the score, which will advance the game, then reset the dice.  If permission is
					// not granted, return.
					} else {
						int[] roll = dice.getRoll();
						int possible = ScoreValidator.getInstance().scoreHand(s.getHand(), roll);
						if (possible == 0) {
							int recordZero = JOptionPane.showConfirmDialog(
									null, 
									"Are you sure you want to record a 0 for that hand?", 
									"Record a zero?", 
									JOptionPane.YES_NO_OPTION
							);
							if (recordZero != JOptionPane.YES_OPTION) 
								return;
						}		
						dice.reset();
						game.takeScore(s.getHand(), ScoreValidator.getInstance().scoreHand(s.getHand(), roll));							
						if (!game.isOver()) {
							game.nextTurn();
						} else 
							game.showWinner();
					}
				}
			}

			// mouseEntered handles any mouse over events
			// When a player mouses over a score the score is highlighted.
			@Override
			public void mouseEntered(MouseEvent e) {
				if (!s.getScoreTaken() && !dice.getStatus().equals(DiceStatus.READY) && game.getPlayers().get(0).equals(p)) {
					scoreName.setBackground(Color.YELLOW);
					score.setBackground(Color.YELLOW);
					scoreName.setForeground(Color.BLACK);
					score.setForeground(Color.BLACK);
				}
			}

			// mouseExited handles any mouse out events
			// When a player moves the mouse out of the label, the score is 
			// returned to it's pre-highlighted state
			@Override
			public void mouseExited(MouseEvent e) {
				if (!s.getScoreTaken() && !dice.getStatus().equals(DiceStatus.READY) && game.getPlayers().get(0).equals(p)) {
					int possible = ScoreValidator.getInstance().scoreHand(s.getHand(), dice.getRoll());
					Color c = (!s.getScoreTaken() && possible > 0) ? Color.LIGHT_GRAY : frame.getBackground();
					scoreName.setBackground(c);
					score.setBackground(c);
					scoreName.setForeground(Color.DARK_GRAY);
					score.setForeground(Color.DARK_GRAY);
				}			
			}

			// Unused
			@Override
			public void mousePressed(MouseEvent e) { }

			// Unused
			@Override
			public void mouseReleased(MouseEvent e) { }

		}
		
		// The inner TotalBonusPanel handles the derived scores that are included
		// on a Yahtzee score card. This panel is identical to a ScorePanel, but 
		// without any interactivity: the panel is static.
		private class TotalBonusPanel extends JPanel {
			private static final long serialVersionUID = 1L;
			private JLabel a;
			private JLabel b;
			
			public TotalBonusPanel(String a, String b) {
				setupControls(a, b);
			}
			
			private void setupControls(String a, String b) {
				this.setLayout(new GridLayout(1, 2));
				this.a = new JLabel(a);
				this.b = new JLabel(b);
				this.b.setHorizontalAlignment(JLabel.RIGHT);
				this.add(this.a);
				this.add(this.b);
			}
		}
	}
	
	// The ControlPanel holds the controls for the Yahtzee game, including the
	// start (new) game button, the reset game button, and the exit button, as
	// well as all necessary handlers.  The ControlPanel observes the Game object,
	// adjusting functionality as necessary based on the Game Status.
	@SuppressWarnings("serial")
	private class ControlPanel extends JPanel {
		private JPanel speedPanel;			// The Speed control panel
		private JLabel speedLabel;			// The Label for the speed control
		private JSlider strategySpeed;		// The Animation speed for the Strategy gameplay
		private JPanel buttonsContainer;	// The container panel
		private JButton newGame;			// The New Game button
		private JButton resetGame;			// The Reset Game button
		private JButton exit;				// The Exit button
		
		/**
		 * The constructor for the ControlPanel
		 */
		public ControlPanel() {
			// Set up the Game Speed components
			speedPanel = new JPanel();
			speedLabel = new JLabel();
			strategySpeed = new JSlider();
			
			// Initialize the buttons
			buttonsContainer = new JPanel();
			newGame = new JButton("Start Game");
			resetGame = new JButton("Reset Game");
			exit = new JButton("Exit");
			
			setupControls();
		}
		
		// Set up the controls for the ControlPanel
		private void setupControls() {
			this.setLayout(new BorderLayout());
			
			strategySpeed.addChangeListener(gameSpeedListener);
			speedPanel.setLayout(new FlowLayout());
			speedLabel.setText("Game Speed");
			speedPanel.add(speedLabel);
			speedPanel.add(strategySpeed);
			
			buttonsContainer.setLayout(new GridLayout(1, 3));
			newGame.addActionListener(newGameListener);
			resetGame.addActionListener(resetGameListener);
			exit.addActionListener(exitListener);
			buttonsContainer.add(newGame);
			buttonsContainer.add(resetGame);
			buttonsContainer.add(exit);
			
			this.add(speedPanel, BorderLayout.NORTH);
			this.add(buttonsContainer, BorderLayout.CENTER);
			
			// Add the GameObserver to observer the Game object
			GameObserver gameObserver = new GameObserver();
			game.addObserver(gameObserver);
		}
		
		private ChangeListener gameSpeedListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) 
				    game.setStrategyPlaySpeed((int) source.getValue());
			}
		};
		
		// Handles the click action of the New Game button
		private ActionListener newGameListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// If there are fewer than two players, alert the user and return.
				if (game.getPlayers().size() < 2) {
					JOptionPane.showMessageDialog(null, "You don't have enough players to play a game!\nPlease add players.");
					return;
				}
				try {
					// If a game is in progress, ask the user if they want to abandon
					// the current game
					if (game.getStatus().equals(Status.GAME_IN_PROGRESS)) {
						int confirm = JOptionPane.showConfirmDialog(
							null, 
							"Abandon the current game?", 
							"New Game", 
							JOptionPane.YES_NO_OPTION, 
							JOptionPane.WARNING_MESSAGE
						);
						
						// If they don't want to abandon, return.
						if (confirm == JOptionPane.NO_OPTION)
							return;
					} else if (game.getStatus().equals(Status.INITIALIZED)) // If a game has been played, record scores
						game.recordScores();
					
					// Initialize a new game
					newGame();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Oops. Something went wrong - please try again.");
				}
			}
		};
		
		// Handles the click action of the reset game button
		private ActionListener resetGameListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// Confirm with the user that they want to reset the game
					int confirm = JOptionPane.showConfirmDialog(
						null,
						"Resetting the Yahtzee game will delete all " +
						"players and remove all scores.\nAre you sure?",
						"Reset Game",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE
					);
					
					// If they don't, return
					if (confirm == JOptionPane.NO_OPTION)
						return;
					
					// Otherwise, reset the game.
					resetGame();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Oops. Something went wrong - please try again.");
				}
			}
		};
		
		// Handles the click action of the exit button
		private ActionListener exitListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};

		// The Game observer Observes the Game object
		private class GameObserver implements Observer {
			@Override
			public void update(Observable arg0, Object arg1) {
				Status status = game.getStatus(); // Get the current status
				
				if (status.equals(Status.GAME_IN_PROGRESS))
					newGame.setText("New Game");
				else if (status.equals(Status.INITIALIZED))
					newGame.setText("Play Again?");
				else if (status.equals(Status.UNINITIALIZED))
					newGame.setText("Start Game");
				
				revalidate();
			}
		}	
	}
	
	// Swaps the current score card panel (if any) with the passed 
	// Player's score card panel
	private void swapScoreCard(Player p) {
		swapScoreCard();
		scp = new PlayerScoreCardPanel(p);
		frame.add(scp, BorderLayout.SOUTH);
	}
	
	// Removes the current score card panel
	private void swapScoreCard() {
		if (scp != null)
			frame.remove(scp);
	}
	
	// Initializes a new game
	private void newGame() {
		// If there is a current game in progress, or if a game just finished, reset the scores
		if (game.getStatus().equals(Status.GAME_IN_PROGRESS) || game.getStatus().equals(Status.INITIALIZED))
			game.resetScores();
		
		// Set the status to game in progress
		game.setStatus(Status.GAME_IN_PROGRESS);
		
		// Reset the dice
		dice.reset();
		
		// Initialize the Game object
		// (this is necessary for the various computer Strategies)
		game.nextTurn();
	}
	
	// Resets the game
	private void resetGame() {
		game.resetGame();
		swapScoreCard();
		frame.repaint();
	}
}