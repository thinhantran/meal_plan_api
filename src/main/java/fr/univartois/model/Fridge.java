package fr.univartois.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Fridge {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int fridgeId;

  @OneToOne
  @JoinColumn(name = "family_id", nullable = false)
  private Family family;

  @OneToMany(mappedBy = "fridge", fetch = FetchType.EAGER)
  @JsonManagedReference
  private List<IngredientFridgeQuantity> ingredients;

  @OneToMany(mappedBy = "fridge",cascade = CascadeType.PERSIST, orphanRemoval = true)
  @JsonManagedReference
  private List<Utensil> ustensils;
}
