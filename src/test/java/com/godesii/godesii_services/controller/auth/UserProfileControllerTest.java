//package com.godesii.godesii_services.controller.auth;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.godesii.godesii_services.dto.UserProfileCreateRequest;
//import com.godesii.godesii_services.dto.UserProfileCreateResponse;
//import com.godesii.godesii_services.service.ProfileService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(UserProfileController.class)
//@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
//public class UserProfileControllerTest {
//
//        @Autowired
//        @MockBean
//        private ProfileService profileService;
//
//        @MockBean
//        private com.godesii.godesii_services.repository.auth.JpaRSAKeysRepository jpaRSAKeysRepository;
//
//        @MockBean
//        private com.godesii.godesii_services.repository.auth.UserRepository userRepository;
//
//        @Autowired
//        private ObjectMapper objectMapper;
//
//        @Test
//        public void createProfile_ValidInput_ReturnsCreated() throws Exception {
//                UserProfileCreateRequest request = new UserProfileCreateRequest();
//                request.setFirstName("John");
//                request.setLastName("Doe");
//
//
//                UserProfileCreateResponse response = new UserProfileCreateResponse();
////                when(profileService.saveOrUpdateProfile(any(UserProfileCreateRequest.class))).thenReturn(response);
//
//                mockMvc.perform(post("/api/v1/user/profile")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request)))
//                                .andExpect(status().isCreated());
//        }
//}
