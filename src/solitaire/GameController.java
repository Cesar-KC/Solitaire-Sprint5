package solitaire;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController {

	private Board board;
	private int size;

	public GameController(Board board) {
		this.board = board;
		this.size = board.getSize();
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
		this.size = board.getSize();
	}

	//factory method: creates the right Board subclass based on type
	public Board createBoard(int size, BoardType type) {
		switch (type) {
		case ENGLISH:
			return new EnglishBoard(size);
		case HEXAGON:
			return new HexagonBoard(size);
		case DIAMOND:
			//ToDO implement Diamond logic in DiamondBoard class
			return new DiamondBoard(size);
		default:
			return new EnglishBoard(size);
		}
	}

	//attempts a move, returns true if successful
	public boolean executeMove(int fromRow, int fromCol, int toRow, int toCol) {

		if (board.getState(toRow, toCol) != CellState.EMPTY) return false;

		boolean sameRow = fromRow == toRow;
		boolean sameCol = fromCol == toCol;
		boolean validDistance =
				(sameRow && Math.abs(toCol - fromCol) == 2) ||
				(sameCol && Math.abs(toRow - fromRow) == 2);

		if (!validDistance) return false;

		int overRow = (fromRow + toRow) / 2;
		int overCol = (fromCol + toCol) / 2;

		if (board.getState(overRow, overCol) != CellState.ACTIVE) return false;

		board.setEmptyState(fromRow, fromCol);
		board.setEmptyState(overRow, overCol);
		board.setActiveState(toRow, toCol);
		return true;
	}

	public boolean isGameOver() {
		//scans board for peg, then checks all surrounding positions to see if a move is possible
		//if not, game is over
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (board.getState(row, col) == CellState.ACTIVE) {

					// valid move above
					if (row - 2 >= 0) {
						if (board.getState(row - 2, col) == CellState.EMPTY &&
							board.getState(row - 1, col) == CellState.ACTIVE) {
							return false;
						}
					}

					// valid move below
					if (row + 2 < size) {
						if (board.getState(row + 2, col) == CellState.EMPTY &&
							board.getState(row + 1, col) == CellState.ACTIVE) {
							return false;
						}
					}

					// valid move to left
					if (col - 2 >= 0) {
						if (board.getState(row, col - 2) == CellState.EMPTY &&
							board.getState(row, col - 1) == CellState.ACTIVE) {
							return false;
						}
					}

					// valid move to right
					if (col + 2 < size) {
						if (board.getState(row, col + 2) == CellState.EMPTY &&
							board.getState(row, col + 1) == CellState.ACTIVE) {
							return false;
						}
					}
				}
			}
		}
		//no valid moves, game over, return true
		return true;
	}

	// returns a random valid move as [fromRow, fromCol, toRow, toCol], or null if none exist
	public int[] getRandomMove() {
		List<int[]> validMoves = new ArrayList<>();
		int[][] directions = {{-2,0},{2,0},{0,-2},{0,2}};

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (board.getState(row, col) == CellState.ACTIVE) {
					for (int[] dir : directions) {
						int toRow = row + dir[0];
						int toCol = col + dir[1];
						int overRow = row + dir[0] / 2;
						int overCol = col + dir[1] / 2;

						if (toRow >= 0 && toRow < size && toCol >= 0 && toCol < size &&
							board.getState(toRow, toCol) == CellState.EMPTY &&
							board.getState(overRow, overCol) == CellState.ACTIVE) {
							validMoves.add(new int[]{row, col, toRow, toCol});
						}
					}
				}
			}
		}

		if (validMoves.isEmpty()) return null;
		return validMoves.get(new Random().nextInt(validMoves.size()));
	}

	// executes a random valid move, returns true if a move was made
	public boolean executeRandomMove() {
		int[] move = getRandomMove();
		if (move == null) return false;
		return executeMove(move[0], move[1], move[2], move[3]);
	}

	// shuffles pegs to random valid positions, keeping the same peg count
	public void randomizeState() {
		List<int[]> validCells = new ArrayList<>();

		// collect all non-inactive cells
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (board.getState(row, col) != CellState.INACTIVE) {
					validCells.add(new int[]{row, col});
				}
			}
		}

		int pegCount = numPegs();

		// clear all valid cells
		for (int[] cell : validCells) {
			board.setEmptyState(cell[0], cell[1]);
		}

		// randomly place the same number of pegs back
		java.util.Collections.shuffle(validCells, new Random());
		for (int i = 0; i < pegCount; i++) {
			board.setActiveState(validCells.get(i)[0], validCells.get(i)[1]);
		}
	}

	//method to count number of pegs left on board
	//if pegs left is 1, player wins!
	public int numPegs() {
		int pegs = 0;

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (board.getState(row, col) == CellState.ACTIVE) {
					pegs++;
				}
			}
		}

		return pegs;
	}
}