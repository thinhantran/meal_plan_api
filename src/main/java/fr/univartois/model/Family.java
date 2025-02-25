package fr.univartois.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
public class Family {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "family", orphanRemoval = true)
  private List<MemberRole> memberRoles;

  private String code;

  public void addMember(MemberRole memberRole) {
    memberRoles.add(memberRole);
  }

}