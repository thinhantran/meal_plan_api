package fr.univartois.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Family {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "family", orphanRemoval = true)
  @JsonIgnore
  private List<MemberRole> memberRoles = new ArrayList<>();

  private String code;

  public void addMember(MemberRole memberRole) {
    memberRoles.add(memberRole);
  }

}