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
     private ClientCollector collector;
     private OutputProcessor op;
     private ArrayList<ClientThread> connectedClients;

     public HubServer(ServerGUI handle)
     {
          op = new OutputProcessor(this);
          collector = new ClientCollector(this);
          connectedClients = new ArrayList<ClientThread>();
          ServerSocketFactory fact = ServerSocketFactory.getDefault();
          
          try
          {
               server = fact.createServerSocket(5000);
          } catch (IOException e)
          {
               e.printStackTrace();
          }
          
          this.start();
          op.start();
          collector.start();
     }
     
     public void run()
     {
          while (!ServerGUI.getServerClosing())
          {
               Command com = OutputProcessor.takeFromOutputQueue();
               
               if(com != null)
               {
                    Integer toRemove = null;
                    
                    int size = connectedClients.size();
                    
                    for (int i = 0; i < size; i++)
                    {
                         ClientThread client = connectedClients.get(i);
                         if (com.output() != null)
                         {
                              if (!client.getClient().getInetAddress().equals(com.sentFrom()))
                              {
                                   client.talkToClient(com.output());
                              }
                         }else
                         {
                              if(client.getClient().getInetAddress().equals(com.sentFrom()))
                              {
                                   toRemove = Integer.valueOf(i);
                              }
                         }
                    }
                    if(toRemove != null)
                    {
                         connectedClients.remove(toRemove.intValue());
                         updateTextArea();
                         System.out.println(com.sentFrom() + " removed");
                    }
               }
               if(connectedClients.size() == 0)
               {
                    Main.gui.setText("No clients connected");
               }
          }
          try
          {
               server.close();
          } catch (IOException e)
          {
               e.printStackTrace();
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

     private void updateTextArea()
     {
          int size = connectedClients.size();
          String text = "";
          for(int i = 0; i < size; i++)
          {
               text += connectedClients.get(i).getClient().getRemoteSocketAddress().toString() + "\n";
          }
          Main.gui.setText(text);
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
                    Main.gui.addText("Waiting for client on port " + hub.server.getLocalPort() + " at address " + "192.168.1.86");
                    while (!ServerGUI.getServerClosing())
                    {
                         Socket client = null;
                         client = hub.server.accept();

                         System.out.println("Connected to " + client.getRemoteSocketAddress());
                         ClientThread ct = new ClientThread(client);
                         hub.addClient(ct);
                         updateTextArea();
                    }
               } catch (IOException e)
               {
                    Main.gui.addText("Not accepting any Clients now...");
                    //e.printStackTrace();
               }
          }
     }
}
