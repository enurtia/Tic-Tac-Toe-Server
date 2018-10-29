package ServerProgram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.DefaultListModel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class ServerListener extends SwingWorker<Integer, String>
{
    private BufferedReader input;
    private User user;
    private JTextArea txtArea;
    private ServerFrame serverFrame;
    private DefaultListModel<User> userModel;
    
    public ServerListener(ServerFrame serverFrame, User user) throws IOException
    {
        this.serverFrame = serverFrame;
        this.user = user;
        txtArea = serverFrame.getTextArea();
        
        InputStream in = user.getSocket().getInputStream();
        input = new BufferedReader(new InputStreamReader(in));
        
        userModel = serverFrame.getUserModel();
    }

    @Override
    public Integer doInBackground()
    {
        try
        {
            while(true)     //While true, readLine from BufferedReader and analyze the input
            {
                String received = input.readLine();
                analyzeInput(received);
                txtArea.setText(txtArea.getText() + received + "\n");
            }
        }
        catch(IOException e)
        {                   //Removes the user that disconnected, updates user model, and alerts 
            user.remove();  //all other users of the change.
            userModel.lastElement().updateUserList();
            
            //If user disconnected while ingame, alert opponent.
            User opponent = user.getOpponent();
            if(opponent != null)
            {
                opponent.out("[OPPONENTDISCONNECT]");
            }
            
            
            //Remove game from gameModel
            serverFrame.getGameModel().removeElement(user.getGame());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return 1;
    }
    
    private void analyzeInput(String s)
    {
        switch(tag(s))
        {
            case "USERNAMECHECK":
            {
                if(user.isUniqueUsername(info(s)))
                {
                    user.setUsername(info(s));
                    user.out("[USERNAMECHECK]true");
                    user.updateUserList();
                }
                else
                {
                    user.out("[USERNAMECHECK]false");
                }
                break;
            }
            case "GLOBALCHAT":
            {
                for(int i = 0; i < userModel.size(); i++)
                {
                    userModel.get(i).out("[GLOBALCHAT]" + user.toString() + ": " + info(s));
                }
                break; 
            }
            case "GAMECHAT":
            {
                User opponent = user.getOpponent();
                if(opponent != null)
                {
                    user.out("[GAMECHAT]" + user.toString() + ": " + info(s));
                    opponent.out("[GAMECHAT]" + user.toString() + ": " + info(s));
                }
                break;
            }
            case "REQUEST":
            {
                User reqUser = null;    //Requested user
                String requestedUsername = info(s);
                for(int i = 0; i < userModel.size(); i++)   //Searches the user model for a User object matching the
                {                                           //username in the input -- info(s)
                    if(userModel.get(i).toString().equals(requestedUsername))
                    {
                        reqUser = userModel.get(i);
                        break;
                    }
                }
                
                if(reqUser != null && reqUser.getRequestUser() == null && !reqUser.isIngame())
                {       //User can be requested
                    reqUser.out("[REQUEST]" + user.toString());
                    reqUser.setRequestUser(user);
                    user.setRequestUser(reqUser);
                }
                else    //User can't be requested
                {
                    user.out("[REQUESTFAILED]");
                }
                break;
            }
            case "REQUESTDENIED":
            {
                //Reset all "requesting" variables when request is denied.
                User reqBy = user.getRequestUser();
                reqBy.setRequestUser(null);
                user.setRequestUser(null);
                reqBy.out("[REQUESTDENIED]");
                break;
            }
            case "REQUESTACCEPTED":
            {
                //Game Creation
                
                //Reset all "requesting" variables when request is accepted.
                //Set both users' "ingame" variables to true.
                User reqBy = user.getRequestUser();
                reqBy.setIngame(true);
                user.setIngame(true);
                reqBy.setRequestUser(null);
                user.setRequestUser(null);
                
                //Set opponent variables
                user.setOpponent(reqBy);
                reqBy.setOpponent(user);
                
                String outUser = "[BEGINGAME]";     //Variables set for building what will be outputted to users
                String outReqBy = "[BEGINGAME]";    //
                //info split by #. 0 is X. 1 is O. 2 is Turn
                //Choose X and O  -- Over 0.5, X is user, else is reqBy
                boolean userIsX = Math.random() > 0.5;
                //Setting X
                outUser += userIsX + "#";
                outUser += !userIsX + "#";

                //Setting O
                outReqBy += !userIsX + "#";
                outReqBy += userIsX + "#";
                
                //Set shapes in User classes
                user.setShape(userIsX ? "X" : "O");
                reqBy.setShape(userIsX ? "O" : "X");
                
                //Choose who goes first -- Over 0.5, user is first, else reqBy is first
                boolean userIsFirst = Math.random() > 0.5;
                outUser += userIsFirst;
                outReqBy += !userIsFirst;
                
                //Set game variables for users
                Game game = new Game(user, reqBy);
                user.setGame(game);
                reqBy.setGame(game);
                
                DefaultListModel<Game> gameModel = serverFrame.getGameModel();
                gameModel.addElement(game);
                
                //Output building variables.
                user.out(outUser);
                reqBy.out(outReqBy);
                
                break;
            }
            case "MOVE":
            {
                User opponent = user.getOpponent();
                Game game = user.getGame();
                int box = Integer.parseInt(info(s));
                
                //Check move before sending outputs.
                int checkNum = game.move(user, box);
                
                switch(checkNum)
                {
                    case(0):
                    {
                        //Outputting move if valid and game continued.
                        user.out("[MOVE]" + box);
                        opponent.out("[OPPONENTMOVE]" + box);
                        break;
                    }
                    case(1):    //User won, opponent lost.
                    {
                        user.out("[WINMOVE]" + box);
                        opponent.out("[LOSSOPPONENTMOVE]" + box);
                        break;
                    }
                    case(2):    //Cat's Game
                    {
                        user.out("[CATSGAME]" + box);
                        opponent.out("[OPPONENTCATSGAME]" + box);
                        break;
                    }
                }//End nested switch
                break;
            }
            case "REMATCH":
            {
                if(user.getGame() != null)
                {
                    user.getGame().rematch(user, true);
                }
                break;
            }
            case "NOREMATCH":
            {
                if(user.getGame() != null)
                {
                    user.getGame().rematch(user, false);
                }
                break;
            }
        }//End Switch
    }
    
    private String tag(String s)
    {
        int startIndex = s.indexOf("[");
        int endIndex = s.indexOf("]");
        
        return s.substring(startIndex + 1, endIndex );  //Returns tag from input
    }
    
    private String info(String s)
    {
        int startIndex = s.indexOf("]");
        int endIndex = s.length();
        
        return s.substring(startIndex + 1, endIndex);   //Returns info from input
    }
}
