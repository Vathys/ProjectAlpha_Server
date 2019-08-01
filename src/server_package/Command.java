package server_package;

import java.net.InetAddress;
import java.util.ArrayList;

import client_package.ClientThread;

public class Command
{
     private String rawCommand;
     private ClientThread sentFrom;
     private String outputCommand;
     
     public Command(ClientThread c, String rawCommand)
     {
          this.sentFrom = c;
          this.rawCommand = rawCommand;
     }
     
     public void process()
     {
          outputCommand = rawCommand;
     }

     public InetAddress sentFrom()
     {
          return sentFrom.getClient().getInetAddress();
     }
     
     public String output()
     {
          return outputCommand;
     }
}
