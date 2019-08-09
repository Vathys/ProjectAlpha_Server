package com.alpha.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class CustomListener implements WindowListener, ActionListener
{

     @Override
     public void actionPerformed(ActionEvent e)
     {
          if(e.getActionCommand().equals("Start"))
          {
               Main.gui.addText("Starting Server");
               Main.startServer();
          }
          else if(e.getActionCommand().equals("Stop"))
          {
               Main.gui.addText("Closing Server");
               ServerGUI.closeServer();
          }
     }

     @Override
     public void windowOpened(WindowEvent e)
     {
          // TODO Auto-generated method stub

     }

     @Override
     public void windowClosing(WindowEvent e)
     {
          ServerGUI.closeServer();
     }

     @Override
     public void windowClosed(WindowEvent e)
     {
          // TODO Auto-generated method stub

     }

     @Override
     public void windowIconified(WindowEvent e)
     {
          // TODO Auto-generated method stub

     }

     @Override
     public void windowDeiconified(WindowEvent e)
     {
          // TODO Auto-generated method stub

     }

     @Override
     public void windowActivated(WindowEvent e)
     {
          // TODO Auto-generated method stub

     }

     @Override
     public void windowDeactivated(WindowEvent e)
     {
          // TODO Auto-generated method stub

     }

}
