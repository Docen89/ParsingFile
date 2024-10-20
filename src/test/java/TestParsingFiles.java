import com.codeborne.pdftest.PDF;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.codeborne.xlstest.XLS;

import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

import static java.util.Objects.requireNonNull;

public class TestParsingFiles {

  private ClassLoader cl = TestParsingFiles.class.getClassLoader();

  @DisplayName("Проверка csv файлов в архиве example.zip")
  @Test
  void zipCheckCsvFileTest() throws Exception {
    try (ZipInputStream zis = new ZipInputStream(
        requireNonNull(cl.getResourceAsStream("example.zip")))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        if (entry.getName().endsWith(".csv")) {
          CSVReader csv = new CSVReader(new InputStreamReader(zis));
          List<String[]> values = csv.readAll();
          assertThat(values).isNotEmpty().hasSize(2);
          assertThat(values.get(0)).isEqualTo(new String[]{"UAZ", "3159"});
          assertThat(values.get(1)).isEqualTo(new String[]{"UAZ", "3153"});

        }
      }
    }

  }

  @DisplayName("Проверка xlsx файлов в архиве example.zip")
  @Test
  void zipFileParsingTest() throws Exception {
    try (ZipInputStream zis = new ZipInputStream((cl.getResourceAsStream("example.zip")))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        if (entry.getName().endsWith(".xlsx")) {
          XLS xls = new XLS(zis);
          String value = xls.excel.getSheetAt(0).getRow(189).getCell(4).getStringCellValue();
          assertThat(value).isEqualTo("Андреева Алла Алексеевна");

        }
      }
    }
  }

  @DisplayName("Проверка pdf файлов в архиве example.zip")
  @Test
  void zipCheckPdfFileTest() throws Exception {
    try (ZipInputStream zis = new ZipInputStream((cl.getResourceAsStream("example.zip")))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        if (entry.getName().contains("role.pdf")) {
          PDF pdf = new PDF(zis);
          assertThat(pdf.numberOfPages).isEqualTo(9);
          assertThat(pdf.author).isEqualTo(null);

        }
      }
    }
  }
}
