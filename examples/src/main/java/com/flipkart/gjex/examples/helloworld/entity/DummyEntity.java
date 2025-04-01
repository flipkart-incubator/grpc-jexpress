package com.flipkart.gjex.examples.helloworld.entity;

import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "dummyEntity")
@XmlRootElement
public class DummyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement
    private Long id;
    
    @XmlElement
    private String status;
}
