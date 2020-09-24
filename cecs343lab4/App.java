/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cecs343lab4;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.net.MalformedURLException;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sandra Chavez 
 * 
 */
public class App extends JFrame {

    BasicPlayer player;
    JTable table;
    JScrollPane scrollPane; 
    int CurrentSelectedRow;
    JButton PlayButton; 
    ButtonListener bl;
    JPanel main;
    
    public App(){
        player = new BasicPlayer();
        bl = new ButtonListener();
        PlayButton = new JButton("Play");
        PlayButton.addActionListener(bl);
        PlayButton.setPreferredSize(new Dimension(100,25));
        PlayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        main = new JPanel();
        //colums labels the columns in the table
        //if the table is not in a scrollpane the column header will not show
        //it would have to be added separately
        String[] columns = {"Stations", "Description"};
        //data holds the table data and maps as a 2d array into the table
        Object[][] data = {{"https://mp3.ffh.de/ffhchannels/hqrock.mp3","Rock"},
                           {"https://mp3.ffh.de/ffhchannels/hq80er.mp3","The 80s"},
                           {"https://mp3.ffh.de/ffhchannels/hqdeutsch.mp3","Per Deutsch"},
                           {"https://mp3.ffh.de/ffhchannels/hq90er.mp3", "The 90s"}};
        
        //create the table
        table = new JTable(data, columns);
        //embed the listener in the declaration

        MouseListener mouseListener = new MouseAdapter() {
            //this will print the selected row index when a user clicks the table
            public void mousePressed(MouseEvent e) {
               CurrentSelectedRow = table.getSelectedRow();
               System.out.println("Selected index = " + CurrentSelectedRow);
            }
        };
        //assign the listener
        table.addMouseListener(mouseListener);
        


        //change some column's width
        //first get the column from the column model from the table
        //column 0 is the leftmost - make it 250 pixels
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(250);
        //using the same TableColumn variable set the "Year" coulumn to 20
        column = table.getColumnModel().getColumn(1); //Year is column 3
        column.setPreferredWidth(20);
        
        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(475,100));

        
        main.setSize(700,500);
        
        main.add(scrollPane);
        main.add(PlayButton);
        this.setTitle("Lab 4 Stream Player by Sandra Chavez");
        this.add(main);
        this.setSize(500, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
    
    class ButtonListener implements ActionListener {
    @Override
        public void actionPerformed(ActionEvent e) {
            String url=null;
            String station = null;
            
            // gets the element from first row in selected column 
            url = (String)table.getValueAt(CurrentSelectedRow, 0); 
            station = (String)table.getValueAt(CurrentSelectedRow, 1);
            System.out.println(station);
            
            try {

        player.open(new URL(url));

        player.play();

    } 
    catch (MalformedURLException ex) {
     Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
    System.out.println("Malformed url");

    }
    catch (BasicPlayerException ex) {

    System.out.println("BasicPlayer exception");

    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);

}
            

        
        }
    }

         
    
}
