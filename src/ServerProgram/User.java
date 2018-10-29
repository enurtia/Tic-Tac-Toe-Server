package ServerProgram;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

public class User
{
    private DefaultListModel<User> userModel;
    private Socket socket;
    private String username;
    private String address;
    private ServerListener listener;
    private ServerFrame serverFrame;
    private PrintWriter output;
    private JTextArea console;
    
    private User requestUser;       //When the user is requesting another or is being requested, this variable will represent
                                    //that other user's User object.
    
    //Game Variables
    private boolean isIngame;
    private User opponent;
    private Game game;
    private String shape;
    
    public User(ServerFrame serverFrame, Socket socket) throws IOException
    {
        this.serverFrame = serverFrame;
        requestUser = null;
        userModel = serverFrame.getUserModel();
        this.socket = socket;
        username = "Unnamed User";
        address = socket.getRemoteSocketAddress().toString().replace("/","");
        output = new PrintWriter(socket.getOutputStream());
        game = null;
        isIngame = false;
        console = serverFrame.getTextArea();
        shape = "";
    }

    public void startListener() throws IOException
    {
        listener = new ServerListener(serverFrame, this);
        listener.execute();
    }

    public void setShape(String s)
    {
        shape = s;
    }
    
    public String getShape()
    {
        return shape;
    }
    
    public void out(String s)
    {
        output.println(s);
        output.flush();
    }
    
    public Socket getSocket()
    {
        return socket;
    }
    
    public void updateUserList()
    {
        try
        {
            String list = userModel.toString().replaceAll("\\[|\\]", "");   //Removes square brackets
            for(int i = 0; i < userModel.size(); i++)
            {
                userModel.get(i).out("[USERLIST]" + list);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public boolean isUniqueUsername(String s)   //Searches user model to return whether the username exists or not.
    {                                           //If it's unique, true will be returned, false otherwise.
        boolean isUnique = true;
        for(int i = 0; i < userModel.size(); i++)
        {
            String user = userModel.get(i).toString();
            if(user.equals(s))
            {
                isUnique = false;
                break;
            }
        }
        
        return isUnique;
    }
    
    public void resetMatchVariables()
    {
        //Remove game object from gameModel if not removed already.
        DefaultListModel<Game> gameModel = serverFrame.getGameModel();
        if(gameModel.contains(game))
        {
            gameModel.removeElement(game);
        }
        
        //Reset match variables
        isIngame = false;
        opponent = null;
        game = null;
        shape = "";
    }
    
    public Game getGame()
    {
        return game;
    }
    
    public void setGame(Game game)
    {
        this.game = game;
    }
    
    public User getOpponent()
    {
        return opponent;
    }
    
    public void setOpponent(User user)
    {
        opponent = user;
    }
    
    public User getRequestUser()
    {
        return requestUser;
    }
    
    public void setRequestUser(User user)
    {
        requestUser = user;
    }
    
    public boolean isIngame()
    {
        return isIngame;
    }
    
    public void setIngame(boolean bool)
    {
        isIngame = bool;
    }
    
    public void setUsername(String s)
    {
        boolean exists = false;
        if(username.equals("") && !s.equals(""))
        {
            for(int i = 0; i < userModel.size(); i++)
            {
                String user = userModel.get(i).toString();
                if(user.equals(s))
                {
                    exists = true;
                    break;
                }
            }
        }
        
        if(!exists)
        {
            username = s;
        }
    }
    
    public void remove()
    {
        try
        {
            listener.cancel(true);
            socket.close();
            userModel.removeElement(this);
            String disconnectName = ((username.equals("")) ? address : username);
            console.setText(console.getText() + disconnectName + " disconnected." + "\n");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public String toString()
    {
        return username;
    }
}
