public class Board {
    private static final int BOARD_SIZE = 3; //standard: 19
    private static final int BOARD_CHAR_SIZE = BOARD_SIZE * 2 + 1;

    private final Piece[][] board = new Piece[BOARD_SIZE][BOARD_SIZE]; //Could be improved by using one Array.


    public Board(){
        resetBoard();
    }

    public int getSize(){
        return BOARD_SIZE;
    }

    public void resetTile(int x, int y){
        board[x][y] = null;
    }

    public boolean set(Piece piece){
        if(board[piece.getRow()][piece.getColumn()] == null){
            board[piece.getRow()][piece.getColumn()] = piece;
            return true;
        }else{
            return false;
        }
    }

    public Piece get(int x, int y){
        return board[x][y];
    }

    public int availableCapacity(){
        int availableElements = 0;
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                if(board[i][j] == null){availableElements++;}
            }
        }
        return availableElements;
    }

    public void resetBoard(){
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                board[i][j] = null;
            }
        }
    }

    public void drawBoard(){
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
