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
   
   //helper function for the getsongsfromplaylist
   public ArrayList<Integer> SongRetriever(String playlist) throws SQLException{
    ArrayList<Integer> songids = new ArrayList<>();
    String stat = "SELECT * from songs";
    PreparedStatement ps = connection.prepareStatement(stat);
    if (ps.execute(stat)){
        ResultSet set = ps.getResultSet();
        
        while(set.next()){
            String pl = set.getString("playlistName");
            if(pl.equals(playlist)){
                int id = set.getInt("songid");
                songids.add(id);
            }
        }
    }
   
   return songids;
   
   }
   //accesses songs from a specific playlist 
   public Object[][] getSongsFromPlaylist(String playlist) throws SQLException {
        Object songs[][] = null;
        
        String stat = "SELECT * FROM playlist";
        statement = connection.prepareStatement(stat,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
        ArrayList<Integer> songids = SongRetriever(playlist);
                //iterating through all the possible song ids 
  
            if(statement.execute(stat)){
            
                // this result set has the songs with the specific ids 
                ResultSet set = statement.getResultSet();
                
                int j = 0;  
                set.last();
                int length = set.getRow();
                set.beforeFirst();    
                if (length > 0) {
                  for(int i : songids){

                    while(set.next()) {
                        int id = set.getInt("songid");
                        //have to manually check since sql is giving problems
                        if (id == i){
                            
                            songs[j][0] = set.getString("Album");
                            songs[j][1] = set.getString("Title");
                            songs[j][2] = set.getString("Artist");
                            songs[j][3] = set.getString("Year");
                            songs[j][4] = set.getString("Genre");
                            songs[j][5] = set.getString("Comments");
                            j++; 
                     
                        }
 
                    }
                  
                  
                  
                  
                  }  

                }

                else {

                    songs = new Object[1][6];
                }
            }
            
            


        return songs;
   }
   
   //adds songs to the main library 
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
   
   //this function works 
   //need to check for duplicates 
   public void addSongstoplaylist(String playlist, String title, String artist) throws SQLException{
   
   int songid = getSongID(title, artist);
   ArrayList<Integer> songids = SongRetriever(playlist);
   //this checks if its already in the database 
    if (!songids.contains(songid)){
         String stat = "INSERT INTO songs(playlistName, songid) VALUES(?,?)";
         statement = connection.prepareStatement(stat);

         statement.setString(1, playlist);
         statement.setInt(2, songid);

         statement.executeUpdate();   


    }

   }
   
   
   
   //deletes songs from the main library 
   public void deleteSongs(String Title, String Artist) throws SQLException {
   
   int songid = getSongID(Title, Artist);
   String stat = "DELETE FROM playlist where songid =?";
//   String stat = "DELETE FROM playlist where Album=? and Title=? and Artist=?";
      PreparedStatement ps = connection.prepareStatement(stat);
      ps.setInt(1, songid);
//      ps.setString(1, album);
//      ps.setString(2, Title);
//      ps.setString(3, Artist);
      ps.executeUpdate();
   
   
   }
   
   //delete a specific song from a specific playlist
   public void deleteSongFromPlaylist(String playlistname, String songname){
   
   
   
   }
   
   
   public ArrayList<String> getCurrentPlaylists() throws SQLException {
       ArrayList<String> playlists = new ArrayList<>();
       
        String stat = "SELECT playlistName from userplaylists";
        statement = connection.prepareStatement(stat,ResultSet.TYPE_SCROLL_SENSITIVE, 
        ResultSet.CONCUR_UPDATABLE);


        if (statement.execute(stat)) {
            ResultSet result = statement.getResultSet();
       
            int j = 0;

             result.last();
             int length = result.getRow();
             result.beforeFirst();
           if(length > 0 ) {
                 while(result.next()){
                  playlists.add(result.getString("playlistname"));
                  j++;  
                }  
            } 
        }

        return playlists;
   }

   public void addPlaylistName(String nameOfPlaylist) throws SQLException{
      String stat = "INSERT INTO userplaylists(playlistName) Values(?)"; 
      PreparedStatement ps  = connection.prepareStatement(stat); 
      ps.setString(1,nameOfPlaylist);
      ps.executeUpdate();
   }
   
   public void DeletePlaylist(String name) throws SQLException{

         String stat = "DELETE FROM userplaylists where playlistName = ?";
         PreparedStatement ps = connection.prepareStatement(stat);
         ps.setString(1,name);
         ps.executeUpdate();


   
   }
   
   //gets the song id given the title and artist 
   public int getSongID(String songname, String artist) throws SQLException {
       int songid = 0;
       String name, artistname;
       String stat = "SELECT * FROM playlist";
       
       statement = connection.prepareStatement(stat);
       
       // still have to write code here 
       if (statement.execute(stat)) {
       ResultSet rs = statement.getResultSet();
        while(rs.next()){
        name = rs.getString("Title");
        artistname = rs.getString("Artist");
            if(name.equals(songname) && artistname.equals(artist)) {
                songid = rs.getInt("songid");

            }
        }
       }
       return songid;
   }

   
}
