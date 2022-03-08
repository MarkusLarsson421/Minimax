import java.util.Random;

class FiveInARow{
	private static final int BOARD_SIZE = 7; //standard: 19
	private static final int BOARD_CHAR_SIZE = BOARD_SIZE * 2 + 1;
	private static final int PIECES_IN_A_ROW = 5;
	
	private static final char PLAYER = 'X';
	private static final char OPPONENT = 'O';

	private static final Random rng = new Random();

	private final InputManager input = new InputManager();
	private final Piece[][] board = new Piece[BOARD_SIZE][BOARD_SIZE];
	private int pieces = 0;

	public FiveInARow(){
		boolean isRunning = true;
		while(isRunning){
			char userInput = input.getChar("Do you want to start [Y/N]");
			switch(userInput){
				case 'Y':
					gameLoop();
					break;
				case 'N':
					isRunning = false;
					break;
				default:
					System.out.println("Error: Not an option!");
			}
		}
	}
	
	private void gameLoop(){
		System.out.println("You start, type in the row and thereafter the column where you would like to place your piece.");
		resetBoard();
		Piece piece;

		boolean gameOnGoing = true;
		while(gameOnGoing){
			drawBoard();

			//Could possibly be made more readable.

			//Ask user where to place their piece.
			piece = getUserPlacement();
			if(checkWin(piece)){
				System.out.println("YOU'VE WON! CONGRATULATIONS! YOU'VE WON! YOU'VE WON!");
				drawBoard();
				gameOnGoing = false;
			}else{
				//Let the AI opponent choose where to place their piece.
				piece = minimax();
				//Bug with opponent, only requires 3 in a row to win!?!?! Should be 5 like it is for the player.
				if(checkWin(piece)){
					System.out.println("YOU'VE LOST! CONGRATULATIONS! YOU'VE LOST! YOU'VE LOST!");
					drawBoard();
					gameOnGoing = false;
				}
			}
			if(pieces >= BOARD_SIZE * BOARD_SIZE){
				System.out.println("YOU'VE DRAWN! CONGRATULATIONS! YOU'VE DRAWN! YOU'VE DRAWN!");
				gameOnGoing = false;
				drawBoard();
			}
		}
	}

	private Piece minimax() {
		int row;
		int column;
		boolean spotAvailable;

		do{
			row = rng.nextInt(BOARD_SIZE);
			column = rng.nextInt(BOARD_SIZE);

			spotAvailable = board[row][column] == null;
		}while(!spotAvailable);
		return createPiece(OPPONENT, row, column);
	}

	private Piece getUserPlacement(){
		int row;
		int column;
		boolean spotAvailable;

		do{
			row = limitRange("What row");
			column = limitRange("What column");

			spotAvailable = board[row][column] == null;
			if(!spotAvailable){System.out.println("Error: You can't place it there, another piece is already there!");}
		}while(!spotAvailable);

		return createPiece(PLAYER, row, column);
	}
	
	private int limitRange(String question){
		int placement;
		do{
			placement = input.getInt(question);
			placement--;
			if(placement > BOARD_SIZE || placement < 0){System.out.println("Error: Your placement can not be outside the board!");}
		}while(placement > BOARD_SIZE || placement < 0);
		return placement;
	}
	
	private Piece createPiece(char player, int row, int column){
		Piece piece = new Piece(player, row, column);
		board[row][column] = piece;
		pieces++;
		return piece;
	}
	
	private void resetBoard(){
		pieces = 0;
		for(int i = 0; i < BOARD_SIZE; i++){
			for(int j = 0; j < BOARD_SIZE; j++){
				board[i][j] = null;
			}
		}
	}
	
	private boolean checkWin(Piece piece){
		if(pieces <= 8){
			return false;
		}

		int diagonalTopLeft = countRow(piece, -1, 1);
		int diagonalTopRight = countRow(piece, 1, 1);
		int horizontal = countRow(piece, 1, 0);
		int vertical = countRow(piece, 0, 1);

		return max(diagonalTopLeft, diagonalTopRight, horizontal, vertical) >= PIECES_IN_A_ROW;
	}

	private int max(int... numbers){
		int output = 0;
		for(int i : numbers){
			if(i > output){
				output = i;
			}
		}
		return output;
	}

	private int countRow(Piece piece, int dirX, int dirY) {
		int concurrentPieces = 1;
		int row = piece.getRow();
		int column = piece.getColumn();
		char player = piece.getPlayer();

		//These two while loops could be improved.
		while(row >= 0 && row < BOARD_SIZE && column >= 0 && column < BOARD_SIZE && board[row][column] != null && board[row][column].getPlayer() == player){
			concurrentPieces++;
			row += dirX;
			column += dirY;
		}

		//Could possibly just subtract the current row and column values with concurrentPieces.
		row = piece.getRow();
		column = piece.getColumn();

		while(row >= 0 && row < BOARD_SIZE && column >= 0 && column < BOARD_SIZE && board[row][column] != null && board[row][column].getPlayer() == player){
			concurrentPieces	++;
			row -= dirX;
			column -= dirY;
		}

		return concurrentPieces;
	}

	private void drawBoard(){
		StringBuilder board = new StringBuilder();
		
		String div = divider();
		board.append(boardTopFrame());
		
		for(int i = 0; i < BOARD_SIZE; i++){
			board.append(playArea(i));
			board.append(div);
		}
		System.out.println(board);
	}
	
	private String playArea(int row){
		String displayRow = row + 1 + "";
		displayRow = displayRow.substring(displayRow.length() - 1);
		
		StringBuilder output = new StringBuilder(displayRow);
		for(int i = 0; i < BOARD_SIZE; i++){
			Piece piece = board[row][i];
			output.append(" ").append(piece == null ? ' ' : piece.getPlayer()).append(" |");
		}
		output.append(System.lineSeparator());
		
		return output.toString();
	}
	
	private String boardTopFrame(){
		int column = 1;
		StringBuilder str = new StringBuilder("+-");
		for(int i = 2; i < BOARD_CHAR_SIZE; i++){
			if(column == 10){column = 0;}
			if(i % 2 == 0){
				str.append(column);
				column++;
			}
			else{str.append("-+-");}
		}
		return str + ("-+" + "") + System.lineSeparator();
	}
	
	private String divider(){
		String start = "+";
		String end = "+";
		String middle = "---+".repeat(BOARD_SIZE - 1) + "---";
		return start + middle + end + System.lineSeparator();
	}
}