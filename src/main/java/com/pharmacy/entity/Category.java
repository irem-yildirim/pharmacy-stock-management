package com.pharmacy.entity;

import java.util.List;

public class Category {
    private Long id;
    private String name;
    private String description;
    private List<Drug> drugs;

    public Category() {}

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Drug> getDrugs() { return drugs; }
    public void setDrugs(List<Drug> drugs) { this.drugs = drugs; }

    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "'}";
    }
}
