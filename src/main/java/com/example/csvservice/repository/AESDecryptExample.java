
package com.cardif.external.service.charges.impl;

import com.cardif.external.model.ExternalReceiptsDTO;
import com.cardif.external.model.TblLoadThirdInfo;
import com.cardif.external.repository.LoadThirdRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ProcessFileThirdPartnerServiceTest {

    @InjectMocks
    private ProcessFileThirdPartnerService service;

    @Mock
    private LoadThirdRepo loadThirdRepo;

    private TblLoadThirdInfo createTblLoadThirdInfo(String policyNumber, String chargeAmount) {
        TblLoadThirdInfo info = new TblLoadThirdInfo();
        info.setPolicyNumber(policyNumber);
        info.setChargeAmount(chargeAmount);
        info.setQuote("0");
        return info;
    }

    private ExternalReceiptsDTO createReceipt(String policyNumber, BigDecimal grossAmount, String status) {
        ExternalReceiptsDTO dto = new ExternalReceiptsDTO();
        dto.setPolicyNumber(policyNumber);
        dto.setGrossAmount(grossAmount);
        dto.setStatus(status);
        return dto;
    }

    @Test
    void shouldCreateSingleRecordOnExactMatch() {
        TblLoadThirdInfo info = createTblLoadThirdInfo("POL123", "100.00");
        Page<TblLoadThirdInfo> page = new PageImpl<>(List.of(info));
        ExternalReceiptsDTO receipt = createReceipt("POL123", new BigDecimal("100.00"), "PENDING");

        List<ExternalReceiptsDTO> receipts = List.of(receipt);

        List<TblLoadThirdInfo> result = service.validCopelReceipts(page, true, receipts);

        assertEquals(1, result.size());
        verify(loadThirdRepo, times(1)).save(any(TblLoadThirdInfo.class));
    }

    @Test
    void shouldCreateMultipleRecordsWhenChargeIsMultiple() {
        TblLoadThirdInfo info = createTblLoadThirdInfo("POL456", "300.00");
        Page<TblLoadThirdInfo> page = new PageImpl<>(List.of(info));
        ExternalReceiptsDTO receipt = createReceipt("POL456", new BigDecimal("100.00"), "PENDING");

        List<ExternalReceiptsDTO> receipts = List.of(receipt);

        List<TblLoadThirdInfo> result = service.validCopelReceipts(page, true, receipts);

        assertEquals(3, result.size());
        verify(loadThirdRepo, times(3)).save(any(TblLoadThirdInfo.class));
    }

    @Test
    void shouldSkipWhenChargeNotDivisible() {
        TblLoadThirdInfo info = createTblLoadThirdInfo("POL789", "250.00");
        Page<TblLoadThirdInfo> page = new PageImpl<>(List.of(info));
        ExternalReceiptsDTO receipt = createReceipt("POL789", new BigDecimal("100.00"), "PENDING");

        List<ExternalReceiptsDTO> receipts = List.of(receipt);

        List<TblLoadThirdInfo> result = service.validCopelReceipts(page, true, receipts);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNoMatch() {
        TblLoadThirdInfo info = createTblLoadThirdInfo("NO_MATCH", "100.00");
        Page<TblLoadThirdInfo> page = new PageImpl<>(List.of(info));
        ExternalReceiptsDTO receipt = createReceipt("DIFFERENT", new BigDecimal("100.00"), "PENDING");

        List<ExternalReceiptsDTO> receipts = List.of(receipt);

        List<TblLoadThirdInfo> result = service.validCopelReceipts(page, true, receipts);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldOnlyUsePendingStatus() {
        TblLoadThirdInfo info = createTblLoadThirdInfo("POL101", "100.00");
        Page<TblLoadThirdInfo> page = new PageImpl<>(List.of(info));
        List<ExternalReceiptsDTO> receipts = List.of(
            createReceipt("POL101", new BigDecimal("100.00"), "COMPLETED"),
            createReceipt("POL101", new BigDecimal("100.00"), "PENDING")
        );

        List<TblLoadThirdInfo> result = service.validCopelReceipts(page, true, receipts);

        assertEquals(1, result.size());
        verify(loadThirdRepo).save(any(TblLoadThirdInfo.class));
    }

    @Test
    void shouldCatchNumberFormatException() {
        TblLoadThirdInfo info = createTblLoadThirdInfo("POL999", "NOT_A_NUMBER");
        Page<TblLoadThirdInfo> page = new PageImpl<>(List.of(info));
        ExternalReceiptsDTO receipt = createReceipt("POL999", new BigDecimal("100.00"), "PENDING");

        List<ExternalReceiptsDTO> receipts = List.of(receipt);

        List<TblLoadThirdInfo> result = service.validCopelReceipts(page, true, receipts);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNoPendingFound() {
        TblLoadThirdInfo info = createTblLoadThirdInfo("POL000", "100.00");
        Page<TblLoadThirdInfo> page = new PageImpl<>(List.of(info));
        ExternalReceiptsDTO receipt = createReceipt("POL000", new BigDecimal("100.00"), "DECLINED");

        List<ExternalReceiptsDTO> receipts = List.of(receipt);

        List<TblLoadThirdInfo> result = service.validCopelReceipts(page, true, receipts);

        assertTrue(result.isEmpty());
    }
}
