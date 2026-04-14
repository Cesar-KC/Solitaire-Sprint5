package solitaire;

public class HexagonBoard extends Board {

	public HexagonBoard(int size) {
		super(size, BoardType.HEXAGON);   //BOard parent class
	}

	@Override
	protected void initBoard() {
		//iterate thru board cells, setting cells active(valid pegs), or inactive cells(not a part of board) Hexagon
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {

				if ((row + col < cut) || (row - col >= (size-cut)) || (row + (size-1-col) < cut) || ((size-1-row) + (size-1-col) < cut)) {
					board[row][col] = CellState.INACTIVE;
				} else {
					board[row][col] = CellState.ACTIVE;
				}
			}
		}
	}
}
