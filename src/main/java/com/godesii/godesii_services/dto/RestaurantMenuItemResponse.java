package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.restaurant.MenuItem;

import java.math.BigDecimal;

/**
 * Lightweight response DTO for the "get all menu items by restaurant" endpoint.
 * Intentionally excludes the restaurant object from the payload since the caller
 * already knows the restaurant (it's in the request path).
 */
public class RestaurantMenuItemResponse {

    private String itemId;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String imageUrl;
    private boolean isAvailable;
    private String dietaryType;
    private CategoryInfo category;

    // ─── Constructors ──────────────────────────────────────────────────────────

    public RestaurantMenuItemResponse() {}

    public RestaurantMenuItemResponse(String itemId, String name, String description,
                                      BigDecimal basePrice, String imageUrl,
                                      boolean isAvailable, String dietaryType,
                                      CategoryInfo category) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.dietaryType = dietaryType;
        this.category = category;
    }

    // ─── Getters & Setters ─────────────────────────────────────────────────────

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getDietaryType() { return dietaryType; }
    public void setDietaryType(String dietaryType) { this.dietaryType = dietaryType; }

    public CategoryInfo getCategory() { return category; }
    public void setCategory(CategoryInfo category) { this.category = category; }

    // ─── Nested DTOs ───────────────────────────────────────────────────────────

    public static class CategoryInfo {

        private Long id;
        private String name;
        private String description;
        private Integer displayOrder;
        private String imageUrl;
        private MenuInfo menu;

        public CategoryInfo() {}

        public CategoryInfo(Long id, String name, String description,
                            Integer displayOrder, String imageUrl, MenuInfo menu) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.displayOrder = displayOrder;
            this.imageUrl = imageUrl;
            this.menu = menu;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Integer getDisplayOrder() { return displayOrder; }
        public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public MenuInfo getMenu() { return menu; }
        public void setMenu(MenuInfo menu) { this.menu = menu; }
    }

    /**
     * Menu info WITHOUT the restaurant field — the caller already has the restaurant ID.
     */
    public static class MenuInfo {

        private Long id;
        private String name;
        private String description;
        private String menuType;
        private Integer sortOrder;

        public MenuInfo() {}

        public MenuInfo(Long id, String name, String description,
                        String menuType, Integer sortOrder) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.menuType = menuType;
            this.sortOrder = sortOrder;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getMenuType() { return menuType; }
        public void setMenuType(String menuType) { this.menuType = menuType; }

        public Integer getSortOrder() { return sortOrder; }
        public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    }

    // ─── Mapper ────────────────────────────────────────────────────────────────

    /**
     * Convert a MenuItem entity to this DTO.
     * Restaurant is deliberately excluded from the response.
     */
    public static RestaurantMenuItemResponse fromEntity(MenuItem menuItem) {
        if (menuItem == null) {
            return null;
        }

        RestaurantMenuItemResponse response = new RestaurantMenuItemResponse();
        response.setItemId(menuItem.getItemId());
        response.setName(menuItem.getName());
        response.setDescription(menuItem.getDescription());
        response.setBasePrice(menuItem.getBasePrice());
        response.setImageUrl(menuItem.getImageUrl());
        response.setAvailable(menuItem.isAvailable());
        response.setDietaryType(menuItem.getDietaryType());

        if (menuItem.getCategory() != null) {
            CategoryInfo categoryInfo = new CategoryInfo();
            categoryInfo.setId(menuItem.getCategory().getId());
            categoryInfo.setName(menuItem.getCategory().getName());
            categoryInfo.setDescription(menuItem.getCategory().getDescription());
            categoryInfo.setDisplayOrder(menuItem.getCategory().getDisplayOrder());
            categoryInfo.setImageUrl(menuItem.getCategory().getImageUrl());

            if (menuItem.getCategory().getMenu() != null) {
                MenuInfo menuInfo = new MenuInfo();
                menuInfo.setId(menuItem.getCategory().getMenu().getId());
                menuInfo.setName(menuItem.getCategory().getMenu().getName());
                menuInfo.setDescription(menuItem.getCategory().getMenu().getDescription());
                menuInfo.setMenuType(menuItem.getCategory().getMenu().getMenuType());
                menuInfo.setSortOrder(menuItem.getCategory().getMenu().getSortOrder());
                // restaurant intentionally omitted
                categoryInfo.setMenu(menuInfo);
            }

            response.setCategory(categoryInfo);
        }

        return response;
    }
}
