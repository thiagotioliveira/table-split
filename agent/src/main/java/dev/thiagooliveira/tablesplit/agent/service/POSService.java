package dev.thiagooliveira.tablesplit.agent.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * TODO Example of insert data in the POS database
 */
@Service
public class POSService {

  private static final Logger log = LoggerFactory.getLogger(POSService.class);

  @Value("${pos.db.url:jdbc:firebirdsql://localhost:3050//firebird/data/pos.fdb}")
  private String dbUrl;

  @Value("${pos.db.user:SYSDBA}")
  private String dbUser;

  @Value("${pos.db.password:masterkey}")
  private String dbPassword;

  public void injectOrder(Map<String, Object> order) {
    log.info("Starting order injection for ticket: {}", order.get("id"));

    try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
      conn.setAutoCommit(false);

      try {
        // Example SQL - Adjust to actual WinRest/Zonesoft schema
        String insertItemSql =
            "INSERT INTO PEDIDOS_LINHAS (TICKET_ID, ARTIGO, QTD, PRECO) VALUES (?, ?, ?, ?)";

        try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSql)) {
          List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");
          for (Map<String, Object> item : items) {
            itemStmt.setString(1, order.get("id").toString());
            itemStmt.setString(2, (String) item.get("name"));
            itemStmt.setDouble(3, Double.parseDouble(item.get("quantity").toString()));
            itemStmt.setDouble(4, Double.parseDouble(item.get("totalPrice").toString()));
            itemStmt.addBatch();
          }
          itemStmt.executeBatch();
        }

        conn.commit();
        log.info("Order injected successfully in POS database!");

      } catch (Exception e) {
        conn.rollback();
        throw e;
      }
    } catch (Exception e) {
      log.error("Failed to inject order into POS: {}", e.getMessage(), e);
    }
  }
}
