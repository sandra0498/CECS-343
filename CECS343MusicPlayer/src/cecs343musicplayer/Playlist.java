/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cecs343musicplayer;

import java.sql.SQLException;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sandra C
 */
public class Playlist extends Library {
    DefaultTableModel tm;
    String playlistname;
    Playlist() throws SQLException{
    String[] columns = {"Album", "Title", "Artist","Year","Genre","Comments"};    
    
    JTable playlist = new JTable(dm);
    
    
    
    // drag n drop

    getTable().setTransferHandler(new TransferHandler("playlistname"));
    
//    Library.getTable().setTransferHandler(new Transfer(selectedPlaylistID));

    
    }

    @Override
    public JTable getTable() {
        return super.getTable(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
    
}
