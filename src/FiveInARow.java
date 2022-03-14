import java.util.ArrayList;
import java.util.Random;

class FiveInARow{
	private static final int PIECES_IN_A_ROW = 2;

	private static final char PLAYER_X = 'x';
	private static final char PLAYER_O = 'o';
	private static final int MINIMAX_SEARCH_DEPTH = 3;

	private final InputManager input = new InputManager();
	private final Random rng = new Random();
	private final Board board = new Board(2);

	public void start(){
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

	private enum CONTROLLER {RANDOM, MINIMAX, HUMAN, ALPHA_BETA}

	/**
	 * Asks the user what player will be played by who and returns the answer as an CONTROLLER enum.
	 * @param player the player in question.
	 * @return the users' answer to the question.
	 */
	private CONTROLLER getPlayer(char player){
		System.out.println("Options: [R]andom AI, [M]inimax AI, [A]lpha-Beta, [H]uman.");
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
			case 'A':
			case 'a':
				return CONTROLLER.ALPHA_BETA;
			default:
				System.out.println("Error: Not an option!");
		}
		return null;
	}

	/**
	 * Main game loop for the game. Will continously loop until one of the players win.
	 * @param controllerX the controller for player X.
	 * @param controllerO the controller for player O.
	 */
	public void gameLoop(CONTROLLER controllerX, CONTROLLER controllerO){
		board.resetBoard();
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
			else if(board.availableCapacity() == 0){
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
			if(board.availableCapacity() == 0){
				congratulate('d');
				return;
			}
		}
	}

	/**
	 * This method will call for the correct method depending on what controller is being used; ask (or calculate),
	 * create, place and return that Piece.
	 * @param controller what controller is doing this action.
	 * @param player what player the Piece will represent.
	 * @return the Piece itself.
	 */
	private Piece getPiece(CONTROLLER controller, char player) {
		//Update the board.
		board.drawBoard();

		player = player == 'x' ? 'x' : 'o';
		System.out.println("Player " + player + "'s turn.");

		Piece piece = null;
		if(controller == CONTROLLER.RANDOM){piece = randomAIController(player);}
		else if(controller == CONTROLLER.MINIMAX){piece = minimaxAIController(player);}
		else if(controller == CONTROLLER.HUMAN){piece = humanController(player);}
		else if(controller == CONTROLLER.ALPHA_BETA){piece = alphaBetaAIController(player);}

		if (piece != null) {
			System.out.println("Placed it at: [" + (piece.getRow() + 1) + "][" + (piece.getColumn() + 1) + "]");
		}
		return piece;
	}

	/**
	 * Implementation of the Minimax algorithm.
	 * @param player what player is making the move.
	 * @return the most optimal move.
	 */
	private Piece minimaxAIController(char player){
		//TODO possible improvements
		// Make the first move random or semi-random.
		// Using a stack of all available spots to test each position

		System.out.println("Controller: Making a move!");

		int bestValue = Integer.MIN_VALUE;
		Piece bestPlacement = null;

		System.out.println("Controller: Looping through all available spots!");
		for(int i = 0; i < board.getSize(); i++){
			for(int j = 0; j < board.getSize(); j++){
				if(board.get(i, j) == null){
					System.out.println("Controller: Found available spot!");
					//Make the move, check how good it is and undo it.
					Piece tmpPiece = new Piece(player, i, j);
					board.set(tmpPiece);
					int currentValue = minimax(tmpPiece, 0, false);
					board.remove(i, j);

					System.out.println("Controller: Undid the move!");
					if(currentValue > bestValue){
						System.out.println("Controller: Updated best value!");
						bestPlacement = new Piece(player, i, j);
						bestValue = currentValue;
					}
				}
			}
		}
		if(bestPlacement != null){
			board.set(bestPlacement);
			System.out.println("Value of the most optional move: " + bestValue +
					", position: [" + (bestPlacement.getRow() + 1) + "][" + (bestPlacement.getColumn() + 1) + "]");
		}
		return bestPlacement;
	}

	/**
	 * Recursive call of the testing part of the Minimax algorithm.
	 * @param piece what piece is being tested.
	 * @param depth the depth of the calculation, how far it will go with the calculation.
	 * @param isMaxPlayer whether or not it should attempt to maximize or minimize the chance of a loss.
	 * @return the score of the tested move.
	 */
	private int minimax(Piece piece, int depth, boolean isMaxPlayer){
		System.out.println("Minimax: Starting evaluation!");
		int score = evaluate(piece.getPlayer());
		if(score == 100){return score - depth * 10;}
		if(score == -100){return score + depth * 10;}
		if(board.availableCapacity() == 0){return 0;}
		if(depth == MINIMAX_SEARCH_DEPTH){return 0;}
		//Repeating code, could maybe be extract into a method.

		int bestValue;
		int currentValue;
		Piece currentPiece = null;
		System.out.println("Minimax: Looping through all available spots! Depth: " + depth);
		if(isMaxPlayer){
			bestValue = Integer.MIN_VALUE;

			for(int i = 0; i < board.getSize(); i++){
				for(int j = 0; j < board.getSize(); j++){
					if(board.get(i, j) == null){
						System.out.println("Minimax: Found available spot!");
						currentPiece = new Piece(piece.getPlayer(), i, j);
						board.set(piece);
						currentValue = minimax(currentPiece, depth + 1, false);
						bestValue = max(bestValue, currentValue);
						board.remove(i, j);
					}
				}
			}
		}
		else{
			bestValue = Integer.MAX_VALUE;

			for(int i = 0; i < board.getSize(); i++){
				for(int j = 0; j < board.getSize(); j++){
					if(board.get(i, j) == null){
						System.out.println("Minimax: Found available spot!");
						currentPiece = new Piece(piece.getPlayer() == 'x' ? 'o' : 'x', i, j);
						board.set(piece);
						currentValue = minimax(currentPiece, depth + 1, true);
						bestValue = max(bestValue, currentValue);
						board.remove(i, j);
					}
				}
			}
		}
		System.out.println("Minimax: Returning " + bestValue + "!");
		return bestValue;
	}

	private int evaluate(char player) {
		System.out.println("Evaluator: Evaluating!");
		for(int i = 0; i < board.getSize(); i++){
			for(int j = 0; j < board.getSize(); j++){
				if(board.get(i, j) != null){
					Piece piece = board.get(i, j);
					int diagonalTopLeft = countConcurrentPieces(piece, -1, 1);
					int diagonalTopRight = countConcurrentPieces(piece, 1, 1);
					int horizontal = countConcurrentPieces(piece, 1, 0);
					int vertical = countConcurrentPieces(piece, 0, 1);

					int concurrentPieces = max(diagonalTopLeft, diagonalTopRight, horizontal, vertical);
					if(concurrentPieces >= PIECES_IN_A_ROW){
						if(piece.getPlayer() == player){
							System.out.println("Evaluator: Finished evaluating, returning 100!");
							return 100;
						}
						else{
							System.out.println("Evaluator: Finished evaluating, returning -100!");
							return -100;
						}
					}
				}
			}
		}
		System.out.println("Evaluator: Finished evaluating, returning 0!");
		return 0;
	}

	private Piece alphaBetaAIController(char player){
		System.out.println("Controller: Making a move!");

		int bestValue = Integer.MIN_VALUE;
		Piece bestPlacement = null;

		System.out.println("Controller: Looping through all available spots!");
		for(int i = 0; i < board.getSize(); i++){
			for(int j = 0; j < board.getSize(); j++){
				if(board.get(i, j) == null){
					System.out.println("Controller: Found available spot!");
					//Make the move, check how good it is and undo it.
					Piece tmpPiece = new Piece(player, i, j);
					board.set(tmpPiece);
					int currentValue = alphaBeta(tmpPiece, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
					board.remove(i, j);

					System.out.println("Controller: Undid the move!");
					if(currentValue > bestValue){
						System.out.println("Controller: Updated best value!");
						bestPlacement = new Piece(player, i, j);
						bestValue = currentValue;
					}
				}
			}
		}
		if(bestPlacement != null){
			board.set(bestPlacement);
			System.out.println("Value of the most optional move: " + bestValue +
					", position: [" + (bestPlacement.getRow() + 1) + "][" + (bestPlacement.getColumn() + 1) + "]");
		}
		return bestPlacement;
	}

	private int alphaBeta(Piece piece, int depth, boolean isMaxPlayer, int alpha, int beta){
		if(depth == MINIMAX_SEARCH_DEPTH || board.availableCapacity() == 0){
			return scoreEvaluation(piece.getPlayer());
		}

		int score;
		if(isMaxPlayer){
			score = Integer.MIN_VALUE;

			for(int i = 0; i < board.getSize(); i++){
				for(int j = 0; j < board.getSize(); j++){
					if(board.get(i, j) == null){
						Piece tmpPiece = new Piece(piece.getPlayer(), i, j);
						board.set(tmpPiece);
						score = max(score, alphaBeta(tmpPiece, depth + 1, false, alpha, beta));
						board.remove(i, j);
						if(score >= beta){break;}
						alpha = max(alpha, score);
					}
				}
			}
		}
		else{
			score = Integer.MAX_VALUE;

			for(int i = 0; i < board.getSize(); i++){
				for(int j = 0; j < board.getSize(); j++){
					if(board.get(i, j) == null){
						Piece tmpPiece = new Piece(piece.getPlayer() == 'x' ? 'o' : 'x', i, j);
						board.set(tmpPiece);
						score = Math.min(score, alphaBeta(board.get(i, j), depth + 1, true, alpha, beta));
						board.remove(i, j);
						if(score <= alpha){break;}
						beta = Math.min(beta, score);
					}
				}
			}
		}
		return score;
	}

	private int scoreEvaluation(char player) {
		return score(player) - score(player == 'x' ? 'o' : 'x');
	}

	private int score(char player){
		int output = 0;
		/*for(int i = 0; i < board.getSize(); i++){
			for(int j = 0; j < board.getSize(); j++){
				Piece piece = board.get(i, j);
				if(piece != null && piece.getPlayer() == player){
					output += countConcurrentPieces(piece, -1, 1);
					output += countConcurrentPieces(piece, 1, 1);
					output += countConcurrentPieces(piece, 1, 0);
					output += countConcurrentPieces(piece, 0, 1);
				}
			}
		}*/
		return output;
	}

	private Piece randomAIController(char player){
		int row;
		int column;
		boolean spotAvailable;

		do{
			row = rng.nextInt(board.getSize());
			column = rng.nextInt(board.getSize());

			spotAvailable = board.get(row, column) == null;
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

			spotAvailable = board.get(row, column) == null;
			if(!spotAvailable){System.out.println("Error: You can't place it there, another piece is already there!");}
		}while(!spotAvailable);

		return createPiece(player, row, column);
	}
	
	private int limitRange(String question){
		int placement;
		do{
			placement = input.getInt(question);
			placement--;
			if(placement >= board.getSize() || placement < 0){System.out.println("Error: Your placement can not be outside the board!");}
		}while(placement >= board.getSize() || placement < 0);
		return placement;
	}
	
	private Piece createPiece(char player, int row, int column){
		Piece piece = new Piece(player, row, column);
		board.set(piece);
		return piece;
	}
	
	private boolean checkWin(Piece piece){
		//Doesn't work, don't have the time to look into why. Only exists for the small preformance boost.
		//if(pieces <= PIECES_IN_A_ROW * 2 - 2){return false;}

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
		while(withinBorder(deltaRow, deltaColumn) &&
				board.get(deltaRow, deltaColumn) != null &&
				board.get(deltaRow, deltaColumn).getPlayer() == player){
			concurrentPieces++;
			pieces.add(board.get(deltaRow, deltaColumn));

			deltaRow += dirY;
			deltaColumn += dirX;
		}

		//Could possibly just subtract the current row and column values with concurrentPieces.
		deltaRow = piece.getRow() - dirY;
		deltaColumn = piece.getColumn() - dirX;

		while(withinBorder(deltaRow, deltaColumn) &&
				board.get(deltaRow, deltaColumn) != null &&
				board.get(deltaRow, deltaColumn).getPlayer() == player){
			concurrentPieces++;
			pieces.add(board.get(deltaRow, deltaColumn));

			deltaRow -= dirY;
			deltaColumn -= dirX;
		}

		//Makes the winning line capitalized.
		if(concurrentPieces >= PIECES_IN_A_ROW){
			for(Piece p : pieces){
				board.remove(p.getRow(), p.getColumn());
				Piece tmpPiece = new Piece(piece.getPlayer() == 'x' ? 'X' : 'O', p.getRow(), p.getColumn());
				board.set(tmpPiece);
			}
		}

		return concurrentPieces;
	}

	private boolean withinBorder(int row, int column) {
		return row >= 0 && row < board.getSize() && column >= 0 && column < board.getSize();
	}

	private void congratulate(char player){
		if(player == 'd'){
			System.out.println("YOU'VE DRAWN! CONGRATULATIONS! YOU'VE DRAWN! YOU'VE DRAWN!");
		}
		else{
			player = player == 'x' ? 'X' : 'O';
			System.out.println("PLAYER " + player + " WON! CONGRATULATIONS! PLAYER " + player + " WON! PLAYER" + player + " WON!");
		}
		board.drawBoard();
	}
}