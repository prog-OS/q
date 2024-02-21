package com.example.project.pharmacy.entity;

import com.example.project.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * JAP를 사용할 때 데이터베이스와 맵핑괼 어떤 컬럼들을 지정하는 클래스
 */
@Entity(name = "pharmacy")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pharmacy extends BaseTimeEntity {

    @Id // 맵핑될 PK값
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK 값 생성을 DB에 위임(id값을 비워서 DB에 값을 저장하면 DB에서 알아서 PK값 생성하여 맵핑함)
    private Long id;

    private String pharmacyName;
    private String pharmacyAddress;
    private double latitude;
    private double longitude;

    public void changePharmacyAddress(String address) {
        this.pharmacyAddress = address;
    }
}
