//Can become a record.
public class Piece{
    private final char player;
    private final int row;
    private final int column;

    Piece(char player, int row, int column){
        this.player = player;
        this.row = row;
        this.column = column;
    }

    public char getPlayer() {
        return player;
    }
    public int getRow(){
        return row;
    }
    public int getColumn(){
        return column;
    }
    public boolean equals(Piece p){
        return player == p.getPlayer() && row == p.getRow() && column == p.getColumn();
    }
    public String toString(){
        return player + "[" + row + "][" +  column + "]";
    }
}