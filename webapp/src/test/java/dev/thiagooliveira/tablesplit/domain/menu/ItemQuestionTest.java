package dev.thiagooliveira.tablesplit.domain.menu;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ItemQuestionTest {

  @Test
  void shouldGetAndSetFieldsSuccessfully() {
    ItemQuestion question = new ItemQuestion();
    UUID id = UUID.randomUUID();

    question.setId(id);
    question.setTitle("Choose your sauce");
    question.setType(ItemQuestionType.SINGLE);
    question.setRequired(true);
    question.setMinSelections(1);
    question.setMaxSelections(2);

    ItemOption option1 = new ItemOption();
    option1.setId(UUID.randomUUID());
    question.setOptions(List.of(option1));

    assertEquals(id, question.getId());
    assertEquals("Choose your sauce", question.getTitle());
    assertEquals(ItemQuestionType.SINGLE, question.getType());
    assertTrue(question.isRequired());
    assertEquals(1, question.getMinSelections());
    assertEquals(2, question.getMaxSelections());
    assertEquals(1, question.getOptions().size());
  }
}
