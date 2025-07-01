package pe.edu.upeu;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Label;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import java.text.Normalizer;
import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.LineChart;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import org.apache.commons.math3.distribution.NormalDistribution;
import javafx.embed.swing.SwingFXUtils;

public class ExcelGUIController {
    @FXML private Button btnImportar;
    @FXML private Button btnExportar;
    @FXML private Button btnExportarGraficos;
    @FXML private TableView<List<String>> tablaExcel;
    @FXML private TableView<ResultadoEstadistica> tablaResultados;
    @FXML private TableColumn<ResultadoEstadistica, String> colVar, colEstadistica, colResultado;
    @FXML private RadioButton radio1, radio2, radio3, radio4, radio5, radio6, radio7;
    @FXML private Label lblExcelTitulo;
    @FXML private VBox vboxDescriptivos;
    @FXML private HBox hboxMetricas, hboxOrdinales, hboxNominales;
    @FXML private VBox vboxVariablesTipo;
    @FXML private CheckBox cbValorMedio, cbMediana, cbModa, cbSuma, cbDesviacion, cbVarianza, cbMinMax, cbRango, cbQuartil, cbAsimetria, cbCurtosis, cbNumValidos, cbIC95, cbMeanStd, cbNormalidad;
    @FXML private CheckBox cbHistograma, cbBoxPlot, cbViolin, cbRaincloud, cbQQ, cbProbNormal, cbLineas;
    @FXML private TabPane tabPaneGraficos;
    @FXML private VBox vboxHipotesis;
    @FXML private CheckBox cbTTest, cbTTestPareada, cbTTestUnaMuestra, cbMannWhitney, cbChiCuadrado, cbAnova, cbAnova3Vias, cbAnovaMixto, cbKruskalWallis, cbFriedman, cbBinomial, cbWilcoxon;
    @FXML private VBox vboxGraficos;
    @FXML private VBox vboxCorrelacion;
    @FXML private CheckBox cbPearson, cbSpearman, cbPuntoBiserial, cbKendall;
    @FXML private VBox vboxRegresion;
    @FXML private CheckBox cbRegresionLineal, cbRegresionLogistica;
    @FXML private VBox vboxMediacion;
    @FXML private CheckBox cbModeracion, cbMediacion;
    @FXML private VBox vboxPCA;
    @FXML private CheckBox cbFactores, cbComponentes;
    @FXML private VBox vboxFiabilidad;
    @FXML private CheckBox cbAlfaCronbach, cbKappaCohen, cbKappaFleiss, cbTauKendall, cbWKendall, cbCorrelacionIntraclase;

    private ObservableList<List<String>> data = FXCollections.observableArrayList();
    private List<CheckBox> allVarCheckBoxes = new ArrayList<>();
    private List<CheckBox> allEstadCheckBoxes = new ArrayList<>();

    @FXML
    public void initialize() {
        btnImportar.setOnAction(e -> importarExcel());
        btnExportar.setOnAction(e -> exportarExcel());
        btnExportarGraficos.setOnAction(e -> exportarGraficoActual());
        tablaExcel.setItems(data);
        ToggleGroup group = new ToggleGroup();
        radio1.setToggleGroup(group);
        radio2.setToggleGroup(group);
        radio3.setToggleGroup(group);
        radio4.setToggleGroup(group);
        radio5.setToggleGroup(group);
        radio6.setToggleGroup(group);
        radio7.setToggleGroup(group);
        // Permitir alternar selección/deselección de RadioButton
        RadioButton[] radios = {radio1, radio2, radio3, radio4, radio5, radio6, radio7};
        for (RadioButton rb : radios) {
            rb.setToggleGroup(group);
            rb.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> {
                if (rb.isSelected()) {
                    group.selectToggle(null);
                    e.consume();
                }
            });
        }
        // Mostrar los checkbox solo si algún RadioButton está seleccionado
        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            boolean selected = newToggle != null && ((RadioButton)newToggle).isSelected();
            vboxDescriptivos.setVisible(selected && newToggle == radio1);
            vboxDescriptivos.setManaged(selected && newToggle == radio1);
            vboxVariablesTipo.setVisible(selected && newToggle == radio1);
            vboxVariablesTipo.setManaged(selected && newToggle == radio1);
            vboxHipotesis.setVisible(selected && newToggle == radio2);
            vboxHipotesis.setManaged(selected && newToggle == radio2);
            vboxCorrelacion.setVisible(selected && newToggle == radio3);
            vboxCorrelacion.setManaged(selected && newToggle == radio3);
            vboxRegresion.setVisible(selected && newToggle == radio4);
            vboxRegresion.setManaged(selected && newToggle == radio4);
            vboxMediacion.setVisible(selected && newToggle == radio5);
            vboxMediacion.setManaged(selected && newToggle == radio5);
            vboxPCA.setVisible(selected && newToggle == radio6);
            vboxPCA.setManaged(selected && newToggle == radio6);
            vboxFiabilidad.setVisible(selected && newToggle == radio7);
            vboxFiabilidad.setManaged(selected && newToggle == radio7);
            vboxGraficos.setVisible((selected && (newToggle == radio1 || newToggle == radio2)));
            vboxGraficos.setManaged((selected && (newToggle == radio1 || newToggle == radio2)));
            tabPaneGraficos.setVisible(false);
            tabPaneGraficos.setManaged(false);
        });
        vboxDescriptivos.setVisible(false);
        vboxDescriptivos.setManaged(false);
        vboxVariablesTipo.setVisible(false);
        vboxVariablesTipo.setManaged(false);
        vboxHipotesis.setVisible(false);
        vboxHipotesis.setManaged(false);
        vboxCorrelacion.setVisible(false);
        vboxCorrelacion.setManaged(false);
        vboxRegresion.setVisible(false);
        vboxRegresion.setManaged(false);
        vboxMediacion.setVisible(false);
        vboxMediacion.setManaged(false);
        vboxPCA.setVisible(false);
        vboxPCA.setManaged(false);
        vboxFiabilidad.setVisible(false);
        vboxFiabilidad.setManaged(false);
        vboxGraficos.setVisible(false);
        vboxGraficos.setManaged(false);
        tabPaneGraficos.setVisible(false);
        tabPaneGraficos.setManaged(false);
        colVar.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().variable));
        colEstadistica.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().estadistica));
        colResultado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().resultado));
        // Guardar referencias a los checkbox de estadística
        allEstadCheckBoxes = Arrays.asList(cbValorMedio, cbMediana, cbModa, cbSuma, cbDesviacion, cbVarianza, cbMinMax, cbRango, cbQuartil, cbAsimetria, cbCurtosis, cbNumValidos, cbIC95, cbMeanStd, cbNormalidad);
        cbHistograma.selectedProperty().addListener((obs, oldVal, newVal) -> actualizarGraficos());
        cbBoxPlot.selectedProperty().addListener((obs, oldVal, newVal) -> actualizarGraficos());
        cbViolin.selectedProperty().addListener((obs, oldVal, newVal) -> actualizarGraficos());
        cbRaincloud.selectedProperty().addListener((obs, oldVal, newVal) -> actualizarGraficos());
        cbQQ.selectedProperty().addListener((obs, oldVal, newVal) -> actualizarGraficos());
        cbProbNormal.selectedProperty().addListener((obs, oldVal, newVal) -> actualizarGraficos());
        cbLineas.selectedProperty().addListener((obs, oldVal, newVal) -> actualizarGraficos());
    }

    private void importarExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            String fileName = file.getName();
            if (fileName.endsWith(".xlsx")) {
                fileName = fileName.substring(0, fileName.length() - 5);
            }
            lblExcelTitulo.setText(fileName);
            try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
                Sheet sheet = workbook.getSheetAt(0);
                data.clear();
                tablaExcel.getColumns().clear();
                boolean firstRow = true;
                List<String> especiales = Arrays.asList(
                    "salario", "peso", "altura", "edad", "velocidad", "temperatura corporal"
                );
                List<String> ordinalesPorDefecto = Arrays.asList(
                    "nivel de satisfacción", "nivel educativo", "calificación de servicio", "grado academico"
                );
                List<String> nominalesPorDefecto = Arrays.asList(
                    "género", "país", "estado civil", "profesión"
                );
                // Limpiar los HBox antes de agregar nuevos checkbox
                hboxMetricas.getChildren().clear();
                hboxOrdinales.getChildren().clear();
                hboxNominales.getChildren().clear();
                List<ComboBox<String>> comboHeaders = new ArrayList<>();
                for (Row row : sheet) {
                    List<String> rowData = new ArrayList<>();
                    for (Cell cell : row) {
                        cell.setCellType(CellType.STRING);
                        rowData.add(cell.getStringCellValue());
                    }
                    if (firstRow) {
                        for (int i = 0; i < rowData.size(); i++) {
                            final int colIndex = i;
                            String colTitle = normalizar(rowData.get(i));
                            TableColumn<List<String>, String> column;
                            column = new TableColumn<>("");
                            column.setCellValueFactory(param -> {
                                List<String> rowList = param.getValue();
                                if (colIndex < rowList.size()) {
                                    return new javafx.beans.property.SimpleStringProperty(rowList.get(colIndex));
                                } else {
                                    return new javafx.beans.property.SimpleStringProperty("");
                                }
                            });
                            ObservableList<String> opciones = FXCollections.observableArrayList(
                                "Metricas", "Ordinales", "Nominales"
                            );
                            HBox headerBox = new HBox(5);
                            headerBox.setAlignment(javafx.geometry.Pos.CENTER);
                            Label headerLabel = new Label(rowData.get(i));
                            ComboBox<String> comboHeader = new ComboBox<>(opciones);
                            if (especiales.stream().anyMatch(e -> normalizar(e).equals(colTitle))) {
                                comboHeader.setValue("Metricas");
                            } else if (ordinalesPorDefecto.stream().anyMatch(e -> normalizar(e).equals(colTitle))) {
                                comboHeader.setValue("Ordinales");
                            } else if (nominalesPorDefecto.stream().anyMatch(e -> normalizar(e).equals(colTitle))) {
                                comboHeader.setValue("Nominales");
                            } else {
                                comboHeader.setValue(null);
                            }
                            headerBox.getChildren().addAll(headerLabel, comboHeader);
                            // Color dinámico según selección para header y celdas
                            Runnable actualizarColor = () -> {
                                String valor = comboHeader.getValue();
                                final String color;
                                if ("Metricas".equals(valor)) {
                                    color = "-fx-background-color: #e3f2fd;";
                                } else if ("Ordinales".equals(valor)) {
                                    color = "-fx-background-color: #ffe0b2;";
                                } else if ("Nominales".equals(valor)) {
                                    color = "-fx-background-color: #e0f2f1;";
                                } else {
                                    color = "";
                                }
                                headerBox.setStyle(color + "-fx-border-radius: 8; -fx-background-radius: 8;");
                                comboHeader.setStyle(color + "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: black; -fx-border-width: 1;");
                                column.setCellFactory(col -> new TableCell<List<String>, String>() {
                                    @Override
                                    protected void updateItem(String item, boolean empty) {
                                        super.updateItem(item, empty);
                                        setStyle(color);
                                        if (empty) {
                                            setText(null);
                                        } else {
                                            setText(item);
                                        }
                                    }
                                });
                            };
                            comboHeader.valueProperty().addListener((obs, oldVal, newVal) -> actualizarColor.run());
                            actualizarColor.run();
                            column.setGraphic(headerBox);
                            tablaExcel.getColumns().add(column);
                            // Guardar referencia para los checkbox
                            comboHeaders.add(comboHeader);
                        }
                        firstRow = false;
                    } else {
                        data.add(rowData);
                    }
                }
                tablaExcel.setEditable(true);
                // Crear los checkbox debajo de la tabla
                allVarCheckBoxes.clear();
                for (int i = 0; i < comboHeaders.size(); i++) {
                    ComboBox<String> combo = comboHeaders.get(i);
                    String varName = ((Label)((HBox)combo.getParent()).getChildren().get(0)).getText();
                    CheckBox cb = new CheckBox(varName);
                    allVarCheckBoxes.add(cb);
                    String valor = combo.getValue();
                    if ("Metricas".equals(valor)) {
                        hboxMetricas.getChildren().add(cb);
                    } else if ("Ordinales".equals(valor)) {
                        hboxOrdinales.getChildren().add(cb);
                    } else if ("Nominales".equals(valor)) {
                        hboxNominales.getChildren().add(cb);
                    }
                    // Actualizar dinámicamente si cambia el tipo
                    combo.valueProperty().addListener((obs, oldVal, newVal) -> {
                        hboxMetricas.getChildren().remove(cb);
                        hboxOrdinales.getChildren().remove(cb);
                        hboxNominales.getChildren().remove(cb);
                        if ("Metricas".equals(newVal)) {
                            hboxMetricas.getChildren().add(cb);
                        } else if ("Ordinales".equals(newVal)) {
                            hboxOrdinales.getChildren().add(cb);
                        } else if ("Nominales".equals(newVal)) {
                            hboxNominales.getChildren().add(cb);
                        }
                    });
                }
                // Listener para mostrar/ocultar vboxDescriptivos según selección de cualquier checkbox
                for (CheckBox cb : allVarCheckBoxes) {
                    cb.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        boolean algunoSeleccionado = allVarCheckBoxes.stream().anyMatch(CheckBox::isSelected);
                        vboxDescriptivos.setVisible(algunoSeleccionado);
                        vboxDescriptivos.setManaged(algunoSeleccionado);
                        actualizarTablaResultados();
                    });
                }
                for (CheckBox cb : allEstadCheckBoxes) {
                    cb.selectedProperty().addListener((obs, oldVal, newVal) -> actualizarTablaResultados());
                }
                // Al importar, ocultar vboxDescriptivos y limpiar resultados
                vboxDescriptivos.setVisible(false);
                vboxDescriptivos.setManaged(false);
                tablaResultados.getItems().clear();
                // Ajustar el ancho de las columnas al contenido
                for (TableColumn<List<String>, ?> column : tablaExcel.getColumns()) {
                    ajustarAnchoColumna(column);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void exportarExcel() {
        if (data.isEmpty() || tablaExcel.getColumns().isEmpty()) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Datos");
                // Escribir encabezados
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < tablaExcel.getColumns().size(); i++) {
                    headerRow.createCell(i).setCellValue(tablaExcel.getColumns().get(i).getText());
                }
                // Escribir datos
                for (int i = 0; i < data.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    List<String> rowData = data.get(i);
                    for (int j = 0; j < rowData.size(); j++) {
                        row.createCell(j).setCellValue(rowData.get(j));
                    }
                }
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void ajustarAnchoColumna(TableColumn<List<String>, ?> column) {
        column.setPrefWidth(80); // Ancho mínimo
        column.setMinWidth(40);
        column.setMaxWidth(600);
        column.setResizable(true);
        // Si la columna tiene celdas con ComboBox, solo ajusta al header
        if (column.getGraphic() != null) {
            column.setMinWidth(180);
            column.setPrefWidth(200);
            return;
        }
        // Si es columna normal, ajusta al texto más largo
        double max = 80;
        for (int row = 0; row < tablaExcel.getItems().size(); row++) {
            Object cellData = column.getCellData(row);
            if (cellData != null) {
                double width = cellData.toString().length() * 7 + 30;
                if (width > max) max = width;
            }
        }
        column.setPrefWidth(max);
    }

    // Función para normalizar texto (sin tildes y en minúsculas)
    private String normalizar(String texto) {
        if (texto == null) return "";
        String nfdNormalizedString = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return nfdNormalizedString.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase().trim();
    }

    private void actualizarTablaResultados() {
        // Verificar si hay alguna estadística seleccionada
        boolean algunaEstadistica = false;
        for (CheckBox cb : allEstadCheckBoxes) {
            if (cb.isSelected()) { algunaEstadistica = true; break; }
        }
        tablaResultados.setVisible(algunaEstadistica);
        tablaResultados.setManaged(algunaEstadistica);
        if (!algunaEstadistica) {
            tablaResultados.getItems().clear();
            return;
        }
        tablaResultados.getItems().clear();
        // Recorrer todas las columnas y sus ComboBox de tipo
        for (int i = 0; i < tablaExcel.getColumns().size(); i++) {
            TableColumn<List<String>, ?> col = tablaExcel.getColumns().get(i);
            HBox header = (HBox) col.getGraphic();
            if (header != null && header.getChildren().get(1) instanceof ComboBox) {
                ComboBox<String> combo = (ComboBox<String>) header.getChildren().get(1);
                String tipo = combo.getValue();
                if (!"Metricas".equals(tipo)) continue; // Solo columnas métricas
                String var = ((Label)header.getChildren().get(0)).getText();
                List<Double> datos = new ArrayList<>();
                for (List<String> fila : data) {
                    try {
                        datos.add(Double.parseDouble(fila.get(i)));
                    } catch (Exception ex) { }
                }
                List<String> estadisticasSeleccionadas = new ArrayList<>();
                String[] nombres = {"Valor Medio","Mediana","Moda","Suma","Desviacion Tipica","Varianza","Minimo y Maximo","Rango","Quartil 1,2y3","Asimetria","Curtosis","Numero de valores validos.","Intervalo de confianza del 95%.","Mean ± Std","Prueba de distribución normal."};
                for (int j = 0; j < allEstadCheckBoxes.size(); j++) {
                    if (allEstadCheckBoxes.get(j).isSelected()) estadisticasSeleccionadas.add(nombres[j]);
                }
                for (String est : estadisticasSeleccionadas) {
                    String resultado = calcularEstadistica(est, datos);
                    tablaResultados.getItems().add(new ResultadoEstadistica(var, est, resultado));
                }
            }
        }
    }

    private String formatNumber(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        } else {
            return String.format("%.2f", value);
        }
    }

    private String calcularEstadistica(String est, List<Double> datos) {
        if (datos.isEmpty()) return "-";
        switch (est) {
            case "Valor Medio":
                return formatNumber(datos.stream().mapToDouble(Double::doubleValue).average().orElse(0));
            case "Mediana":
                List<Double> sorted = new ArrayList<>(datos);
                Collections.sort(sorted);
                int n = sorted.size();
                if (n % 2 == 0) return formatNumber((sorted.get(n/2-1)+sorted.get(n/2))/2);
                else return formatNumber(sorted.get(n/2));
            case "Moda":
                Map<Double, Long> freq = datos.stream().collect(java.util.stream.Collectors.groupingBy(e->e, java.util.stream.Collectors.counting()));
                long max = freq.values().stream().mapToLong(l->l).max().orElse(0);
                List<Double> modas = freq.entrySet().stream().filter(e->e.getValue()==max).map(Map.Entry::getKey).toList();
                return modas.size()==1 ? formatNumber(modas.get(0)) : modas.toString();
            case "Suma":
                return formatNumber(datos.stream().mapToDouble(Double::doubleValue).sum());
            case "Desviacion Tipica":
                double media = datos.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                double desv = Math.sqrt(datos.stream().mapToDouble(x->Math.pow(x-media,2)).sum()/datos.size());
                return formatNumber(desv);
            case "Varianza":
                double m = datos.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                double var = datos.stream().mapToDouble(x->Math.pow(x-m,2)).sum()/datos.size();
                return formatNumber(var);
            case "Minimo y Maximo":
                double min = datos.stream().mapToDouble(Double::doubleValue).min().orElse(0);
                double maxv = datos.stream().mapToDouble(Double::doubleValue).max().orElse(0);
                return formatNumber(min)+" / "+formatNumber(maxv);
            case "Rango":
                double minR = datos.stream().mapToDouble(Double::doubleValue).min().orElse(0);
                double maxR = datos.stream().mapToDouble(Double::doubleValue).max().orElse(0);
                return formatNumber(maxR-minR);
            case "Quartil 1,2y3":
                List<Double> s = new ArrayList<>(datos); Collections.sort(s);
                double q1 = s.get((int)Math.floor((s.size()-1)*0.25));
                double q2 = s.get((int)Math.floor((s.size()-1)*0.5));
                double q3 = s.get((int)Math.floor((s.size()-1)*0.75));
                return "Q1: "+formatNumber(q1)+", Q2: "+formatNumber(q2)+", Q3: "+formatNumber(q3);
            case "Asimetria":
                double mean = datos.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                double sd = Math.sqrt(datos.stream().mapToDouble(x->Math.pow(x-mean,2)).sum()/datos.size());
                double skew = datos.stream().mapToDouble(x->Math.pow((x-mean)/sd,3)).sum()/datos.size();
                return formatNumber(skew);
            case "Curtosis":
                double mean2 = datos.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                double sd2 = Math.sqrt(datos.stream().mapToDouble(x->Math.pow(x-mean2,2)).sum()/datos.size());
                double kurt = datos.stream().mapToDouble(x->Math.pow((x-mean2)/sd2,4)).sum()/datos.size()-3;
                return formatNumber(kurt);
            case "Numero de valores validos.":
                return String.valueOf(datos.size());
            case "Intervalo de confianza del 95%.":
                double avg = datos.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                double std = Math.sqrt(datos.stream().mapToDouble(x->Math.pow(x-avg,2)).sum()/datos.size());
                double error = 1.96*std/Math.sqrt(datos.size());
                return formatNumber(avg-error)+" - "+formatNumber(avg+error);
            case "Mean ± Std":
                double avg2 = datos.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                double std2 = Math.sqrt(datos.stream().mapToDouble(x->Math.pow(x-avg2,2)).sum()/datos.size());
                return formatNumber(avg2)+" ± "+formatNumber(std2);
            case "Prueba de distribución normal.":
                // Test de Shapiro-Wilk o similar requiere librería externa, aquí solo se muestra si la asimetría y curtosis están cerca de 0
                double mean3 = datos.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                double sd3 = Math.sqrt(datos.stream().mapToDouble(x->Math.pow(x-mean3,2)).sum()/datos.size());
                double skew3 = datos.stream().mapToDouble(x->Math.pow((x-mean3)/sd3,3)).sum()/datos.size();
                double kurt3 = datos.stream().mapToDouble(x->Math.pow((x-mean3)/sd3,4)).sum()/datos.size()-3;
                return (Math.abs(skew3)<1 && Math.abs(kurt3)<1) ? "Aprox. Normal" : "No Normal";
            default:
                return "-";
        }
    }

    private void actualizarGraficos() {
        tabPaneGraficos.getTabs().clear();
        boolean mostrar = false;
        // Determinar si estamos en descriptivos o hipótesis
        boolean esDescriptivo = radio1.isSelected();
        boolean esHipotesis = radio2.isSelected();
        if (!(esDescriptivo || esHipotesis)) {
            tabPaneGraficos.setVisible(false);
            tabPaneGraficos.setManaged(false);
            return;
        }
        // Recorrer todas las columnas métricas
        for (int i = 0; i < tablaExcel.getColumns().size(); i++) {
            TableColumn<List<String>, ?> col = tablaExcel.getColumns().get(i);
            HBox header = (HBox) col.getGraphic();
            if (header != null && header.getChildren().get(1) instanceof ComboBox) {
                ComboBox<String> combo = (ComboBox<String>) header.getChildren().get(1);
                String tipo = combo.getValue();
                if (!"Metricas".equals(tipo)) continue;
                String var = ((Label)header.getChildren().get(0)).getText();
                List<Double> datos = new ArrayList<>();
                for (List<String> fila : data) {
                    try {
                        datos.add(Double.parseDouble(fila.get(i)));
                    } catch (Exception ex) { }
                }
                if (cbHistograma.isSelected() && (esDescriptivo || esHipotesis)) {
                    Tab tab = new Tab(var+" - Histograma");
                    tab.setContent(crearHistograma(datos, var));
                    tabPaneGraficos.getTabs().add(tab);
                    mostrar = true;
                }
                if (cbQQ.isSelected() && (esDescriptivo || esHipotesis)) {
                    Tab tab = new Tab(var+" - Q-Q");
                    tab.setContent(crearQQPlot(datos, var));
                    tabPaneGraficos.getTabs().add(tab);
                    mostrar = true;
                }
                if (cbBoxPlot.isSelected() && esDescriptivo) {
                    Tab tab = new Tab(var+" - BoxPlot");
                    tab.setContent(crearBoxPlot(datos, var));
                    tabPaneGraficos.getTabs().add(tab);
                    mostrar = true;
                }
                if (cbViolin.isSelected() && esDescriptivo) {
                    Tab tab = new Tab(var+" - Violín");
                    tab.setContent(new Label("[Datos no suficientes]") );
                    tabPaneGraficos.getTabs().add(tab);
                    mostrar = true;
                }
                if (cbRaincloud.isSelected() && esDescriptivo) {
                    Tab tab = new Tab(var+" - Raincloud");
                    tab.setContent(new Label("[Datos no suficientes]") );
                    tabPaneGraficos.getTabs().add(tab);
                    mostrar = true;
                }
                if (cbProbNormal.isSelected() && esDescriptivo) {
                    Tab tab = new Tab(var+" - Prob. Normal");
                    tab.setContent(new Label("[Datos no suficientes]") );
                    tabPaneGraficos.getTabs().add(tab);
                    mostrar = true;
                }
                if (cbLineas.isSelected() && esDescriptivo) {
                    Tab tab = new Tab(var+" - Líneas");
                    tab.setContent(crearLineChart(datos, var));
                    tabPaneGraficos.getTabs().add(tab);
                    mostrar = true;
                }
            }
        }
        tabPaneGraficos.setVisible(mostrar);
        tabPaneGraficos.setManaged(mostrar);
    }

    private BarChart<String, Number> crearHistograma(List<Double> datos, String var) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Histograma de " + var);
        xAxis.setLabel(var);
        yAxis.setLabel("Frecuencia");
        int bins = 8;
        if (datos.isEmpty()) return chart;
        double min = datos.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = datos.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double binSize = (max-min)/bins;
        int[] counts = new int[bins];
        for (double d : datos) {
            int idx = (int)((d-min)/binSize);
            if (idx==bins) idx--;
            counts[idx]++;
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < bins; i++) {
            String label = String.format("%.2f-%.2f", min+i*binSize, min+(i+1)*binSize);
            series.getData().add(new XYChart.Data<>(label, counts[i]));
        }
        chart.getData().add(series);
        chart.setLegendVisible(false);
        return chart;
    }

    private LineChart<Number, Number> crearLineChart(List<Double> datos, String var) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Diagrama de líneas de " + var);
        xAxis.setLabel("Índice");
        yAxis.setLabel(var);
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < datos.size(); i++) {
            series.getData().add(new XYChart.Data<>(i+1, datos.get(i)));
        }
        chart.getData().add(series);
        chart.setLegendVisible(false);
        return chart;
    }

    private Pane crearBoxPlot(List<Double> datos, String var) {
        if (datos.isEmpty()) return new Pane(new Label("Sin datos"));
        List<Double> sorted = new ArrayList<>(datos);
        Collections.sort(sorted);
        int n = sorted.size();
        double q1 = sorted.get((int)Math.floor((n-1)*0.25));
        double q2 = sorted.get((int)Math.floor((n-1)*0.5));
        double q3 = sorted.get((int)Math.floor((n-1)*0.75));
        double min = sorted.get(0);
        double max = sorted.get(n-1);
        // Outliers: 1.5*IQR
        double iqr = q3-q1;
        double lowerFence = q1-1.5*iqr;
        double upperFence = q3+1.5*iqr;
        List<Double> outliers = new ArrayList<>();
        for (double d : sorted) {
            if (d<lowerFence || d>upperFence) outliers.add(d);
        }
        double width = 320, height = 180;
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0,0,width,height);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        double xC = width/2;
        double yMin = 30, yMax = height-30;
        // Escalado
        double scale = (yMax-yMin)/(max-min==0?1:max-min);
        java.util.function.DoubleFunction<Double> y = v -> yMax-(v-min)*scale;
        // Caja
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(xC-30, y.apply(q3), 60, y.apply(q1)-y.apply(q3));
        gc.setStroke(Color.DARKBLUE);
        gc.strokeRect(xC-30, y.apply(q3), 60, y.apply(q1)-y.apply(q3));
        // Mediana
        gc.setStroke(Color.RED);
        gc.strokeLine(xC-30, y.apply(q2), xC+30, y.apply(q2));
        // Bigotes
        gc.setStroke(Color.BLACK);
        gc.strokeLine(xC, y.apply(q3), xC, y.apply(max));
        gc.strokeLine(xC, y.apply(q1), xC, y.apply(min));
        gc.strokeLine(xC-15, y.apply(max), xC+15, y.apply(max));
        gc.strokeLine(xC-15, y.apply(min), xC+15, y.apply(min));
        // Outliers
        gc.setFill(Color.ORANGE);
        for (double d : outliers) {
            gc.fillOval(xC-5, y.apply(d)-5, 10, 10);
        }
        // Etiquetas
        gc.setFill(Color.BLACK);
        gc.fillText("Min: "+formatNumber(min), 10, y.apply(min));
        gc.fillText("Q1: "+formatNumber(q1), 10, y.apply(q1));
        gc.fillText("Mediana: "+formatNumber(q2), 10, y.apply(q2));
        gc.fillText("Q3: "+formatNumber(q3), 10, y.apply(q3));
        gc.fillText("Max: "+formatNumber(max), 10, y.apply(max));
        Label title = new Label("BoxPlot de "+var);
        title.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 8 0");
        VBox box = new VBox(title, canvas);
        box.setAlignment(javafx.geometry.Pos.CENTER);
        return box;
    }

    private Pane crearQQPlot(List<Double> datos, String var) {
        if (datos.isEmpty()) return new Pane(new Label("Sin datos"));
        List<Double> sorted = new ArrayList<>(datos);
        Collections.sort(sorted);
        int n = sorted.size();
        Canvas canvas = new Canvas(320,180);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0,0,320,180);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        // Calcular cuantiles teóricos normales
        NormalDistribution nd = new NormalDistribution();
        List<Double> theor = new ArrayList<>();
        for (int i=1; i<=n; i++) {
            theor.add(nd.inverseCumulativeProbability((i-0.5)/n));
        }
        // Escalado
        double minX = theor.stream().min(Double::compare).orElse(0.0);
        double maxX = theor.stream().max(Double::compare).orElse(1.0);
        double minY = sorted.get(0);
        double maxY = sorted.get(n-1);
        double scaleX = 260/(maxX-minX==0?1:maxX-minX);
        double scaleY = 130/(maxY-minY==0?1:maxY-minY);
        // Ejes
        gc.setStroke(Color.GRAY);
        gc.strokeLine(40,150,300,150);
        gc.strokeLine(40,20,40,150);
        // Puntos
        gc.setFill(Color.DARKBLUE);
        for (int i=0; i<n; i++) {
            double x = 40+(theor.get(i)-minX)*scaleX;
            double y = 150-(sorted.get(i)-minY)*scaleY;
            gc.fillOval(x-2,y-2,4,4);
        }
        // Línea de referencia
        gc.setStroke(Color.RED);
        gc.strokeLine(40,150,300,20);
        Label title = new Label("Q-Q Plot de "+var);
        title.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 8 0");
        VBox box = new VBox(title, canvas);
        box.setAlignment(javafx.geometry.Pos.CENTER);
        return box;
    }

    private void exportarGraficoActual() {
        if (tabPaneGraficos.getTabs().isEmpty() || tabPaneGraficos.getSelectionModel().getSelectedItem() == null) return;
        Tab tab = tabPaneGraficos.getSelectionModel().getSelectedItem();
        javafx.scene.Node content = tab.getContent();
        javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
        javafx.scene.image.WritableImage image = content.snapshot(params, null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar gráfico como imagen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagen PNG", "*.png"));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                javax.imageio.ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static class ResultadoEstadistica {
        public String variable;
        public String estadistica;
        public String resultado;
        public ResultadoEstadistica(String v, String e, String r) { variable=v; estadistica=e; resultado=r; }
    }
} 