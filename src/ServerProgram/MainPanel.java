package ServerProgram;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;


public class MainPanel extends JPanel implements ActionListener
{
    final private int tick = 100;
    
    private Timer timer;
    private Game game;
    
    private User user1;
    private User user2;
    private String[][] board;
    
    public MainPanel()
    {
        game = null;
        user1 = null;
        user2 = null;
        board = null;
        
        timer = new Timer(tick, this);
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);        
        setBackground(Color.BLACK); 
        
        if(game != null)
        {
            Graphics2D g1 = (Graphics2D)g;
            g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Font font = g.getFont().deriveFont((float)75);
            g1.setFont(font);

            drawBoard(g1);
        }
    }        
       
    @Override
    public void actionPerformed(ActionEvent e)  //Called by timer every tick
    {
        repaint();
    }
    
    public void setGame(Game game)  //If game is not null, begin timer and panel drawing
    {
        this.game = game;
        if(game == null)
        {
            timer.stop();
            board = null;
            user1 = null;
            user2 = null;
            repaint();
        }
        else 
        {
            user1 = game.getUser1();
            user2 = game.getUser2();
            
            timer.start();
        }
    }
    
    private void drawBoard(Graphics2D g1)
    {
        board = game.getBoard();
        String u1Shape = user1.getShape();
        String u2Shape = user2.getShape();
        
        //Game object's board array does not include X and O
        //so when iterated, it's values must be drawn properly.
        //This is done by establishing which user is X and O
        //through u1Shape and u2Shape
        
        for(int row = 0; row < board.length; row++)
        {
            for(int col = 0; col < board[0].length; col++)
            {
                String userShape = "";
                if(board[row][col].equals("U1"))
                {
                    userShape = u1Shape;
                }
                else if(board[row][col].equals("U2"))
                {
                    userShape = u2Shape;
                }
                int xCoord = (col * 75) + 25;   //Columns
                int yCoord = (row * 75) + 75;   //Rows
                    
                g1.setColor(userShape.equals("X") ? Color.MAGENTA : Color.CYAN);
                g1.drawString(userShape, xCoord, yCoord);
            }
        }
    }
}
