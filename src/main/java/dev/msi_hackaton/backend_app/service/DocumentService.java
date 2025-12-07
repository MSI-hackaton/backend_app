package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dto.request.DocumentSignRequest;
import dev.msi_hackaton.backend_app.dto.response.DocumentResponse;
import dev.msi_hackaton.backend_app.entity.Document;
import dev.msi_hackaton.backend_app.entity.Order;
import dev.msi_hackaton.backend_app.repository.DocumentRepository;
import dev.msi_hackaton.backend_app.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final OrderRepository orderRepository;
    private final SMSService smsService;

    private static final String UPLOAD_DIR = "uploads/documents/";

    public DocumentService(DocumentRepository documentRepository,
                           OrderRepository orderRepository,
                           SMSService smsService) {
        this.documentRepository = documentRepository;
        this.orderRepository = orderRepository;
        this.smsService = smsService;

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для загрузки", e);
        }
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getOrderDocuments(Long orderId) {
        return documentRepository.findByOrder_Id(orderId).stream()
                .map(DocumentResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getPendingDocuments(Long orderId) {
        return documentRepository.findPendingDocumentsByOrderId(orderId).stream()
                .map(DocumentResponse::fromEntity)
                .toList();
    }

    @Transactional
    public DocumentResponse uploadDocument(Long orderId,
                                           MultipartFile file,
                                           String title,
                                           String description,
                                           Document.DocumentType type) throws IOException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = Paths.get(UPLOAD_DIR + fileName);

        Files.copy(file.getInputStream(), filePath);

        Document document = new Document();
        document.setOrder(order);
        document.setTitle(title);
        document.setDescription(description);
        document.setFileUrl("/api/v1/documents/file/" + fileName);
        document.setType(type);
        document.setStatus(Document.DocumentStatus.PENDING);

        Document saved = documentRepository.save(document);

        return DocumentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public byte[] downloadDocument(Long documentId) throws IOException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Документ не найден"));

        String fileName = document.getFileUrl().replace("/api/v1/documents/file/", "");
        Path filePath = Paths.get(UPLOAD_DIR + fileName);

        return Files.readAllBytes(filePath);
    }

    @Transactional
    public void sendSignRequest(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Документ не найден"));

        String phone = document.getOrder().getUser().getPhone();
        smsService.sendVerificationCode(phone);
    }

    @Transactional
    public DocumentResponse signDocument(Long documentId, DocumentSignRequest request) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Документ не найден"));

        String phone = document.getOrder().getUser().getPhone();
        boolean isValid = smsService.verifyCode(phone, request.getSmsCode());

        if (!isValid) {
            throw new RuntimeException("Неверный SMS код");
        }

        document.setStatus(Document.DocumentStatus.SIGNED);
        document.setSignedAt(LocalDateTime.now());
        document.setSignatureData("SIGNED_WITH_SMS_" + LocalDateTime.now());

        Document saved = documentRepository.save(document);

        checkAllDocumentsSigned(document.getOrder());

        return DocumentResponse.fromEntity(saved);
    }

    @Transactional
    public void createInitialDocuments(Order order) {
        Document contract = new Document();
        contract.setOrder(order);
        contract.setTitle("Договор подряда №" + order.getId());
        contract.setDescription("Договор на строительство объекта");
        contract.setType(Document.DocumentType.CONTRACT);
        contract.setFileUrl("/documents/contract_" + order.getId() + ".pdf");
        documentRepository.save(contract);

        Document estimate = new Document();
        estimate.setOrder(order);
        estimate.setTitle("Смета к договору №" + order.getId());
        estimate.setDescription("Детальная смета расходов");
        estimate.setType(Document.DocumentType.ESTIMATE);
        estimate.setFileUrl("/documents/estimate_" + order.getId() + ".pdf");
        documentRepository.save(estimate);

        Document schedule = new Document();
        schedule.setOrder(order);
        schedule.setTitle("График работ");
        schedule.setDescription("График выполнения строительных работ");
        schedule.setType(Document.DocumentType.WORK_SCHEDULE);
        schedule.setFileUrl("/documents/schedule_" + order.getId() + ".pdf");
        documentRepository.save(schedule);
    }

    @Transactional
    public byte[] generateArchive(Long orderId) throws IOException {
        List<Document> documents = documentRepository.findByOrder_Id(orderId);

        try (var baos = new java.io.ByteArrayOutputStream();
             var zos = new ZipOutputStream(baos)) {

            for (Document document : documents) {
                if (document.getFileUrl() != null && document.getFileUrl().startsWith("/api/v1/documents/file/")) {
                    String fileName = document.getFileUrl().replace("/api/v1/documents/file/", "");
                    Path filePath = Paths.get(UPLOAD_DIR + fileName);

                    if (Files.exists(filePath)) {
                        ZipEntry zipEntry = new ZipEntry(document.getTitle() + ".pdf");
                        zos.putNextEntry(zipEntry);
                        Files.copy(filePath, zos);
                        zos.closeEntry();
                    }
                }
            }

            zos.finish();
            return baos.toByteArray();
        }
    }

    @Transactional
    public void createFinalDocuments(Order order) {
        Document acceptanceAct = new Document();
        acceptanceAct.setOrder(order);
        acceptanceAct.setTitle("Акт приема-передачи №" + order.getId());
        acceptanceAct.setDescription("Акт сдачи-приемки выполненных работ");
        acceptanceAct.setType(Document.DocumentType.ACT_OF_ACCEPTANCE);
        acceptanceAct.setFileUrl("/documents/acceptance_act_" + order.getId() + ".pdf");
        documentRepository.save(acceptanceAct);

        Document warranty = new Document();
        warranty.setOrder(order);
        warranty.setTitle("Гарантийный сертификат №" + order.getId());
        warranty.setDescription("Гарантия на выполненные работы");
        warranty.setType(Document.DocumentType.WARRANTY_CERTIFICATE);
        warranty.setFileUrl("/documents/warranty_" + order.getId() + ".pdf");
        documentRepository.save(warranty);
    }

    private void checkAllDocumentsSigned(Order order) {
        List<Document> pendingDocuments = documentRepository.findPendingDocumentsByOrderId(order.getId());
        if (pendingDocuments.isEmpty()) {
            if (order.getStatus() == Order.OrderStatus.DOCUMENTS_PENDING) {
                order.setStatus(Order.OrderStatus.PREPARATION);
                orderRepository.save(order);
            } else if (order.getStatus() == Order.OrderStatus.FINAL_DOCUMENTS) {
                order.setStatus(Order.OrderStatus.WARRANTY);
                order.setActualEndDate(LocalDateTime.now());
                orderRepository.save(order);
            }
        }
    }

    @Transactional(readOnly = true)
    public byte[] getDocumentFile(String fileName) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Файл не найден: " + fileName);
        }
        return Files.readAllBytes(filePath);
    }
}