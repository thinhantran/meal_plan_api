package fr.univartois.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Family {

  static private final int LENGTHCODE = 6;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "family", orphanRemoval = true)
  @JsonIgnore
  private List<MemberRole> memberRoles = new ArrayList<>();

  @Column(unique = true, length = LENGTHCODE)
  private String code;

  public void addMember(MemberRole memberRole) {
    memberRoles.add(memberRole);
  }

  @PrePersist
  private void generateCode() {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    Random random = new Random();
    StringBuilder value = new StringBuilder();
    for (int i = 0; i < LENGTHCODE; i++) {
      int index = random.nextInt(characters.length());
      value.append(characters.charAt(index));
    }
    this.code = value.toString();
  }

}