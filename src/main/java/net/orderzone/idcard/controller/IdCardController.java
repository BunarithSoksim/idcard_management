package net.orderzone.idcard.controller;

import net.orderzone.idcard.model.Profile;
import net.orderzone.idcard.service.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping
public class IdCardController {

    private final ProfileService profileService;
    private final PdfExportService pdfExportService;
    private final QrCodeService qrCodeService;
    private final BarcodeService barcodeService;

    public IdCardController(ProfileService profileService,
                             PdfExportService pdfExportService,
                             QrCodeService qrCodeService,
                             BarcodeService barcodeService) {
        this.profileService  = profileService;
        this.pdfExportService = pdfExportService;
        this.qrCodeService   = qrCodeService;
        this.barcodeService  = barcodeService;
    }

    /** Live preview page */
    @GetMapping("/preview/{id}")
    public String preview(@PathVariable Long id, Model model) {
        Profile profile = profileService.findById(id);
        model.addAttribute("profile", profile);
        return "idcard/preview";       // Thymeleaf template
    }

    /** Verification page (QR code lands here) */
    @GetMapping("/verify/{uuid}")
    public String verify(@PathVariable String uuid, Model model) {
        Profile profile = profileService.findByUuid(uuid);
        model.addAttribute("profile", profile);
        return "idcard/verify";
    }

    /** Single PDF export */
    @GetMapping(value = "/export/pdf/{id}", produces = "application/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) throws Exception {
        Profile profile = profileService.findById(id);
        byte[] pdf = pdfExportService.exportSingle(profile);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"idcard-" + profile.getRegistrationNumber() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    /** Batch PDF export for a department */
    @GetMapping(value = "/export/pdf/batch", produces = "application/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> exportBatch(@RequestParam String department) throws Exception {
        List<Profile> profiles = profileService.findByDepartment(department);
        byte[] pdf = pdfExportService.exportBatch(profiles);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"idcards-" + department + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    /** QR code image for a profile */
    @GetMapping(value = "/qr/{uuid}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] qrCode(@PathVariable String uuid) throws Exception {
        Profile p = profileService.findByUuid(uuid);
        return qrCodeService.generate("http://localhost:8080/verify/" + p.getUuid(), 200, 200);
    }

    /** Barcode image for a profile */
    @GetMapping(value = "/barcode/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] barcode(@PathVariable Long id) throws Exception {
        Profile p = profileService.findById(id);
        return barcodeService.generate(p.getRegistrationNumber(), p.getBarcodeType());
    }
}