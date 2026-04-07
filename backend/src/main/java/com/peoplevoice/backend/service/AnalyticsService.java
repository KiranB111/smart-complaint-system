package com.peoplevoice.backend.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.peoplevoice.backend.dto.AnalyticsSummaryResponse;
import com.peoplevoice.backend.model.Complaint;
import com.peoplevoice.backend.model.ComplaintPriority;
import com.peoplevoice.backend.model.ComplaintStatus;
import com.peoplevoice.backend.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintService complaintService;

    public AnalyticsSummaryResponse buildSummary() {
        long total = complaintRepository.count();
        return new AnalyticsSummaryResponse(
                total,
                complaintRepository.countByStatus(ComplaintStatus.OPEN),
                complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS),
                complaintRepository.countByStatus(ComplaintStatus.RESOLVED),
                complaintService.averageResolutionHours(),
                aggregateByCategory(),
                aggregateByPriority(),
                aggregateByStatus()
        );
    }

    public byte[] exportPdf() {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, output);
            document.open();
            document.add(new Paragraph("People Voice Complaints Report"));
            PdfPTable table = new PdfPTable(5);
            table.addCell("Title");
            table.addCell("Category");
            table.addCell("Status");
            table.addCell("Priority");
            table.addCell("Citizen");
            for (Complaint complaint : complaintRepository.findAll()) {
                table.addCell(complaint.getTitle());
                table.addCell(complaint.getCategory());
                table.addCell(complaint.getStatus().name());
                table.addCell(complaint.getPriority().name());
                table.addCell(complaint.getCitizen().getName());
            }
            document.add(table);
            document.close();
            return output.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to export PDF report", exception);
        }
    }

    public byte[] exportExcel() {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Complaints");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Title");
            header.createCell(2).setCellValue("Category");
            header.createCell(3).setCellValue("Status");
            header.createCell(4).setCellValue("Priority");
            header.createCell(5).setCellValue("Created At");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            List<Complaint> complaints = complaintRepository.findAll();
            for (int i = 0; i < complaints.size(); i++) {
                Complaint complaint = complaints.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(complaint.getId());
                row.createCell(1).setCellValue(complaint.getTitle());
                row.createCell(2).setCellValue(complaint.getCategory());
                row.createCell(3).setCellValue(complaint.getStatus().name());
                row.createCell(4).setCellValue(complaint.getPriority().name());
                row.createCell(5).setCellValue(complaint.getCreatedAt().format(formatter));
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to export Excel report", exception);
        }
    }

    private List<Map<String, Object>> aggregateByCategory() {
        return complaintRepository.findAll().stream()
                .collect(Collectors.groupingBy(Complaint::getCategory, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("label", entry.getKey());
                    item.put("count", entry.getValue());
                    return item;
                })
                .toList();
    }

    private List<Map<String, Object>> aggregateByPriority() {
        List<Map<String, Object>> items = new ArrayList<>();
        Arrays.stream(ComplaintPriority.values()).forEach(priority -> {
            Map<String, Object> item = new HashMap<>();
            item.put("label", priority.name());
            item.put("count", complaintRepository.countByPriority(priority));
            items.add(item);
        });
        return items;
    }

    private List<Map<String, Object>> aggregateByStatus() {
        List<Map<String, Object>> items = new ArrayList<>();
        Arrays.stream(ComplaintStatus.values()).forEach(status -> {
            Map<String, Object> item = new HashMap<>();
            item.put("label", status.name());
            item.put("count", complaintRepository.countByStatus(status));
            items.add(item);
        });
        return items;
    }
}
