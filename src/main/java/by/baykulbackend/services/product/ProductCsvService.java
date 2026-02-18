package by.baykulbackend.services.product;

import by.baykulbackend.database.dao.product.Currency;
import by.baykulbackend.database.dao.product.Part;
import by.baykulbackend.database.dto.product.ProductDto;
import by.baykulbackend.database.repository.product.IPartRepository;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCsvService {
    private static final char SEPARATOR = ';';
    private static final int EXPECTED_COLUMNS = 8;

    private final IPartRepository iPartRepository;
    private final Set<String> uniqueArticles = new HashSet<>();

    /**
     * Parses and imports parts from a CSV file.
     * The CSV file must be in UTF-8 encoding with semicolon separator and the following columns:
     * article;name;weight;min_count;storage_count;return_part;price;brand
     *
     * @param productDto DTO containing the CSV file to parse
     * @return ResponseEntity with success/error message
     */
    public ResponseEntity<?> parseParts(ProductDto productDto) {
        Map<String, String> response = new HashMap<>();
        List<Part> parts = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(productDto.getCsvFile().getInputStream(), StandardCharsets.UTF_8))) {
            CSVReader csvReader = new CSVReaderBuilder(br)
                    .withCSVParser(new CSVParserBuilder()
                            .withSeparator(SEPARATOR)
                            .withIgnoreQuotations(true)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build())
                    .withSkipLines(1)
                    .build();

            String[] line;
            int lineNumber = 2;

            while ((line = csvReader.readNext()) != null) {
                Part part = parsePartFromLine(line, response, lineNumber);

                if (part != null) {
                    parts.add(part);
                }
                lineNumber++;
            }

            iPartRepository.saveAll(parts);

        } catch (Exception e) {
            log.error("Error while parsing csv file: {}", e.getMessage());
            response.put("error", "Error while parsing csv file");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } finally {
            uniqueArticles.clear();
        }

        response.put("parsed", "true");

        return ResponseEntity.ok(response);
    }

    /**
     * Parses a single part from a CSV line.
     *
     * @param line CSV line as string array
     * @param response Map to collect validation errors
     * @param lineNumber Current line number for error reporting
     * @return Parsed Part object or null if parsing failed
     */
    @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
    private Part parsePartFromLine(String[] line, Map<String, String> response, int lineNumber) {
        if (isInvalidLine(line, response, lineNumber)) {
            return null;
        }

        Part part = new Part();
        part.setArticle(line[0]);
        part.setName(line[1]);

        if (StringUtils.isNotBlank(line[2])) {
            part.setWeight(Double.parseDouble(line[2].replace(',', '.')));
        }

        if (StringUtils.isNotBlank(line[3])) {
            part.setMinCount(Integer.parseInt(line[3]));
        } else {
            part.setMinCount(1);
        }

        if (StringUtils.isNotBlank(line[4])) {
            part.setStorageCount(Integer.parseInt(line[4]));
        }

        if (StringUtils.isNotBlank(line[5])) {
            part.setReturnPart(new BigDecimal(line[5].replace(',', '.')).setScale(2));
        } else {
            part.setReturnPart(new BigDecimal("0.00"));
        }

        part.setPrice(new BigDecimal(line[6].replace(',', '.')).setScale(2));
        part.setCurrency(Currency.EUR);
        part.setBrand(line[7]);

        uniqueArticles.add(part.getArticle());

        return part;
    }

    /**
     * Validates a CSV line for correctness.
     *
     * @param line CSV line as string array
     * @param response Map to collect validation errors
     * @param lineNumber Current line number for error reporting
     * @return true if line is invalid, false otherwise
     */
    private boolean isInvalidLine(String[] line, Map<String, String> response, int lineNumber) {
        if (line.length != EXPECTED_COLUMNS) {
            log.warn("Incorrect number of columns, line {}", lineNumber);
            response.put("error_row_" + lineNumber, "Incorrect number of columns");
            return true;
        }

        if (StringUtils.isEmpty(line[0]) || StringUtils.isEmpty(line[1])
                || StringUtils.isEmpty(line[6]) || StringUtils.isEmpty(line[7])) {
            log.warn("Incorrect column values, line {}", lineNumber);
            response.put("error_row_" + lineNumber,
                    "Incorrect column values. All of the following must be filled in: article, name, price, brand");
            return true;
        }

        if (iPartRepository.existsByArticle(line[0]) || uniqueArticles.contains(line[0])) {
            response.put("error_row_" + lineNumber, "Duplicate article " + line[0]);
            return true;
        }

        if (line[0].length() > 50) {
            log.warn("Incorrect article size {}, line {}", line[0], lineNumber);
            response.put("error_row_" + lineNumber, "Incorrect article size " + line[0]);
            return true;
        }

        if (line[1].length() > 255) {
            log.warn("Incorrect name size {}, line {}", line[1], lineNumber);
            response.put("error_row_" + lineNumber, "Incorrect name size " + line[1]);
            return true;
        }

        if (line[7].length() > 50) {
            log.warn("Incorrect brand size {}, line {}", line[7], lineNumber);
            response.put("error_row_" + lineNumber, "Incorrect brand size " + line[7]);
            return true;
        }

        if (StringUtils.isNotEmpty(line[2])) {
            try {
                if (Double.parseDouble(line[2].replace(',', '.')) < 0) {
                    log.warn("Invalid weight {}, line {}", line[2], lineNumber);
                    response.put("error_row_" + lineNumber, "Invalid weight " + line[2]);
                    return true;
                }
            } catch (NumberFormatException e) {
                log.warn("Incorrect number format {}, line {}", line[2], lineNumber);
                response.put("error_row_" + lineNumber, "Incorrect number format " + line[6]);
                return true;
            }
        }

        if (StringUtils.isNotEmpty(line[3])) {
            try {
                if (Integer.parseInt(line[3]) < 1) {
                    log.warn("Invalid minCount {}, line {}", line[3], lineNumber);
                    response.put("error_row_" + lineNumber, "Invalid minCount " + line[3]);
                    return true;
                }
            } catch (NumberFormatException e) {
                log.warn("Incorrect number format {}, line {}", line[3], lineNumber);
                response.put("error_row_" + lineNumber, "Incorrect number format " + line[3]);
                return true;
            }
        }

        if (StringUtils.isNotEmpty(line[4])) {
            try {
                if (Integer.parseInt(line[4]) < 0) {
                    log.warn("Invalid storageCount {}, line {}", line[4], lineNumber);
                    response.put("error_row_" + lineNumber, "Invalid storageCount " + line[4]);
                    return true;
                }
            } catch (NumberFormatException e) {
                log.warn("Incorrect number format {}, line {}", line[4], lineNumber);
                response.put("error_row_" + lineNumber, "Incorrect number format " + line[4]);
                return true;
            }
        }

        if (StringUtils.isNotEmpty(line[5])) {
            try {
                BigDecimal price = new BigDecimal(line[5].replace(',', '.'));

                if (price.scale() > 2 || price.compareTo(BigDecimal.ZERO) < 0) {
                    log.warn("Invalid returnPart {}, line {}", line[5], lineNumber);
                    response.put("error_row_" + lineNumber, "Invalid returnPart " + line[5]);
                }
            } catch (NumberFormatException e) {
                log.warn("Incorrect number format {}, line {}", line[5], lineNumber);
                response.put("error_row_" + lineNumber, "Incorrect number format " + line[5]);
                return true;
            }
        }

        try {
            BigDecimal price = new BigDecimal(line[6].replace(',', '.'));

            if (price.scale() > 2 || price.compareTo(BigDecimal.ZERO) < 0) {
                log.warn("Invalid price {}, line {}", line[6], lineNumber);
                response.put("error_row_" + lineNumber, "Invalid price " + line[6]);
            }
        } catch (NumberFormatException e) {
            log.warn("Incorrect number format {}, line {}", line[6], lineNumber);
            response.put("error_row_" + lineNumber, "Incorrect number format " + line[6]);
            return true;
        }

        return false;
    }
}
