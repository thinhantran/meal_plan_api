package fr.univartois.model;

import jakarta.persistence.*;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Family {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int familyId;

  @OneToMany(mappedBy = "family", fetch = FetchType.EAGER)
  private List<MemberRole> memberRoles;
}
