class FiveInARow{
	private static final int BOARD_SIZE = 7; //standard: 19
	private static final int BOARD_CHAR_SIZE = BOARD_SIZE * 2 + 1;
	
	private static final char PLAYER = 'X';
	private static final char OPPONENT = 'O';
	
	private static final byte EMPTY_TILE = 0;
	private static final byte PLAYER_BYTE = 1;
	private static final byte OPPONENT_BYTE = 2;
	
	private final InputManager input = new InputManager();
	private final Byte[][] board = new Byte[BOARD_SIZE][BOARD_SIZE];
	
	private static boolean gameOnGoing = true;
	
	public FiveInARow(){
		gameLoop();
	}
	
	private void gameLoop(){
		System.out.println("You start, type in the row and thereafter the column where you would like to place your piece.");
		resetBoard();
		
		while(gameOnGoing){
			checkWin();
			drawBoard();
			
			//Ask user where to place their piece.
			int row = input.getInt("What row");
			int column = input.getInt("What column");
			createPiece(row, column, PLAYER_BYTE);
			
			checkWin();
			drawBoard();
			
			//Let the AI opponent choose where to place their piece.
			//TODO
		}
	}
	
	private void resetBoard(){
		for(int i = 0; i < BOARD_SIZE; i++){
			for(int j = 0; j < BOARD_SIZE; j++){
				board[i][j] = 0;
			}
		}
	}
	
	private void checkWin(){
		//TODO
	}
	
	private void createPiece(int row, int column, byte player){
		board[row][column] = player;
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
		String displayRow = row + "";
		displayRow = displayRow.substring(displayRow.length() - 1);
		
		StringBuilder output = new StringBuilder(displayRow);
		for(int i = 0; i < BOARD_SIZE; i++){
			output.append(tile(row, i));
		}
		output.append(System.lineSeparator());
		
		return output.toString();
	}
	
	private String tile(int row, int column){
		byte b = board[row][column];
		String output = " ";
		
		if(b == 0){output += " ";}
		else if(b == 1){output += PLAYER;}
		else if(b == 2){output += OPPONENT;}
		
		return output + " |";
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