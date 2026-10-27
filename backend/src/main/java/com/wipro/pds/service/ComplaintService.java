package com.wipro.pds.service;

import com.wipro.pds.dto.ComplaintRequest;
import com.wipro.pds.dto.ComplaintResolveRequest;
import com.wipro.pds.entity.*;
import com.wipro.pds.exception.ResourceNotFoundException;
import com.wipro.pds.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintHistoryRepository historyRepository;
    private final CardHolderRepository cardHolderRepository;
    private final ShopRepository shopRepository;

    public ComplaintService(ComplaintRepository complaintRepository,
                            ComplaintHistoryRepository historyRepository,
                            CardHolderRepository cardHolderRepository,
                            ShopRepository shopRepository) {
        this.complaintRepository = complaintRepository;
        this.historyRepository = historyRepository;
        this.cardHolderRepository = cardHolderRepository;
        this.shopRepository = shopRepository;
    }

    public Page<Complaint> getAllComplaints(Pageable pageable) {
        return complaintRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public List<Complaint> getComplaintsByShop(Long shopId) {
        return complaintRepository.findByShop_ShopId(shopId);
    }

    public Complaint getComplaintById(Long complaintId) {
        return complaintRepository.findById(complaintId)
            .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + complaintId));
    }

    public List<ComplaintHistory> getComplaintHistory(Long complaintId) {
        return historyRepository.findByComplaint_ComplaintIdOrderByActionTimestampAsc(complaintId);
    }

    @Transactional
    public Complaint registerComplaint(ComplaintRequest request, String registeredBy) {
        Complaint complaint = new Complaint();
        String ref = "GRV-" + System.currentTimeMillis() % 1000000;
        complaint.setComplaintRef(ref);

        if (request.getCardId() != null) {
            CardHolder card = cardHolderRepository.findById(request.getCardId()).orElse(null);
            complaint.setCardHolder(card);
        }

        if (request.getShopId() != null) {
            Shop shop = shopRepository.findById(request.getShopId()).orElse(null);
            complaint.setShop(shop);
        }

        complaint.setCategory(request.getCategory());
        complaint.setDescription(request.getDescription());
        complaint.setLanguage(request.getLanguage() != null ? request.getLanguage() : "en");
        complaint.setPriority(request.getPriority() != null ? request.getPriority() : "MEDIUM");
        complaint.setStatus("REGISTERED");
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setUpdatedAt(LocalDateTime.now());

        Complaint saved = complaintRepository.save(complaint);

        // Record initial history
        ComplaintHistory ch = new ComplaintHistory();
        ch.setComplaint(saved);
        ch.setPreviousStatus(null);
        ch.setNewStatus("REGISTERED");
        ch.setChangedByUser(registeredBy);
        ch.setActionTaken("Complaint registered with ref: " + ref);
        ch.setActionTimestamp(LocalDateTime.now());
        ch.setComments("Initial citizen grievance registration");
        historyRepository.save(ch);

        return saved;
    }

    @Transactional
    public Complaint assignOfficer(Long complaintId, Long officerId, String officerUsername) {
        Complaint complaint = getComplaintById(complaintId);
        String oldStatus = complaint.getStatus();

        complaint.setAssignedOfficerId(officerId);
        complaint.setStatus("IN_INVESTIGATION");
        complaint.setUpdatedAt(LocalDateTime.now());
        Complaint updated = complaintRepository.save(complaint);

        ComplaintHistory ch = new ComplaintHistory();
        ch.setComplaint(updated);
        ch.setPreviousStatus(oldStatus);
        ch.setNewStatus("IN_INVESTIGATION");
        ch.setChangedByUser(officerUsername);
        ch.setActionTaken("Assigned to Officer ID #" + officerId);
        ch.setActionTimestamp(LocalDateTime.now());
        historyRepository.save(ch);

        return updated;
    }

    @Transactional
    public Complaint resolveComplaint(Long complaintId, ComplaintResolveRequest request, String officerUsername) {
        Complaint complaint = getComplaintById(complaintId);
        String oldStatus = complaint.getStatus();

        complaint.setResolutionNotes(request.getResolutionNotes());
        complaint.setStatus(request.getStatus() != null ? request.getStatus() : "RESOLVED");
        complaint.setResolvedAt(LocalDateTime.now());
        complaint.setUpdatedAt(LocalDateTime.now());
        Complaint updated = complaintRepository.save(complaint);

        ComplaintHistory ch = new ComplaintHistory();
        ch.setComplaint(updated);
        ch.setPreviousStatus(oldStatus);
        ch.setNewStatus(complaint.getStatus());
        ch.setChangedByUser(officerUsername);
        ch.setActionTaken("Complaint resolved");
        ch.setActionTimestamp(LocalDateTime.now());
        ch.setComments(request.getResolutionNotes());
        historyRepository.save(ch);

        return updated;
    }
}
