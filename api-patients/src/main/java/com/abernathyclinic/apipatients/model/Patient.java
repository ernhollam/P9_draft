package com.abernathyclinic.apipatients.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Integer id;
    @Size(max = 50, message = "Maximum of {max} characters")
    @NotBlank(message = "Last name is mandatory")
    private String family;
    @Size(max = 50, message = "Maximum of {max} characters")
    @NotBlank(message = "First name is mandatory")
    private String given;
    @Column(name = "date_of_birth")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
    @Size(max = 1, message = "Maximum of {max} character, select either Male or Female")
    private String gender;
    @Size(max = 50, message = "Maximum of {max} characters")
    private String address;
    @Pattern(regexp = "^([0-9]{3}-[0-9]{3}-[0-9]{4})?$", message = "Phone number must be in 123-456-7890 format")
    private String phone;
}
