package de.sep.server.adapter;

import de.sep.server.errors.NotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class ImageAdapterTest {

    ImageAdapter imageAdapter;
    BufferedImage testImage;

    @Before
    public void onSetup() throws SQLException {
        testImage = new BufferedImage(16,16,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = testImage.createGraphics();
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0,0,8,8);
        graphics2D.fillRect(8,8,16,16);
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(8,0,16,8);
        graphics2D.fillRect(0,8,8,16);
        graphics2D.dispose();
        imageAdapter = new ImageAdapter();
    }


    @Test
    public void addImageTest() throws SQLException, IOException, NotFoundException {
        //given
        String uuid = imageAdapter.generateUUID(imageAdapter.getImageBytes(testImage));
        //when
        imageAdapter.addImage(testImage);
        //then
        assertTrue(imageAdapter.imageExists(uuid));
    }

    @Test
    public void getImageTest() throws NotFoundException, SQLException, IOException {
        //given
        int imageId = imageAdapter.addImage(testImage);
        //when
        BufferedImage image = imageAdapter.getImageById(imageId);
        //then
        assertArrayEquals(imageAdapter.getImageBytes(testImage),imageAdapter.getImageBytes(image));
    }

    @Test
    public void storageDirectoryCreatedTest(){
        assertTrue(new File(ImageAdapter.storageLocation + ImageAdapter.storagePath).exists());
    }

    @Test
    public void generateUuidTest(){
        byte[] bytes = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
        String firstUuidResult = imageAdapter.generateUUID(bytes);
        System.out.println(firstUuidResult);
        String secondUuidResult = imageAdapter.generateUUID(bytes);
        System.out.println(secondUuidResult);
        assertEquals(firstUuidResult, secondUuidResult);
    }

}
