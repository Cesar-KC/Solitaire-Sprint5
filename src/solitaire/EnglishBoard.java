package solitaire;

public class EnglishBoard extends Board {

	public EnglishBoard(int size) {
		super(size, BoardType.ENGLISH);  //BOARD parent
	}

	@Override
	protected void initBoard() {
		//iterate thru board cells, setting cells active(valid pegs), or inactive cells(not a part of board)
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {

				if ((row < cut && col < cut) || (row < cut && col >= size-cut) || (row >= size-cut && col < cut) || (row >= size-cut && col >= size-cut)) {
					board[row][col] = CellState.INACTIVE;
				} else {
					board[row][col] = CellState.ACTIVE;
				}
			}
		}
	}
}
