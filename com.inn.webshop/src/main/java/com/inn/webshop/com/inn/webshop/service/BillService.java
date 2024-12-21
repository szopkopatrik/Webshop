package com.inn.webshop.com.inn.webshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.inn.webshop.com.inn.webshop.data.entity.BillEntity;
import com.inn.webshop.com.inn.webshop.data.repository.BillRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BillService {

    private static final String BILL_FILE_DIRECTORY = "C:\\Users\\Patrik\\Desktop\\Egyetem\\Egyetemmm\\Webshop\\com.inn.webshop\\Bills";

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Save a bill to the database
    public void saveBill(BillEntity billEntity) {
        billRepository.save(billEntity);
    }

    // Get all bills
    public List<BillEntity> getAllBills() {
        return billRepository.findAll();
    }

    // Get a bill by its ID
    public BillEntity getBillById(Integer id) {
        return billRepository.findById(id).orElse(null);
    }

    public byte[] getBillPdfById(Integer id) {
        // Logic to fetch the PDF file, e.g., reading from disk, generating dynamically, etc.
        // This is just a placeholder example, you will need to implement the actual logic
        try {
            Path pdfPath = Paths.get("C:\\Users\\Patrik\\Desktop\\Egyetem\\Egyetemmm\\Webshop\\com.inn.webshop\\Bills", "uuid_" + id + ".pdf");
            return Files.readAllBytes(pdfPath);
        } catch (IOException e) {
            return null; // Return null if the PDF doesn't exist or an error occurs
        }
    }

    public boolean deleteBillById(Integer id) {
        // Check if the bill exists in the repository
        Optional<BillEntity> bill = billRepository.findById(id);

        if (bill.isPresent()) {
            // Perform the deletion
            billRepository.delete(bill.get());
            return true; // Return true if the bill was successfully deleted
        } else {
            return false; // Return false if the bill doesn't exist
        }
    }

    // Generate a PDF for a bill
    public void saveBillToPdf(BillEntity billEntity) throws DocumentException, IOException {
        // Create a Document object
        Document document = new Document();

        // Create a file name based on the UUID or ID of the bill
        String fileName = "uuid_" + billEntity.getUuid() + ".pdf";

        // Ensure the directory exists
        File directory = new File(BILL_FILE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }

        // Create a PDF file
        File billFile = new File(directory, fileName);

        // Create a PdfWriter instance
        PdfWriter.getInstance(document, new FileOutputStream(billFile));

        // Open the document to start writing content
        document.open();

        // Fonts
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font contentFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        // Add Title
        Paragraph title = new Paragraph("Webshop Management System", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(Chunk.NEWLINE);

        // Add Customer Details
        document.add(new Paragraph("Name: " + billEntity.getName(), contentFont));
        document.add(new Paragraph("Contact Number: " + billEntity.getContactNumber(), contentFont));
        document.add(new Paragraph("Email: " + billEntity.getEmail(), contentFont));
        document.add(new Paragraph("Payment Method: " + billEntity.getPaymentMethod(), contentFont));

        document.add(Chunk.NEWLINE);

        // Create a table for product details
        PdfPTable table = new PdfPTable(5); // 5 columns: Name, Category, Quantity, Price, Subtotal
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add table headers
        String[] headers = {"Name", "Category", "Quantity", "Price", "Sub Total"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.YELLOW);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Parse productDetail JSON string
        String productDetailJson = billEntity.getProductDetail();
        List<Map<String, Object>> products = objectMapper.readValue(productDetailJson, new TypeReference<List<Map<String, Object>>>() {});

        for (Map<String, Object> product : products) {
            table.addCell(new PdfPCell(new Phrase(product.get("name").toString(), contentFont)));
            table.addCell(new PdfPCell(new Phrase(product.get("category").toString(), contentFont)));
            table.addCell(new PdfPCell(new Phrase(product.get("quantity").toString(), contentFont)));
            table.addCell(new PdfPCell(new Phrase(product.get("price").toString(), contentFont)));
            table.addCell(new PdfPCell(new Phrase(product.get("subtotal").toString(), contentFont))); // Changed to 'subtotal'
        }

        // Add table to document
        document.add(table);

        // Add total
        document.add(new Paragraph("Total: " + billEntity.getTotal(), headerFont));

        document.add(Chunk.NEWLINE);

        // Add footer message
        Paragraph footer = new Paragraph("Thank you for visiting. Please visit again!!", contentFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        // Close the document
        document.close();
    }
}
