package com.helpdesk.ticket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpdesk.auth.entity.User;
import com.helpdesk.auth.repository.UserRepository;
import com.helpdesk.config.JwtService;
import com.helpdesk.ticket.dto.CreateTicketRequest;
import com.helpdesk.ticket.entity.Category;
import com.helpdesk.ticket.entity.Priority;
import com.helpdesk.ticket.entity.Ticket;
import com.helpdesk.ticket.entity.TicketStatus;
import com.helpdesk.ticket.repository.CategoryRepository;
import com.helpdesk.ticket.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TicketControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String jwtToken;
    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Clear data
        ticketRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("test-ticket-user@example.com")
                .password(passwordEncoder.encode("TestPassword123!"))
                .role(com.helpdesk.auth.UserRole.EMPLOYEE)
                .build();
        testUser = userRepository.save(testUser);

        // Generate JWT token
        jwtToken = jwtService.generateToken(testUser);

        // Create test category
        testCategory = Category.builder()
                .name("Hardware")
                .description("Hardware issues")
                .active(true)
                .build();
        testCategory = categoryRepository.save(testCategory);
    }

    @Test
    void testCreateTicket() throws Exception {
        CreateTicketRequest request = CreateTicketRequest.builder()
                .title("Laptop not starting")
                .description("My laptop fails to power on after the latest update")
                .priority(Priority.HIGH)
                .categoryId(testCategory.getId())
                .build();

        mockMvc.perform(post("/api/tickets")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Laptop not starting"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("HIGH"));

        assertThat(ticketRepository.count()).isEqualTo(1);
    }

    @Test
    void testGetAllTickets() throws Exception {
        // Create sample ticket
        Ticket ticket = Ticket.builder()
                .title("Software bug")
                .description("The application crashes when clicking the export button")
                .priority(Priority.MEDIUM)
                .category(testCategory)
                .creator(testUser)
                .status(TicketStatus.OPEN)
                .build();
        ticketRepository.save(ticket);

        mockMvc.perform(get("/api/tickets")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Software bug"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetTicketById() throws Exception {
        Ticket ticket = Ticket.builder()
                .title("Network down")
                .description("Internet connection is not working properly")
                .priority(Priority.CRITICAL)
                .category(testCategory)
                .creator(testUser)
                .status(TicketStatus.OPEN)
                .build();
        ticket = ticketRepository.save(ticket);

        mockMvc.perform(get("/api/tickets/" + ticket.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Network down"))
                .andExpect(jsonPath("$.id").value(ticket.getId()));
    }

    @Test
    void testUpdateTicketStatus() throws Exception {
        Ticket ticket = Ticket.builder()
                .title("Update status test")
                .description("Testing status update functionality with sufficient content")
                .priority(Priority.LOW)
                .category(testCategory)
                .creator(testUser)
                .status(TicketStatus.OPEN)
                .build();
        ticket = ticketRepository.save(ticket);

        String updateRequest = "{\"status\": \"IN_PROGRESS\"}";

        mockMvc.perform(patch("/api/tickets/" + ticket.getId() + "/status")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        Ticket updatedTicket = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertThat(updatedTicket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
    }

    @Test
    void testSearchTickets() throws Exception {
        Ticket ticket = Ticket.builder()
                .title("Critical database error")
                .description("The database connection is failing intermittently during peak hours")
                .priority(Priority.CRITICAL)
                .category(testCategory)
                .creator(testUser)
                .status(TicketStatus.OPEN)
                .build();
        ticketRepository.save(ticket);

        mockMvc.perform(get("/api/tickets/search?q=database")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Critical database error"));
    }

    @Test
    void testDeleteTicket() throws Exception {
        Ticket ticket = Ticket.builder()
                .title("Delete test ticket")
                .description("This ticket will be deleted by the test to verify delete functionality")
                .priority(Priority.LOW)
                .category(testCategory)
                .creator(testUser)
                .status(TicketStatus.OPEN)
                .build();
        ticket = ticketRepository.save(ticket);

        mockMvc.perform(delete("/api/tickets/" + ticket.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertThat(ticketRepository.findById(ticket.getId())).isEmpty();
    }
}
