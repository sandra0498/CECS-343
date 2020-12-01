/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cecs343musicplayer;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sandra C
 */
public class Playlist extends Library {

    TransferHandler th;
    JTable playlist;
    private String userplaylist; 

//    private final DataFlavor localObjectFlavor = null;
//    private JComponent source;
//    private int[] indices;
//    String playlistname;
//    private int addIndex = -1; //Location where items were added
//    private int addCount; //Number of items added.
    
    Playlist(String playlistname) throws SQLException{
        

    playlist = new JTable();

    this.userplaylist = playlistname;
    System.out.println("playlist name: "+ userplaylist);
    
    
    
    
//    playlist.setDropTarget(new DropTarget() {
//        @Override
//        public synchronized void dragOver(DropTargetDragEvent dtde) {
//            Point point = dtde.getLocation();
//            int row = table.rowAtPoint(point);
//            if (row < 0) {
//                table.clearSelection();
//            } else {
//                table.setRowSelectionInterval(row, row);
//            }
//            dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
//        }
//
//        @Override
//        public synchronized void drop(DropTargetDropEvent dtde) {
//            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
//                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
//                Transferable t = dtde.getTransferable();
//                List fileList = null;
//                try {
//                    fileList = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
//                    int size = (int)fileList.size();
//                    if (size > 0) {
//                        playlist.clearSelection();
//                        Point point = dtde.getLocation();
//                        int row = playlist.rowAtPoint(point);
//                        DefaultTableModel model = (DefaultTableModel) playlist.getModel();
//                        for (Object value : fileList) {
//                             Mp3File mp3 = new Mp3File((File) value);
//                             ID3v2 id3v2Tag = mp3.getId3v2Tag();
//                             String album = id3v2Tag.getAlbum();
//                             String artist = id3v2Tag.getArtist();
//                             String title = id3v2Tag.getTitle();
//                             String year = id3v2Tag.getYear();
//                             String genre = id3v2Tag.getGenreDescription();
//                             String comments = id3v2Tag.getComment();
//                             mydb.addSongs(album, title, artist,year,genre,comments);
//                             System.out.println("This is the title "+title);
//                            if (value instanceof File) {
//                                File f = (File) value;
//                                if (row < 0) {
//                                    model.addRow(new Object[]{album, title,artist,year,genre,comments});
//                                } else {
//                                    model.insertRow(row, new Object[]{album, title,artist,year,genre,comments});
//                                    row++;
//                                }
//                            }
//                        }
//                    }
//                } catch (UnsupportedFlavorException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (SQLException ex) {
//                    Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (UnsupportedTagException ex) {
//                    Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (InvalidDataException ex) {
//                    Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            } else {
//                dtde.rejectDrop();
//            }
//        }
//
//    });
       
    
   
    }
            


    @Override
    public JTable getTable() {
        return playlist; //To change body of generated methods, choose Tools | Templates.
    }
    

    @Override // overrides the function from the main library 
    public void deleteSong() throws SQLException{
        System.out.println("goes into this overriden function");
        CurrentSelectedRow = playlist.getSelectedRow();
        String title = (String)playlist.getValueAt(CurrentSelectedRow, 1);
        String artist = (String)playlist.getValueAt(CurrentSelectedRow, 2);
        mydb.deleteSongFromPlaylist(userplaylist, title, artist);
        DefaultTableModel model = (DefaultTableModel) playlist.getModel();
        model.removeRow(CurrentSelectedRow);
        
    
    }
    
    
    @Override
    public void addSong(DefaultTableModel m) throws SQLException {
    System.out.println("goes into the overriden add song method");
    int returnValue = fc.showOpenDialog(this);
     
     if(returnValue == JFileChooser.APPROVE_OPTION) {
           System.out.println("goes into this conditional");
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
            mydb.addSongstoplaylist(userplaylist, title, artist);
            
            ArrayList<Integer> amount = mydb.SongRetriever(userplaylist);
            Object[][] data = mydb.getSongsFromPlaylist(userplaylist);
            String[] columns = {"Album", "Title", "Artist","Year","Genre","Comments"};    
            System.out.println("Amount of songs in the playlist " + amount.size());
//            DefaultTableModel model =  (DefaultTableModel) playlist.getModel();
//            playlist = new JTable(model);
            System.out.println("Number of rows in the model: " + m.getRowCount());
            System.out.println("Number of rows in the table: " + playlist.getRowCount());
            if (amount.size()> 0) {
                System.out.println("goes inside the greater than 0");
                System.out.println("Album: "+ album);
                m.insertRow(m.getRowCount(), new Object[]{album, title,artist,year,genre,comments});
            }
            else{
            m.addRow(new Object[]{album, title,artist,year,genre,comments});
            }
//            mydb.addSongstoplaylist(userplaylist, title, artist);

            

             
         } catch (IOException ex) {
             Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
         } catch (UnsupportedTagException ex) {
             Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
         } catch (InvalidDataException ex) {
             Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
         }
     }
    
    
    }
    
    

    
    
    
    
    
}
