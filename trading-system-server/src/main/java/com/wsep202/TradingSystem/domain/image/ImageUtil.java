package com.wsep202.TradingSystem.domain.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
public class ImageUtil {

    public static String saveImage(String imageUrl, MultipartFile file){
        String url = null;
        if (!StringUtils.isEmpty(imageUrl) && Objects.nonNull(file)) {
        try {
                InputStream is = file.getInputStream();
                Image image = ImageIO.read(is);

                BufferedImage bi = createResizedCopy(image, 180, true);
                ImageIO.write(bi, "jpg", new File(imageUrl));
                log.info("The image is saved");
                url = imageUrl;
            } catch(IOException e){
                log.error("Cant save the image", e);
            }
        }
        return url;
    }


    private static BufferedImage createResizedCopy(Image originalImage, int scaledHeight, boolean preserveAlpha) {
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(180, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, 180, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }
}
