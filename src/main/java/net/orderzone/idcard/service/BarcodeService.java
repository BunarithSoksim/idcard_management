package net.orderzone.idcard.service;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.output.OutputException;
import net.orderzone.idcard.model.BarcodeType;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
public class BarcodeService {

    public byte[] generate(String data, BarcodeType type) throws Exception {
        Barcode barcode;
        if (type == BarcodeType.EAN_13) {
            // EAN-13 requires exactly 12 digits (13th is check digit)
            String digits = data.replaceAll("[^0-9]", "");
            if (digits.length() > 12) digits = digits.substring(0, 12);
            while (digits.length() < 12) digits = "0" + digits;
            barcode = BarcodeFactory.createEAN13(digits);
        } else {
            barcode = BarcodeFactory.createCode128(data);
        }
        barcode.setDrawingText(true);

        BufferedImage img = new BufferedImage(
                barcode.getWidth(),
                barcode.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        java.awt.Graphics2D g2d = img.createGraphics();
        barcode.draw(g2d, 0, 0);
        g2d.dispose();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", out);
        return out.toByteArray();
    }
}