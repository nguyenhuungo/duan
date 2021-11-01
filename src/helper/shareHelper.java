/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.awt.Image;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.ImageIcon;
import model.nhanvien;
/**
 *
 * @author Admin
 */
public class shareHelper {
    public static final Image APP_ICON;
 public static final ImageIcon APP_ICON_1;
 static{ 
     String file = "/icon/fpt.png";     
     APP_ICON = new ImageIcon(shareHelper.class.getResource(file)).getImage();
     APP_ICON_1 = new ImageIcon(shareHelper.class.getResource(file));
 }


 public static boolean saveLogo(File file){
     File dir = new File("logos");  
     if(!dir.exists()){
        dir.mkdirs();
     }
     File newFile = new File(dir, file.getName());
     try {
         // Copy vào thư mục logos (đè nếu đã tồn tại)
         Path source = Paths.get(file.getAbsolutePath());
         Path destination = Paths.get(newFile.getAbsolutePath());
         Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
         return true;
     }
     catch (Exception ex) {
        return false;
     }
 }
 public static ImageIcon readLogo(String fileName){
    File path = new File("logos", fileName);
    return new ImageIcon(new ImageIcon(path.getAbsolutePath()).getImage().getScaledInstance(180, 180, Image.SCALE_DEFAULT));
 }


 public static nhanvien USER = null;

 public static void logoff() {
    shareHelper.USER = null;
 }

 public static boolean authenticated() {
    return shareHelper.USER != null;
 }
}
