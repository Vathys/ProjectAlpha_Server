package com.alpha.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OutputProcessor extends Thread
{

     private static BlockingQueue<Command> clientOutputQueue;
     private static BlockingQueue<Command> clientInputQueue;

     public OutputProcessor(HubServer hub)
     {
          clientOutputQueue = new LinkedBlockingQueue<Command>();
          clientInputQueue = new LinkedBlockingQueue<Command>();
          Main.gui.addText("Queues Active");
     }

     public void run()
     {
          while (!ServerGUI.getServerClosing())
          {
               Command out = clientInputQueue.poll();
               if(out != null)
               {
                    out.process();
                    
                    clientOutputQueue.add(out);
               }
          }
     }

     public static void addToOutputQueue(Command com)
     {
          clientOutputQueue.add(com);
     }

     public static void addToInputQueue(Command input)
     {
          clientInputQueue.add(input);
     }

     public static BlockingQueue<Command> getInputQueue()
     {
          return clientInputQueue;
     }

     public static Command takeFromInputQueue()
     {
          return clientInputQueue.poll();
     }

     public static Command takeFromOutputQueue()
     {
          return clientOutputQueue.poll();
     }
}
