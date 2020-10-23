/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cecs343musicplayer;

import java.awt.*;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.awt.Dimension;
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
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

/**
 *
 * @author Serenity Brown
 * @author Sandra Chavez
 */

public class App extends JFrame implements ActionListener{

    BasicPlayer player;
    BasicController BC;

    JTable table;
    JScrollPane scrollPane; 
    final JPopupMenu popup;
    JMenuItem addMenuSong;
    JMenuItem deleteMenuSong;
    JMenuItem exit;
    JMenuItem song;
    JMenuItem deleted;
    JMenuItem open;
    JMenuBar menuBar;
    JMenu menu;
    MyDB mydb;
    
    
    
    int CurrentSelectedRow;
    String currentTitlePlaying; 
    
    JButton PlayButton; // play button
    JButton PauseButton; // pause button
    JButton PreviousButton; // previous song
    JButton NextButton; // next song
    JButton StopButton; //
    
  
    JPanel main;
    final JFileChooser fc;
    
    public App() throws SQLException {
        mydb = new MyDB();
        mydb.Connect();

        player = new BasicPlayer();        
        fc = new JFileChooser();
//        fc.setCurrentDirectory(new File("c:/Users/Sandra C/Desktop/CECS343"));
        
        menuBar = new JMenuBar();
        menu = new JMenu("File");
        song =new JMenuItem("Add Song");
        deleted =new JMenuItem("Delete Song");
        open = new JMenuItem("Open Song");
        exit =new JMenuItem("Exit"); 
        
        menu.add(song);
        menu.add(deleted);
        menu.add(open);
        menu.add(exit);
        
        menuBar.add(menu);
        
        open.addActionListener(this);
        
        
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
        
        PreviousButton = new JButton("|<");
        PreviousButton.addActionListener(this);
        PreviousButton.setPreferredSize(new Dimension(100, 25));
        
        PlayButton = new JButton("Play");
        PlayButton.addActionListener(this);
        PlayButton.setPreferredSize(new Dimension(100,25));
        PlayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        PauseButton = new JButton("Pause");
        PauseButton.addActionListener(this);
        PauseButton.setPreferredSize(new Dimension(100,25));
        
        NextButton = new JButton(">|");
        NextButton.addActionListener(this);
        NextButton.setPreferredSize(new Dimension(100, 25));
        
       
        main = new JPanel();
        

        
        //colums labels the columns in the table
        //if the table is not in a scrollpane the column header will not show
        //it would have to be added separately
        
        // add genre, year, comment 
        String[] columns = {"Album", "Title", "Artist","Year","Genre","Comments"};    
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
        column.setPreferredWidth(75);
        //setting the size for title a little wider than the rest 
        column = table.getColumnModel().getColumn(1); 
        column.setPreferredWidth(180);
        
        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500,100));
        
        main.setSize(700,500);
        
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
                        for (Object value : fileList) {
                             Mp3File mp3 = new Mp3File((File) value);
                             ID3v2 id3v2Tag = mp3.getId3v2Tag();
                             String album = id3v2Tag.getAlbum();
                             String artist = id3v2Tag.getArtist();
                             String title = id3v2Tag.getTitle();
                             String year = id3v2Tag.getYear();
                             String genre = id3v2Tag.getGenreDescription();
                             String comments = id3v2Tag.getComment();
                             mydb.addSongs(album, title, artist,year,genre,comments);
                             System.out.println("This is the title "+title);
                            if (value instanceof File) {
                                File f = (File) value;
                                if (row < 0) {
                                    model.addRow(new Object[]{album, title,artist,year,genre,comments});
                                } else {
                                    model.insertRow(row, new Object[]{album, title,artist,year,genre,comments});
                                    row++;
                                }
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
    
    
    //function to open and play a song that is not in the library 
   public void openSong() throws BasicPlayerException, IOException {
    int returnValue = fc.showOpenDialog(this);
    if(returnValue == JFileChooser.APPROVE_OPTION){
    
    File file = fc.getSelectedFile();
    player.open(file);
    player.play();
    }

   
   
   } 
   public void addSong() throws SQLException{
     int returnValue = fc.showOpenDialog(this);
     
     if(returnValue == JFileChooser.APPROVE_OPTION) {

         try {
            File file = fc.getSelectedFile();
             Mp3File mp3 = new Mp3File(file);
            ID3v2 id3v2Tag = mp3.getId3v2Tag();
            String album = id3v2Tag.getAlbum();
            String artist = id3v2Tag.getArtist();
            String title = id3v2Tag.getTitle();
            String year = id3v2Tag.getYear();
            String genre = id3v2Tag.getGenreDescription();
            String comments = id3v2Tag.getComment();
            mydb.addSongs(album, title, artist,year,genre,comments);
            
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            
            if (table.getRowCount() > 0) {
                model.insertRow(model.getRowCount(), new Object[]{album, title,artist,year,genre,comments});
            }
            else{
            model.insertRow(0, new Object[]{album, title,artist,year,genre,comments});
            }
            

             
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
   
   public boolean isSongPlaying() {
   if (player.getStatus() == BasicPlayer.PLAYING) {
   
   return true;
   
   }
   return false;
   }
   
   public boolean isSongPaused() {
   if (player.getStatus() == BasicPlayer.PAUSED) {
   
   return true;
   
   }
   return false;
   }
   
   
   
   public void playSong() throws BasicPlayerException, IOException {
       
     
//   File arg = null;
//   String dir = null;
   CurrentSelectedRow = table.getSelectedRow();

       String title = (String)table.getValueAt(CurrentSelectedRow, 1);
      if (title != null) {
          String directory = null;
          File f = null;
        if (!isSongPaused()  && currentTitlePlaying != title) {
            
       if (title.equalsIgnoreCase("Stronger")) {
           directory = "C:/Users/Sandra C/Desktop/FALL 2020/CECS 343/Stronger.mp3";
           
            f = new File(directory);
            player.open(f);
            player.play();
            System.out.println("Playing this song");

       }
       
       else if (title.equalsIgnoreCase("Disturbia")){
           System.out.println("goes into this conditional ");
        directory = "C:/Users/Sandra C/Desktop/FALL 2020/CECS 343/Disturbia.mp3";
        
        f = new File(directory);

        player.open(f);
        player.play();
        System.out.println("Playing this song");

       
       }
       
       else if (title.equalsIgnoreCase("1, 2 Step (ft. Missy Elliott)")) {
           
        directory  = "C:/Users/Sandra C/Desktop/FALL 2020/CECS 343/12step.mp3";
        
         f= new File(directory);

        player.open(f);
        player.play();
        System.out.println("Playing this song");

     
       }
       
        else if (title.equalsIgnoreCase("Eyes like sky")) {
            
               
        directory = "C:/Users/Sandra C/Desktop/FALL 2020/CECS 343/eyeslikesky.mp3";
        
        f = new File(directory);

        player.open(f);
        player.play();
        System.out.println("Playing this song");
     
   
        }
       else if (title.equalsIgnoreCase("Whatta Man")){
        
       directory = "C:/Users/Sandra C/Desktop/FALL 2020/CECS 343/WhattaMan.mp3";
        
        File arg = new File(directory);

        player.open(arg);
        player.play();
        System.out.println("Playing this song");

       }
        
      }
        
        else {
        
                player.resume();
        }
      
   }
      
      else {
      
      System.out.println("There is nothing to play!");
      }
       
       
 
 
   }
   
   public void skiptoprevious()throws BasicPlayerException, IOException{
      CurrentSelectedRow = table.getSelectedRow();
      
     
      if (CurrentSelectedRow == 0) {
        table.setRowSelectionInterval(table.getRowCount()-1 , table.getRowCount()-1);
          playSong(table.getRowCount() - 1 );
      }
      
      else {
        table.setRowSelectionInterval(CurrentSelectedRow - 1 , CurrentSelectedRow - 1 );
        playSong(CurrentSelectedRow - 1);
      }

   } 
   
   public void skiptonext()throws BasicPlayerException, IOException{
        CurrentSelectedRow = table.getSelectedRow();
        
        // if selected is the last song -> play first song 
        if (CurrentSelectedRow == table.getRowCount() - 1) {
            table.setRowSelectionInterval(0 , 0);
            playSong(0);
        }
        
        else {
            table.setRowSelectionInterval(CurrentSelectedRow + 1 , CurrentSelectedRow + 1 );
        playSong(CurrentSelectedRow + 1);
        }

   
   } 
   
    /**
     *
     * @param newRow - the new row that is playing 
     */
    public void playSong(int newRow) throws BasicPlayerException {
      File arg = null;
    String dir = null;
    Mp3File mp3 = null;
   CurrentSelectedRow = newRow;
        String title = (String)table.getValueAt(CurrentSelectedRow, 1);
      
       if (title != null){
           
        if (isSongPaused()) {
            
            player.resume();
        
        }
        
        else  {
           
       if (title.equalsIgnoreCase("Stronger")) {
            dir = "C:/Users/Sandra C/Desktop/FALL 2020/CECS 343/Stronger.mp3";

            File f = new File(dir);
            player.open(f);
            player.play();
            System.out.println("Playing this song");


       }
       
       else if (title.equalsIgnoreCase("Disturbia")){
           System.out.println("goes into this conditional ");
        dir = "C:/Users/Sandra C/Desktop/FALL 2020/CECS 343/Disturbia.mp3";
        
        arg = new File(dir);

        player.open(arg);
        player.play();
        System.out.println("Playing this song");
       
       }
       
       else if (title.equalsIgnoreCase("1, 2 Step (ft. Missy Elliott)")) {
           
        dir = "C:/Users/Sandra C/Desktop/FALL 2020/CECS 343/12step.mp3";
        
        arg = new File(dir);

        player.open(arg);
        player.play();
        System.out.println("Playing this song");
     
       }
       
        else if (title.equalsIgnoreCase("Eyes like sky")) {
            
               
        dir = "C:/Users/Sandra C/Desktop/FALL 2020/CECS 343/eyeslikesky.mp3";
        
        arg = new File(dir);

        player.open(arg);
        player.play();
        System.out.println("Playing this song");
     
   
        }
       
        else if (title.equalsIgnoreCase("Whatta Man")){
        
        dir = "C:/Users/Sandra C/Desktop/FALL 2020/CECS 343/WhattaMan.mp3";
        
        arg = new File(dir);

        player.open(arg);
        player.play();
        System.out.println("Playing this song");
    
       }
        
       }
        
        
    }
       
       else {
       
       System.out.println("There is still no song to play!");
       }
      
   }
  
        public void actionPerformed(ActionEvent e)  {
             String choice = e.getActionCommand();
            if(choice.equals("Exit")){
             System.exit(0);
        }else if(choice.equals("Add Song")){
                 try {
                     addSong();
                 } catch (SQLException ex) {
                     Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 catch (ArrayIndexOutOfBoundsException indexOut) {
                  System.out.println("");
                 
                 }
        }
        else if (choice.equals("Delete Song")) {
                 try {
                  
                     deleteSong();
                 } catch (SQLException ex) {
                     Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                 }
                catch (ArrayIndexOutOfBoundsException indexOut) {
                  System.out.println("Nothing to delete, add songs first!");
                 
                 }
                 
        }
        else if (choice.equals("Open Song")) {
                 try {
                     openSong();
                 } catch (BasicPlayerException ex) {
                     Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                 } catch (IOException ex) {
                     Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                 }
        
        }
        else if (choice.equals("Play")) {
                 try {
                     playSong();
                 } catch (BasicPlayerException ex) {
                     Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                 } catch (IOException ex) {
                     Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                 }
                catch (ArrayIndexOutOfBoundsException indexOut) {
                  System.out.println("No song chosen");
                 
                 }

        
        }
        
        else if (choice.equals("Pause")) {
                 try {
                     player.pause();
                 } catch (BasicPlayerException ex) {
                     Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                 }
                    catch (ArrayIndexOutOfBoundsException indexOut) {
                  System.out.println("No song chosen");
                 
                 }
        
        }
            
        else if (choice.equals("|<")) {
                 try {
                     skiptoprevious();
                 } catch (BasicPlayerException ex) {
                     Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                 } catch (IOException ex) {
                     Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                 }
                    catch (ArrayIndexOutOfBoundsException indexOut) {
                  System.out.println("No song chosen");
                 
                 }
                    catch(IllegalArgumentException ill) {
                 System.out.println("cannot skip to previous!");
                 }
        
        }
            
        else if (choice.equals(">|")) {
                 try {
                     skiptonext();
                 } catch (BasicPlayerException ex) {
                     Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                 } catch (IOException ex) {
                     Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                 }
                    catch (ArrayIndexOutOfBoundsException indexOut) {
                  System.out.println("No song chosen");
                 
                 }
                 catch(IllegalArgumentException ill) {
                 System.out.println("cannot skip ahead!");
                 }
        
        }
     
       
        } // end actionPerformed     
    }
    
