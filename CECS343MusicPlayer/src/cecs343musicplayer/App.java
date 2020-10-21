/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cecs343musicplayer;
//import java.awt.datatransfer.DataFlavor;
//import java.awt.dnd.DnDConstants;
//import java.awt.dnd.DropTarget;
//import java.awt.dnd.DropTargetDropEvent;
//import java.io.File;
//import java.util.ArrayList;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.JPopupMenu;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Menu;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;



public class App extends JFrame implements ActionListener{

    BasicPlayer player;
    JTable table;
    JScrollPane scrollPane; 
    final JPopupMenu popup;
    JMenuItem addMenuSong;
    JMenuItem deleteMenuSong;
    JMenuItem exit;
    JMenuItem song;
    JMenuItem deleted;
    JMenuBar menuBar;
    JMenu menu;
    MyDB mydb;
    
    
    
    int CurrentSelectedRow;
    
    JButton PlayButton; // play button
    JButton PauseButton; // pause button
    JButton PreviousButton; // previous song
    JButton NextButton; // next song
//    JButton AddSongButton; // add song
    
  
    JPanel main;
    final JFileChooser fc;
    
    public App() throws SQLException{
        mydb = new MyDB();
        mydb.Connect();

        player = new BasicPlayer();
      
        fc = new JFileChooser();
        fc.setCurrentDirectory(new File("c:\\Users\\Sandra C\\Desktop\\CECS343"));
        
        menuBar = new JMenuBar();
        menu = new JMenu("File");
        song =new JMenuItem("Add Song");
        deleted =new JMenuItem("Delete Song");
        exit =new JMenuItem("Exit"); 
        
        menu.add(song);
        menu.add(deleted);
        menu.add(exit);
        
        menuBar.add(menu);
        
        song.addActionListener(this);
        // ^^ adds the menu item to add song to action listener
                                       
        exit.addActionListener(this); //adds exit button to listener 
        
        deleted.addActionListener(this); 
        // adds delete in menu bar to listener 
        
        
        popup = new JPopupMenu();
        addMenuSong = new JMenuItem("Add Song"); // menu item add song
        deleteMenuSong = new JMenuItem("Delete Song"); // menu item delete song
        
        popup.add(addMenuSong);
        popup.add(deleteMenuSong);
        
        addMenuSong.addActionListener(this);
        
        deleteMenuSong.addActionListener(this);  // adds popup delete to listener
        
        PreviousButton = new JButton("<<");
        PreviousButton.addActionListener(this);
        PreviousButton.setPreferredSize(new Dimension(100, 25));
        
        PlayButton = new JButton("Play");
        PlayButton.addActionListener(this);
        PlayButton.setPreferredSize(new Dimension(100,25));
        PlayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        PauseButton = new JButton("Pause");
        PauseButton.addActionListener(this);
        PauseButton.setPreferredSize(new Dimension(100,25));
        
        NextButton = new JButton(">>");
        NextButton.addActionListener(this);
        NextButton.setPreferredSize(new Dimension(100, 25));
        
       
        main = new JPanel();
        

        
        //colums labels the columns in the table
        //if the table is not in a scrollpane the column header will not show
        //it would have to be added separately
        
        // add genre, year, comment 
        String[] columns = {"Album", "Title", "Artist"};    
        //data holds the table data and maps as a 2d array into the table
        Object[][] data = mydb.getSongs();
        

        DefaultTableModel dm = new DefaultTableModel(data,columns);
                
        //create the table
        table = new JTable(dm);
        
        // creating the pop up menu
        table.setComponentPopupMenu(popup);
        main.setComponentPopupMenu(popup); 
        
        //assign the listener
        table.addMouseListener(mouseListener);
        
        //change some column's width
        //first get the column from the column model from the table
        //column 0 is the leftmost - make it 250 pixels
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(25);
        //using the same TableColumn variable set the "Year" coulumn to 20
        column = table.getColumnModel().getColumn(1); 
        column.setPreferredWidth(20);
        
        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(475,100));
        
        main.setSize(700,500);
        
//        main.add(AddSongButton);
        main.add(scrollPane);
        main.add(PreviousButton);
        main.add(PlayButton);
        main.add(PauseButton);
        main.add(NextButton);
        this.setTitle("DJ Play My Song!");
        this.add(main);
        this.setSize(500, 200);
        this.setJMenuBar(menuBar);
        this.setSize(500,200);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        table.setDropTarget(new DropTarget() {
        @Override
        public synchronized void dragOver(DropTargetDragEvent dtde) {
            Point point = dtde.getLocation();
            int row = table.rowAtPoint(point);
            if (row < 0) {
                table.clearSelection();
            } else {
                table.setRowSelectionInterval(row, row);
            }
            dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        }

        @Override
        public synchronized void drop(DropTargetDropEvent dtde) {
            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                Transferable t = dtde.getTransferable();
                List fileList = null;
                try {
                    fileList = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
                    int size = (int)fileList.size();
                    if (size > 0) {
                        table.clearSelection();
                        Point point = dtde.getLocation();
                        int row = table.rowAtPoint(point);
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
//                        for (Object value : fileList) {
                            Object value = fileList.get(0);
                             Mp3File mp3 = new Mp3File((File) value);
                             ID3v2 id3v2Tag = mp3.getId3v2Tag();
                             String album = id3v2Tag.getAlbum();
                             String artist = id3v2Tag.getArtist();
                             String title = id3v2Tag.getTitle();
                             mydb.addSongs(album, title, artist);
                             System.out.println("This is the title "+title);
                            if (value instanceof File) {
                                File f = (File) value;
                                if (row < 0) {
                                    model.addRow(new Object[]{album, title,artist});
                                } else {
                                    model.insertRow(row, new Object[]{album, title,artist});
                                    row++;
//                                }
                            }
                        }
                    }
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedTagException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidDataException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                dtde.rejectDrop();
            }
        }

    });
        this.add(main);
        this.setSize(500, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
        //embed the listener in the declaration
    MouseListener mouseListener = new MouseAdapter() {
        //this will print the selected row index when a user clicks the table
        public void mousePressed(MouseEvent e) {
           CurrentSelectedRow = table.getSelectedRow();
           System.out.println("Selected index = " + CurrentSelectedRow);
        }
    };
    
   public void addSongFromBar() throws SQLException{
     int returnValue = fc.showOpenDialog(this);
     
     if(returnValue == JFileChooser.APPROVE_OPTION) {

         try {
            File file = fc.getSelectedFile();
             Mp3File mp3 = new Mp3File(file);
            ID3v2 id3v2Tag = mp3.getId3v2Tag();
            String album = id3v2Tag.getAlbum();
            String artist = id3v2Tag.getArtist();
            String title = id3v2Tag.getTitle();
            mydb.addSongs(album, title, artist);
            
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            
            if (table.getRowCount() > 0) {
                model.insertRow(model.getRowCount() - 1, new Object[]{album, title,artist});
            }
            model.insertRow(0, new Object[]{album, title,artist});

             
         } catch (IOException ex) {
             Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
         } catch (UnsupportedTagException ex) {
             Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
         } catch (InvalidDataException ex) {
             Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
         }
     }
    }
   
   public void deleteSong() throws SQLException {
     //get the selected row -->  (SR) 
     //call the default table model .deleterow(SR)
    CurrentSelectedRow = table.getSelectedRow();

     String album = (String)table.getValueAt(CurrentSelectedRow, 0);
     String title = (String)table.getValueAt(CurrentSelectedRow, 1);
     String artist = (String)table.getValueAt(CurrentSelectedRow, 2);
     
     mydb.deleteSongs(album,title,artist);
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    model.removeRow(CurrentSelectedRow);
   } 
   
   public void playSong() {
   File arg = null;
   String dir = null;
   CurrentSelectedRow = table.getSelectedRow();
   try {
       String title = (String)table.getValueAt(CurrentSelectedRow, 1);
      
       if (title.equalsIgnoreCase("Stronger")) {
       dir = "Kanye West - Stronger.mp3";
       arg = new File(dir);

       }
       
       else if (title.equalsIgnoreCase("Disturbia")){
        dir = "C:\\Users\\Sandra C\\Desktop\\FALL 2020\\CECS 343\\Rihanna-Disturbia.mp3";
        
        arg = new File(dir);
        

       }
       
              
       player.open(arg);
      
        player.play();
       
       
   
   }
   catch(Exception ex) {
       
           System.out.println("BasicPlayer exception");

    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
   
   
   }
   
   }
   
        public void actionPerformed(ActionEvent e)  {
             String choice = e.getActionCommand();
            if(choice.equals("Exit")){
             System.exit(0);
        }else if(choice.equals("Add Song")){
                 try {
                     addSongFromBar();
                 } catch (SQLException ex) {
                     Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                 }
        }
        else if (choice.equals("Delete Song")) {
                 try {
                  
                     deleteSong();
                 } catch (SQLException ex) {
                     Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                 }
        }
        else if (choice.equals("Play")) {

        
        }
     
       
        } // end actionPerformed
   
           
    }
    
