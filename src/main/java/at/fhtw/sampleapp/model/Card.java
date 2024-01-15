package at.fhtw.sampleapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Card {

    @JsonAlias({"id"})
    private String id;

    @JsonAlias({"name"})
    private String name;

    @JsonAlias({"damage"})
    private Integer damage;

    @JsonAlias({"type"})
    private int type;


    public Card(String id, String name, Integer damage, CardType type) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.type = type.ordinal();
    }

}
