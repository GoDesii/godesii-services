package com.godesii.godesii_services.entity.restaurant;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.godesii.godesii_services.common.FoodCategory;
import com.godesii.godesii_services.entity.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;
import org.hibernate.search.engine.backend.types.Sortable;

import java.util.List;

@Entity
@Table(name = "restaurant")
@Indexed
public class Restaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FullTextField(analyzer = "food_analyzer", searchAnalyzer = "food_query_analyzer")
    @KeywordField(name = "name_sort", normalizer = "sort_normalizer", sortable = Sortable.YES)
    private String name;

    private String phoneNo;

    @FullTextField(analyzer = "food_analyzer", searchAnalyzer = "food_query_analyzer")
    private String cuisineType;

    @FullTextField(analyzer = "food_analyzer", searchAnalyzer = "food_query_analyzer")
    private String description;

    private boolean isVerified;

    @Column(name = "food_category")
    @Enumerated(EnumType.STRING)
    private FoodCategory foodCategory;

    @GenericField
    private boolean isActive;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private List<OperationalHour> operatingHours;

    @Embedded
    @IndexedEmbedded
    private RestaurantAddress address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public FoodCategory getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(FoodCategory foodCategory) {
        this.foodCategory = foodCategory;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<OperationalHour> getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(List<OperationalHour> operatingHours) {
        this.operatingHours = operatingHours;
    }

    public RestaurantAddress getAddress() {
        return address;
    }

    public void setAddress(RestaurantAddress address) {
        this.address = address;
    }

}
