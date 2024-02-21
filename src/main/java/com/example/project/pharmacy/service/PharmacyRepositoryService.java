package com.example.project.pharmacy.service;

import com.example.project.pharmacy.entity.Pharmacy;
import com.example.project.pharmacy.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyRepositoryService {

    private final PharmacyRepository pharmacyRepository;

    // self invocation test
//    @Transactional
    public void bar(List<Pharmacy> pharmacyList) {
        log.info("bar CurrentTransactionName: " + TransactionSynchronizationManager.getCurrentTransactionName());
        foo(pharmacyList);
    }

    @Transactional
    public void foo(List<Pharmacy> pharmacyList) {
        log.info("foo CurrentTransactionName: " + TransactionSynchronizationManager.getCurrentTransactionName());
        pharmacyList.forEach(pharmacy -> {
            pharmacyRepository.save(pharmacy);
            throw new RuntimeException("error"); // 에러가 발생해서 롤백이 되어야 한다.(bar에 있는 @Transactional을 주석처리하면 롤백이 발생하지 않는다.)
        });
    }

    // read only test
    @Transactional(readOnly = true) // 읽기 전용으로 설정하게 되면, JPA에서 스냅샷 저장 및 Dirty Checking 작업을 수행하지 않기 때문에 성능적으로 이점
    public void startReadOnlyMethod(Long id) {
        pharmacyRepository.findById(id).ifPresent(pharmacy ->
                pharmacy.changePharmacyAddress("서울 특별시 광진구"));
    }

    @Transactional
    public void updateAddress(Long id, String address) {
        Pharmacy entity = pharmacyRepository.findById(id).orElse(null);

        if (Objects.isNull(entity)) {
            log.error("[PharmacyRepositoryService updateAddress] not found id: {}", id);
            return;
        }

        entity.changePharmacyAddress(address);
    }

    public void updateAddressWithoutTransaction(Long id, String address) {
        Pharmacy entity = pharmacyRepository.findById(id).orElse(null);

        if (Objects.isNull(entity)) {
            log.error("[PharmacyRepositoryService updateAddress] not found id: {}", id);
            return;
        }

        entity.changePharmacyAddress(address);
    }

    @Transactional(readOnly = true)
    public List<Pharmacy> findAll() {
        return pharmacyRepository.findAll();
    }
}
