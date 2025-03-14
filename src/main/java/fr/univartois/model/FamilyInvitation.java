package fr.univartois.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DialectOverride;

@Getter
@Setter
@Entity
public class FamilyInvitation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  private Family family;

  @ManyToOne(cascade = CascadeType.ALL)
  private User user;
}
