package org.example.util;

import org.example.model.Balance;
import org.example.model.Statement;
import org.example.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal MT940 parser that supports the most common tags used in bank statements.
 * Supported tags: :20:, :25:, :28C:, :60F:, :61:, :86:, :62F:
 * This implementation is intentionally simple and forgiving to handle pasted inputs.
 */
public class Mt940Parser {

    private static final Pattern TAG_PATTERN = Pattern.compile("^:(\\d{2}[A-Z]?):(.*)$");
    private static final Pattern TXN_PATTERN = Pattern.compile(
            // :61:YYMMDD[...][C|D|DD|CD etc.][amount][...]
            "^(\\d{6})(?:\\d{4})?[CD]*([CD])([0-9,\\.\\,]+).*$");

    private static final DateTimeFormatter DATE_YYMMDD = DateTimeFormatter.ofPattern("yyMMdd");

    public Statement parse(String input) {
        Statement st = new Statement();
        List<String> lines = normalize(input);

        Transaction currentTxn = null;
        for (String raw : lines) {
            Matcher m = TAG_PATTERN.matcher(raw);
            if (!m.matches()) {
                // continuation line (often used to continue :86:)
                if (currentTxn != null && currentTxn.getDescription() != null) {
                    currentTxn.setDescription((currentTxn.getDescription() + " " + raw).trim());
                }
                continue;
            }
            String tag = m.group(1);
            String content = m.group(2).trim();

            switch (tag) {
                case "20" -> st.setTransactionReference(content);
                case "25" -> st.setAccount(content);
                case "28C" -> st.setStatementNumber(content);
                case "60F", "60M" -> st.setOpeningBalance(parseBalance(content));
                case "62F", "62M" -> st.setClosingBalance(parseBalance(content));
                case "61" -> {
                    // finalize previous txn if any
                    if (currentTxn != null) {
                        st.getTransactions().add(currentTxn);
                    }
                    currentTxn = parseTxn61(content);
                }
                case "86" -> {
                    if (currentTxn == null) {
                        // orphan 86, create placeholder
                        currentTxn = new Transaction();
                    }
                    String desc = currentTxn.getDescription();
                    currentTxn.setDescription(desc == null ? content : (desc + " " + content).trim());
                }
                default -> {
                    // ignore other tags for now
                }
            }
        }
        if (currentTxn != null) {
            st.getTransactions().add(currentTxn);
        }
        return st;
    }

    private List<String> normalize(String input) {
        String[] arr = input.replace("\r\n", "\n").replace('\r', '\n').split("\n");
        List<String> out = new ArrayList<>();
        for (String s : arr) {
            String t = s.strip();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }

    private Transaction parseTxn61(String content) {
        // Example: 240101D123,45NTRFNONREF
        Matcher m = TXN_PATTERN.matcher(content);
        LocalDate date = null;
        boolean credit = false;
        BigDecimal amount = BigDecimal.ZERO;
        if (m.matches()) {
            try {
                date = LocalDate.parse(m.group(1), DATE_YYMMDD);
            } catch (DateTimeParseException ignored) { }
            credit = "C".equals(m.group(2));
            String rawAmt = m.group(3).replace('.', ' ').replace(',', '.').replace(" ", "");
            try {
                amount = new BigDecimal(rawAmt);
            } catch (NumberFormatException ignored) { }
        }
        Transaction t = new Transaction();
        t.setDate(date);
        t.setCredit(credit);
        t.setAmount(amount);
        t.setDescription(null);
        return t;
    }

    private Balance parseBalance(String content) {
        // Format: [C|D][YYMMDD][Currency][Amount]
        // Example: C251103USD0,00 or D251103EUR1234,56
        Pattern balancePattern = Pattern.compile("^([CD])(\\d{6})([A-Z]{3})([0-9,\\.]+)$");
        Matcher m = balancePattern.matcher(content);
        
        if (m.matches()) {
            boolean credit = "C".equals(m.group(1));
            LocalDate date = null;
            try {
                date = LocalDate.parse(m.group(2), DATE_YYMMDD);
            } catch (DateTimeParseException ignored) { }
            String currency = m.group(3);
            String rawAmt = m.group(4).replace('.', ' ').replace(',', '.').replace(" ", "");
            BigDecimal amount = BigDecimal.ZERO;
            try {
                amount = new BigDecimal(rawAmt);
            } catch (NumberFormatException ignored) { }
            
            return new Balance(credit, date, currency, amount);
        }
        
        // If parsing fails, return a default/empty Balance
        return new Balance(true, null, "", BigDecimal.ZERO);
    }
}
