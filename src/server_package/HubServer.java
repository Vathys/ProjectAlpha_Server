package server_package;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.net.ServerSocketFactory;

import client_package.ClientThread;

public class HubServer extends Thread
{

     private ServerSocket server;
     //List of connected Clients
     private ArrayList<ClientThread> connectedClients;
     //Private class that waits for clients to connect
     private ClientCollector collector;
     // private ArrayList<ServerThread> connectedServers;

     public HubServer()
     {
          connectedClients = new ArrayList<ClientThread>();
          ServerSocketFactory fact = ServerSocketFactory.getDefault();

          try
          {
               server = fact.createServerSocket(5000);
          } catch (IOException e)
          {
               e.printStackTrace();
          }
          new OutputProcessor(this).start();
          collector = new ClientCollector(this);
          collector.start();
     }

     public void run()
     {
          while (true)
          {
               Command com;
               com = OutputProcessor.takeFromOutputQueue();
               for (ClientThread client : connectedClients)
               {
                    if (!client.getClient().getInetAddress().equals(com.sentFrom()))
                    {
                         client.talkToClient(com.output());
                    }
               }
          }
     }

     public ArrayList<ClientThread> getConnectedClients()
     {
          return connectedClients;
     }

     private void addClient(ClientThread e)
     {
          connectedClients.add(e);
          connectedClients.get(connectedClients.size() - 1).startThreads();
     }

     private class ClientCollector extends Thread
     {
          private HubServer hub;

          public ClientCollector(HubServer hub)
          {
               this.hub = hub;
          }

          public void run()
          {
               try
               {
                    hub.server.getInetAddress();
                    System.out.println("Waiting for client on port " + hub.server.getLocalPort() + " at address " + "192.168.1.86");
                    while (true)
                    {
                         Socket client = null;
                         client = hub.server.accept();

                         System.out.println("Connected to " + client.getRemoteSocketAddress());
                         ClientThread ct = new ClientThread(client);
                         hub.addClient(ct);

                         if (hub.getConnectedClients().size() == 1)
                         {
                              //Initiate starting the document
                         }
                    }
               } catch (IOException e)
               {
                    e.printStackTrace();
               }
          }
     }
}
