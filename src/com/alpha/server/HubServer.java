package com.alpha.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.net.ServerSocketFactory;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.alpha.server.client_package.ClientThread;

public class HubServer extends Thread
{

     private ServerSocket server;
     private ClientCollector collector;
     private OutputProcessor op;
     private ArrayList<ClientThread> connectedClients;
     private PlainDocument pd;
     private File test;

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

          pd = new PlainDocument();
          test = new File(".\\test\\saveTest");

          this.start();
          op.start();
          collector.start();
     }

     public void run()
     {
          while (!ServerGUI.getServerClosing())
          {
               Command com = OutputProcessor.takeFromOutputQueue();

               if (com != null)
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
                              updateFile(com.output());
                         } else
                         {
                              if (client.getClient().getInetAddress().equals(com.sentFrom()))
                              {
                                   toRemove = Integer.valueOf(i);
                              }
                         }
                    }
                    if (toRemove != null)
                    {
                         connectedClients.remove(toRemove.intValue());
                         updateTextArea();
                         System.out.println(com.sentFrom() + " removed");
                    }
               }
               if (connectedClients.size() == 0)
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
          try
          {
               PrintWriter pw = new PrintWriter(test);
               String text = pd.getText(0, pd.getLength());
               text = text.replaceAll("(?!\\r)\\n", "\r\n");

               pw.write(text);

               pw.close();
          } catch (FileNotFoundException e)
          {
               // TODO Auto-generated catch block
               e.printStackTrace();
          } catch (BadLocationException e)
          {
               // TODO Auto-generated catch block
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
          for (int i = 0; i < size; i++)
          {
               text += connectedClients.get(i).getClient().getRemoteSocketAddress().toString() + "\n";
          }
          Main.gui.setText(text);
     }

     private void updateFile(String com)
     {
          ArrayList<String> check = RegexParser.matches("\\[([+|-])\\]\\[off(\\d+)\\]\\[len(\\d+)\\]\"(.*?)\"", com);
          /*
          for(int i = 1; i < check.size(); i++)
          {
               System.out.println(i + ": " + check.get(i));
          }
          */
          int offset = Integer.valueOf(check.get(2)).intValue();
          int length = Integer.valueOf(check.get(3)).intValue();
          String str = check.get(4);

          //"\n\n1" length = 3: str.length(): 15
          if (str.length() != length && !str.equals(""))
          {
               String temp = str;
               int n = str.length() - length;
               n = n / 6;

               int[] offsetArr = new int[n];

               for (int i = 0; i < n; i++)
               {
                    offsetArr[i] = temp.indexOf("newLine");
                    temp = str.substring(offsetArr[i] + 7);
               }

               if (n == 1)
               {
                    str = str.substring(0, offsetArr[0]) + "\n" + str.substring(offsetArr[0] + 7);
               } else
               {
                    String temp2 = str.substring(0, offsetArr[0]);
                    for (int i = 1; i < n; i++)
                    {
                         temp2 += "\n" + str.substring(offsetArr[0] + 7, offsetArr[i]);
                    }
                    temp2 += str.substring(offsetArr[n - 1] + 7);
                    str = temp2;
               }
          }

          try
          {
               if (check.get(1).equals("+"))
               {
                    pd.insertString(offset, str, null);
               } else if (check.get(1).equals("-"))
               {
                    pd.remove(offset, length);
               }
          } catch (BadLocationException e)
          {
               e.printStackTrace();
          }
     }

     private void updateClient(ClientThread c)
     {
          try
          {
               String[] content = pd.getText(0, pd.getLength()).split("\\n");
               
               int off = 0;

               for (int i = 0; i < content.length; i++)
               {
                    String msg = "";

                    msg += "[+]";

                    msg += "[off" + off + "]";
                    msg += "[len" + content[i].length();
                    
                    if(i != content.length - 1)
                    {
                         msg += "newLine]";
                    }
                    else
                    {
                         msg += "]";
                    }

                    msg += "\"" + content[i] + "\"";

                    c.talkToClient(msg);
                    off += content[i].length() + 1;
               }
          } catch (BadLocationException e)
          {
               e.printStackTrace();
          }
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
                         hub.updateClient(ct);
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
