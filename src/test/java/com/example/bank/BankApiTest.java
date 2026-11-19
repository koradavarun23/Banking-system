package com.example.bank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BankApiTest {

    @Autowired
    private MockMvc mvc;

    private String createAccount(String name) throws Exception {
        String body = mvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"holderName\":\"" + name + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.holderName").value(name))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.accountNumber");
    }

    @Test
    void depositThenOverdraftReturnsBadRequest() throws Exception {
        String acc = createAccount("Test User");

        mvc.perform(post("/api/accounts/" + acc + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(100));

        mvc.perform(post("/api/accounts/" + acc + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":500}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void unknownAccountReturnsNotFound() throws Exception {
        mvc.perform(get("/api/accounts/DOES_NOT_EXIST/transactions"))
                .andExpect(status().isNotFound());
    }

    @Test
    void transferBetweenAccounts() throws Exception {
        String a = createAccount("Sender");
        String b = createAccount("Receiver");
        mvc.perform(post("/api/accounts/" + a + "/deposit")
                .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":250}"));

        mvc.perform(post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"from\":\"" + a + "\",\"to\":\"" + b + "\",\"amount\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].balance").value(150))
                .andExpect(jsonPath("$[1].balance").value(100));
    }
}
