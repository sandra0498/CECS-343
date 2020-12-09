package miniplayertest;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dr. H
 */
public class OpenDlg extends JDialog {
    JFileChooser chooser;
    String filename;
    boolean Ok = false;
    
    public OpenDlg(){
        setSize(300, 300);
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("C:\\javaMP"));
        //use an anonymous ActionListener with 
        //actionPerformed embedded in addActionListener param
        chooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getFile(evt);
            }
            });
        add(chooser);
        setModal(true);
        
    }
    
    
    void getFile(ActionEvent e) {
        if(e.getActionCommand().equalsIgnoreCase("ApproveSelection")){ 
            filename = chooser.getSelectedFile().getAbsolutePath();
            Ok = true;
        }
        else
            Ok=false;
        this.setVisible(false);
    }
}
