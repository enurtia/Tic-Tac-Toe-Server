package ServerProgram;

public class Game
{
    private User user1;
    private User user2;
    
    private boolean[] user1Rematch;
    private boolean[] user2Rematch;
    
    private String[][] board;
    
    public Game(User user1, User user2)
    {
        //By default, user1 will be U1, and user2 will be U2.
        //These values used in this class do not reflect the values
        //seen by the players. (X's and O's)
        this.user1 = user1;     //U1
        this.user2 = user2;     //U2
        
        user1Rematch = new boolean[]{false, false};     //User replied, User wants rematch
        user2Rematch = new boolean[]{false, false};
        
        board = new String[3][3];
        
        for(int row = 0; row < board.length; row++)     //Instantiate array elements
        {
            for(int col = 0; col < board.length; col++)
            {
                board[row][col] = "";
            }
        }
    }
    
    public int move(User user, int box)
    {
        int row = boxRow(box);
        int col = boxCol(box);
        
        if(!board[row][col].equals(""))     //If space on board is taken, 3 will be returned indicating
        {                                   //an invalid move.
            return 3;               
        }
        else
        {
            if(user == user1)
            {
                board[row][col] = "U1";
            }
            else if(user == user2)
            {
                board[row][col] = "U2";
            }
        }
        
        return boardCheck();    //Check board for wins, losses, or a cat's game
    }
    
    public void rematch(User user, boolean bool)    //Taking in rematch inputs
    {
        if(user == user1)
        {
            user1Rematch[0] = true;
            user1Rematch[1] = bool;
        }
        else if(user == user2)
        {
            user2Rematch[0] = true;
            user2Rematch[1] = bool;
        }
        
        if(user1Rematch[0] && user2Rematch[0])
        {
            if(user1Rematch[1] && user2Rematch[1])      //Rematch
            {   
                //Reset board
                for(int row = 0; row < board.length; row++)
                {
                    for(int col = 0; col < board.length; col++)
                    {
                        board[row][col] = "";
                    }
                }
                
                //Choose who goes first -- Over 0.5, user1 is first, else user2 is first
                boolean u1First = Math.random() > 0.5;
                user1.out("[REMATCH]" + u1First);
                user2.out("[REMATCH]" + !u1First);
            }
            else                                        //No rematch
            {
                user1.resetMatchVariables();
                user2.resetMatchVariables();
                
                user1.out("[NOREMATCH]");
                user2.out("[NOREMATCH]");
            }
        }
    }
    
    public User getUser1()
    {
        return user1;
    }
    
    public User getUser2()
    {
        return user2;
    }
    
    public String[][] getBoard()
    {
        return board;
    }
    
    private int boardCheck()
    {
        //RETURNS
        //0 - Game not finished
        //1 - Inputting user won
        //2 - Cat's game
        //Checking across rows
        for (String[] row : board)
        {
            if (!row[0].equals("") && row[0].equals(row[1]) && row[1].equals(row[2]))
            {
                return 1;   //Return user won
            }
        }
        
        //Checking down columns
        for(int col = 0; col < board[0].length; col++)
        {
            if(!board[0][col].equals("") && board[0][col].equals(board[1][col]) && board[1][col].equals(board[2][col]))
            {
                return 1;   //Return user won
            }
        }
        
        //Checking diagonals
        //Top left to bottom right
        if(!board[0][0].equals("") && board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2]))
        {
            return 1;   //Return user won
        }
        //Bottom left to top right
        if(!board[2][0].equals("") && board[2][0].equals(board[1][1]) && board[1][1].equals(board[0][2]))
        {
            return 1;   //Return user won
        }
        
        //Checking for cat's game
        boolean catsGame = true;
        for (String[] row : board)
        {
            for (int col = 0; col < board[0].length; col++)
            {
                if (row[col].equals(""))
                {
                    catsGame = false;
                    break;
                }
            }
        }
        
        if(catsGame)
        {
            return 2;
        }
        
        return 0;
    }
    
    private static int boxRow(int box)
    {
        if(box <= 3)
        {
            return 0;
        }
        else if(box <= 6)
        {
            return 1;
        }
        else 
        {
            return 2;
        }
    }
    
    private static int boxCol(int box)
    {
        int n = box % 3;
        
        if(n == 1)
        {
            return 0;
        }
        else if(n == 2)
        {
            return 1;
        }
        else
        {
            return 2;
        }
    }
    
    @Override
    public String toString()
    {
        return user1.toString() + " VS. " + user2.toString();
    }
}
