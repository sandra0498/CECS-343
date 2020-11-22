package cecs343musicplayer;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;




import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
//import java.lang.System.Logger;
//import java.lang.System.Logger.Level;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Serenity Brown
 * @author Sandra Chavez
 */
public class MyDB {
   String user = "root";
   String password = "";
   String url = "jdbc:mysql://localhost:3306/mytunes";
   
   private ArrayList<String> Playlists;
   Connection connection;
   PreparedStatement statement;
   
   public void Connect(){
       System.out.println("Connecting to my tunes...");
       try{
           connection = DriverManager.getConnection(url, user, password);
           System.out.println("Connected to mytunes");
       }catch (SQLException ex){
           System.out.println("Connection unsuccessful");
       }
   }
   public Object[][] getSongs() throws SQLException{
       String stat = "SELECT * FROM playlist";
      statement = connection.prepareStatement(stat,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
      Object songs[][] = null;
      
    
//      ResultSet rs = statement.executeQuery();
     if (statement.execute(stat)) {
        ResultSet result = statement.getResultSet();
        
     
      int j = 0;
       
       result.last();
       int length = result.getRow();
       result.beforeFirst();
       System.out.println("Amount of rows "+length);
       
       if(length > 0 ) {
       songs = new Object[length][6];
        while(result.next()){

         songs[j][0] = result.getString("Album");
         songs[j][1] = result.getString("Title");
         songs[j][2] = result.getString("Artist");
         songs[j][3] = result.getString("Year");
         songs[j][4] = result.getString("Genre");
         songs[j][5] = result.getString("Comments");
         j++;  
       }  
        
     }
       else {
       
       songs = new Object[0][6];
       } 


     }
      return songs;
    }
   
   public void addSongs(String album, String Title, String Artist, String Year, 
           String Genre, String Comments) throws SQLException {
       
       String stat = "INSERT INTO playlist(Album,Title,Artist,Year,Genre,Comments) Values(?,?,?,?,?,?)";
       PreparedStatement ps  = connection.prepareStatement(stat);
       
       ps.setString(1,album);
       ps.setString(2, Title);
       ps.setString(3,Artist);
       ps.setString(4,Year);
       ps.setString(5,Genre);
       ps.setString(6,Comments);
       
       
       ps.executeUpdate();
            
   }
   
   public void deleteSongs(String album, String Title, String Artist) throws SQLException {
   
   String stat = "DELETE FROM playlist where Album=? and Title=? and Artist=?";
      PreparedStatement ps = connection.prepareStatement(stat);

      ps.setString(1, album);
      ps.setString(2, Title);
      ps.setString(3, Artist);
      ps.executeUpdate();
   
   
   }
   
      public void createPlaylistTable(String playlist) throws SQLException{
      java.sql.Statement st = null;
       st =  connection.createStatement();
       String playlistTable=  "CREATE TABLE REGISTRATION " +
                                "(Album VARCHAR(30), " +
                                "Title VARCHAR(30)," +
                                "Artist VARCHAR(30)," +
                                "Year VARCHAR(30)," +
                                "Genre VARCHAR(30),"+
                                "Comments VARCHAR(30)," +
                                "PRIMARY KEY (Title))";
       st.executeUpdate(playlistTable); 
   }
      
    public void addPlayList(String name) {
        if (!Playlists.contains(name)) {
            Playlists.add(name);
        }
    }
      
    public ArrayList<String> getPlaylists() {
        
        return Playlists;
    }
   
   
  
   
}
