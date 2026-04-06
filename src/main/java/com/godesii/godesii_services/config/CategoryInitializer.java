package com.godesii.godesii_services.config;

import com.godesii.godesii_services.entity.restaurant.Category;
import com.godesii.godesii_services.repository.restaurant.CategoryRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CategoryInitializer {


    private final CategoryRepository categoryRepository;

    public CategoryInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void initializeCategory(){

        Arrays
                .stream(DefaultCategory.values())
                .filter(c -> this.categoryRepository.findByName(c.name()).isEmpty()).forEachOrdered(c -> {
                    Category category = new Category();
                    category.setName(c.name());
                    this.categoryRepository.save(category);
            });

    }

    public enum DefaultCategory {

        FRUITS("Fruits"),
        SWEETS("Sweets"),
        DINNER("Dinner"),
        LUNCH("Lunch"),
        BREAKFAST("Breakfast"),
        SNACKS("Snacks"),
        STARTER("Starter");

        private final String value;

        DefaultCategory(String value){
            this.value = value;
        }

        public String getValue(){
            return value;
        }
    }

}
