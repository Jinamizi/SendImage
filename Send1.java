package mine;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Document;

public class Send1 extends JFrame implements ActionListener{
    JButton sendButton;
    JButton browseButton;
    JTextField field;
    Send1(){
        field = new JTextField(20);
        field.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent event ){
                sendButton.setEnabled(!field.getText().equals("")); //enable send button if data field set
            }
        });
        
        field.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                sendButton.setEnabled(!field.getText().equals(""));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sendButton.setEnabled(!field.getText().equals(""));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                sendButton.setEnabled(!field.getText().equals(""));
            }
        });
        
        browseButton = new JButton("...");
        browseButton.addActionListener(new FileSelector());
        
        JPanel holder = new JPanel();
        holder.add(field);
        holder.add(browseButton);
        add(holder, BorderLayout.CENTER);
        
        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        sendButton.addActionListener(this);
        
        add(sendButton, BorderLayout.PAGE_END);
        
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String filePath = field.getText();
        
        File file = new File(filePath);
        if(!file.exists()){
            JOptionPane.showMessageDialog(null, "\"" + file.getPath()+"\" does not exist", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Sender.send(filePath);
            setCursor(null); //set cursor to default;
            JOptionPane.showMessageDialog(null, "File sent");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    class FileSelector implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser("C:\\tonny\\fingerprint idea\\samples");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG files", "jpg","png");
            fileChooser.setFileFilter(filter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); //only files to be allowed
        
            int result = fileChooser.showOpenDialog(null);
        
            //if user clicked Cancel button on dialog, return
            if (result == JFileChooser.CANCEL_OPTION )
                return;
        
            File fileName = fileChooser.getSelectedFile(); //get file
        
            //display error if invalid
            if ((fileName == null ) || (fileName.getName().equals(""))){
                JOptionPane.showMessageDialog(null, "Invalid Name", "error", JOptionPane.ERROR_MESSAGE);
                return ;
            }
        
            Send1.this.field.setText(fileName.getPath());
           
        }
        
    }
    
    
    
    public static void main(String[] args) {
        Send1 send = new Send1();
        send.setVisible(true);
    }
}

class Sender {
    private Sender(){}
        static void send(String filePath) throws IOException{
            Socket socket;
            OutputStream outputStream ;
            //connect to server
            try{
                socket = new Socket("127.0.0.1", 50001);
                outputStream = socket.getOutputStream(); //get the output stream of the socket
            }catch(IOException e){
                throw new IOException("Error connecting to server");
            }
            
            ByteArrayOutputStream baos; //used to store the image
            //create image
            try{
                BufferedImage bufferedImage = ImageIO.read(new File(filePath)); //read the image and decode it
                
                baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", baos);
            }catch(IOException e){
                throw new IOException("Error creating image");
            }
            
            //send image
            try{
                byte[] size = ByteBuffer.allocate(4).putInt(baos.size()).array(); //create 4 byte buffers and store image size in them
                outputStream.write(size);
                outputStream.write(baos.toByteArray());
                outputStream.flush();
                System.out.println("Sent");
            } catch(IOException e){
                throw new IOException("Error sending image");
            }
        }
    }
