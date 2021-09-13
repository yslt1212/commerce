package de.sep.server.adapter;

import de.sep.server.errors.NotFoundException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ImageAdapter extends DataAdapter{

    static String storageLocation = System.getProperty("user.home");
    static String storagePath = "\\sep\\images";
    static String storageFormat = "png";
    static String uuid_column = "image_uuid";
    static String id_column = "image_id";
    File storageDirectory;


    public ImageAdapter() throws SQLException {
        super("image");
        // Check if the storage location exists
        File storageLocationDirectory = new File(storageLocation);
        if(!storageLocationDirectory.exists()){
            throw new RuntimeException("Could not find USER.HOME");
        }

        // Check if the storage directory exists, create it if not
        storageDirectory = new File(storageLocation + storagePath);
        try{
            if(!storageDirectory.exists()){
                System.out.printf("Creating image storage folder at %s",storagePath);
                if(!storageDirectory.mkdirs()){
                    throw new RuntimeException(String.format("Could not create image storage folder at %s", storagePath));
                }
            }
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    byte[] getImageBytes(RenderedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image,storageFormat,baos);
        return baos.toByteArray();
    }

    void clearImageStorage(){
        boolean success = true;
        for(File file : storageDirectory.listFiles()){
            boolean couldDelete = file.delete();
            if(!couldDelete){
                System.err.printf("Could not clear image storage because %s could not be deleted!\n",file.getAbsolutePath());
            }
            success = couldDelete && success;
        }
    }

    String generateUUID(byte[] bytes){
        return UUID.nameUUIDFromBytes(bytes).toString();
    }

    File getImageFile(String uuid){
        return new File(storageDirectory,String.format("%s.%s",uuid,storageFormat));
    }

    BufferedImage readImage(String uuid) throws IOException {
        File imageFile = getImageFile(uuid);
        System.out.println(imageFile.getAbsolutePath());
        return ImageIO.read(imageFile);
    }

    boolean imageExists(String uuid) throws SQLException {
        String imageExistsQuery = String.format("SELECT COUNT(1) FROM image WHERE %s LIKE '%s'",uuid_column,uuid);
        PreparedStatement statement = connection.prepareStatement(imageExistsQuery);
        try {
            ResultSet result = statement.executeQuery();
            if(result.next()){
                return result.getInt(1) == 1;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    int getImageIdByUuid(String uuid) throws SQLException, NotFoundException {
        String query = String.format("SELECT %s FROM image WHERE %s LIKE '%s'",id_column,uuid_column,uuid);
        ResultSet result = getQuery(query);
        if(result.next()){
            return result.getInt(id_column);
        }else{
            throw new NotFoundException(String.format("Could not find image with %s = %s",uuid_column,uuid));
        }
    }

    public BufferedImage getImageById(int imageId) throws SQLException, NotFoundException, IOException {
        String query = String.format("SELECT * FROM image WHERE %s = %d",id_column,imageId);
        ResultSet result = getQuery(query);
        if(result.next()){
            String uuid = result.getString(uuid_column);
            return readImage(uuid);
        }else{
            throw new NotFoundException(String.format("Could not find image with id %d",imageId));
        }
    }


    public int addImage(BufferedImage image) throws IOException, SQLException, NotFoundException {
        String imageUuid = generateUUID(getImageBytes(image));
        if(!imageExists(imageUuid)){
            File imageFile = getImageFile(imageUuid);
            System.out.printf("Storing Image into %s/n",imageFile.getAbsolutePath());
            if(!imageFile.exists()){
                ImageIO.write(image,storageFormat,imageFile);
            }
            String addImageQuery = String.format(
                    "INSERT INTO image (%s) VALUES ('%s')",uuid_column,imageUuid);
            return addQuery(addImageQuery);
        }else{
            return getImageIdByUuid(imageUuid);
        }
    }

}
