import java.util.ArrayList;

class FiveInARow{
	private final static int BOARD_SIZE = 7; //standard: 19
	private final static int BOARD_CHAR_SIZE = BOARD_SIZE * 2 + 1;
	private final static char PLAYER = 'X';
	private final static char OPPONENT = 'O';
	
	private final ArrayList<Piece> pieces = new ArrayList<>();
	private final InputManager input = new InputManager();
	
	private static boolean gameOnGoing = true;
	
	public FiveInARow(){
		System.out.println("You start, type in the row and thereafter the column where you would like to place your piece.");
		
		while(gameOnGoing){
			drawBoard();
			
			//Ask user where to place their piece.
			int row = input.getInt("What row");
			int column = input.getInt("What column");
			createPiece(row, column, PLAYER);
			
			drawBoard();
			
			//Let the AI opponent choose where to place their piece.
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
		
		for(int i = 0; i < BOARD_SIZE; i++){
			board.append(playArea(i));
			board.append(div);
		}
		System.out.println(board);
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
	
	private String playArea(int row){
		String rowStr = row + "";
		rowStr = rowStr.substring(rowStr.length() - 1);
		
		int column = 1;
		StringBuilder str = new StringBuilder(rowStr);
		for(int i = 1; i < BOARD_CHAR_SIZE; i++){
			if(i % 2 == 0){
				str.append("|");
			}else{
				str.append("   ");
				column++;
			}
		}
		return str + System.lineSeparator();
	}
}

class Piece{
	private final int xPos;
	private final int yPos;
	private final char player;
	
	public Piece(int xPos, int yPos, char player){
		this.xPos = xPos;
		this.yPos = yPos;
		this.player = player;
	}
	
	public int getXPos(){return xPos;} //column
	public int getYPos(){return yPos;} //row
	public char getPlayer(){return player;}
}