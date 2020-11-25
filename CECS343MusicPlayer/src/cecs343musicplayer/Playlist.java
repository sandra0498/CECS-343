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
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sandra C
 */
public class Playlist extends Library {
    DefaultTableModel tm;
    TransferHandler th;
    private final DataFlavor localObjectFlavor = null;
    private JComponent source;
    private int[] indices;
    String playlistname;
    private int addIndex = -1; //Location where items were added
    private int addCount; //Number of items added.
    
    Playlist() throws SQLException{
    String[] columns = {"Album", "Title", "Artist","Year","Genre","Comments"};    
    
    JTable playlist = new JTable();
    
    playlist.setDropTarget(new DropTarget() {
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
                        playlist.clearSelection();
                        Point point = dtde.getLocation();
                        int row = playlist.rowAtPoint(point);
                        DefaultTableModel model = (DefaultTableModel) playlist.getModel();
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
    
    // drag n drop

//    getTable().setTransferHandler(th);
//    
//     th = new TransferHandler(){
//  @Override protected Transferable createTransferable(JComponent c) {
//    source = c;
//    JTable table = (JTable) c;
//    DefaultTableModel model = (DefaultTableModel) table.getModel();
//    List<Object> list = new ArrayList<>();
//    indices = table.getSelectedRows();
//    for (int i : indices) {
//      list.add(model.getDataVector().elementAt(i));
//    }
//    Object[] transferredObjects = list.toArray();
//    return new Transferable() {
//      @Override public DataFlavor[] getTransferDataFlavors() {
//        return new DataFlavor[] {};
//      }
//      @Override public boolean isDataFlavorSupported(DataFlavor flavor) {
//        return Objects.equals(list, flavor);
//      }
//      @Override public Object getTransferData(DataFlavor flavor)
//            throws UnsupportedFlavorException, IOException {
//        if (isDataFlavorSupported(flavor)) {
//          return true;
//        } else {
//          throw new UnsupportedFlavorException(flavor);
//        }
//      }
//    };
//    }
//  
//  
//  @Override public boolean importData(TransferHandler.TransferSupport info) {
//    TransferHandler.DropLocation tdl = info.getDropLocation();
//    if (!(tdl instanceof JTable.DropLocation)) {
//      return false;
//    }
//    JTable.DropLocation dl = (JTable.DropLocation) tdl;
//    JTable target = (JTable) info.getComponent();
//    DefaultTableModel model = (DefaultTableModel) target.getModel();
//    int index = dl.getRow();
//    //boolean insert = dl.isInsert();
//    int max = model.getRowCount();
//    if (index < 0 || index > max) {
//      index = max;
//    }
//    addIndex = index;
//    target.setCursor(Cursor.getDefaultCursor());
//    try {
//      Object[] values =
//        (Object[]) info.getTransferable().getTransferData(localObjectFlavor);
//      if (Objects.equals(source, target)) {
//        addCount = values.length;
//      }
//      for (int i = 0; i < values.length; i++) {
//        int idx = index++;
//        // model.insertRow(idx, (Vector<?>) values[i]);
////        model.insertRow(i, );
//        target.getSelectionModel().addSelectionInterval(idx, idx);
//      }
//      return true;
//    } catch (UnsupportedFlavorException | IOException ex) {
//      ex.printStackTrace();
//    }
//    return false;
//  }
//    };
//    
////
////    
////   
////    dragndrop();
//    
////    Library.getTable().setTransferHandler(new Transfer(selectedPlaylistID));
//
//    
//    }
//
//    public void dragndrop(){
//   
//    TransferHandler th = new TransferHandler(){
//  @Override protected Transferable createTransferable(JComponent c) {
//    source = c;
//    JTable table = (JTable) c;
//    DefaultTableModel model = (DefaultTableModel) table.getModel();
//    List<Object> list = new ArrayList<>();
//    indices = table.getSelectedRows();
//    for (int i : indices) {
//      list.add(model.getDataVector().elementAt(i));
//    }
//    Object[] transferredObjects = list.toArray();
//    return new Transferable() {
//      @Override public DataFlavor[] getTransferDataFlavors() {
//        return new DataFlavor[] {};
//      }
//      @Override public boolean isDataFlavorSupported(DataFlavor flavor) {
//        return Objects.equals(list, flavor);
//      }
//      @Override public Object getTransferData(DataFlavor flavor)
//            throws UnsupportedFlavorException, IOException {
//        if (isDataFlavorSupported(flavor)) {
//          return true;
//        } else {
//          throw new UnsupportedFlavorException(flavor);
//        }
//      }
//    };
//    }
//  
//  
//  @Override public boolean importData(TransferSupport info) {
//    TransferHandler.DropLocation tdl = info.getDropLocation();
//    if (!(tdl instanceof JTable.DropLocation)) {
//      return false;
//    }
//    JTable.DropLocation dl = (JTable.DropLocation) tdl;
//    JTable target = (JTable) info.getComponent();
//    DefaultTableModel model = (DefaultTableModel) target.getModel();
//    int index = dl.getRow();
//    //boolean insert = dl.isInsert();
//    int max = model.getRowCount();
//    if (index < 0 || index > max) {
//      index = max;
//    }
//    addIndex = index;
//    target.setCursor(Cursor.getDefaultCursor());
//    try {
//      Object[] values =
//        (Object[]) info.getTransferable().getTransferData(localObjectFlavor);
//      if (Objects.equals(source, target)) {
//        addCount = values.length;
//      }
//      for (int i = 0; i < values.length; i++) {
//        int idx = index++;
//        // model.insertRow(idx, (Vector<?>) values[i]);
////        model.insertRow(i, );
//        target.getSelectionModel().addSelectionInterval(idx, idx);
//      }
//      return true;
//    } catch (UnsupportedFlavorException | IOException ex) {
//      ex.printStackTrace();
//    }
//    return false;
//  }
//    };
    
    }
            


    @Override
    public JTable getTable() {
        return super.getTable(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
    
}
