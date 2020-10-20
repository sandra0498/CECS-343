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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class App extends JFrame{

    BasicPlayer player;
    JTable table;
    JScrollPane scrollPane; 
    
    MyDB mydb;
    
    private final JPopupMenu popup = new JPopupMenu();
    int CurrentSelectedRow;
    JButton PlayButton; // play button
    JButton PauseButton; // pause button
    JButton PreviousButton; // previous song
    JButton NextButton; // next song
    JButton AddSongButton; // add song
    
    ButtonListener bl; // button listener play
    ButtonListener b2; // button listener pause
    ButtonListener b3; // button listener previous
    ButtonListener b4; // button listener next
    ButtonListener b5; // button listener add song
    JPanel main;
    
    public App() throws SQLException{
        mydb = new MyDB();
        mydb.Connect();

        player = new BasicPlayer();
        bl = new ButtonListener();
        b2 = new ButtonListener();
        b3 = new ButtonListener();
        b4 = new ButtonListener();
        b5 = new ButtonListener();
        
        PreviousButton = new JButton("<<");
        PreviousButton.addActionListener(b3);
        PreviousButton.setPreferredSize(new Dimension(100, 25));
        
        PlayButton = new JButton(">");
        PlayButton.addActionListener(bl);
        PlayButton.setPreferredSize(new Dimension(100,25));
        PlayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        PauseButton = new JButton("||");
        PauseButton.addActionListener(b2);
        PauseButton.setPreferredSize(new Dimension(100,25));
        
        NextButton = new JButton(">>");
        NextButton.addActionListener(b4);
        NextButton.setPreferredSize(new Dimension(100, 25));
        
        AddSongButton = new JButton("+");
        AddSongButton.addActionListener(b5);
        AddSongButton.setPreferredSize(new Dimension(45, 25));
        
        main = new JPanel();
        

        
        //colums labels the columns in the table
        //if the table is not in a scrollpane the column header will not show
        //it would have to be added separately
        String[] columns = {"Album", "Title", "Artist"};    
        //data holds the table data and maps as a 2d array into the table
        Object[][] data = mydb.getSongs();
        

        DefaultTableModel dm = new DefaultTableModel(data,columns);
                
        //create the table
        table = new JTable(dm);
        
        

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
        
        main.add(AddSongButton);
        main.add(scrollPane);
        main.add(PreviousButton);
        main.add(PlayButton);
        main.add(PauseButton);
        main.add(NextButton);
        this.setTitle("DJ Play My Song!");

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
                } catch (UnsupportedTagException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidDataException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
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
        } // end actionPerformed
    public void pauseBtnPerformed(ActionEvent evt){
        try{
            if(evt.getSource() == PauseButton){
                player.stop();
            }
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,e);
        }
    } // end pause buttonListener
    
    
    
    } // end button listener
   /* class MyDropTarget extends DropTarget {
        public  void drop(DropTargetDropEvent evt) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_COPY);
               
                List result = new ArrayList();
                result = (List) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                

                for(Object o : result)
                    System.out.println(o.toString());
              
                        }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }*/
           
    }
    



        