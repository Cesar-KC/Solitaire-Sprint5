package solitaire;


public class DiamondBoard extends Board {

	public DiamondBoard(int size) {
		super(size, BoardType.DIAMOND);   //BOARD parent class, need to implement later
	}

	protected void initBoard() {  // use manhattan distance to calculate Diamond type
	    int center = size / 2;
	    for (int row = 0; row < size; row++) {
	        for (int col = 0; col < size; col++) {
	            if (Math.abs(row - center) + Math.abs(col - center) <= center) {
	                board[row][col] = CellState.ACTIVE;
	            } else {
	                board[row][col] = CellState.INACTIVE;
	            }
	        }
	    }
	}
}
