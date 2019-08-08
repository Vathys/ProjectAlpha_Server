package server_package;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class ServerGUI extends JFrame
{
     private static final long serialVersionUID = 1L;

     private JTextArea textArea;
     private static boolean closingServer;
     private CustomListener lis;

     public ServerGUI()
     {
          super("Server");

          lis = new CustomListener();
          closingServer = false;

          
          textArea = new JTextArea();
          textArea.setPreferredSize(new Dimension(500, 200));
          textArea.setEditable(false);
          JScrollPane sp = new JScrollPane(textArea);
          
          add(sp, BorderLayout.CENTER);

          JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
          buttonPane.setPreferredSize(new Dimension(500, 100));

          JButton start = new JButton("Start");
          start.setPreferredSize(new Dimension(200, 100));
          start.addActionListener(lis);
          buttonPane.add(start);
          JButton stop = new JButton("Stop");
          stop.setPreferredSize(new Dimension(200, 100));
          stop.addActionListener(lis);
          buttonPane.add(stop);

          add(buttonPane, BorderLayout.PAGE_END);

          addWindowListener(lis);
          
          setPreferredSize(new Dimension(500, 300));
          setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
          pack();
          setVisible(true);
     }

     public void setText(String text)
     {
          textArea.setText(text + "\n");
     }
     
     public void addText(String text)
     {
          textArea.append(text + "\n");
     }

     public static boolean getServerClosing()
     {
          return closingServer;
     }

     public static void closeServer()
     {
          closingServer = true;
     }
     
     public static void startServer()
     {
          closingServer = false;
     }
}
