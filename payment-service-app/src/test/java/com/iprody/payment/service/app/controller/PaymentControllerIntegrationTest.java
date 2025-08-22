package com.iprody.payment.service.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iprody.payment.service.app.AbstractPostgresIntegrationTest;
import com.iprody.payment.service.app.TestJwtFactory;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import com.iprody.payment.service.app.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
class PaymentControllerIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Test
    void shouldReturnOnlyLiquibasePayments() throws Exception {
        //given-when
        mockMvc.perform(get("/payments/all")

                        .with(
                                TestJwtFactory.jwtWithRole("admin",

                                        "ADMIN")

                        )
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000001')]").exists())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000002')]").exists())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000003')]").exists());
    }

    @Test
    void shouldCreatePaymentAndVerifyInDatabase() throws Exception {
        // given
        PaymentDto dto = new PaymentDto();
        dto.setInquiryRefId(UUID.randomUUID());
        dto.setAmount(new BigDecimal("123.45"));
        dto.setCurrency("EUR");
        dto.setStatus(PaymentStatus.PENDING);
        dto.setCreatedAt(OffsetDateTime.now());
        dto.setUpdatedAt(OffsetDateTime.now());
        String json = objectMapper.writeValueAsString(dto);

        // when
        String response = mockMvc.perform(post("/payments")

                        .with(
                                TestJwtFactory.jwtWithRole("admin",
                                        "ADMIN")
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.guid").exists())

                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.amount").value(123.45))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaymentDto created = objectMapper.readValue(response,
                PaymentDto.class);
        Optional<Payment> saved =
                paymentRepository.findById(created.getGuid());
        assertThat(saved).isPresent();
        assertThat(saved.get().getCurrency()).isEqualTo("EUR");
        assertThat(saved.get().getAmount()).isEqualByComparingTo("123.45");
    }

    @Test
    void shouldReturnPaymentById() throws Exception {
        // given
        UUID existingId =
                UUID.fromString("00000000-0000-0000-0000-000000000002");

        // when
        mockMvc.perform(get("/payments/" + existingId)

                        .with(
                                TestJwtFactory.jwtWithRole("admin",
                                        "ADMIN")
                        )
                        .accept(MediaType.APPLICATION_JSON))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value(existingId.toString()))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.amount").value(50.00));

    }

    @Test
    void shouldReturn404ForNonexistentPayment() throws Exception {
        // given
        UUID nonexistentId = UUID.randomUUID();

        // when
        mockMvc.perform(get("/payments/" + nonexistentId)

                        .with(
                                TestJwtFactory.jwtWithRole("admin",
                                        "ADMIN")
                        )

                        .accept(MediaType.APPLICATION_JSON))

                // then
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Payment not found"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.operation").value("getById"))
                .andExpect(jsonPath("$.entityId").value(nonexistentId.toString()));
    }

    @Test
    void shouldReturnPaymentsByStatus_RECEIVED() throws Exception {
        // given-when
        mockMvc.perform(get("/payments/by_status/{status}", "RECEIVED")
                        .with(TestJwtFactory.jwtWithRole("reader", "READER"))
                        .accept(MediaType.APPLICATION_JSON))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.guid=='00000000-0000-0000-0000-000000000001')]").exists())
                .andExpect(jsonPath("$[0].status").value("RECEIVED"));
    }

    @Test
    void shouldReturnPaymentsByStatus_APPROVED() throws Exception {
        // given-when
        mockMvc.perform(get("/payments/by_status/{status}", "APPROVED")
                        .with(TestJwtFactory.jwtWithRole("admin", "ADMIN"))
                        .accept(MediaType.APPLICATION_JSON))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.guid=='00000000-0000-0000-0000-000000000002')]").exists())
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void shouldUpdateNote() throws Exception {
        //given
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        String newNote = "Updated test note";

        //when
        mockMvc.perform(patch("/payments/" + id + "/note")
                        .with(TestJwtFactory.jwtWithRole("admin", "ADMIN"))
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(newNote))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value(id.toString()))
                .andExpect(jsonPath("$.note").value(newNote));

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Payment not found"));
        assertThat(payment.getNote()).isEqualTo(newNote);
    }

    @Test
    void checkDatabaseEmpty() {
        long count = paymentRepository.count();
        System.out.println("Количество платежей в базе: " + count);
        assertThat(count).isGreaterThanOrEqualTo(0); // для проверки количества записей в бд
    }
}
