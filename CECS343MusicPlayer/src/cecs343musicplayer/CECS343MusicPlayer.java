/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cecs343musicplayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Sandra C
 */
public class CECS343MusicPlayer {


    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws SQLException {

        App app = new App();
        app.setVisible(true);

    }
    
}
