import java.util.ArrayList;
import java.util.Random;

class FiveInARow{
	private static final int BOARD_SIZE = 3; //standard: 19
	private static final int BOARD_CHAR_SIZE = BOARD_SIZE * 2 + 1;
	private static final int PIECES_IN_A_ROW = 3;
	
	private static final char PLAYER_X = 'x';
	private static final char PLAYER_O = 'o';
	private static final int MINIMAX_SEARCH_DEPTH = 3;

	private final Random rng = new Random();
	private final InputManager input = new InputManager();
	private final Piece[][] board = new Piece[BOARD_SIZE][BOARD_SIZE]; //Could be improved by using one Array.

	private int pieces = 0;

	public FiveInARow(){
		menu();
	}

	private void menu(){
		boolean isRunning = true;
		while(isRunning){
			char userInput = input.getChar("Do you want to start [Y/N]");
			switch(userInput){
				case 'Y':
				case 'y':
					gameLoop(getPlayer(PLAYER_X), getPlayer(PLAYER_O));
					break;
				case 'N':
				case 'n':
					isRunning = false;
					break;
				default:
					System.out.println("Error: Not an option!");
			}
		}
	}

	private enum CONTROLLER {RANDOM, MINIMAX, HUMAN}

	private CONTROLLER getPlayer(char player){
		System.out.println("Options:");
		System.out.println("[R]andom AI, [M]inimax AI, [H]uman.");
		player = player == 'x' ? 'X' : 'O';
		char userInput = input.getChar("Who is player " + player);
		switch(userInput){
			case 'R':
			case 'r':
				return CONTROLLER.RANDOM;
			case 'M':
			case 'm':
				return CONTROLLER.MINIMAX;
			case 'H':
			case 'h':
				return CONTROLLER.HUMAN;
			default:
				System.out.println("Error: Not an option!");
		}
		return null;
	}

	private void gameLoop(CONTROLLER controllerX, CONTROLLER controllerO){
		resetBoard();
		Piece piece;

		while(true){
			//Let whoever is playing PLAYER_X make their move.
			piece = getPiece(controllerX, PLAYER_X);

			//Check if it resulted in a win.
			if(piece != null && checkWin(piece)){
				congratulate(PLAYER_X);
				return;
			}
			//Check if it resulted in a draw.
			else if(boardCapacity() == 0){
				congratulate('d');
				return;
			}

			//Let whoever is playing PLAYER_O make their move.
			piece = getPiece(controllerO, PLAYER_O);

			//Check if it resulted in a win.
			if(piece != null && checkWin(piece)){
				congratulate(PLAYER_O);
				return;
			}
			//Check if it resulted in a draw.
			if(boardCapacity() == 0){
				congratulate('d');
				return;
			}
		}
	}

	private Piece getPiece(CONTROLLER controller, char c) {
		//Update the board.
		drawBoard();

		c = c == 'x' ? 'X' : 'O';
		System.out.println("Player " + c + "'s turn.");

		Piece piece = null;
		if(controller == CONTROLLER.RANDOM){piece = randomAIController(c);}
		else if(controller == CONTROLLER.MINIMAX){piece = minimaxAIController(c);}
		else if(controller == CONTROLLER.HUMAN){piece= humanController(c);}

		if (piece != null) {
			System.out.println("Placed it at: [" + piece.getRow() + "][" + piece.getColumn() + "]");
		}
		return piece;
	}

	private Piece minimaxAIController(char player){
		int alpha = Integer.MAX_VALUE;
		int beta = Integer.MIN_VALUE;

		int bestValue = Integer.MIN_VALUE;
		Piece bestPlacement = null;

		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[i].length; j++){
				if(board[i][j] == null){
					//Make the move, check how good it is and undo it.
					//Repeat with the next option.
					Piece tmpPiece = new Piece(player, i, j);
					board[i][j] = tmpPiece;
					int currentValue = minimax(tmpPiece, 0, false);
					board[i][j] = null;

					if(currentValue > bestValue){
						bestPlacement = new Piece(player, i, j);
						bestValue = currentValue;
					}
				}
			}
		}
		System.out.println("Value of the most optional move: " + bestValue);

		if(bestPlacement != null){board[bestPlacement.getRow()][bestPlacement.getColumn()] = bestPlacement;}
		return bestPlacement;
	}

	//Is isMaxPlayer needed? Could I do without it?
	private int minimax(Piece piece, int depth, boolean isMaxPlayer){
		if(depth == MINIMAX_SEARCH_DEPTH)

		if(boardCapacity() == 0){return 0;}
		int value = evaluate(piece);
		if(value == 1){return value;}
		if(value == -1){return value;}

		int bestValue;
		if(isMaxPlayer){
			bestValue = Integer.MIN_VALUE;

			for(int i = 0; i < board.length; i++){
				for(int j = 0; j < board[i].length; j++){
					if(board[i][j] == null){
						//Repeating code, could maybe be extract into a method.
						Piece tmpPiece = new Piece(piece.getPlayer(), i, j);
						board[i][j] = tmpPiece;
						bestValue = Math.max(bestValue, minimax(tmpPiece, depth + 1, !isMaxPlayer));
						board[i][j] = null;
					}
				}
			}
		}
		else{
			bestValue = Integer.MAX_VALUE;

			for(int i = 0; i < board.length; i++){
				for(int j = 0; j < board[i].length; j++){
					if(board[i][j] == null){
						//Repeating code, could maybe be extract into a method.
						Piece tmpPiece = new Piece(piece.getPlayer() == 'x' ? 'o' : 'x', i, j);
						board[i][j] = tmpPiece;
						bestValue = Math.min(bestValue, minimax(tmpPiece, depth + 1, !isMaxPlayer));
						board[i][j] = null;
					}
				}
			}
		}
		return bestValue;
	}

	//hopefully works, has been heavily modified
	private int evaluate(Piece piece){
		boolean won = checkWin(piece);
		return won ? 1 : 0;
	}

	private int boardCapacity(){
		return pieces - BOARD_SIZE * BOARD_SIZE;
	}

	private Piece randomAIController(char player){
		int row;
		int column;
		boolean spotAvailable;

		do{
			row = rng.nextInt(BOARD_SIZE);
			column = rng.nextInt(BOARD_SIZE);

			spotAvailable = board[row][column] == null;
		}while(!spotAvailable);

		return createPiece(player, row, column);
	}

	private Piece humanController(char player){
		int row;
		int column;
		boolean spotAvailable;

		do{
			row = limitRange("What row");
			column = limitRange("What column");

			spotAvailable = board[row][column] == null;
			if(!spotAvailable){System.out.println("Error: You can't place it there, another piece is already there!");}
		}while(!spotAvailable);

		return createPiece(player, row, column);
	}
	
	private int limitRange(String question){
		int placement;
		do{
			placement = input.getInt(question);
			placement--;
			if(placement >= BOARD_SIZE || placement < 0){System.out.println("Error: Your placement can not be outside the board!");}
		}while(placement >= BOARD_SIZE || placement < 0);
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
		if(pieces <= PIECES_IN_A_ROW * 2 - 2){
			return false;
		}

		//Could have an if-statement to check if the just checked one is above PIECES_IN_A_ROW and just return then.
		//Would be a bit less readable.
		int diagonalTopLeft = countConcurrentPieces(piece, -1, 1);
		int diagonalTopRight = countConcurrentPieces(piece, 1, 1);
		int horizontal = countConcurrentPieces(piece, 1, 0);
		int vertical = countConcurrentPieces(piece, 0, 1);

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

	private int countConcurrentPieces(Piece piece, int dirX, int dirY) {
		int concurrentPieces = 1;
		int deltaRow = piece.getRow() + dirY;
		int deltaColumn = piece.getColumn() + dirX;
		char player = piece.getPlayer();

		ArrayList<Piece> pieces = new ArrayList<>();
		pieces.add(piece);

		//These two while loops could be improved.
		while(withinBoarder(deltaRow, deltaColumn) &&
				board[deltaRow][deltaColumn] != null &&
				board[deltaRow][deltaColumn].getPlayer() == player){
			concurrentPieces++;
			pieces.add(board[deltaRow][deltaColumn]);

			deltaRow += dirY;
			deltaColumn += dirX;
		}

		//Could possibly just subtract the current row and column values with concurrentPieces.
		deltaRow = piece.getRow() - dirY;
		deltaColumn = piece.getColumn() - dirX;

		while(withinBoarder(deltaRow, deltaColumn) &&
				board[deltaRow][deltaColumn] != null &&
				board[deltaRow][deltaColumn].getPlayer() == player){
			concurrentPieces++;
			pieces.add(board[deltaRow][deltaColumn]);

			deltaRow -= dirY;
			deltaColumn -= dirX;
		}

		//Makes the winning line capitalized.
		if(concurrentPieces >= PIECES_IN_A_ROW){
			for(Piece p : pieces){
				Piece newPiece = new Piece(piece.getPlayer() == 'x' ? 'X' : 'O', p.getRow(), p.getColumn());
				board[p.getRow()][p.getColumn()] = newPiece;
			}
		}

		return concurrentPieces;
	}

	private boolean withinBoarder(int row, int column) {
		return row >= 0 && row < BOARD_SIZE && column >= 0 && column < BOARD_SIZE;
	}

	private void congratulate(char player){
		if(player == 'd'){
			System.out.println("YOU'VE DRAWN! CONGRATULATIONS! YOU'VE DRAWN! YOU'VE DRAWN!");
		}
		else{
			player = player == 'x' ? 'X' : 'O';
			System.out.println("PLAYER " + player + " WON! CONGRATULATIONS! PLAYER " + player + " WON! PLAYER" + player + " WON!");
		}
		drawBoard();
	}

	//---GRAPHICS---

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