package server_package;

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
          System.out.println("Queues Active");
     }

     public void run()
     {
          while (true)
          {
               Command out;
               try
               {
                    out = clientInputQueue.take();

                    out.process();

                    clientOutputQueue.add(out);
               } catch (InterruptedException e)
               {
                    e.printStackTrace();
               }
          }
     }

     public static synchronized void addToOutputQueue(Command com)
     {
          clientOutputQueue.add(com);
     }

     public static void addToInputQueue(Command input)
     {
          clientInputQueue.add(input);
     }

     public synchronized static BlockingQueue<Command> getInputQueue()
     {
          return clientInputQueue;
     }

     public synchronized static Command takeFromInputQueue()
     {
          try
          {
               return clientInputQueue.take();
          } catch (InterruptedException e)
          {
               e.printStackTrace();
          }
          return null;
     }

     public static Command takeFromOutputQueue()
     {
          try
          {
               return clientOutputQueue.take();
          } catch (InterruptedException e)
          {
               e.printStackTrace();
          }
          return null;
     }
}
