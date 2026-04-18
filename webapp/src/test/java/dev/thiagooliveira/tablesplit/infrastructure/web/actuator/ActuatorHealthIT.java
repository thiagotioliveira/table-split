package dev.thiagooliveira.tablesplit.infrastructure.web.actuator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.thiagooliveira.tablesplit.infrastructure.web.H2IT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ActuatorHealthIT extends H2IT {

  @Test
  @DisplayName("Should return healthy status from actuator health endpoint")
  void shouldReturnUpStatus() throws Exception {
    mockMvc
        .perform(get("/actuator/health"))
        .andDo(print())
        .andExpect(
            status()
                .is(
                    org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(200), org.hamcrest.Matchers.is(503))))
        .andExpect(jsonPath("$.status").value("UP"));
  }
}
