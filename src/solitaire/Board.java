package solitaire;

public abstract class Board {

	protected int size, cut, center;
	protected BoardType type;
	CellState[][] board;

	public Board(int size, BoardType type) {

		this.size = size;
		this.type = type;
		board = new CellState[size][size];

		cut = (size - 3) / 2;

		//fill cells, depending on size and type
		initBoard();

		//set center cell to empty
		center = size / 2;
		board[center][center] = CellState.EMPTY;
	}

	//subclasses define how to fill the board
	protected abstract void initBoard();

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public BoardType getType() {
		return type;
	}

	public void setType(BoardType type) {
		this.type = type;
	}

	public CellState getState(int row, int col) {
		return board[row][col];
	}

	public CellState setEmptyState(int row, int col) {
		return board[row][col] = CellState.EMPTY;
	}

	public CellState setActiveState(int row, int col) {
		return board[row][col] = CellState.ACTIVE;
	}

	public CellState setInactiveState(int row, int col) {
		return board[row][col] = CellState.INACTIVE;
	}
}
