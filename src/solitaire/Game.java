package solitaire;
import javax.swing.*;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class Game extends JFrame {

	private static final long serialVersionUID = -143619586853385367L;

	// Default Game Values
	int size = 7;
	BoardType boardType = BoardType.ENGLISH;

	CellState cellType;
	JComboBox<String> cmbSize = new JComboBox<String>(new String[] {"5", "7", "9", "11"});
	JLabel lblBoardSize, lblBoardType, lblTitle, lblEmpty;
	JButton btnReplay, btnNewGame, btnAuto, btnRandom;
	JCheckBox cboxRecord, cboxRandomizeState;
	int moveCount = 0;
	JRadioButton rbtnEnglish, rbtnHex, rbtnDiamond;
	GameController controller;
	GameRecorder recorder;
	JButton[][] buttons = new JButton[size][size];
	ButtonGroup group;
	JPanel northPanel, southPanel, eastPanel, westPanel, centerPanel;

	int selectedRow = -1;
	int selectedCol = -1;
	boolean autoPlaying = false;

	public Game() {
		cmbSize.setSelectedItem("7");
		controller = new GameController(new EnglishBoard(size));
		recorder = new GameRecorder();
		buildUI();
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Peg Solitaire");
		setLocationRelativeTo(null);
	}

	public void buildUI() {

		lblBoardSize = new JLabel("Board Size");
		lblBoardType = new JLabel("Board Type");
		lblTitle = new JLabel("Peg Solitaire");
		lblEmpty = new JLabel("");
		cboxRecord = new JCheckBox("Record game");
		cboxRandomizeState = new JCheckBox("Randomize State (every 5 moves)");
		rbtnEnglish = new JRadioButton("English", true);
		rbtnHex = new JRadioButton("Hex");
		rbtnDiamond = new JRadioButton("Diamond");

		// REPLAY — reads file and plays back recorded game
		btnReplay = new JButton("Replay");
		btnReplay.addActionListener(e -> {
			startReplay();
		});

		// NEW GAME
		btnNewGame = new JButton("New Game");
		btnNewGame.addActionListener(e -> {
			BoardType selectedType = null;
			int selectedSize = Integer.parseInt((String) cmbSize.getSelectedItem());

			if (rbtnEnglish.isSelected()) {
				selectedType = BoardType.ENGLISH;
			} else if (rbtnHex.isSelected()) {
				selectedType = BoardType.HEXAGON;
			} else if (rbtnDiamond.isSelected()) {
				selectedType = BoardType.DIAMOND;
			}

			// stop any active recording before starting new game
			recorder.stopRecording();

			buildBoard(selectedSize, selectedType);

			// start recording if checkbox is checked
			if (cboxRecord.isSelected()) {
				recorder.startRecording(selectedSize, selectedType);
			}
		});

		// AUTOPLAY
		btnAuto = new JButton("AutoPlay");
		btnAuto.addActionListener(e -> {
			startAutoPlay();
		});

		// RANDOMIZE BOARD
		btnRandom = new JButton("Randomize");
		btnRandom.addActionListener(e -> {
			Random random = new Random();
			int sizeSeed = random.nextInt(4) + 1;
			int typeSeed = random.nextInt(2) + 1;

			switch (sizeSeed) {
			case 1: size = 5; break;
			case 2: size = 7; break;
			case 3: size = 9; break;
			case 4: size = 11; break;
			}

			switch (typeSeed) {
			case 1: boardType = BoardType.ENGLISH; break;
			case 2: boardType = BoardType.HEXAGON; break;
			}

			recorder.stopRecording();
			buildBoard(size, boardType);

			if (cboxRecord.isSelected()) {
				recorder.startRecording(size, boardType);
			}
		});

		// Layout
		setLayout(new BorderLayout());

		// NORTH PANEL
		northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		northPanel.add(lblBoardSize);
		northPanel.add(cmbSize);
		northPanel.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 8));

		// WEST PANEL
		westPanel = new JPanel(new GridLayout(4, 1));
		group = new ButtonGroup();
		group.add(rbtnEnglish);
		group.add(rbtnHex);
		group.add(rbtnDiamond);
		westPanel.add(lblBoardType);
		westPanel.add(rbtnEnglish);
		westPanel.add(rbtnHex);
		westPanel.add(rbtnDiamond);
		westPanel.setBorder(BorderFactory.createEmptyBorder(20, 8, 20, 20));

		// EAST PANEL — New Game, AutoPlay, Randomize
		eastPanel = new JPanel(new GridLayout(3, 1, 8, 8));
		eastPanel.add(btnNewGame);
		eastPanel.add(btnAuto);
		eastPanel.add(btnRandom);
		eastPanel.setBorder(BorderFactory.createEmptyBorder(20, 8, 20, 20));

		// SOUTH PANEL — Record checkbox, Randomize State checkbox, Replay button
		southPanel = new JPanel(new GridLayout(3, 1, 8, 8));
		southPanel.add(cboxRecord);
		southPanel.add(cboxRandomizeState);
		southPanel.add(btnReplay);
		southPanel.setBorder(BorderFactory.createEmptyBorder(20, 8, 20, 20));

		// CENTER PANEL
		centerPanel = new JPanel(new GridLayout(size, size, 20, 20));

		add(northPanel, BorderLayout.NORTH);
		add(southPanel, BorderLayout.SOUTH);
		add(eastPanel, BorderLayout.EAST);
		add(westPanel, BorderLayout.WEST);
		add(centerPanel, BorderLayout.CENTER);

		updateBoard();
	}

	public void updateBoard() {
		centerPanel.removeAll();
		centerPanel.setLayout(new GridLayout(size, size, 20, 20));
		buttons = new JButton[size][size];

		selectedRow = -1;
		selectedCol = -1;

		Board board = controller.getBoard();

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				int r = row;
				int c = col;

				if (board.getState(r, c) == CellState.ACTIVE ||
					board.getState(r, c) == CellState.EMPTY) {

					JButton cellButton;
					if (board.getState(r, c) == CellState.ACTIVE) {
						cellButton = new JButton("Peg");
						cellButton.setBackground(new Color(101, 67, 33)); // dark brown
						cellButton.setForeground(Color.WHITE);
						cellButton.setOpaque(true);
						cellButton.setBorderPainted(false);

					} else {
						cellButton = new JButton("EMPTY");
						cellButton.setBackground(new Color(34, 139, 34)); // forest green
						cellButton.setOpaque(true);
						cellButton.setBorderPainted(false);
					}

					cellButton.addActionListener(e -> {
						// FIRST CLICK
						if (selectedRow == -1 && selectedCol == -1) {
							if (board.getState(r, c) == CellState.ACTIVE) {
								selectedRow = r;
								selectedCol = c;
							}
						}
						// SECOND CLICK
						else {
							int fromRow = selectedRow;
							int fromCol = selectedCol;
							int toRow = r;
							int toCol = c;

							if (controller.executeMove(fromRow, fromCol, toRow, toCol)) {

								// record the move if recording is on
								if (recorder.isRecording()) {
									recorder.recordMove(fromRow, fromCol, toRow, toCol);
								}

								moveCount++;

								// randomize every 5 moves if checkbox on
								if (cboxRandomizeState.isSelected() && moveCount % 5 == 0) {
									controller.randomizeState();

									// record randomize snapshot if recording
									if (recorder.isRecording()) {
										recorder.recordRandomize(controller.getBoard());
									}

									JOptionPane.showMessageDialog(this, "Board state randomized!");
								}

								updateBoard();

								if (controller.isGameOver()) {
									int pegs = controller.numPegs();
									recorder.stopRecording();

									if (pegs == 1) {
										JOptionPane.showMessageDialog(this, "WINNER!");
									} else {
										JOptionPane.showMessageDialog(this, "Game over.");
									}

									buildBoard(size, boardType);
								}

								return;
							}

							selectedRow = -1;
							selectedCol = -1;
						}
					});

					buttons[r][c] = cellButton;
					centerPanel.add(cellButton);
				} else {
					centerPanel.add(new JLabel(""));
				}
			}
		}

		centerPanel.revalidate();
		centerPanel.repaint();
	}

	public void startAutoPlay() {
		if (autoPlaying) {
			JOptionPane.showMessageDialog(this, "AutoPlay is already ON.");
			return;
		}

		autoPlaying = true;
		javax.swing.Timer autoTimer = new javax.swing.Timer(500, null);
		autoTimer.addActionListener(e -> {
			int[] move = controller.getRandomMove();

			if (move != null) {
				controller.executeMove(move[0], move[1], move[2], move[3]);

				// record autoplay move if recording is on
				if (recorder.isRecording()) {
					recorder.recordMove(move[0], move[1], move[2], move[3]);
				}
			}

			updateBoard();

			if (controller.isGameOver()) {
				autoTimer.stop();
				autoPlaying = false;
				recorder.stopRecording();
				JOptionPane.showMessageDialog(this, "AutoPlay game is over.");
				buildBoard(size, boardType);
			}
		});
		autoTimer.start();
	}

	public void startReplay() {
		List<String> lines = recorder.loadRecording();

		if (lines.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No recorded game found.");
			return;
		}

		// parse header — first line is size,TYPE
		String[] header = lines.get(0).split(",");
		int replaySize = Integer.parseInt(header[0]);
		BoardType replayType = BoardType.valueOf(header[1]);

		// build fresh board to replay on
		buildBoard(replaySize, replayType);

		// replay moves one by one with a timer
		final int[] index = {1}; // start from line 1 (skip header)
		javax.swing.Timer replayTimer = new javax.swing.Timer(600, null);

		replayTimer.addActionListener(e -> {
			if (index[0] >= lines.size()) {
				replayTimer.stop();
				JOptionPane.showMessageDialog(this, "Replay complete!");
				return;
			}

			String line = lines.get(index[0]);
			index[0]++;

			if (line.equals("RANDOMIZE")) {
				// next line is the board snapshot
				if (index[0] < lines.size()) {
					String snapshot = lines.get(index[0]);
					index[0]++;
					applyBoardSnapshot(snapshot, replaySize);
				}
			} else {
				// regular move: fromRow,fromCol,toRow,toCol
				String[] parts = line.split(",");
				int fromRow = Integer.parseInt(parts[0]);
				int fromCol = Integer.parseInt(parts[1]);
				int toRow   = Integer.parseInt(parts[2]);
				int toCol   = Integer.parseInt(parts[3]);
				controller.executeMove(fromRow, fromCol, toRow, toCol);
			}

			updateBoard();
		});

		replayTimer.start();
	}

	// Applies a full board snapshot from a recorded RANDOMIZE line
	private void applyBoardSnapshot(String snapshot, int replaySize) {
		String[] rows = snapshot.split(";");
		Board board = controller.getBoard();
		for (int r = 0; r < replaySize; r++) {
			String[] cols = rows[r].split(",");
			for (int c = 0; c < replaySize; c++) {
				CellState state = CellState.valueOf(cols[c]);
				if (state == CellState.ACTIVE) {
					board.setActiveState(r, c);
				} else if (state == CellState.EMPTY) {
					board.setEmptyState(r, c);
				} else {
					board.setInactiveState(r, c);
				}
			}
		}
	}

	public void buildBoard(int newSize, BoardType newType) {
		this.size = newSize;
		this.boardType = newType;
		moveCount = 0;
		autoPlaying = false;
		Board newBoard = controller.createBoard(newSize, newType);
		controller.setBoard(newBoard);
		updateBoard();
	}
}