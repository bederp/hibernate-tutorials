package org.hibernate.tutorial.em;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String description;

    @OneToMany(mappedBy = "family")
    private final List<Person> members = new ArrayList<Person>();
}
