
package cecs343lab3;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sandra Chavez
 * CECS 343 LAB 3 
 */
 public class StreamPlayerGUI extends JFrame {

        BasicPlayer player;

        JPanel main;

        JButton rock, the80s, purDeutsch;//the three buttons

        JLabel nowPlaying;

        ButtonListener bl, b2, b3;
       

    public StreamPlayerGUI() {

    player = new BasicPlayer();

    main = new JPanel();

    main.setLayout(new FlowLayout());

    bl = new ButtonListener();

    rock = new JButton("Rock");

    rock.addActionListener(bl);

    //create the other two buttons and assign them an action listener
    b2 = new ButtonListener();
    the80s = new JButton("The 80s");
    the80s.addActionListener(b2);
    
    b3 = new ButtonListener();
    purDeutsch = new JButton("Pur Deutsch");
    purDeutsch.addActionListener(b3);


    nowPlaying = new JLabel("Now playing: nothing");


    main.add(rock);

    main.add(the80s);

    main.add(purDeutsch);

    main.add(nowPlaying);

    this.setTitle("StreamPlayer by Sandra Chavez");//change the name to yours

    this.setSize(400, 150);

    this.add(main);

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }


    class ButtonListener implements ActionListener {


    @Override

    public void actionPerformed(ActionEvent e) {
     String station = null;
    String url=null;

    if("Rock".equals(e.getActionCommand())){
        station = "Rock";
        System.out.println("Rock");
        url = "https://mp3.ffh.de/ffhchannels/hqrock.mp3";
    }
    //create if, output and url assignment statements for the other two channels
    else if ("The 80s".equalsIgnoreCase(e.getActionCommand())){
        station = "The 80s";
        System.out.println("the80s");
        url = "https://mp3.ffh.de/ffhchannels/hq80er.mp3";
    }
    else if ("Pur Deutsch".equalsIgnoreCase(e.getActionCommand())){
        station ="Pur Deutsch";
        System.out.println("purDeutsch");
        url = "https://mp3.ffh.de/ffhchannels/hqdeutsch.mp3";
    }
    try {
    nowPlaying.setText(String.format("Now playing: %s", station));

    player.open(new URL(url));

    player.play();

    } 
    catch (MalformedURLException ex) {
     Logger.getLogger(StreamPlayerGUI.class.getName()).log(Level.SEVERE, null, ex);
    System.out.println("Malformed url");

    }
    catch (BasicPlayerException ex) {

    System.out.println("BasicPlayer exception");

    Logger.getLogger(StreamPlayerGUI.class.getName()).log(Level.SEVERE, null, ex);

}

}

}

}