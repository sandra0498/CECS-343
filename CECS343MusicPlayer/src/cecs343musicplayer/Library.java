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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

/**
 *
 * @author Serenity Brown
 * @author Sandra Chavez
 */

public class Library extends JFrame implements ActionListener{

    BasicPlayer player;
    BasicController BC;

    String userPlayList;
    JTable table;
    JScrollPane scrollPane; 
    JPanel libraryPanel;
    final JPopupMenu popup;
    final JPopupMenu popupPlaylist;
    JMenuItem addMenuSong;
    JMenuItem deleteMenuSong;
    JMenuItem exit;
    JMenuItem song;
    JMenuItem deleted;
    JMenuItem open;
    JMenuItem createPlayList;
    JMenu addPlayListMenu;
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem deletePlaylist;
    JMenuItem openNewWindow;
    
    JTree tree;
    JTree library;
    
    MyDB mydb;
    
    int CurrentSelectedRow;
    String currentTitlePlaying; 
    
    JButton PlayButton; // play button
    JButton PauseButton; // pause button
    JButton PreviousButton; // previous song
    JButton NextButton; // next song
    JButton StopButton; // stop button
    
    DefaultTreeModel model;
    DefaultMutableTreeNode playlist;
    DefaultMutableTreeNode newNode;
    DefaultTableModel dm;
    TreePath path;
    private ArrayList<String>playlistnames;
    JPanel main;
    final JFileChooser fc;
    
    public Library() throws SQLException {
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
        createPlayList = new JMenuItem("Create Playlist");
        
        menuBar.add(menu);
        menu.add(song);
        menu.add(deleted);
        menu.add(open);
        menu.add(createPlayList);
        menu.add(exit);
        
        
        open.addActionListener(this);
        song.addActionListener(this); //  adds the menu item to add song to action listener                               
        exit.addActionListener(this); //adds exit button to listener 
        deleted.addActionListener(this); // adds delete in menu bar to listener 
        createPlayList.addActionListener(this); // when create playlist is selected
        
        
        library = new JTree();
        popup = new JPopupMenu();
        addMenuSong = new JMenuItem("Add Song"); // menu item add song
        deleteMenuSong = new JMenuItem("Delete Song"); // menu item delete song
        popup.add(addMenuSong);
        popup.add(deleteMenuSong);
        
        addMenuSong.addActionListener(this);
        deleteMenuSong.addActionListener(this);  // adds popup delete to listener
        
        addPlayListMenu = new JMenu("Add to playlist"); // will have a submenu
        //addPlayListMenu.add(new JMenuItem("Playlists...")); // should be nodes created from user entered playlist
        popup.add(addPlayListMenu);
        
        
        popupPlaylist = new JPopupMenu();
        openNewWindow = new JMenuItem("Open New Window");
        deletePlaylist = new JMenuItem("Delete Playlist");
        popupPlaylist.add(openNewWindow);
        popupPlaylist.add(deletePlaylist);
        openNewWindow.addActionListener(this); // create action listener to open in new window
        deletePlaylist.addActionListener(this); // create action listener to delete playlist
       
        
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
        
        libraryPanel = new JPanel(); // our library panel that will hold playlists
        main = new JPanel(); // our main jpanel
        
        
        
        //colums labels the columns in the table
        //if the table is not in a scrollpane the column header will not show
        //it would have to be added separately
        
        // add genre, year, comment 
        String[] columns = {"Album", "Title", "Artist","Year","Genre","Comments"};    
        //data holds the table data and maps as a 2d array into the table
        Object[][] data = mydb.getSongs();
        
        DefaultMutableTreeNode libraryTree = new DefaultMutableTreeNode("Library"); // tree node for library
        playlist = new DefaultMutableTreeNode("Playlist"); // the root
        playlistnames = mydb.getCurrentPlaylists();
        if (playlistnames != null){
            for(int i = 0; i < playlistnames.size(); i++){
            newNode = new DefaultMutableTreeNode(playlistnames.get(i));
            playlist.add(newNode);
            addPlayListMenu.add(new JMenuItem(userPlayList));
        
        }
        
 
        }

//        for (String playlistname : playlistnames) {
//            newNode = new DefaultMutableTreeNode(playlistname);
//            playlist.add(newNode);
//            addPlayListMenu.add(new JMenuItem(userPlayList));
//        }

        
        //DefaultMutableTreeNode createdList = new DefaultMutableTreeNode(newNode); // new node when user creates a playlist
        dm = new DefaultTableModel(data,columns);
                
        //create the table
        table = new JTable(dm);
        tree = new JTree(playlist); // this playlist is for when the user creates it
        library = new JTree(libraryTree);
        
        // creating the pop up menu
        table.setComponentPopupMenu(popup); // taken out? 
        main.setComponentPopupMenu(popup);
        tree.setComponentPopupMenu(popupPlaylist);
        
        //assign the listener
        table.addMouseListener(mouseListener);
         library.addMouseListener(mListener);
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
       
       libraryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
       libraryPanel.setPreferredSize(new Dimension(100 , 200));
       libraryPanel.setBackground(Color.WHITE);
       
      
       
       
       libraryPanel.add(library);
       libraryPanel.add(tree);
        
        main.setSize(700,500);
        main.add(libraryPanel); 
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
                    Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedTagException ex) {
                    Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidDataException ex) {
                    Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
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
    
    MouseListener mListener = new MouseAdapter() {
       public void mousePressed(MouseEvent e) {
         
          String[] columns = {"Album", "Title", "Artist","Year","Genre","Comments"};  
           try {
               dm.setDataVector(mydb.getSongs(), columns);
           } catch (SQLException ex) {
               Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
        
    };
    
    public JPanel getLibraryPanel(){
        return libraryPanel;
    }
    public DefaultTableModel getModel(){
        return dm;
       }
    public JTable getTable(){
        return table;
    }
    
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
             Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
         } catch (UnsupportedTagException ex) {
             Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
         } catch (InvalidDataException ex) {
             Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
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
       
     
  // File arg = null;
//   String dir = null;
  // CurrentSelectedRow = table.getSelectedRow(); //Disturbia
       // currentTitle is the current song playing or the index
       // title should be what the user presses
       String title = (String)table.getValueAt(CurrentSelectedRow, 1); // Disturbia
       //String currentTitlePlaying = (String)table.getValueAt(CurrentSelectedRow, 1);
         //int titleValue = (int)table.getValueAt(CurrentSelectedRow, 1);
       //int currentTitleValue = table.getSelectedRow();
       
       // if title's current selected row is not the selected row of current song, then 
       
      if (title != null) {
          String directory = null;
          File f = null;
          System.out.println("This is the previous track " + currentTitlePlaying +" Title that was clicked (current): "+ title );

        if (!isSongPaused()  || currentTitlePlaying != title) {
          
            if (title.equalsIgnoreCase("Stronger")) { // turn back to title
           directory = "C:/Users/Sandra C/Desktop/Fall 2020/CECS 343/Stronger.mp3";
           
            f = new File(directory);
            player.open(f);
            player.play();
            System.out.println("Playing Stronger");
          

       }
          
       
       else if (title.equalsIgnoreCase("Disturbia")){
           System.out.println("goes into this conditional ");
        directory = "C:/Users/Sandra C/Desktop/Fall 2020/CECS 343/Disturbia.mp3";
        
        f = new File(directory);

        player.open(f);
        player.play();
        System.out.println("Playing this Disturbia");
         

       
       }
       
        else if (title.equalsIgnoreCase("1, 2 Step (ft. Missy Elliott)")) {
           
        directory  = "C:/Users/Sandra C/Desktop/Fall 2020/CECS 343/12step.mp3";
        
         f= new File(directory);

        player.open(f);
        player.play();
        System.out.println("Playing this 1 2 Step");
       
       }
       
        else if (title.equalsIgnoreCase("Eyes like sky")) {
            
               
        directory = "C:/Users/Sandra C/Desktop/Fall 2020/CECS 343/eyeslikesky.mp3";
        
        f = new File(directory);

        player.open(f);
        player.play();
        System.out.println("Playing this eyes like sky");
        
        }
        
         else if (title.equalsIgnoreCase("Whatta Man")){
        
       directory = "C:/Users/Sandra C/Desktop/Fall 2020/CECS 343/WhattaMan.mp3";
        
        File arg = new File(directory);

        player.open(arg);
        player.play();
        System.out.println("Playing this whatta man");
        

       }

        
      }
        

          
        else {
        
                player.resume();
               title = (String)table.getValueAt(CurrentSelectedRow, 1);
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
           
        
            if (title.equalsIgnoreCase("Stronger")) {
            dir = "C:/Users/Sandra C/Desktop/Fall 2020/CECS 343/Stronger.mp3";

            File f = new File(dir);
            player.open(f);
            player.play();
            System.out.println("Playing this stronger");


       }
       
       else if (title.equalsIgnoreCase("Disturbia")){
           System.out.println("goes into this conditional ");
        dir = "C:/Users/Sandra C/Desktop/Fall 2020/CECS 343/Disturbia.mp3";
        
        arg = new File(dir);

        player.open(arg);
        player.play();
        System.out.println("Playing this disturbia");
       
       }
       
       else if (title.equalsIgnoreCase("1, 2 Step (ft. Missy Elliott)")) {
           
        dir = "C:/Users/Sandra C/Desktop/Fall 2020/CECS 343/12step.mp3";
        
        arg = new File(dir);

        player.open(arg);
        player.play();
        System.out.println("Playing this song");
     
       }
       
        else if (title.equalsIgnoreCase("Eyes like sky")) {
            
               
        dir = "C:/Users/Sandra C/Desktop/Fall 2020/CECS 343/eyeslikesky.mp3";
        
        arg = new File(dir);

        player.open(arg);
        player.play();
        System.out.println("Playing this eyes like sky");
     
   
        }
       
        else if (title.equalsIgnoreCase("Whatta Man")){
        
        dir = "C:/Users/Sandra C/Desktop/Fall 2020/CECS 343/WhattaMan.mp3";
        
        arg = new File(dir);

        player.open(arg);
        player.play();
        System.out.println("Playing this whatta man");
    
       }
        
         // end else
        
       
        
        
    } // end title != null
       
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
                     Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 catch (ArrayIndexOutOfBoundsException indexOut) {
                  System.out.println("");
                 
                 }
        }
        else if (choice.equals("Delete Song")) {
                 try {
                  
                     deleteSong();
                 } catch (SQLException ex) {
                     Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                 }
                catch (ArrayIndexOutOfBoundsException indexOut) {
                  System.out.println("Nothing to delete, add songs first!");
                 
                 }
                 
        }
        else if (choice.equals("Open Song")) {
                 try {
                     openSong();
                 } catch (BasicPlayerException ex) {
                     Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                 } catch (IOException ex) {
                     Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                 }
        
        }
        else if (choice.equals("Play")) {
                 try {
                     playSong();
                     currentTitlePlaying =  (String)table.getValueAt(CurrentSelectedRow, 1);
                     System.out.println("Finally out of the function !");
                     
                     
                     
                 } catch (BasicPlayerException ex) {
                     Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                 } catch (IOException ex) {
                     Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                 }
                catch (ArrayIndexOutOfBoundsException indexOut) {
                  System.out.println("No song chosen");
                 
                 }

        
        }
        
        else if (choice.equals("Pause")) {
                 try {
                     player.pause();
                 } catch (BasicPlayerException ex) {
                     Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                 }
                    catch (ArrayIndexOutOfBoundsException indexOut) {
                  System.out.println("No song chosen");
                 
                 }
        
        }
            
        else if (choice.equals("|<")) {
                 try {
                     skiptoprevious();
                 } catch (BasicPlayerException ex) {
                     Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                 } catch (IOException ex) {
                     Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
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
                     Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                 } catch (IOException ex) {
                     Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                 }
                    catch (ArrayIndexOutOfBoundsException indexOut) {
                  System.out.println("No song chosen");
                 
                 }
                 catch(IllegalArgumentException ill) {
                 System.out.println("cannot skip ahead!");
                 }
        
        }else if(choice.equals("Create Playlist")){
            userPlayList = JOptionPane.showInputDialog(createPlayList,
                    "Playlist name: ");
            
            
            if(JOptionPane.OK_OPTION == 0){ // is true
                newNode = new DefaultMutableTreeNode(userPlayList); // node is what user types in
                playlist.add(newNode);
                addPlayListMenu.add(new JMenuItem(userPlayList));
                try {
                   Object [][] dataVector = new Object[0][6];
                   String[] columns = {"Album", "Title", "Artist","Year","Genre","Comments"};  
                    mydb.addPlaylistName(userPlayList);
                     dm.setDataVector(dataVector, columns);
                   
                } catch (SQLException ex) {
                    Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                 
            
            
        }else if(choice.equals("Delete Playlist")){
            model = (DefaultTreeModel) tree.getModel();
            path = tree.getSelectionPath();
            
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            int confirmation = JOptionPane.showConfirmDialog(deletePlaylist, "Are you sure you want to delete?");
            
            if(confirmation == 0){ // 0 = yes, 1 = no, 2 = cancel
            model.removeNodeFromParent(currentNode);
            addPlayListMenu.remove(new JMenuItem(userPlayList));
          }
            
        }else if(choice.equals("Open New Window")){
           
                 try {
                     Playlist play = new Playlist();
                     play.setTitle(userPlayList);
                     Object [][] dataVector = new Object[0][6];
                     String[] columns = {"Album", "Title", "Artist","Year","Genre","Comments"};  
                     dm.setDataVector(mydb.getSongs(), columns);
                     play.getModel().setDataVector(dataVector, columns);
                     play.getLibraryPanel().setVisible(false);
                     
                     
                     
                    
                 } catch (SQLException ex) {
                     Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                 }
            
          
            
        }
       
        } // end actionPerformed     
    }