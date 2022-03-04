import java.util.ArrayList;

class FiveInARow{
	private final static int BOARD_SIZE = 7; //standard: 19
	private final static int BOARD_CHAR_SIZE = BOARD_SIZE * 2 + 1;
	private final static char PLAYER = 'X';
	private final static char OPPONENT = 'O';
	private final static char FRAME_CHAR = '-'; //Alternative frame character: ☐, but I had issues with it.
	
	private final InputManager input = new InputManager();
	private static boolean gameOnGoing = true;
	private ArrayList<Piece> pieces = new ArrayList<>();
	
	public FiveInARow(){
		System.out.println("You start, type in the row and thereafter the column where you would like to place your piece.");
		
		while(gameOnGoing){
			drawBoard();
			
			//Ask user where to place their piece.
			int row = input.getInt("What row");
			int column = input.getInt("What column");
			createPiece(row, column, PLAYER);
			
			drawBoard();
			
			//Let the opponent choose where to place their piece.
			//TODO
		}
	}
	
	private void createPiece(int row, int column, char player){
		Piece p = new Piece(row, column, player);
		pieces.add(p);
	}
	
	private void drawBoard(){
		StringBuilder board = new StringBuilder();
		
		String div = divider();
		board.append(boardTopFrame());
		
		int row = 1;
		for(int i = 0; i < BOARD_SIZE; i++){
			if(row >= 10){row = 0;}
			board.append(playArea(row));
			board.append(div);
			row++;
		}
		System.out.println(board);
	}
	
	private String boardTopFrame(){
		int column = 1;
		StringBuilder str = new StringBuilder();
		for(int i = 1; i < BOARD_CHAR_SIZE; i++){
			if(column == 10){column = 0;}
			if(i % 2 == 0){
				str.append(column);
				column++;
			}
			else{str.append(FRAME_CHAR);}
		}
		return str + (FRAME_CHAR + "") + System.lineSeparator();
	}
	
	private String divider(){
		return (FRAME_CHAR + "").repeat(BOARD_CHAR_SIZE) + System.lineSeparator();
	}
	
	private String playArea(int row){
		StringBuilder str = new StringBuilder(row + "");
		for(int i = 1; i < BOARD_CHAR_SIZE; i++){
			if(i % 2 == 0){
				str.append("|");
			}else{
				str.append(" ");
			}
		}
		return str + System.lineSeparator();
	}
}

record Piece(int xPos, int yPos, char player){
	public int getXPos(){return xPos;} //column
	public int getYPos(){return yPos;} //row
	public char getPlayer(){return player;}
}