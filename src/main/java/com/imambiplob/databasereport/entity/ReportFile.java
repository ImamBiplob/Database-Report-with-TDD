package com.imambiplob.databasereport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReportFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "report_id")
    private Report report;

    private String fileName;

    private String fileType;

    @Column(columnDefinition = "LONGBLOB")
    @Lob
    private byte[] data;

    private Date creationTime = new Date();

}
