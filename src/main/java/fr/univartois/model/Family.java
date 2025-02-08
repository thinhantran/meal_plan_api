package fr.univartois.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Family {

  private int familyId;

  private List<MemberRole> memberRoles;
}
