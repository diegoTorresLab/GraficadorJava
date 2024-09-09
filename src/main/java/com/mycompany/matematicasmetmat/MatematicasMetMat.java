/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.matematicasmetmat;
import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 *
 * @author diego
 */
public class MatematicasMetMat {
    public static void graficarFuncion(Expression funcion, double xMin, double xMax, double paso) {
        XYSeries series = new XYSeries("f(x)");

        for (double x = xMin; x <= xMax; x += paso) {
            double y = funcion.setVariable("x", x).evaluate();
            series.add(x, y);
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Gráfico",
                "X",
                "f(X)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        plot.setRangeCrosshairVisible(true);
        plot.setDomainCrosshairVisible(true);

        JFrame frame = new JFrame("Gráfica de la función");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(new ChartPanel(chart), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public static double metodoNewton(Expression funcion, Expression derivada, double x0, double tolerancia, int maxIteraciones) {
        double x = x0;
        int iteraciones = 0;
        double error;

        do {
            double fx = funcion.setVariable("x", x).evaluate();
            double fpx = derivada.setVariable("x", x).evaluate();
            if (fpx == 0) {
                throw new ArithmeticException("Derivada cero, método falló.");
            }
            double xNuevo = x - fx / fpx;
            error = Math.abs(xNuevo - x);
            x = xNuevo;
            iteraciones++;
        } while (error > tolerancia && iteraciones < maxIteraciones);

        return x;
    }

    public static double metodoSecante(Expression funcion, double x0, double x1, double tolerancia, int maxIteraciones) {
        double xAnt = x0;
        double x = x1;
        int iteraciones = 0;
        double error;

        do {
            double fxAnt = funcion.setVariable("x", xAnt).evaluate();
            double fx = funcion.setVariable("x", x).evaluate();
            if (fx == fxAnt) {
                throw new ArithmeticException("División por cero, método falló.");
            }
            double xNuevo = x - fx * (x - xAnt) / (fx - fxAnt);
            error = Math.abs(xNuevo - x);
            xAnt = x;
            x = xNuevo;
            iteraciones++;
        } while (error > tolerancia && iteraciones < maxIteraciones);

        return x;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Métodos Numéricos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(0, 1));

        // Panel de entrada de datos
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));

        JLabel funcionLabel = new JLabel("Función (ejemplo: x^2 - 4):");
        JTextField funcionField = new JTextField();
        panel.add(funcionLabel);
        panel.add(funcionField);

        JLabel xMinLabel = new JLabel("Valor mínimo de x:");
        JTextField xMinField = new JTextField();
        panel.add(xMinLabel);
        panel.add(xMinField);

        JLabel xMaxLabel = new JLabel("Valor máximo de x:");
        JTextField xMaxField = new JTextField();
        panel.add(xMaxLabel);
        panel.add(xMaxField);

        JLabel pasoLabel = new JLabel("Valor del paso:");
        JTextField pasoField = new JTextField();
        panel.add(pasoLabel);
        panel.add(pasoField);

        JLabel toleranciaLabel = new JLabel("Tolerancia:");
        JTextField toleranciaField = new JTextField("0.001");
        panel.add(toleranciaLabel);
        panel.add(toleranciaField);

        JLabel maxIteracionesLabel = new JLabel("Máx Iteraciones:");
        JTextField maxIteracionesField = new JTextField("100");
        panel.add(maxIteracionesLabel);
        panel.add(maxIteracionesField);

        JLabel metodoLabel = new JLabel("Seleccionar método:");
        JComboBox<String> metodoComboBox = new JComboBox<>(new String[]{"Método de Newton", "Método de la Secante"});
        panel.add(metodoLabel);
        panel.add(metodoComboBox);

        frame.add(panel, BorderLayout.CENTER);

        // Botón para ejecutar
        JButton ejecutarButton = new JButton("Ejecutar");
        frame.add(ejecutarButton, BorderLayout.SOUTH);

        // Acción del botón
        ejecutarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Leer entradas
                    String funcionStr = funcionField.getText();
                    double xMin = Double.parseDouble(xMinField.getText());
                    double xMax = Double.parseDouble(xMaxField.getText());
                    double paso = Double.parseDouble(pasoField.getText());
                    double tolerancia = Double.parseDouble(toleranciaField.getText());
                    int maxIteraciones = Integer.parseInt(maxIteracionesField.getText());
                    String metodo = (String) metodoComboBox.getSelectedItem();

                    // Crear la expresión
                    Expression funcion = new ExpressionBuilder(funcionStr).variable("x").build();

                    // Graficar la función
                    graficarFuncion(funcion, xMin, xMax, paso);

                    // Ejecutar el método numérico
                    if (metodo.equals("Método de Newton")) {
                        String derivadaStr = JOptionPane.showInputDialog("Ingrese la derivada de la función (ejemplo: 6*x^2 - 8*x + 1):");
                        Expression derivada = new ExpressionBuilder(derivadaStr).variable("x").build();
                        double x0 = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el valor inicial para el método de Newton:"));
                        double resultado = metodoNewton(funcion, derivada, x0, tolerancia, maxIteraciones);
                        JOptionPane.showMessageDialog(frame, "Método de Newton:\nValor solución aproximado: " + resultado + "\nNúmero de iteraciones: " + maxIteraciones);
                    } else if (metodo.equals("Método de la Secante")) {
                        double x0 = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el primer valor inicial para el método de la Secante:"));
                        double x1 = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el segundo valor inicial para el método de la Secante:"));
                        double resultado = metodoSecante(funcion, x0, x1, tolerancia, maxIteraciones);
                        JOptionPane.showMessageDialog(frame, "Método de la Secante:\nValor solución aproximado: " + resultado + "\nNúmero de iteraciones: " + maxIteraciones);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }
}
