package com.godesii.godesii_services.entity.oauth2;

import jakarta.persistence.*;

@Entity
@Table(name = "user_profile")
public class UserProfile {

    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private Gender gender;
    private User user;

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "first_name", length = 25)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "middle_name", length = 25)
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Column(name = "last_name", length = 25)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Enumerated(value = EnumType.ORDINAL)
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @OneToOne(mappedBy = "userProfile")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    //    @ManyToMany(
//            fetch = FetchType.EAGER,
//            cascade = {
//                    CascadeType.ALL
//            })
//    @JoinTable(
//            name = "user_address",
//            joinColumns = {
//                    @JoinColumn(name = "user_profile_id", referencedColumnName = "id")
//            },
//            inverseJoinColumns = {
//                    @JoinColumn(name = "address_id", referencedColumnName = "id")
//            }
//    )
//    @JsonIgnoreProperties(value = "userProfiles")
//    public List<Address> getAddresses() {
//        return addresses;
//    }
//
//    public void setAddresses(List<Address> addresses) {
//        this.addresses = addresses;
//    }

}