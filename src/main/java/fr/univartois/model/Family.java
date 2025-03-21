package fr.univartois.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode
public class Family {

  private static final int LENGTHCODE = 6;

  private static final Random random = new Random();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "family", orphanRemoval = true)
  @JsonIgnore
  private List<MemberRole> memberRoles = new ArrayList<>();

  private String name;

  @Column(unique = true, length = LENGTHCODE)
  private String code;

  @JoinColumn(unique = true)
  @OneToOne(cascade = CascadeType.ALL)
  @JsonManagedReference("family")
  private Fridge fridge;

  public void addMember(MemberRole memberRole) {
    memberRoles.add(memberRole);
  }

  @PrePersist
  private void generateCode() {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder value = new StringBuilder();
    for (int i = 0; i < LENGTHCODE; i++) {
      int index = random.nextInt(characters.length());
      value.append(characters.charAt(index));
    }
    this.code = value.toString();
  }

}