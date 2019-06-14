package mine;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

public class Server1 implements Runnable{
    ServerSocket port;
    public int PORT_NUMBER = 50001;
    boolean running = false;
    
    public Server1() {
    }
    
    public void startServer() throws IOException{
        if(!running){ //start server if it is not running
            port = new ServerSocket(PORT_NUMBER); //initialize port
            Thread serverThread = new Thread(this);
            serverThread.start();
        }
    }
    
    public void stopServer() throws IOException{
        if(running){
            port.close();
            running = false;
        }
    }
    
    @Override
    public void run() {
        while(running) { //run continuously
            try(Socket socket = port.accept()){ //wait and accept sockets
                InputStream inputStream = socket.getInputStream(); //used to read data sent through the socket
                
                byte[] sizeAr = new byte[4]; //to hold the size of the image received
                inputStream.read(sizeAr); //read size of file into the byte
                int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
                
                byte [] imageAr = new byte[size]; //to hold the image in byte form
                inputStream.read(imageAr);
                
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr)); //put he image bytes into a bytearratinputstream and decode the data using ImageIO then return a bufferedImage
                
                System.out.println("Received " + image.getHeight() + "x" + image.getWidth() + ": " + System.currentTimeMillis());
                
                String location = getRightLocation(); //where to store the image
                File file = new File(location);
                ImageIO.write(image, "jpg", file); //store the image in hard drive
                
                Database.insert(file.getName(), location);
                
            } catch (IOException ex) {
            
            } catch(Exception e){
                System.err.println("error during insertion of image into database:-" + e.getMessage());
            }
        }
    }
    /**
     * used to get the right location to store the image
     * @return the right location
     */
    private String getRightLocation() {
        String directory = "C:\\xampp\\pictures"; //directory to store the image
        String name = "image"; //the name of the image
        String fileName = name; 
        int fileCount = 1;
        while(new File(directory, fileName).exists())
            fileName = name + ++fileCount;
        
        return new File(directory, fileName +".jpg").getPath();
    }
    
}
