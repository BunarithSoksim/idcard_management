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
        BufferedImage img = barcode.createCompatibleImage(barcode.getWidth(), barcode.getHeight());
        barcode.draw(img.getGraphics(), 0, 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", out);
        return out.toByteArray();
    }
}