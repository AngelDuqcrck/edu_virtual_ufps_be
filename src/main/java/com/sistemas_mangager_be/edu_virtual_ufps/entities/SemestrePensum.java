package com.sistemas_mangager_be.edu_virtual_ufps.entities;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "semestres_pensums")
public class SemestrePensum {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "semestre_id")
    private Semestre semestreId;

    @ManyToOne
    @JoinColumn(name = "pensum_id")
    private Pensum pensumId;

    @ManyToOne
    @JoinColumn(name = "programa_id")
    private Programa programaId;

    private String moodleId;
    
}
