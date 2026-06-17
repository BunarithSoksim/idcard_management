package net.orderzone.idcard.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import net.orderzone.idcard.model.Profile;
import net.orderzone.idcard.model.Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class PdfExportService {

    @Value("${app.base-url}")
    private String baseUrl;

    private final QrCodeService qrCodeService;
    private final BarcodeService barcodeService;
    private final PhotoStorageService photoStorage;

    public PdfExportService(QrCodeService qrCodeService,
                             BarcodeService barcodeService,
                             PhotoStorageService photoStorage) {
        this.qrCodeService = qrCodeService;
        this.barcodeService = barcodeService;
        this.photoStorage = photoStorage;
    }

    public byte[] exportSingle(Profile profile) throws Exception {
        return buildPdf(List.of(profile));
    }

    public byte[] exportBatch(List<Profile> profiles) throws Exception {
        return buildPdf(profiles);
    }

    private byte[] buildPdf(List<Profile> profiles) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(new Rectangle(243, 153)); // Credit-card size in points
        PdfWriter.getInstance(doc, out);
        doc.open();

        for (int i = 0; i < profiles.size(); i++) {
            if (i > 0) doc.newPage();
            addCard(doc, profiles.get(i));
        }

        doc.close();
        return out.toByteArray();
    }

    private void addCard(Document doc, Profile p) throws Exception {
        Template tmpl = p.getTemplate();
        BaseColor primary   = parseColor(tmpl != null ? tmpl.getPrimaryColor()   : "#1d4ed8");
        BaseColor secondary = parseColor(tmpl != null ? tmpl.getSecondaryColor() : "#e0e7ff");
        BaseColor textColor = parseColor(tmpl != null ? tmpl.getTextColor()      : "#111827");

        String orgName = tmpl != null && tmpl.getOrganizationName() != null
                ? tmpl.getOrganizationName() : "My Institution";

        PdfPTable card = new PdfPTable(2);
        card.setWidthPercentage(100);
        card.setWidths(new float[]{1.2f, 2.8f});

        // ── Left column: photo ──────────────────────────────────────
        PdfPCell photoCell = new PdfPCell();
        photoCell.setBackgroundColor(secondary);
        photoCell.setPadding(8);
        photoCell.setBorder(Rectangle.NO_BORDER);

        if (p.hasPhoto()) {
            try {
                Path photoPath = photoStorage.resolve(p.getPhotoFileName());
                Image img = Image.getInstance(Files.readAllBytes(photoPath));
                img.scaleToFit(70, 80);
                photoCell.addElement(img);
            } catch (Exception ignored) {}
        }
        card.addCell(photoCell);

        // ── Right column: details ────────────────────────────────────
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBackgroundColor(BaseColor.WHITE);
        infoCell.setPadding(8);
        infoCell.setBorder(Rectangle.NO_BORDER);

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, primary);
        Font labelFont  = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.GRAY);
        Font valueFont  = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, textColor);

        infoCell.addElement(new Paragraph(orgName, headerFont));
        infoCell.addElement(new Paragraph(p.getType().name() + " ID CARD", labelFont));
        infoCell.addElement(Chunk.NEWLINE);
        infoCell.addElement(new Paragraph(p.getFullName(), new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, textColor)));
        if (p.getDepartment() != null)
            infoCell.addElement(new Paragraph("Dept: " + p.getDepartment(), valueFont));
        if (p.getTitle() != null)
            infoCell.addElement(new Paragraph(p.getTitle(), valueFont));
        infoCell.addElement(new Paragraph("ID: " + p.getRegistrationNumber(), valueFont));
        if (p.getExpiryDate() != null)
            infoCell.addElement(new Paragraph("Valid: " + p.getExpiryDate(), valueFont));

        // QR code
        String qrContent = baseUrl + "/verify/" + p.getUuid();
        byte[] qrBytes = qrCodeService.generate(qrContent, 50, 50);
        Image qr = Image.getInstance(qrBytes);
        qr.scaleToFit(40, 40);
        infoCell.addElement(qr);

        card.addCell(infoCell);

        // ── Header bar ───────────────────────────────────────────────
        PdfPTable wrapper = new PdfPTable(1);
        wrapper.setWidthPercentage(100);
        PdfPCell headerBar = new PdfPCell(new Phrase(orgName + " · " + p.getType().name(), headerFont));
        headerBar.setBackgroundColor(primary);
        headerBar.setPadding(4);
        headerBar.setBorder(Rectangle.NO_BORDER);
        wrapper.addCell(headerBar);

        doc.add(wrapper);
        doc.add(card);
    }

    private BaseColor parseColor(String hex) {
        try {
            hex = hex.replace("#", "");
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return new BaseColor(r, g, b);
        } catch (Exception e) {
            return BaseColor.DARK_GRAY;
        }
    }
}