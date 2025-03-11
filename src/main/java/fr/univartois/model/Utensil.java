package fr.univartois.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;


@Getter
@Setter
@Entity
public class Utensil {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int utensilId;

  @Column(nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "fridge_id")
  @JsonBackReference
  private Fridge fridge;
}
