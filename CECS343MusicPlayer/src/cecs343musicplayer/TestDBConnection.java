/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cecs343musicplayer;
import java.sql.SQLException;
/**
 *
 * @author Seren
 */
public class TestDBConnection {
    public static void main(String[] args) throws SQLException{
       MyDB mydb = new MyDB();
        mydb.Connect();
//        mydb.getSongs();*/
    }
}
