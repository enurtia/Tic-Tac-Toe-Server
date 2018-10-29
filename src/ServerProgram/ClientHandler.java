package ServerProgram;

import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.DefaultListModel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class ClientHandler extends SwingWorker<Boolean, String>
{
    private DefaultListModel<User> userModel;
    private int port;
    private JTextArea txtArea;
    private ServerFrame serverFrame;
    
    public ClientHandler(ServerFrame serverFrame, int port)
    {
        this.serverFrame = serverFrame;
        txtArea = serverFrame.getTextArea();
        userModel = serverFrame.getUserModel();
        
        this.port = port;
    }
    
    @Override
    public Boolean doInBackground()
    {
        try
        {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true)
            {
                txtArea.setText(txtArea.getText() + "ACCEPTING NEW CLIENTS." + "\n" );
                Socket socket = serverSocket.accept();
                txtArea.setText(txtArea.getText() + "CLIENT ACCEPTED" + "\n" );
                
                User user = new User(serverFrame, socket);
                userModel.addElement(user);
                user.startListener();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return true;
    }
}
