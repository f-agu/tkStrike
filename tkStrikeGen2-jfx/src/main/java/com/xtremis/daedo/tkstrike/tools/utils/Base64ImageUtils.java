package com.xtremis.daedo.tkstrike.tools.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public final class Base64ImageUtils {
  public static BufferedImage decodeToImage(String imageString) {
    BufferedImage image = null;
    try {
      BASE64Decoder decoder = new BASE64Decoder();
      byte[] imageByte = decoder.decodeBuffer(imageString);
      ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
      image = ImageIO.read(bis);
      bis.close();
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return image;
  }
  
  public static Image decodeToFXImage(String imageString) {
    BufferedImage bf = decodeToImage(imageString);
    WritableImage wr = null;
    if (bf != null) {
      wr = new WritableImage(bf.getWidth(), bf.getHeight());
      PixelWriter pw = wr.getPixelWriter();
      for (int x = 0; x < bf.getWidth(); x++) {
        for (int y = 0; y < bf.getHeight(); y++)
          pw.setArgb(x, y, bf.getRGB(x, y)); 
      } 
    } 
    return (Image)wr;
  }
  
  public static String encodeToString(BufferedImage image, String type) {
    String imageString = null;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try {
      ImageIO.write(image, type, bos);
      byte[] imageBytes = bos.toByteArray();
      BASE64Encoder encoder = new BASE64Encoder();
      imageString = encoder.encode(imageBytes);
      bos.close();
    } catch (IOException e) {
      e.printStackTrace();
    } 
    return imageString;
  }
}
