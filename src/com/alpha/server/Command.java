package com.alpha.server;

import java.net.InetAddress;

import com.alpha.server.client_package.ClientThread;

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
          if (rawCommand.equals("exit"))
          {
               outputCommand = null;
          } else
          {
               outputCommand = "{" + rawCommand + "}";
          }
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
