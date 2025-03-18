package fr.univartois.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;


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
