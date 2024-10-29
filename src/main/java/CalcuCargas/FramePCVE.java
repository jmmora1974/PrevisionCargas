/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package CalcuCargas;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.io.File;
import java.io.IOException;

import java.util.ConcurrentModificationException;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

/**
 *
 * @author Jose Miguel Mora jmmora1974@gmail.com
 */
public class FramePCVE extends javax.swing.JFrame {

    //
    double[] tablaITC10 = {1, 2, 3, 3.8, 4.6, 5.4, 6.2, 7, 7.8, 8.5, 9.2, 9.9, 10.6, 11.3, 11.9, 12.5, 13.1, 13.7, 14.3, 14.8, 15.3};
    int[][] tablaMat = new int[3][3];

    /**
     * Crea la ventana FramePCVE
     */
    public FramePCVE() {

        initComponents();

        //Creamos la tabla de materiales 
        tablaMat[0][0] = 56;
        tablaMat[0][1] = 48;
        tablaMat[0][2] = 44;
        tablaMat[1][0] = 35;
        tablaMat[1][1] = 30;
        tablaMat[1][2] = 28;
        

    }

    //Calcula la previsión de cargas de las viviendas.
    public double calcularP1() {
        try {
            double numbas = Double.parseDouble(NumVivConsuBasico.getText());
            double numele = Double.parseDouble(NumVivConsuAlto.getText());
            double numcontr = Double.parseDouble(NumVivConsuContratado.getText());
            double potcontrada = Double.parseDouble(PotContradaVivCont.getText());
            double numTotalViv = numbas + numele ;

            double vivBasConIrve = Double.parseDouble(VivBasConIrve.getText());
            double vivAltoConIrve = Double.parseDouble(VivAltoCIrve.getText());
            double vivContraConIrve = Double.parseDouble(VivContratadoconIrve.getText());
            double numTotalVivConIrve = vivBasConIrve + vivAltoConIrve + vivContraConIrve;
            double numTotalVivSinIrve = numTotalViv - numTotalVivConIrve;

            double potBasPC = Double.parseDouble(PotPCVivBasConIrve.getText());
            double potAltoPC = Double.parseDouble(PotPCVivCAltoConIrve.getText());
            double potContPC = Double.parseDouble(PotPCVivContConIrve.getText());

            double potVCAltoPC = Double.parseDouble(PotPCVivCAltoConIrve.getText());

            //double numedif = numbas + numele+vivContraConIrve;
            double potmed, pdiurno, pnocturno, pviv = 0.0;

            double cs = 0.0;
            if (numTotalViv > 0) {

                //System.out.println("Por med " + potmed + "edif " + numedif);
                if (numTotalViv > 0 && numTotalViv < 22) {
                    cs = tablaITC10[(int) numTotalViv - 1];
                } else if (numTotalViv > 21) {
                    cs = 15.3 + (numTotalViv - 21) * 0.5;
                } else {
                    System.err.print("El número de edificios no es correcto");
                    return 0;
                }
                //Comprobamos el tipo de esquema para incluir P5
                switch (ComboBoxEsquema.getSelectedItem().toString()) {
                    case "2":
                    case "4a":

                        pdiurno = (((vivBasConIrve * (5.75 + 0.3 * potBasPC) + (vivAltoConIrve * (9.2 + 0.3 * potAltoPC)) + (vivContraConIrve * (potcontrada + 0.3 * potContPC)) + ((numbas - vivBasConIrve) * 5.75 + (numele - vivAltoConIrve) * 5.75) + (numcontr - vivContraConIrve) * potcontrada)) / numTotalViv) * cs;
                        pnocturno = 0.5 * cs * 5.75 + (numTotalVivConIrve * potBasPC);

                        if (pdiurno > pnocturno) {
                            pviv = pdiurno;
                        } else {
                            pviv = pnocturno;
                        }
                        break;
                    default:
                        potmed = ((numbas * 5.75) + (numele * 9.2) ) / (numbas+numele);
                        pviv = potmed * cs + (numcontr * potcontrada);
                        break;
                }

                TResP1.setText(Double.toString(pviv));
                return pviv;
            }

        } catch (NumberFormatException | ConcurrentModificationException e) {
            System.err.println(e);
            JOptionPane.showMessageDialog(null, "Los datos introducidos han de ser númericos.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return 0;
        }
        return 0;
    }

    //Calcula la previsión de carga de los servicios generales
    public double calcularP2() {
        try {

            //Realizamos el calculo de la potencia de los ascensores.
            double potAsc1, potAsc2, potAscensores, motMayor;
            potAsc1 = Double.parseDouble(NumAsc1.getText()) * Double.parseDouble(PotAsc1kW.getText());
            potAsc2 = Double.parseDouble(NumAsc2.getText()) * Double.parseDouble(PotAsc2kW.getText());

            //Comprobamos si ha introducido potencia en CV y calculamos
            if (ComboBoxkWAsc1.getSelectedItem().toString().equals("CV")) {
                potAsc1 *= 0.736;
            }
            if (ComboBoxkWAsc2.getSelectedItem().toString().equals("CV")) {
                potAsc2 *= 0.736;
            }
            potAscensores = potAsc1 * 1.3 + potAsc2 * 1.3;

            // Realizamos el cálculos para el resto de motores.
            double potGrPresion, potDepu, potOtrosMot, potOtros, potTotalMotores;
            potGrPresion = Double.parseDouble(NumGPresion.getText()) * Double.parseDouble(PotGPresion.getText());
            potDepu = Double.parseDouble(NumDepuradora.getText()) * Double.parseDouble(PotDepuradora.getText());
            potOtrosMot = Double.parseDouble(NumOtrosMotores.getText()) * Double.parseDouble(PotOtrosMotores.getText());
            potOtros = Double.parseDouble(NumOtros.getText()) * Double.parseDouble(PotOtrosMotores2.getText());

            //Comprobamos si ha introducido potencia en CV y calculamos
            if (CBoxGPkW.getSelectedItem().toString().equals("CV")) {
                potGrPresion *= 0.736;
            }

            if (CBoxDepukW.getSelectedItem().toString().equals("CV")) {
                potDepu *= 0.736;
            }

            if (CBoxOTkW.getSelectedItem().toString().equals("CV")) {
                potOtrosMot *= 0.736;
            }

            if (CBoxOT2kW.getSelectedItem().toString().equals("CV")) {
                potOtros *= 0.736;
            }

            //Sumamos todas las potencias de los motores
            potTotalMotores = potGrPresion + potDepu + potOtrosMot + potOtros;

            //Multiplicamos por 1,25 el motor de mayor capacidad
            double[] listapotmot = {potGrPresion, potDepu, potOtrosMot, potOtros};
            motMayor = potGrPresion;
            for (int g = 0; g < listapotmot.length; g++) {
                if (listapotmot[g] > motMayor) {
                    motMayor = listapotmot[g];
                }
            }
            potTotalMotores = potTotalMotores - motMayor + (motMayor * 1.25);

            //Calculamos la potencia del alumbrado
            double potAlumFl, potAlumInc, potAlTotal;
            potAlumFl = Double.parseDouble(PotAlFLour.getText()) * 1.8;
            potAlumInc = Double.parseDouble(PotAlInca.getText());
            potAlTotal = potAlumFl + potAlumInc;

            TResP2.setText(Double.toString(potAscensores + potTotalMotores + potAlTotal));

            //Devolvemos la suma de las potencias generales aquiridas. 
            return potAscensores + potTotalMotores + potAlTotal;
        } catch (NumberFormatException | ConcurrentModificationException e) {
            System.err.println(e);
            JOptionPane.showMessageDialog(null, "Los datos introducidos han de ser númericos.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return 0.0;
        }
    }

    //Calcula la previsión de carga de los locales comerciales y oficinas
    public double calcularP3() {
        try {
            double supLocal1, supLocal2, supLocal3, supLocal4;
            double potLocal1, potLocal2, potLocal3, potLocal4, potLocalTotal;

            supLocal1 = Double.parseDouble(SuperLocal1.getText());
            supLocal2 = Double.parseDouble(SuperLocal2.getText());
            supLocal3 = Double.parseDouble(SuperLocal3.getText());
            supLocal4 = Double.parseDouble(SuperLocal4.getText());

            //Comprobamos que los locales tengan la superficie para la potencia minima de 3450W.
            if (supLocal1 < 34.5) {
                supLocal1 = 34.5;
            }
            if (supLocal2 < 34.5) {
                supLocal2 = 34.5;
            }
            if (supLocal3 < 34.5) {
                supLocal3 = 34.5;
            }
            if (supLocal4 < 34.5) {
                supLocal4 = 34.5;
            }

            potLocal1 = Double.parseDouble(NumLocal1.getText()) * supLocal1 * 100;
            potLocal2 = Double.parseDouble(NumLocal2.getText()) * supLocal2 * 100;
            potLocal3 = Double.parseDouble(NumLocal3.getText()) * supLocal3 * 100;
            potLocal4 = Double.parseDouble(NumLocal4.getText()) * supLocal4 * 100;

            potLocalTotal = (potLocal1 + potLocal2 + potLocal3 + potLocal4) / 1000;
            TResP3.setText(Double.toString(potLocalTotal));
            return potLocalTotal;
        } catch (NumberFormatException | ConcurrentModificationException e) {
            System.err.println(e);
            JOptionPane.showMessageDialog(null, "Los datos introducidos han de ser númericos.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return 0.0;
        }
    }

    //Calcula la previsión de carga del garage
    public double calcularP4() {
        try {
            double potGaraVentNat, potGaraVenFor, potGarageTotal;

            potGaraVentNat = Double.parseDouble(SupGarVenNatural.getText()) * 10;
            if (potGaraVentNat >0 && potGaraVentNat<3450){
                potGaraVentNat =3450;
            }
            
            potGaraVenFor = Double.parseDouble(SupGarVenForzada.getText()) * 20;
            if ( potGaraVenFor >0 && potGaraVenFor<3450){
                potGaraVenFor =3450;
            }
            
            if (potGaraVentNat != 0 || potGaraVenFor != 0) {
                potGarageTotal = (potGaraVentNat + potGaraVenFor) / 1000;
            } else {
                potGarageTotal = 0.0;
            }
            TResP4.setText(Double.toString(potGarageTotal));
            return potGarageTotal;
        } catch (NumberFormatException | ConcurrentModificationException e) {
            System.err.println(e);
            JOptionPane.showMessageDialog(null, "Los datos introducidos han de ser númericos.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return 0.0;
        }
    }

    //Calcula la previsión de carga del garage
    public double calcularP5() {

        try {
            double resP5, numVivIrve, potPC;
            numVivIrve = Double.parseDouble(VivBasConIrve.getText());
            potPC = Double.parseDouble(PotPCVivBasConIrve.getText());
            resP5 = numVivIrve * potPC;
            //Comprobamos si dispone de SPL el edificio.
            if (CheckBoxSPL.isSelected()) {
                resP5 *= 0.3;
            }
            System.out.println("El resultado para P5 es : " + resP5);
            TResP5.setText(Double.toString(resP5));
            return resP5;
        } catch (NumberFormatException | ConcurrentModificationException e) {
            System.err.println(e);
            JOptionPane.showMessageDialog(null, "Los datos introducidos han de ser númericos.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return 0.0;
        }

    }

    //Devuelve el valor de la potencia del ascensor de la lista del combo
    public String potenciaAscensor(String tipoMotor) {
        switch (tipoMotor) {
            case "ITA-1":

                return "4.5";
            case "ITA-2":
                return "7.5";
            case "ITA-3":
                return "11.5";
            case "ITA-4":
                return "18.5";
            case "ITA-5":
                return "29.5";
            case "ITA-6":
                return "46";
            default:

                System.out.println("Potencia ascensor no registrada para ese tipo de motor");
                return "";
        }

    }

    //Activa o desactiva las casillas opcionales del ascensor1
    private void activacionCasillasAscensor1(boolean b) {
        PotAsc1kW.setEditable(b);
        PotAsc1kW.setEnabled(b);
        ComboBoxkWAsc1.setEnabled(b);
        ComboBoxkWAsc1.setEditable(b);
    }
    //Activa o desactiva las casillas opcionales del ascensor1

    private void activacionCasillasAscensor2(boolean b) {
        PotAsc2kW.setEditable(b);
        PotAsc2kW.setEnabled(b);
        ComboBoxkWAsc2.setEnabled(b);
        ComboBoxkWAsc2.setEditable(b);
    }
    //Calcula la conductividad del material segun la tabla de mat y temp.
    private void calculaConductividad() {
        int mat = CBoxMaterialSec.getSelectedIndex();
        int temp = CBoxTempMAtSec.getSelectedIndex();
        int valor = tablaMat[mat][temp];
        conductividad.setText(String.valueOf(valor));
    }

    //Calcula el voltage de la caida de tensión
     private void calculaVoltageCaida (){
         double tension = Double.parseDouble(TensionSec.getText());
         double porcen = Double.parseDouble(caidaTensionSec.getText());
         double vcaida = tension * porcen*0.01;
         VCaidaTension.setText(Double.toString(vcaida));
         
         
     }
     //Calcula intensidad maxima
     private void  calculaImax(){
         
         String tipoLinea = CboxTipoLinea.getSelectedItem().toString();
         double intensidad, potencia, voltage, factPotSec;
                 potencia = Double.parseDouble(potenciaSec.getText());
                 voltage= Double.parseDouble(TensionSec.getText());
                 factPotSec = Double.parseDouble(factorPotSec.getText());
         switch (tipoLinea) {
             case "Continua":
                 intensidad = potencia/voltage;
                 
                 break;
                 
             case "Monofasica":
                 intensidad = potencia/(voltage*factPotSec);
                 
                 break;
             case "Trifasica":
                 intensidad = potencia/(Math.sqrt(3)*voltage*factPotSec);
                 System.out.println(potencia+"Inte = "+ intensidad + " voltage : "+voltage + " fac "+factPotSec );
                 break;
             default:
                 System.out.println("Tipo de linea desconocida.");
                 intensidad=0.0;
                 break;
         }
         intensidadSec.setText(Double.toString(intensidad));
         System.out.println("Intensidad:"+intensidad);
         
         
     }
      
     // Calcula la potencia
     
     private double calculaPotencia (){
         double  tensionlin, inten, fact,  potencia;
         inten = Double.parseDouble(intensidadSec.getText());
         fact = Double.parseDouble(factorPotSec.getText());
         tensionlin = Double.parseDouble(TensionSec.getText());
         
         potencia = tensionlin * inten * fact;
         
         if (CboxTipoLinea.getSelectedItem().toString().equals("Trifasica")){
             potencia*=Math.sqrt(3);
         }
         potenciaSec.setText(Double.toString(potencia));
         CBoxPotkW.setSelectedItem("W");
         System.out.println("Potencia : "+potencia);
         return potencia ;
         
     }
     //calcula la seccion 
     private void calculaSeccion(){
         
         try {
         double secc, longi, inten, fact, conduc, caida, potencia, tensionlin;
         longi = Double.parseDouble(longSec.getText());
         inten = Double.parseDouble(intensidadSec.getText());
         fact = Double.parseDouble(factorPotSec.getText());
         conduc = Double.parseDouble(conductividad.getText());
         caida = Double.parseDouble(VCaidaTension.getText());
         potencia = Double.parseDouble(potenciaSec.getText());
         tensionlin = Double.parseDouble(TensionSec.getText());
         
        
         
         switch (CboxTipoLinea.getSelectedItem().toString()){
             case "Monofasica":
                 
                  if (potencia==0){
                      secc= (2*longi*inten*fact)/(caida*conduc);
                  } else {
                      secc= (2*longi*potencia)/(caida*tensionlin*conduc);
                  }
                  tseccion.setText(Double.toString(secc));
                  break;
             case "Trifasica":
                 
                  if (potencia==0){
                      secc= (Math.sqrt(3)*longi*inten*fact)/(caida*conduc);
                  } else {
                      secc= (longi*potencia)/(caida*tensionlin*conduc);
                  }
                  tseccion.setText(Double.toString(secc));
                  break;
             default:
                 System.out.println("No está seleccionado el tipo de linea correcto");
                  
                  
         }
         
         } catch (java.lang.NumberFormatException er){
             System.out.println( er.getMessage());
         }
                 
     }
     
     //Calcula la longitud
     private void calculaLongitud(){
         double secc, longi, inten, fact, conduc, caida, potencia, tensionlin;
         secc = Double.parseDouble(tseccion.getText());
         inten = Double.parseDouble(intensidadSec.getText());
         fact = Double.parseDouble(factorPotSec.getText());
         conduc = Double.parseDouble(conductividad.getText());
         caida = Double.parseDouble(VCaidaTension.getText());
         potencia = Double.parseDouble(potenciaSec.getText());
         tensionlin = Double.parseDouble(TensionSec.getText());
         
         
         switch (CboxTipoLinea.getSelectedItem().toString()){
             case "Monofasica":
                 
                  if (potencia==0){
                      longi= (secc* caida*conduc) / (2*inten*fact);
                  } else {
                      longi =(secc *caida*tensionlin*conduc) /(2*potencia);
                  }
                  longSec.setText(Double.toString(longi));
                  System.out.println(" Longitud : "+Double.toString(longi));
                  break;
             case "Trifasica":
                 
                  if (potencia==0){
                      longi= (secc*caida*conduc)/(Math.sqrt(3)*inten*fact);
                  } else {
                      longi= (caida*tensionlin*conduc)/ (secc*potencia);
                  }
                  longSec.setText(Double.toString(longi));
                  System.out.println(" Longitud : "+Double.toString(longi));
                  break;
             default:
                 System.out.println("No está seleccionado el tipo de linea correcto");
                  
                  
         }
         
     }
     
    
            
    /* This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new JScrollPane();
        PanelPCVE = new JPanel();
        PViviendas = new JPanel();
        LabelConsumoAlto = new JLabel();
        NumVivConsuAlto = new JTextField();
        LabelConsumoBasico = new JLabel();
        NumVivConsuBasico = new JTextField();
        LabelVivConIrve = new JLabel();
        VivBasConIrve = new JTextField();
        LabelSuperficieCAlto = new JLabel();
        TextVivConsuAlto4 = new JTextField();
        LabelSuperficieCBasico = new JLabel();
        jLabelM2Viviendas2 = new JTextField();
        LabelSPL = new JLabel();
        CheckBoxSPL = new JCheckBox();
        LabelSPL1 = new JLabel();
        ComboBoxEsquema = new JComboBox<>();
        jLabelM2Viviendas1 = new JLabel();
        jLabelM2Local6 = new JLabel();
        LabelVivConIrve1 = new JLabel();
        PotPCVivBasConIrve = new JTextField();
        LabelkW9 = new JLabel();
        LabelVivCAltoConIrve = new JLabel();
        VivAltoCIrve = new JTextField();
        LabelVivConIrve3 = new JLabel();
        PotPCVivCAltoConIrve = new JTextField();
        LabelkW10 = new JLabel();
        LabelConsumoAlto1 = new JLabel();
        NumVivConsuContratado = new JTextField();
        LabelVivCContratConIrve1 = new JLabel();
        VivContratadoconIrve = new JTextField();
        LabelVivConIrve4 = new JLabel();
        PotPCVivContConIrve = new JTextField();
        LabelkW13 = new JLabel();
        LabelSuperficieCBasico1 = new JLabel();
        jLabelM2Viviendas3 = new JTextField();
        jLabelM2Local7 = new JLabel();
        LabelVivConIrve5 = new JLabel();
        PotContradaVivCont = new JTextField();
        LabelkW14 = new JLabel();
        PAscensor2 = new JPanel();
        LabelAscensor2 = new JLabel();
        LabelAsceNum3 = new JLabel();
        NumAsc2 = new JTextField();
        LabelAsceNum4 = new JLabel();
        ComboPotAsc2 = new JComboBox<>();
        PotAsc2kW = new JTextField();
        ComboBoxkWAsc2 = new JComboBox<>();
        PAscensor1 = new JPanel();
        LabelAscensor1 = new JLabel();
        LabelAsceNum1 = new JLabel();
        NumAsc1 = new JTextField();
        LabelAsceNum2 = new JLabel();
        ComboPotAsc1 = new JComboBox<>();
        PotAsc1kW = new JTextField();
        ComboBoxkWAsc1 = new JComboBox<>();
        PGMotor = new JPanel();
        LabelAscensor3 = new JLabel();
        LabelAsceNum5 = new JLabel();
        NumGPresion = new JTextField();
        LabelAsceNum6 = new JLabel();
        PotGPresion = new JTextField();
        LabelAscensor4 = new JLabel();
        LabelAsceNum7 = new JLabel();
        NumDepuradora = new JTextField();
        LabelAsceNum8 = new JLabel();
        PotDepuradora = new JTextField();
        LabelAscensor5 = new JLabel();
        LabelAsceNum9 = new JLabel();
        NumOtrosMotores = new JTextField();
        LabelAsceNum10 = new JLabel();
        PotOtrosMotores = new JTextField();
        LabelAscensor6 = new JLabel();
        LabelAsceNum11 = new JLabel();
        NumOtros = new JTextField();
        LabelAsceNum12 = new JLabel();
        PotOtrosMotores2 = new JTextField();
        CBoxDepukW = new JComboBox<>();
        CBoxOTkW = new JComboBox<>();
        CBoxGPkW = new JComboBox<>();
        CBoxOT2kW = new JComboBox<>();
        PAlumbrado = new JPanel();
        LabelAlumbrado1 = new JLabel();
        PotAlFLour = new JTextField();
        LabelkW7 = new JLabel();
        LabelAlumbrado2 = new JLabel();
        PotAlInca = new JTextField();
        LabelkW8 = new JLabel();
        PLocales = new JPanel();
        LabelLocal1 = new JLabel();
        NumLocal1 = new JTextField();
        LabelCantidadLocal1 = new JLabel();
        LabelSupLocal1 = new JLabel();
        SuperLocal1 = new JTextField();
        jLabelM2Local1 = new JLabel();
        LabelLocal2 = new JLabel();
        LabelCantidadLocal2 = new JLabel();
        NumLocal2 = new JTextField();
        LabelSupLocal2 = new JLabel();
        SuperLocal2 = new JTextField();
        jLabelM2Local2 = new JLabel();
        LabelLocal3 = new JLabel();
        LabelCantidadLocal3 = new JLabel();
        NumLocal3 = new JTextField();
        LabelSupLocal3 = new JLabel();
        SuperLocal3 = new JTextField();
        jLabelM2Local3 = new JLabel();
        LabelCantidadLocal4 = new JLabel();
        NumLocal4 = new JTextField();
        LabelSupLocal4 = new JLabel();
        SuperLocal4 = new JTextField();
        jLabelM2Local4 = new JLabel();
        LabelLocal4 = new JLabel();
        PGarage1 = new JPanel();
        LabelGarage2 = new JLabel();
        SupGarVenNatural = new JTextField();
        LabelkW11 = new JLabel();
        LabelGarage1b1 = new JLabel();
        SupGarVenForzada = new JTextField();
        LabelkW12 = new JLabel();
        PResultados = new JPanel();
        LabelP1 = new JLabel();
        TResP1 = new JTextField();
        jButtonP1 = new JButton();
        LabelkWP1 = new JLabel();
        LabelP2 = new JLabel();
        TResP2 = new JTextField();
        LabelkWP2 = new JLabel();
        jButtonP2 = new JButton();
        LabelP3 = new JLabel();
        TResP3 = new JTextField();
        LabelkWP3 = new JLabel();
        jButtonP3 = new JButton();
        LabelP4 = new JLabel();
        TResP4 = new JTextField();
        jButtonP4 = new JButton();
        LabelkWP4 = new JLabel();
        LabelP5 = new JLabel();
        TResP5 = new JTextField();
        LabelkWP5 = new JLabel();
        jButtonP5 = new JButton();
        LabelP6 = new JLabel();
        TResP6 = new JTextField();
        LabelkWP6 = new JLabel();
        jButtonPrevision = new JButton();
        Titulo = new JLabel();
        jButtonReset = new JButton();
        PSeccion = new JPanel();
        jLabel1 = new JLabel();
        CBoxMaterialSec = new JComboBox<>();
        jLabel2 = new JLabel();
        longSec = new JTextField();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        intensidadSec = new JTextField();
        jLabel5 = new JLabel();
        jLabel6 = new JLabel();
        caidaTensionSec = new JTextField();
        CBoxTempMAtSec = new JComboBox<>();
        jLabel7 = new JLabel();
        jLabel8 = new JLabel();
        conductividad = new JTextField();
        jLabel9 = new JLabel();
        factorPotSec = new JTextField();
        jLabel10 = new JLabel();
        TensionSec = new JTextField();
        jLabel11 = new JLabel();
        potenciaSec = new JTextField();
        ButtonAyudaIntensidad = new JButton();
        CboxTipoLinea = new JComboBox<>();
        jLabel12 = new JLabel();
        jButtonCalcLongitud = new JButton();
        VCaidaTension = new JTextField();
        jLabel13 = new JLabel();
        jLabel14 = new JLabel();
        jButton2 = new JButton();
        jButtonIMax = new JButton();
        jLabel16 = new JLabel();
        tseccion = new JTextField();
        ButtonCalculaSec = new JButton();
        jButtonCalcPot = new JButton();
        CBoxPotkW = new JComboBox<>();
        jLabel15 = new JLabel();
        Titulo1 = new JLabel();
        jLabelAutor = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Prevision de carga Edificios con Vehiculos Eléctricos");
        setBackground(new Color(204, 255, 204));
        setBounds(new Rectangle(0, 0, 0, 0));
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        setFont(new Font("Malgun Gothic", 0, 12)); // NOI18N
        setSize(new Dimension(1293, 700));

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setPreferredSize(new Dimension(1293, 700));

        PanelPCVE.setBackground(new Color(204, 255, 204));
        PanelPCVE.setAutoscrolls(true);
        PanelPCVE.setFont(new Font("Malgun Gothic", 0, 12)); // NOI18N

        PViviendas.setBackground(new Color(204, 204, 255));
        PViviendas.setBorder(BorderFactory.createTitledBorder("Viviendas"));

        LabelConsumoAlto.setText("Numero de viviendas con consumo eléctrico alto: ");

        NumVivConsuAlto.setHorizontalAlignment(JTextField.RIGHT);
        NumVivConsuAlto.setText("0");
        NumVivConsuAlto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NumVivConsuAltoActionPerformed(evt);
            }
        });

        LabelConsumoBasico.setText("Numero de viviendas con consumo eléctrico básico: ");

        NumVivConsuBasico.setHorizontalAlignment(JTextField.RIGHT);
        NumVivConsuBasico.setText("0");
        NumVivConsuBasico.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NumVivConsuBasicoActionPerformed(evt);
            }
        });

        LabelVivConIrve.setText("con IRVE: ");

        VivBasConIrve.setHorizontalAlignment(JTextField.RIGHT);
        VivBasConIrve.setText("0");
        VivBasConIrve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                VivBasConIrveActionPerformed(evt);
            }
        });

        LabelSuperficieCAlto.setText("Superficie  :");

        TextVivConsuAlto4.setHorizontalAlignment(JTextField.RIGHT);
        TextVivConsuAlto4.setText("0");
        TextVivConsuAlto4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                TextVivConsuAlto4ActionPerformed(evt);
            }
        });

        LabelSuperficieCBasico.setText("Superficie  :");

        jLabelM2Viviendas2.setHorizontalAlignment(JTextField.RIGHT);
        jLabelM2Viviendas2.setText("0");
        jLabelM2Viviendas2.setToolTipText("");
        jLabelM2Viviendas2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jLabelM2Viviendas2ActionPerformed(evt);
            }
        });

        LabelSPL.setText("¿Tiene SPL ?");

        LabelSPL1.setText("Esquema :");

        ComboBoxEsquema.setModel(new DefaultComboBoxModel<>(new String[] { "1a", "1b", "1c", "2", "3a", "3b", "4a", "4b" }));
        ComboBoxEsquema.setName("CBoxEsquema"); // NOI18N
        ComboBoxEsquema.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ComboBoxEsquemaActionPerformed(evt);
            }
        });

        jLabelM2Viviendas1.setText("m2");

        jLabelM2Local6.setText("m2");

        LabelVivConIrve1.setText("Potencia PC:");

        PotPCVivBasConIrve.setHorizontalAlignment(JTextField.RIGHT);
        PotPCVivBasConIrve.setText("3.68");
        PotPCVivBasConIrve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                PotPCVivBasConIrveActionPerformed(evt);
            }
        });

        LabelkW9.setText("kW");

        LabelVivCAltoConIrve.setText("con IRVE: ");

        VivAltoCIrve.setHorizontalAlignment(JTextField.RIGHT);
        VivAltoCIrve.setText("0");
        VivAltoCIrve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                VivAltoCIrveActionPerformed(evt);
            }
        });

        LabelVivConIrve3.setText("Potencia PC:");

        PotPCVivCAltoConIrve.setHorizontalAlignment(JTextField.RIGHT);
        PotPCVivCAltoConIrve.setText("3.68");
        PotPCVivCAltoConIrve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                PotPCVivCAltoConIrveActionPerformed(evt);
            }
        });

        LabelkW10.setText("kW");

        LabelConsumoAlto1.setText("Numero de viviendas consu contratado :");

        NumVivConsuContratado.setHorizontalAlignment(JTextField.RIGHT);
        NumVivConsuContratado.setText("0");
        NumVivConsuContratado.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NumVivConsuContratadoActionPerformed(evt);
            }
        });

        LabelVivCContratConIrve1.setText("con IRVE: ");

        VivContratadoconIrve.setHorizontalAlignment(JTextField.RIGHT);
        VivContratadoconIrve.setText("0");
        VivContratadoconIrve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                VivContratadoconIrveActionPerformed(evt);
            }
        });

        LabelVivConIrve4.setText("Potencia PC:");

        PotPCVivContConIrve.setHorizontalAlignment(JTextField.RIGHT);
        PotPCVivContConIrve.setText("3.68");
        PotPCVivContConIrve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                PotPCVivContConIrveActionPerformed(evt);
            }
        });

        LabelkW13.setText("kW");

        LabelSuperficieCBasico1.setText("Superficie  :");

        jLabelM2Viviendas3.setHorizontalAlignment(JTextField.RIGHT);
        jLabelM2Viviendas3.setText("0");
        jLabelM2Viviendas3.setToolTipText("");
        jLabelM2Viviendas3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jLabelM2Viviendas3ActionPerformed(evt);
            }
        });

        jLabelM2Local7.setText("m2");

        LabelVivConIrve5.setText("Pot Contrada :");

        PotContradaVivCont.setHorizontalAlignment(JTextField.RIGHT);
        PotContradaVivCont.setText("0");
        PotContradaVivCont.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                PotContradaVivContActionPerformed(evt);
            }
        });

        LabelkW14.setText("kW");

        GroupLayout PViviendasLayout = new GroupLayout(PViviendas);
        PViviendas.setLayout(PViviendasLayout);
        PViviendasLayout.setHorizontalGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PViviendasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                        .addComponent(LabelConsumoBasico, GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                        .addComponent(LabelConsumoAlto, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(LabelConsumoAlto1, GroupLayout.PREFERRED_SIZE, 216, GroupLayout.PREFERRED_SIZE)
                    .addGroup(GroupLayout.Alignment.TRAILING, PViviendasLayout.createSequentialGroup()
                        .addComponent(LabelVivConIrve5, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)))
                .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(PViviendasLayout.createSequentialGroup()
                        .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(PViviendasLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addGroup(PViviendasLayout.createSequentialGroup()
                                        .addComponent(NumVivConsuBasico, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(LabelVivConIrve, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(PViviendasLayout.createSequentialGroup()
                                        .addComponent(NumVivConsuAlto, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(LabelVivCAltoConIrve, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE))))
                            .addGroup(PViviendasLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addComponent(NumVivConsuContratado, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(PotContradaVivCont, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(LabelkW14)
                                    .addComponent(LabelVivCContratConIrve1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(PViviendasLayout.createSequentialGroup()
                                    .addComponent(VivBasConIrve, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(LabelVivConIrve1, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(PotPCVivBasConIrve, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(LabelkW9))
                                .addGroup(PViviendasLayout.createSequentialGroup()
                                    .addComponent(VivAltoCIrve, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(LabelVivConIrve3, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(PotPCVivCAltoConIrve, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(LabelkW10)))
                            .addGroup(PViviendasLayout.createSequentialGroup()
                                .addComponent(VivContratadoconIrve, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(LabelVivConIrve4, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(PotPCVivContConIrve, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(LabelkW13)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(LabelSuperficieCBasico, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(LabelSuperficieCAlto, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
                            .addGroup(PViviendasLayout.createSequentialGroup()
                                .addComponent(LabelSuperficieCBasico1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(4, 4, 4)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(TextVivConsuAlto4, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelM2Viviendas2, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabelM2Viviendas3, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelM2Viviendas1, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelM2Local6, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelM2Local7, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(PViviendasLayout.createSequentialGroup()
                        .addComponent(LabelSPL, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CheckBoxSPL)
                        .addGap(37, 37, 37)
                        .addComponent(LabelSPL1, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBoxEsquema, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(192, 192, 192))))
        );
        PViviendasLayout.setVerticalGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PViviendasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelSuperficieCAlto)
                    .addComponent(TextVivConsuAlto4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelConsumoBasico)
                    .addComponent(NumVivConsuBasico, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelM2Viviendas1)
                    .addComponent(LabelVivConIrve)
                    .addComponent(VivBasConIrve, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelVivConIrve1)
                    .addComponent(PotPCVivBasConIrve, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelkW9))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelM2Viviendas2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelConsumoAlto)
                    .addComponent(NumVivConsuAlto, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelM2Local6)
                    .addComponent(LabelVivCAltoConIrve)
                    .addComponent(VivAltoCIrve, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelVivConIrve3)
                    .addComponent(PotPCVivCAltoConIrve, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelkW10)
                    .addComponent(LabelSuperficieCBasico))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelConsumoAlto1)
                    .addComponent(NumVivConsuContratado, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelVivCContratConIrve1)
                    .addComponent(VivContratadoconIrve, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelVivConIrve4)
                    .addComponent(PotPCVivContConIrve, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelkW13)
                    .addComponent(LabelSuperficieCBasico1)
                    .addComponent(jLabelM2Viviendas3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelM2Local7))
                .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(PViviendasLayout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(LabelkW14)
                            .addComponent(LabelVivConIrve5))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(GroupLayout.Alignment.TRAILING, PViviendasLayout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(PotContradaVivCont, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(CheckBoxSPL, GroupLayout.Alignment.TRAILING)
                        .addComponent(LabelSPL))
                    .addGroup(PViviendasLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(LabelSPL1)
                        .addComponent(ComboBoxEsquema, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
        );

        PAscensor2.setBackground(new Color(153, 204, 255));

        LabelAscensor2.setText("Ascesores Tipo 2:");

        LabelAsceNum3.setText("Cantidad:");

        NumAsc2.setText("0");
        NumAsc2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NumAsc2ActionPerformed(evt);
            }
        });

        LabelAsceNum4.setText("Potencia: ");

        ComboPotAsc2.setModel(new DefaultComboBoxModel<>(new String[] { "ITA-1", "ITA-2", "ITA-3", "ITA-4", "ITA-5", "ITA-6", " " }));
        ComboPotAsc2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ComboPotAsc2ActionPerformed(evt);
            }
        });

        PotAsc2kW.setEditable(false);
        PotAsc2kW.setHorizontalAlignment(JTextField.RIGHT);
        PotAsc2kW.setText("4.5");
        PotAsc2kW.setToolTipText("");
        PotAsc2kW.setBorder(null);
        PotAsc2kW.setDisabledTextColor(new Color(51, 51, 51));
        PotAsc2kW.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                PotAsc2kWActionPerformed(evt);
            }
        });

        ComboBoxkWAsc2.setFont(new Font("Malgun Gothic", 0, 12)); // NOI18N
        ComboBoxkWAsc2.setModel(new DefaultComboBoxModel<>(new String[] { "kW", "CV" }));
        ComboBoxkWAsc2.setEnabled(false);

        GroupLayout PAscensor2Layout = new GroupLayout(PAscensor2);
        PAscensor2.setLayout(PAscensor2Layout);
        PAscensor2Layout.setHorizontalGroup(PAscensor2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PAscensor2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PAscensor2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(LabelAscensor2, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
                    .addGroup(PAscensor2Layout.createSequentialGroup()
                        .addComponent(LabelAsceNum3, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(NumAsc2, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE))
                    .addGroup(PAscensor2Layout.createSequentialGroup()
                        .addComponent(LabelAsceNum4)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ComboPotAsc2, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(PotAsc2kW, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBoxkWAsc2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PAscensor2Layout.setVerticalGroup(PAscensor2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PAscensor2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LabelAscensor2)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PAscensor2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelAsceNum3)
                    .addComponent(NumAsc2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PAscensor2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(LabelAsceNum4)
                    .addGroup(PAscensor2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(ComboPotAsc2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(PotAsc2kW, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBoxkWAsc2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PAscensor1.setBackground(new Color(153, 204, 255));

        LabelAscensor1.setText("Ascesores Tipo 1:");

        LabelAsceNum1.setText("Cantidad:");

        NumAsc1.setHorizontalAlignment(JTextField.RIGHT);
        NumAsc1.setText("0");
        NumAsc1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NumAsc1ActionPerformed(evt);
            }
        });

        LabelAsceNum2.setText("Potencia: ");

        ComboPotAsc1.setModel(new DefaultComboBoxModel<>(new String[] { "ITA-1", "ITA-2", "ITA-3", "ITA-4", "ITA-5", "ITA-6", " " }));
        ComboPotAsc1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ComboPotAsc1ActionPerformed(evt);
            }
        });

        PotAsc1kW.setEditable(false);
        PotAsc1kW.setHorizontalAlignment(JTextField.RIGHT);
        PotAsc1kW.setText("4.5");
        PotAsc1kW.setToolTipText("");
        PotAsc1kW.setBorder(null);
        PotAsc1kW.setDisabledTextColor(new Color(51, 51, 51));
        PotAsc1kW.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                PotAsc1kWActionPerformed(evt);
            }
        });

        ComboBoxkWAsc1.setFont(new Font("Malgun Gothic", 0, 12)); // NOI18N
        ComboBoxkWAsc1.setModel(new DefaultComboBoxModel<>(new String[] { "kW", "CV" }));
        ComboBoxkWAsc1.setEnabled(false);

        GroupLayout PAscensor1Layout = new GroupLayout(PAscensor1);
        PAscensor1.setLayout(PAscensor1Layout);
        PAscensor1Layout.setHorizontalGroup(PAscensor1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PAscensor1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PAscensor1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(LabelAscensor1, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
                    .addGroup(PAscensor1Layout.createSequentialGroup()
                        .addComponent(LabelAsceNum1, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(NumAsc1, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE))
                    .addGroup(PAscensor1Layout.createSequentialGroup()
                        .addComponent(LabelAsceNum2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ComboPotAsc1, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(PotAsc1kW, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBoxkWAsc1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PAscensor1Layout.setVerticalGroup(PAscensor1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PAscensor1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LabelAscensor1)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PAscensor1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelAsceNum1)
                    .addComponent(NumAsc1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PAscensor1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(LabelAsceNum2)
                    .addGroup(PAscensor1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(ComboPotAsc1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(PotAsc1kW, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBoxkWAsc1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PGMotor.setBackground(new Color(153, 204, 255));

        LabelAscensor3.setText("Grupo Presión : ");

        LabelAsceNum5.setText("Cantidad:");

        NumGPresion.setHorizontalAlignment(JTextField.RIGHT);
        NumGPresion.setText("0");
        NumGPresion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NumGPresionActionPerformed(evt);
            }
        });

        LabelAsceNum6.setText("Potencia: ");

        PotGPresion.setHorizontalAlignment(JTextField.RIGHT);
        PotGPresion.setText("0");
        PotGPresion.setToolTipText("");
        PotGPresion.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        PotGPresion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                PotGPresionActionPerformed(evt);
            }
        });

        LabelAscensor4.setText("Depuradora: ");

        LabelAsceNum7.setText("Cantidad:");

        NumDepuradora.setHorizontalAlignment(JTextField.RIGHT);
        NumDepuradora.setText("0");
        NumDepuradora.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NumDepuradoraActionPerformed(evt);
            }
        });

        LabelAsceNum8.setText("Potencia: ");

        PotDepuradora.setHorizontalAlignment(JTextField.RIGHT);
        PotDepuradora.setText("0");
        PotDepuradora.setToolTipText("");
        PotDepuradora.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        PotDepuradora.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                PotDepuradoraActionPerformed(evt);
            }
        });

        LabelAscensor5.setText("Otros motores :");

        LabelAsceNum9.setText("Cantidad:");

        NumOtrosMotores.setHorizontalAlignment(JTextField.RIGHT);
        NumOtrosMotores.setText("0");
        NumOtrosMotores.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NumOtrosMotoresActionPerformed(evt);
            }
        });

        LabelAsceNum10.setText("Potencia: ");

        PotOtrosMotores.setHorizontalAlignment(JTextField.RIGHT);
        PotOtrosMotores.setText("0");
        PotOtrosMotores.setToolTipText("");
        PotOtrosMotores.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        PotOtrosMotores.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                PotOtrosMotoresActionPerformed(evt);
            }
        });

        LabelAscensor6.setText("Otros : ");

        LabelAsceNum11.setText("Cantidad:");

        NumOtros.setHorizontalAlignment(JTextField.RIGHT);
        NumOtros.setText("0");
        NumOtros.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NumOtrosActionPerformed(evt);
            }
        });

        LabelAsceNum12.setText("Potencia: ");

        PotOtrosMotores2.setHorizontalAlignment(JTextField.RIGHT);
        PotOtrosMotores2.setText("0");
        PotOtrosMotores2.setToolTipText("");
        PotOtrosMotores2.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        PotOtrosMotores2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                PotOtrosMotores2ActionPerformed(evt);
            }
        });

        CBoxDepukW.setFont(new Font("Malgun Gothic", 0, 12)); // NOI18N
        CBoxDepukW.setModel(new DefaultComboBoxModel<>(new String[] { "kW", "CV" }));

        CBoxOTkW.setFont(new Font("Malgun Gothic", 0, 12)); // NOI18N
        CBoxOTkW.setModel(new DefaultComboBoxModel<>(new String[] { "kW", "CV" }));
        CBoxOTkW.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                CBoxOTkWActionPerformed(evt);
            }
        });

        CBoxGPkW.setFont(new Font("Malgun Gothic", 0, 12)); // NOI18N
        CBoxGPkW.setModel(new DefaultComboBoxModel<>(new String[] { "kW", "CV" }));
        CBoxGPkW.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                CBoxGPkWActionPerformed(evt);
            }
        });

        CBoxOT2kW.setFont(new Font("Malgun Gothic", 0, 12)); // NOI18N
        CBoxOT2kW.setModel(new DefaultComboBoxModel<>(new String[] { "kW", "CV" }));
        CBoxOT2kW.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                CBoxOT2kWActionPerformed(evt);
            }
        });

        GroupLayout PGMotorLayout = new GroupLayout(PGMotor);
        PGMotor.setLayout(PGMotorLayout);
        PGMotorLayout.setHorizontalGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PGMotorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(PGMotorLayout.createSequentialGroup()
                        .addComponent(LabelAscensor3, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(LabelAsceNum6)
                            .addComponent(LabelAsceNum5, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PGMotorLayout.createSequentialGroup()
                        .addComponent(LabelAscensor4, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(LabelAsceNum8)
                            .addComponent(LabelAsceNum7, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PGMotorLayout.createSequentialGroup()
                        .addComponent(LabelAscensor5, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(LabelAsceNum10)
                            .addComponent(LabelAsceNum9, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PGMotorLayout.createSequentialGroup()
                        .addComponent(LabelAscensor6, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(LabelAsceNum12)
                            .addComponent(LabelAsceNum11, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(PGMotorLayout.createSequentialGroup()
                        .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(GroupLayout.Alignment.TRAILING, PGMotorLayout.createSequentialGroup()
                                .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addComponent(PotOtrosMotores2, GroupLayout.Alignment.LEADING)
                                    .addComponent(PotOtrosMotores)
                                    .addComponent(PotDepuradora))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(CBoxDepukW, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CBoxOTkW, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CBoxOT2kW, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(6, 6, 6))
                            .addGroup(PGMotorLayout.createSequentialGroup()
                                .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(NumDepuradora, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                                    .addGroup(PGMotorLayout.createSequentialGroup()
                                        .addComponent(PotGPresion, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(CBoxGPkW, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 6, Short.MAX_VALUE)))
                        .addGap(19, 19, 19))
                    .addGroup(PGMotorLayout.createSequentialGroup()
                        .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(NumGPresion, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                            .addComponent(NumOtrosMotores, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                            .addComponent(NumOtros, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        PGMotorLayout.setVerticalGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PGMotorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelAscensor3)
                    .addComponent(LabelAsceNum5)
                    .addComponent(NumGPresion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelAsceNum6)
                    .addComponent(PotGPresion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(CBoxGPkW, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelAscensor4)
                    .addComponent(LabelAsceNum7)
                    .addComponent(NumDepuradora, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelAsceNum8)
                    .addComponent(PotDepuradora, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(CBoxDepukW, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelAscensor5)
                    .addComponent(LabelAsceNum9)
                    .addComponent(NumOtrosMotores, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelAsceNum10)
                    .addComponent(PotOtrosMotores, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(CBoxOTkW, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelAscensor6)
                    .addComponent(LabelAsceNum11)
                    .addComponent(NumOtros, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PGMotorLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelAsceNum12)
                    .addComponent(PotOtrosMotores2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(CBoxOT2kW, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        PAlumbrado.setBackground(new Color(153, 204, 255));

        LabelAlumbrado1.setText("Alumbrado Fluorescente :");

        PotAlFLour.setHorizontalAlignment(JTextField.RIGHT);
        PotAlFLour.setText("0");
        PotAlFLour.setToolTipText("");
        PotAlFLour.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        PotAlFLour.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                PotAlFLourActionPerformed(evt);
            }
        });

        LabelkW7.setText("kW");

        LabelAlumbrado2.setText("Alumbrado Incadescente : ");

        PotAlInca.setHorizontalAlignment(JTextField.RIGHT);
        PotAlInca.setText("0");
        PotAlInca.setToolTipText("");
        PotAlInca.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        PotAlInca.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                PotAlIncaActionPerformed(evt);
            }
        });

        LabelkW8.setText("kW");

        GroupLayout PAlumbradoLayout = new GroupLayout(PAlumbrado);
        PAlumbrado.setLayout(PAlumbradoLayout);
        PAlumbradoLayout.setHorizontalGroup(PAlumbradoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PAlumbradoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PAlumbradoLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(LabelAlumbrado2, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                    .addComponent(LabelAlumbrado1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PAlumbradoLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(PotAlFLour, GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                    .addComponent(PotAlInca))
                .addGap(12, 12, 12)
                .addGroup(PAlumbradoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(LabelkW7, GroupLayout.Alignment.TRAILING)
                    .addComponent(LabelkW8, GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        PAlumbradoLayout.setVerticalGroup(PAlumbradoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PAlumbradoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PAlumbradoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelAlumbrado1)
                    .addComponent(PotAlFLour, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelkW7))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PAlumbradoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelAlumbrado2)
                    .addComponent(PotAlInca, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelkW8))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PLocales.setBackground(new Color(153, 153, 255));
        PLocales.setBorder(BorderFactory.createTitledBorder("Locales"));

        LabelLocal1.setText("Local1: ");

        NumLocal1.setHorizontalAlignment(JTextField.RIGHT);
        NumLocal1.setText("0");
        NumLocal1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NumLocal1ActionPerformed(evt);
            }
        });

        LabelCantidadLocal1.setText("Cantidad:");

        LabelSupLocal1.setText("Superficie:");

        SuperLocal1.setHorizontalAlignment(JTextField.RIGHT);
        SuperLocal1.setText("0");
        SuperLocal1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SuperLocal1ActionPerformed(evt);
            }
        });

        jLabelM2Local1.setText("m2");

        LabelLocal2.setText("Local2: ");

        LabelCantidadLocal2.setText("Cantidad:");

        NumLocal2.setHorizontalAlignment(JTextField.RIGHT);
        NumLocal2.setText("0");
        NumLocal2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NumLocal2ActionPerformed(evt);
            }
        });

        LabelSupLocal2.setText("Superficie:");

        SuperLocal2.setHorizontalAlignment(JTextField.RIGHT);
        SuperLocal2.setText("0");
        SuperLocal2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SuperLocal2ActionPerformed(evt);
            }
        });

        jLabelM2Local2.setText("m2");

        LabelLocal3.setText("Local3: ");

        LabelCantidadLocal3.setText("Cantidad:");

        NumLocal3.setHorizontalAlignment(JTextField.RIGHT);
        NumLocal3.setText("0");
        NumLocal3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NumLocal3ActionPerformed(evt);
            }
        });

        LabelSupLocal3.setText("Superficie:");

        SuperLocal3.setHorizontalAlignment(JTextField.RIGHT);
        SuperLocal3.setText("0");
        SuperLocal3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SuperLocal3ActionPerformed(evt);
            }
        });

        jLabelM2Local3.setText("m2");

        LabelCantidadLocal4.setText("Cantidad:");

        NumLocal4.setHorizontalAlignment(JTextField.RIGHT);
        NumLocal4.setText("0");
        NumLocal4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                NumLocal4ActionPerformed(evt);
            }
        });

        LabelSupLocal4.setText("Superficie:");

        SuperLocal4.setHorizontalAlignment(JTextField.RIGHT);
        SuperLocal4.setText("0");
        SuperLocal4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SuperLocal4ActionPerformed(evt);
            }
        });

        jLabelM2Local4.setText("m2");

        LabelLocal4.setText("Local4: ");

        GroupLayout PLocalesLayout = new GroupLayout(PLocales);
        PLocales.setLayout(PLocalesLayout);
        PLocalesLayout.setHorizontalGroup(PLocalesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PLocalesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PLocalesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(PLocalesLayout.createSequentialGroup()
                        .addComponent(LabelLocal1, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LabelCantidadLocal1, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(NumLocal1, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LabelSupLocal1, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(SuperLocal1, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelM2Local1, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                    .addGroup(PLocalesLayout.createSequentialGroup()
                        .addComponent(LabelLocal2, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LabelCantidadLocal2, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(NumLocal2, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LabelSupLocal2, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(SuperLocal2, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelM2Local2, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                    .addGroup(PLocalesLayout.createSequentialGroup()
                        .addComponent(LabelLocal3, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LabelCantidadLocal3, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(NumLocal3, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LabelSupLocal3, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(SuperLocal3, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelM2Local3, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                    .addGroup(PLocalesLayout.createSequentialGroup()
                        .addComponent(LabelLocal4, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LabelCantidadLocal4, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(NumLocal4, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LabelSupLocal4, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(SuperLocal4, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelM2Local4, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 66, Short.MAX_VALUE))
        );
        PLocalesLayout.setVerticalGroup(PLocalesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PLocalesLayout.createSequentialGroup()
                .addGroup(PLocalesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelLocal1)
                    .addComponent(LabelCantidadLocal1)
                    .addComponent(NumLocal1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelSupLocal1)
                    .addComponent(SuperLocal1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelM2Local1))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PLocalesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelLocal2)
                    .addComponent(LabelCantidadLocal2)
                    .addComponent(NumLocal2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelSupLocal2)
                    .addComponent(SuperLocal2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelM2Local2))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PLocalesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelLocal3)
                    .addComponent(LabelCantidadLocal3)
                    .addComponent(NumLocal3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelSupLocal3)
                    .addComponent(SuperLocal3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelM2Local3))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PLocalesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelCantidadLocal4)
                    .addComponent(NumLocal4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelSupLocal4)
                    .addComponent(SuperLocal4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelM2Local4)
                    .addComponent(LabelLocal4))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PGarage1.setBackground(new Color(255, 204, 204));
        PGarage1.setBorder(BorderFactory.createTitledBorder("Garage"));

        LabelGarage2.setText(" Garage : Superf ventil natural  :");

        SupGarVenNatural.setHorizontalAlignment(JTextField.RIGHT);
        SupGarVenNatural.setText("0");
        SupGarVenNatural.setToolTipText("");
        SupGarVenNatural.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        SupGarVenNatural.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SupGarVenNaturalActionPerformed(evt);
            }
        });

        LabelkW11.setText("m2");

        LabelGarage1b1.setText("Superficie vent Forzada :");

        SupGarVenForzada.setHorizontalAlignment(JTextField.RIGHT);
        SupGarVenForzada.setText("0");
        SupGarVenForzada.setToolTipText("");
        SupGarVenForzada.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        SupGarVenForzada.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SupGarVenForzadaActionPerformed(evt);
            }
        });

        LabelkW12.setText("m2");

        GroupLayout PGarage1Layout = new GroupLayout(PGarage1);
        PGarage1.setLayout(PGarage1Layout);
        PGarage1Layout.setHorizontalGroup(PGarage1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PGarage1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PGarage1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(PGarage1Layout.createSequentialGroup()
                        .addComponent(LabelGarage2)
                        .addGap(22, 22, 22))
                    .addGroup(GroupLayout.Alignment.TRAILING, PGarage1Layout.createSequentialGroup()
                        .addComponent(LabelGarage1b1, GroupLayout.PREFERRED_SIZE, 164, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(PGarage1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(PGarage1Layout.createSequentialGroup()
                        .addComponent(SupGarVenForzada, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(LabelkW12))
                    .addGroup(PGarage1Layout.createSequentialGroup()
                        .addComponent(SupGarVenNatural, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(LabelkW11)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PGarage1Layout.setVerticalGroup(PGarage1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PGarage1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PGarage1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelGarage2)
                    .addComponent(SupGarVenNatural, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelkW11))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PGarage1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelGarage1b1)
                    .addComponent(SupGarVenForzada, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelkW12))
                .addContainerGap(56, Short.MAX_VALUE))
        );

        PResultados.setBackground(new Color(255, 153, 51));
        PResultados.setBorder(new SoftBevelBorder(BevelBorder.RAISED));

        LabelP1.setText("P1 Viviendas :");

        TResP1.setEditable(false);
        TResP1.setHorizontalAlignment(JTextField.RIGHT);
        TResP1.setText("0");
        TResP1.setBorder(null);

        jButtonP1.setText("Calcular P1");
        jButtonP1.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        jButtonP1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonP1ActionPerformed(evt);
            }
        });

        LabelkWP1.setText("kW");

        LabelP2.setText("P2 Servicios Generales :");

        TResP2.setEditable(false);
        TResP2.setHorizontalAlignment(JTextField.RIGHT);
        TResP2.setText("0");
        TResP2.setBorder(null);

        LabelkWP2.setText("kW");

        jButtonP2.setText("Calcular P2");
        jButtonP2.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        jButtonP2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonP2ActionPerformed(evt);
            }
        });

        LabelP3.setText("P3 Locales :");

        TResP3.setEditable(false);
        TResP3.setHorizontalAlignment(JTextField.RIGHT);
        TResP3.setText("0");
        TResP3.setBorder(null);

        LabelkWP3.setText("kW");

        jButtonP3.setText("Calcular P3");
        jButtonP3.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        jButtonP3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonP3ActionPerformed(evt);
            }
        });

        LabelP4.setText("P4 Garage :");

        TResP4.setEditable(false);
        TResP4.setHorizontalAlignment(JTextField.RIGHT);
        TResP4.setText("0");
        TResP4.setBorder(null);

        jButtonP4.setText("Calcular P4");
        jButtonP4.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        jButtonP4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonP4ActionPerformed(evt);
            }
        });

        LabelkWP4.setText("kW");

        LabelP5.setText("P5 IRVE :");

        TResP5.setEditable(false);
        TResP5.setHorizontalAlignment(JTextField.RIGHT);
        TResP5.setText("0");
        TResP5.setBorder(null);

        LabelkWP5.setText("kW");

        jButtonP5.setText("Calcular P5");
        jButtonP5.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        jButtonP5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonP5ActionPerformed(evt);
            }
        });

        LabelP6.setText("Previsión de carga");

        TResP6.setEditable(false);
        TResP6.setHorizontalAlignment(JTextField.RIGHT);
        TResP6.setText("0");
        TResP6.setBorder(null);

        LabelkWP6.setText("kW");

        jButtonPrevision.setBackground(new Color(51, 255, 51));
        jButtonPrevision.setText("Calcular PC");
        jButtonPrevision.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        jButtonPrevision.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonPrevisionActionPerformed(evt);
            }
        });

        GroupLayout PResultadosLayout = new GroupLayout(PResultados);
        PResultados.setLayout(PResultadosLayout);
        PResultadosLayout.setHorizontalGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(PResultadosLayout.createSequentialGroup()
                        .addGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                            .addComponent(LabelP1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(LabelP2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(LabelP3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(TResP1, GroupLayout.Alignment.TRAILING)
                            .addComponent(TResP2, GroupLayout.Alignment.TRAILING)
                            .addComponent(TResP3, GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(GroupLayout.Alignment.TRAILING, PResultadosLayout.createSequentialGroup()
                                .addGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(LabelkWP1)
                                    .addComponent(LabelkWP2))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(jButtonP1)
                                    .addComponent(jButtonP2)))
                            .addGroup(PResultadosLayout.createSequentialGroup()
                                .addComponent(LabelkWP3)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonP3))))
                    .addGroup(PResultadosLayout.createSequentialGroup()
                        .addComponent(LabelP6, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                        .addComponent(TResP6, GroupLayout.PREFERRED_SIZE, 122, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(LabelkWP6)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonPrevision))
                    .addGroup(GroupLayout.Alignment.TRAILING, PResultadosLayout.createSequentialGroup()
                        .addGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(PResultadosLayout.createSequentialGroup()
                                .addComponent(LabelP5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18))
                            .addGroup(PResultadosLayout.createSequentialGroup()
                                .addComponent(LabelP4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(25, 25, 25)))
                        .addGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                            .addComponent(TResP4, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                            .addComponent(TResP5))
                        .addGap(18, 18, 18)
                        .addGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(PResultadosLayout.createSequentialGroup()
                                .addComponent(LabelkWP4)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonP4))
                            .addGroup(PResultadosLayout.createSequentialGroup()
                                .addComponent(LabelkWP5)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonP5)))))
                .addContainerGap())
        );
        PResultadosLayout.setVerticalGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelP1)
                    .addComponent(TResP1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonP1)
                    .addComponent(LabelkWP1))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelP2)
                    .addComponent(TResP2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonP2)
                    .addComponent(LabelkWP2))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelP3)
                    .addComponent(TResP3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonP3)
                    .addComponent(LabelkWP3))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelP4)
                    .addComponent(TResP4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonP4)
                    .addComponent(LabelkWP4, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelP5)
                    .addComponent(TResP5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelkWP5, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonP5))
                .addGap(18, 18, 18)
                .addGroup(PResultadosLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelP6)
                    .addComponent(TResP6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelkWP6, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPrevision))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        Titulo.setFont(new Font("Malgun Gothic", 0, 14)); // NOI18N
        Titulo.setText("PREVISION DE CARGAS EDIFICIOS CON VE");

        jButtonReset.setBackground(new Color(255, 0, 51));
        jButtonReset.setText("Resetea valores");
        jButtonReset.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(255, 153, 0)));
        jButtonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonResetActionPerformed(evt);
            }
        });

        PSeccion.setBackground(new Color(204, 255, 51));
        PSeccion.setBorder(BorderFactory.createTitledBorder("Seccion"));

        jLabel1.setText("Material :");

        CBoxMaterialSec.setModel(new DefaultComboBoxModel<>(new String[] { "Cobre", "Aluminio" }));
        CBoxMaterialSec.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                CBoxMaterialSecActionPerformed(evt);
            }
        });

        jLabel2.setText("Longitud :");

        longSec.setHorizontalAlignment(JTextField.RIGHT);
        longSec.setText("0.0");
        longSec.setToolTipText("");

        jLabel3.setText("mts");

        jLabel4.setText("Intensidad :");

        intensidadSec.setHorizontalAlignment(JTextField.RIGHT);
        intensidadSec.setText("0.0");
        intensidadSec.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                intensidadSecActionPerformed(evt);
            }
        });

        jLabel5.setText("A");

        jLabel6.setText("% caida  tension :");

        caidaTensionSec.setHorizontalAlignment(JTextField.RIGHT);
        caidaTensionSec.setText("0.0");
        caidaTensionSec.addInputMethodListener(new InputMethodListener() {
            public void caretPositionChanged(InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(InputMethodEvent evt) {
                caidaTensionSecInputMethodTextChanged(evt);
            }
        });

        CBoxTempMAtSec.setModel(new DefaultComboBoxModel<>(new String[] { "20", "70", "90" }));
        CBoxTempMAtSec.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                CBoxTempMAtSecActionPerformed(evt);
            }
        });

        jLabel7.setText("Temp :");

        jLabel8.setText("Conduc :");

        conductividad.setEditable(false);
        conductividad.setHorizontalAlignment(JTextField.RIGHT);
        conductividad.setText("56");
        conductividad.setToolTipText("");

        jLabel9.setText("Factor potencia: ");

        factorPotSec.setHorizontalAlignment(JTextField.RIGHT);
        factorPotSec.setText("0.0");

        jLabel10.setText("Tension / Voltage : ");

        TensionSec.setHorizontalAlignment(JTextField.RIGHT);
        TensionSec.setText("0.0");
        TensionSec.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                TensionSecActionPerformed(evt);
            }
        });

        jLabel11.setText("Potencia :");

        potenciaSec.setHorizontalAlignment(JTextField.RIGHT);
        potenciaSec.setText("0.0");
        potenciaSec.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                potenciaSecActionPerformed(evt);
            }
        });

        ButtonAyudaIntensidad.setBackground(new Color(255, 0, 0));
        ButtonAyudaIntensidad.setForeground(new Color(51, 51, 51));
        ButtonAyudaIntensidad.setText("?");
        ButtonAyudaIntensidad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ButtonAyudaIntensidadActionPerformed(evt);
            }
        });

        CboxTipoLinea.setModel(new DefaultComboBoxModel<>(new String[] { "Continua", "Monofasica", "Trifasica" }));

        jLabel12.setText("Tipo :");

        jButtonCalcLongitud.setBackground(new Color(102, 255, 204));
        jButtonCalcLongitud.setFont(new Font("Segoe UI", 0, 10)); // NOI18N
        jButtonCalcLongitud.setText("Calcula Longitud");
        jButtonCalcLongitud.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonCalcLongitudActionPerformed(evt);
            }
        });

        VCaidaTension.setEditable(false);
        VCaidaTension.setText("0.0");

        jLabel13.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel13.setText("V");

        jLabel14.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel14.setText("V");

        jButton2.setBackground(new Color(102, 255, 204));
        jButton2.setFont(new Font("Segoe UI", 0, 10)); // NOI18N
        jButton2.setText("Calcula Caida");
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButtonIMax.setBackground(new Color(102, 255, 204));
        jButtonIMax.setText("Calcula Imax");
        jButtonIMax.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonIMaxActionPerformed(evt);
            }
        });

        jLabel16.setText("Seccion :");

        tseccion.setHorizontalAlignment(JTextField.RIGHT);
        tseccion.setText("0.0");
        tseccion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                tseccionActionPerformed(evt);
            }
        });

        ButtonCalculaSec.setBackground(new Color(0, 153, 153));
        ButtonCalculaSec.setText("Calcula SEC");
        ButtonCalculaSec.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ButtonCalculaSecActionPerformed(evt);
            }
        });

        jButtonCalcPot.setBackground(new Color(102, 255, 204));
        jButtonCalcPot.setFont(new Font("Segoe UI", 0, 10)); // NOI18N
        jButtonCalcPot.setText("Calcula Potencia");
        jButtonCalcPot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonCalcPotActionPerformed(evt);
            }
        });

        CBoxPotkW.setFont(new Font("Malgun Gothic", 0, 12)); // NOI18N
        CBoxPotkW.setModel(new DefaultComboBoxModel<>(new String[] { "W", "kW", " " }));
        CBoxPotkW.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                CBoxPotkWActionPerformed(evt);
            }
        });

        jLabel15.setText("mm2");

        GroupLayout PSeccionLayout = new GroupLayout(PSeccion);
        PSeccion.setLayout(PSeccionLayout);
        PSeccionLayout.setHorizontalGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PSeccionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(PSeccionLayout.createSequentialGroup()
                        .addComponent(jLabel10, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(TensionSec, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
                            .addComponent(CboxTipoLinea, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
                            .addGroup(PSeccionLayout.createSequentialGroup()
                                .addComponent(intensidadSec, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5))
                            .addGroup(PSeccionLayout.createSequentialGroup()
                                .addComponent(longSec, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
                    .addGroup(PSeccionLayout.createSequentialGroup()
                        .addComponent(jLabel9, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(factorPotSec, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE))
                    .addGroup(PSeccionLayout.createSequentialGroup()
                        .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(PSeccionLayout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addComponent(jLabel7, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE))
                            .addGroup(PSeccionLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(caidaTensionSec, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE))))
                    .addGroup(PSeccionLayout.createSequentialGroup()
                        .addComponent(jLabel11, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(potenciaSec, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(CBoxPotkW, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonCalcPot))
                    .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(GroupLayout.Alignment.LEADING, PSeccionLayout.createSequentialGroup()
                            .addGap(207, 207, 207)
                            .addComponent(VCaidaTension, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel13, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                        .addGroup(PSeccionLayout.createSequentialGroup()
                            .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(GroupLayout.Alignment.TRAILING, PSeccionLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addGroup(PSeccionLayout.createSequentialGroup()
                                        .addComponent(jLabel16, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
                                        .addGap(12, 12, 12)
                                        .addComponent(tseccion, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(PSeccionLayout.createSequentialGroup()
                                        .addComponent(jLabel12, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
                                        .addGap(132, 132, 132)
                                        .addComponent(ButtonAyudaIntensidad))
                                    .addComponent(CBoxTempMAtSec, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE))
                                .addGroup(GroupLayout.Alignment.TRAILING, PSeccionLayout.createSequentialGroup()
                                    .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(CBoxMaterialSec, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(115, 115, 115)))
                            .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(PSeccionLayout.createSequentialGroup()
                                    .addGap(30, 30, 30)
                                    .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(jButtonCalcLongitud)
                                        .addComponent(jButtonIMax)
                                        .addComponent(jButton2)))
                                .addGroup(PSeccionLayout.createSequentialGroup()
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel15)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(ButtonCalculaSec))
                                .addGroup(PSeccionLayout.createSequentialGroup()
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel8, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(conductividad, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PSeccionLayout.setVerticalGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PSeccionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(CBoxMaterialSec, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(CBoxTempMAtSec, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(conductividad, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, GroupLayout.PREFERRED_SIZE, 13, GroupLayout.PREFERRED_SIZE)
                    .addComponent(CboxTipoLinea, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(TensionSec, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(longSec, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jButtonCalcLongitud))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(intensidadSec, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(ButtonAyudaIntensidad)
                    .addComponent(jButtonIMax))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(caidaTensionSec, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(VCaidaTension, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jButton2))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(factorPotSec, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(potenciaSec, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(CBoxPotkW, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCalcPot))
                .addGap(18, 18, 18)
                .addGroup(PSeccionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(tseccion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(ButtonCalculaSec))
                .addGap(60, 60, 60))
        );

        Titulo1.setFont(new Font("Malgun Gothic", 0, 14)); // NOI18N
        Titulo1.setText("CALCULO DE SECCION");

        jLabelAutor.setBackground(new Color(0, 153, 153));
        jLabelAutor.setFont(new Font("Malgun Gothic", 0, 10)); // NOI18N
        jLabelAutor.setForeground(new Color(0, 51, 204));
        jLabelAutor.setText("by Jose Mora jmmora1974@gmail.com");

        GroupLayout PanelPCVELayout = new GroupLayout(PanelPCVE);
        PanelPCVE.setLayout(PanelPCVELayout);
        PanelPCVELayout.setHorizontalGroup(PanelPCVELayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PanelPCVELayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelPCVELayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addGroup(GroupLayout.Alignment.LEADING, PanelPCVELayout.createSequentialGroup()
                        .addGroup(PanelPCVELayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                            .addComponent(PLocales, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PAlumbrado, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PAscensor2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PAscensor1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(PanelPCVELayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(PanelPCVELayout.createSequentialGroup()
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(PGMotor, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(PanelPCVELayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(PGarage1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(PViviendas, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                    .addGroup(PanelPCVELayout.createSequentialGroup()
                        .addComponent(jLabelAutor)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonReset, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE)))
                .addGroup(PanelPCVELayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(PanelPCVELayout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(Titulo, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelPCVELayout.createSequentialGroup()
                        .addGap(120, 120, 120)
                        .addComponent(Titulo1, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelPCVELayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(PanelPCVELayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(PSeccion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(PResultados, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        PanelPCVELayout.setVerticalGroup(PanelPCVELayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, PanelPCVELayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelPCVELayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelPCVELayout.createSequentialGroup()
                        .addComponent(PViviendas, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelPCVELayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addGroup(PanelPCVELayout.createSequentialGroup()
                                .addComponent(PAscensor1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(PAscensor2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(PAlumbrado, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addComponent(PGMotor, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelPCVELayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(PLocales, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(PanelPCVELayout.createSequentialGroup()
                                .addComponent(PGarage1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelPCVELayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonReset, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelAutor)))
                    .addGroup(PanelPCVELayout.createSequentialGroup()
                        .addComponent(Titulo)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PResultados, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(Titulo1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PSeccion, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(109, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(PanelPCVE);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SupGarVenForzadaActionPerformed(ActionEvent evt) {//GEN-FIRST:event_SupGarVenForzadaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SupGarVenForzadaActionPerformed

    private void SupGarVenNaturalActionPerformed(ActionEvent evt) {//GEN-FIRST:event_SupGarVenNaturalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SupGarVenNaturalActionPerformed

    private void SuperLocal4ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_SuperLocal4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SuperLocal4ActionPerformed

    private void NumLocal4ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_NumLocal4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumLocal4ActionPerformed

    private void SuperLocal3ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_SuperLocal3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SuperLocal3ActionPerformed

    private void NumLocal3ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_NumLocal3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumLocal3ActionPerformed

    private void SuperLocal2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_SuperLocal2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SuperLocal2ActionPerformed

    private void NumLocal2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_NumLocal2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumLocal2ActionPerformed

    private void SuperLocal1ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_SuperLocal1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SuperLocal1ActionPerformed

    private void NumLocal1ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_NumLocal1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumLocal1ActionPerformed

    private void PotAlIncaActionPerformed(ActionEvent evt) {//GEN-FIRST:event_PotAlIncaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PotAlIncaActionPerformed

    private void PotAlFLourActionPerformed(ActionEvent evt) {//GEN-FIRST:event_PotAlFLourActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PotAlFLourActionPerformed

    private void PotOtrosMotores2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_PotOtrosMotores2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PotOtrosMotores2ActionPerformed

    private void NumOtrosActionPerformed(ActionEvent evt) {//GEN-FIRST:event_NumOtrosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumOtrosActionPerformed

    private void PotOtrosMotoresActionPerformed(ActionEvent evt) {//GEN-FIRST:event_PotOtrosMotoresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PotOtrosMotoresActionPerformed

    private void PotDepuradoraActionPerformed(ActionEvent evt) {//GEN-FIRST:event_PotDepuradoraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PotDepuradoraActionPerformed

    private void NumDepuradoraActionPerformed(ActionEvent evt) {//GEN-FIRST:event_NumDepuradoraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumDepuradoraActionPerformed

    private void PotGPresionActionPerformed(ActionEvent evt) {//GEN-FIRST:event_PotGPresionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PotGPresionActionPerformed

    private void NumGPresionActionPerformed(ActionEvent evt) {//GEN-FIRST:event_NumGPresionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumGPresionActionPerformed

    private void PotAsc1kWActionPerformed(ActionEvent evt) {//GEN-FIRST:event_PotAsc1kWActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PotAsc1kWActionPerformed

    private void ComboPotAsc1ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_ComboPotAsc1ActionPerformed
        // TODO add your handling code here:
        System.out.println(ComboPotAsc1.getSelectedItem().toString());
        switch (ComboPotAsc1.getSelectedItem().toString()) {
            case "ITA-1":
                PotAsc1kW.setText("4.5");
                activacionCasillasAscensor1(false);
                break;
            case "ITA-2":
                PotAsc1kW.setText("7.5");
                activacionCasillasAscensor1(false);
                break;
            case "ITA-3":
                PotAsc1kW.setText("11.5");
                activacionCasillasAscensor1(false);
                break;
            case "ITA-4":
                PotAsc1kW.setText("18.5");
                activacionCasillasAscensor1(false);
                break;
            case "ITA-5":
                PotAsc1kW.setText("29.5");
                activacionCasillasAscensor1(false);
                break;
            case "ITA-6":
                PotAsc1kW.setText("46");
                activacionCasillasAscensor1(false);
                break;

            default:
                PotAsc1kW.setText("");
                activacionCasillasAscensor1(true);
                System.out.println("Potencia no registrada.");
        }
    }//GEN-LAST:event_ComboPotAsc1ActionPerformed

    private void NumAsc1ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_NumAsc1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumAsc1ActionPerformed

    private void PotAsc2kWActionPerformed(ActionEvent evt) {//GEN-FIRST:event_PotAsc2kWActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PotAsc2kWActionPerformed

    private void ComboPotAsc2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_ComboPotAsc2ActionPerformed

        String potAscL = potenciaAscensor(ComboPotAsc2.getSelectedItem().toString());

        if ("".equals(potAscL)) {
            PotAsc2kW.setText("");
            activacionCasillasAscensor2(true);
        } else {
            PotAsc2kW.setText(potAscL);
            activacionCasillasAscensor2(false);
        }


    }//GEN-LAST:event_ComboPotAsc2ActionPerformed

    private void NumAsc2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_NumAsc2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumAsc2ActionPerformed

    private void jLabelM2Viviendas2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jLabelM2Viviendas2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabelM2Viviendas2ActionPerformed

    private void TextVivConsuAlto4ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_TextVivConsuAlto4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TextVivConsuAlto4ActionPerformed

    private void VivBasConIrveActionPerformed(ActionEvent evt) {//GEN-FIRST:event_VivBasConIrveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_VivBasConIrveActionPerformed

    private void NumVivConsuBasicoActionPerformed(ActionEvent evt) {//GEN-FIRST:event_NumVivConsuBasicoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumVivConsuBasicoActionPerformed

    private void NumVivConsuAltoActionPerformed(ActionEvent evt) {//GEN-FIRST:event_NumVivConsuAltoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumVivConsuAltoActionPerformed

    private void NumOtrosMotoresActionPerformed(ActionEvent evt) {//GEN-FIRST:event_NumOtrosMotoresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumOtrosMotoresActionPerformed

    private void jButtonResetActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButtonResetActionPerformed

        super.dispose();
        FramePCVE pp = new FramePCVE();
        pp.setVisible(true);

    }//GEN-LAST:event_jButtonResetActionPerformed

    private void ComboBoxEsquemaActionPerformed(ActionEvent evt) {//GEN-FIRST:event_ComboBoxEsquemaActionPerformed
        //Comprobamos el tipo de esquema para habilitar/dehabilitar opciones, por ej P5
        switch (ComboBoxEsquema.getSelectedItem().toString()) {
            case "2":
            case "4a":
                //System.out.println(ComboBoxEsquema.getSelectedItem());
                LabelP5.setVisible(false);
                TResP5.setVisible(false);
                LabelkWP5.setVisible(false);
                jButtonP5.setVisible(false);
                break;

            default:
                LabelP5.setVisible(true);
                TResP5.setVisible(true);
                LabelkWP5.setVisible(true);
                jButtonP5.setVisible(true);
                System.out.println("El esquema no esta en la lista.");

        }


    }//GEN-LAST:event_ComboBoxEsquemaActionPerformed

    private void PotPCVivBasConIrveActionPerformed(ActionEvent evt) {//GEN-FIRST:event_PotPCVivBasConIrveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PotPCVivBasConIrveActionPerformed

    private void VivAltoCIrveActionPerformed(ActionEvent evt) {//GEN-FIRST:event_VivAltoCIrveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_VivAltoCIrveActionPerformed

    private void PotPCVivCAltoConIrveActionPerformed(ActionEvent evt) {//GEN-FIRST:event_PotPCVivCAltoConIrveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PotPCVivCAltoConIrveActionPerformed

    private void NumVivConsuContratadoActionPerformed(ActionEvent evt) {//GEN-FIRST:event_NumVivConsuContratadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumVivConsuContratadoActionPerformed

    private void VivContratadoconIrveActionPerformed(ActionEvent evt) {//GEN-FIRST:event_VivContratadoconIrveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_VivContratadoconIrveActionPerformed

    private void PotPCVivContConIrveActionPerformed(ActionEvent evt) {//GEN-FIRST:event_PotPCVivContConIrveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PotPCVivContConIrveActionPerformed

    private void jLabelM2Viviendas3ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jLabelM2Viviendas3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabelM2Viviendas3ActionPerformed

    private void PotContradaVivContActionPerformed(ActionEvent evt) {//GEN-FIRST:event_PotContradaVivContActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PotContradaVivContActionPerformed

    private void intensidadSecActionPerformed(ActionEvent evt) {//GEN-FIRST:event_intensidadSecActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_intensidadSecActionPerformed

    private void CBoxOTkWActionPerformed(ActionEvent evt) {//GEN-FIRST:event_CBoxOTkWActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CBoxOTkWActionPerformed

    private void CBoxGPkWActionPerformed(ActionEvent evt) {//GEN-FIRST:event_CBoxGPkWActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CBoxGPkWActionPerformed

    private void CBoxOT2kWActionPerformed(ActionEvent evt) {//GEN-FIRST:event_CBoxOT2kWActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CBoxOT2kWActionPerformed

    private void jButtonPrevisionActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButtonPrevisionActionPerformed
        double p1, p2, p3, p4, p5;
        p1 = calcularP1();
        p2 = calcularP2();
        p3 = calcularP3();
        p4 = calcularP4();
        p5 = calcularP5();

        TResP6.setText(Double.toString(p1 + p2 + p3 + p4 + p5));
    }//GEN-LAST:event_jButtonPrevisionActionPerformed

    private void jButtonP5ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButtonP5ActionPerformed
        calcularP5();
    }//GEN-LAST:event_jButtonP5ActionPerformed

    private void jButtonP4ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButtonP4ActionPerformed
        double resP4 = calcularP4();

        System.out.println("El resultado para P4 es : " + Double.toString(resP4));
    }//GEN-LAST:event_jButtonP4ActionPerformed

    private void jButtonP3ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButtonP3ActionPerformed
        double resP3 = calcularP3();
        TResP3.setText(Double.toString(resP3));
        System.out.println("El resultado para P3 es : " + Double.toString(resP3));
    }//GEN-LAST:event_jButtonP3ActionPerformed

    private void jButtonP2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButtonP2ActionPerformed
        double resP2 = calcularP2();

        System.out.println("El resultado para P2 es : " + Double.toString(resP2));
    }//GEN-LAST:event_jButtonP2ActionPerformed

    private void jButtonP1ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButtonP1ActionPerformed
        double resP1 = calcularP1();

        System.out.println("El resultado para P1 es : " + Double.toString(resP1));
    }//GEN-LAST:event_jButtonP1ActionPerformed

    private void CBoxTempMAtSecActionPerformed(ActionEvent evt) {//GEN-FIRST:event_CBoxTempMAtSecActionPerformed
        calculaConductividad();
    }//GEN-LAST:event_CBoxTempMAtSecActionPerformed

    private void CBoxMaterialSecActionPerformed(ActionEvent evt) {//GEN-FIRST:event_CBoxMaterialSecActionPerformed
        calculaConductividad();
    }//GEN-LAST:event_CBoxMaterialSecActionPerformed
    //Abre el fichero de ayuda de intensidades máximas en pdf
    private void ButtonAyudaIntensidadActionPerformed(ActionEvent evt) {//GEN-FIRST:event_ButtonAyudaIntensidadActionPerformed
        try {
            File path = new File("IntensidadesMaximas.pdf");
            Desktop.getDesktop().open(path);
        } catch (IOException ex) {
            System.out.println("Error al abrir el fichero de intensidades maximas.");
        }
    }//GEN-LAST:event_ButtonAyudaIntensidadActionPerformed

    private void jButtonCalcLongitudActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButtonCalcLongitudActionPerformed
        calculaLongitud();
    }//GEN-LAST:event_jButtonCalcLongitudActionPerformed

    private void potenciaSecActionPerformed(ActionEvent evt) {//GEN-FIRST:event_potenciaSecActionPerformed
        calculaPotencia ();
    }//GEN-LAST:event_potenciaSecActionPerformed

    private void jButton2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        calculaVoltageCaida ();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButtonIMaxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButtonIMaxActionPerformed
        calculaImax ();
    }//GEN-LAST:event_jButtonIMaxActionPerformed

    private void tseccionActionPerformed(ActionEvent evt) {//GEN-FIRST:event_tseccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tseccionActionPerformed

    private void ButtonCalculaSecActionPerformed(ActionEvent evt) {//GEN-FIRST:event_ButtonCalculaSecActionPerformed
        calculaSeccion ();
    }//GEN-LAST:event_ButtonCalculaSecActionPerformed

    private void jButtonCalcPotActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButtonCalcPotActionPerformed
        calculaPotencia();
    }//GEN-LAST:event_jButtonCalcPotActionPerformed

    private void caidaTensionSecInputMethodTextChanged(InputMethodEvent evt) {//GEN-FIRST:event_caidaTensionSecInputMethodTextChanged
        calculaVoltageCaida ();
    }//GEN-LAST:event_caidaTensionSecInputMethodTextChanged

    private void CBoxPotkWActionPerformed(ActionEvent evt) {//GEN-FIRST:event_CBoxPotkWActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CBoxPotkWActionPerformed

    private void TensionSecActionPerformed(ActionEvent evt) {//GEN-FIRST:event_TensionSecActionPerformed
        calculaVoltageCaida ();
    }//GEN-LAST:event_TensionSecActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }

        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FramePCVE.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FramePCVE.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FramePCVE.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FramePCVE.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        try {
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    FramePCVE FPCVE = new FramePCVE();
                    FPCVE.setVisible(true);
                }
            });
        } catch (NumberFormatException | ConcurrentModificationException e) {
            System.err.println(e);
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    JButton ButtonAyudaIntensidad;
    JButton ButtonCalculaSec;
    JComboBox<String> CBoxDepukW;
    JComboBox<String> CBoxGPkW;
    JComboBox<String> CBoxMaterialSec;
    JComboBox<String> CBoxOT2kW;
    JComboBox<String> CBoxOTkW;
    JComboBox<String> CBoxPotkW;
    JComboBox<String> CBoxTempMAtSec;
    JComboBox<String> CboxTipoLinea;
    JCheckBox CheckBoxSPL;
    JComboBox<String> ComboBoxEsquema;
    JComboBox<String> ComboBoxkWAsc1;
    JComboBox<String> ComboBoxkWAsc2;
    JComboBox<String> ComboPotAsc1;
    JComboBox<String> ComboPotAsc2;
    JLabel LabelAlumbrado1;
    JLabel LabelAlumbrado2;
    JLabel LabelAsceNum1;
    JLabel LabelAsceNum10;
    JLabel LabelAsceNum11;
    JLabel LabelAsceNum12;
    JLabel LabelAsceNum2;
    JLabel LabelAsceNum3;
    JLabel LabelAsceNum4;
    JLabel LabelAsceNum5;
    JLabel LabelAsceNum6;
    JLabel LabelAsceNum7;
    JLabel LabelAsceNum8;
    JLabel LabelAsceNum9;
    JLabel LabelAscensor1;
    JLabel LabelAscensor2;
    JLabel LabelAscensor3;
    JLabel LabelAscensor4;
    JLabel LabelAscensor5;
    JLabel LabelAscensor6;
    JLabel LabelCantidadLocal1;
    JLabel LabelCantidadLocal2;
    JLabel LabelCantidadLocal3;
    JLabel LabelCantidadLocal4;
    JLabel LabelConsumoAlto;
    JLabel LabelConsumoAlto1;
    JLabel LabelConsumoBasico;
    JLabel LabelGarage1b1;
    JLabel LabelGarage2;
    JLabel LabelLocal1;
    JLabel LabelLocal2;
    JLabel LabelLocal3;
    JLabel LabelLocal4;
    JLabel LabelP1;
    JLabel LabelP2;
    JLabel LabelP3;
    JLabel LabelP4;
    JLabel LabelP5;
    JLabel LabelP6;
    JLabel LabelSPL;
    JLabel LabelSPL1;
    JLabel LabelSupLocal1;
    JLabel LabelSupLocal2;
    JLabel LabelSupLocal3;
    JLabel LabelSupLocal4;
    JLabel LabelSuperficieCAlto;
    JLabel LabelSuperficieCBasico;
    JLabel LabelSuperficieCBasico1;
    JLabel LabelVivCAltoConIrve;
    JLabel LabelVivCContratConIrve1;
    JLabel LabelVivConIrve;
    JLabel LabelVivConIrve1;
    JLabel LabelVivConIrve3;
    JLabel LabelVivConIrve4;
    JLabel LabelVivConIrve5;
    JLabel LabelkW10;
    JLabel LabelkW11;
    JLabel LabelkW12;
    JLabel LabelkW13;
    JLabel LabelkW14;
    JLabel LabelkW7;
    JLabel LabelkW8;
    JLabel LabelkW9;
    JLabel LabelkWP1;
    JLabel LabelkWP2;
    JLabel LabelkWP3;
    JLabel LabelkWP4;
    JLabel LabelkWP5;
    JLabel LabelkWP6;
    JTextField NumAsc1;
    JTextField NumAsc2;
    JTextField NumDepuradora;
    JTextField NumGPresion;
    JTextField NumLocal1;
    JTextField NumLocal2;
    JTextField NumLocal3;
    JTextField NumLocal4;
    JTextField NumOtros;
    JTextField NumOtrosMotores;
    JTextField NumVivConsuAlto;
    JTextField NumVivConsuBasico;
    JTextField NumVivConsuContratado;
    JPanel PAlumbrado;
    JPanel PAscensor1;
    JPanel PAscensor2;
    JPanel PGMotor;
    JPanel PGarage1;
    JPanel PLocales;
    JPanel PResultados;
    JPanel PSeccion;
    JPanel PViviendas;
    JPanel PanelPCVE;
    JTextField PotAlFLour;
    JTextField PotAlInca;
    JTextField PotAsc1kW;
    JTextField PotAsc2kW;
    JTextField PotContradaVivCont;
    JTextField PotDepuradora;
    JTextField PotGPresion;
    JTextField PotOtrosMotores;
    JTextField PotOtrosMotores2;
    JTextField PotPCVivBasConIrve;
    JTextField PotPCVivCAltoConIrve;
    JTextField PotPCVivContConIrve;
    JTextField SupGarVenForzada;
    JTextField SupGarVenNatural;
    JTextField SuperLocal1;
    JTextField SuperLocal2;
    JTextField SuperLocal3;
    JTextField SuperLocal4;
    JTextField TResP1;
    JTextField TResP2;
    JTextField TResP3;
    JTextField TResP4;
    JTextField TResP5;
    JTextField TResP6;
    JTextField TensionSec;
    JTextField TextVivConsuAlto4;
    JLabel Titulo;
    JLabel Titulo1;
    JTextField VCaidaTension;
    JTextField VivAltoCIrve;
    JTextField VivBasConIrve;
    JTextField VivContratadoconIrve;
    JTextField caidaTensionSec;
    JTextField conductividad;
    JTextField factorPotSec;
    JTextField intensidadSec;
    JButton jButton2;
    JButton jButtonCalcLongitud;
    JButton jButtonCalcPot;
    JButton jButtonIMax;
    JButton jButtonP1;
    JButton jButtonP2;
    JButton jButtonP3;
    JButton jButtonP4;
    JButton jButtonP5;
    JButton jButtonPrevision;
    JButton jButtonReset;
    JLabel jLabel1;
    JLabel jLabel10;
    JLabel jLabel11;
    JLabel jLabel12;
    JLabel jLabel13;
    JLabel jLabel14;
    JLabel jLabel15;
    JLabel jLabel16;
    JLabel jLabel2;
    JLabel jLabel3;
    JLabel jLabel4;
    JLabel jLabel5;
    JLabel jLabel6;
    JLabel jLabel7;
    JLabel jLabel8;
    JLabel jLabel9;
    JLabel jLabelAutor;
    JLabel jLabelM2Local1;
    JLabel jLabelM2Local2;
    JLabel jLabelM2Local3;
    JLabel jLabelM2Local4;
    JLabel jLabelM2Local6;
    JLabel jLabelM2Local7;
    JLabel jLabelM2Viviendas1;
    JTextField jLabelM2Viviendas2;
    JTextField jLabelM2Viviendas3;
    JScrollPane jScrollPane1;
    JTextField longSec;
    JTextField potenciaSec;
    JTextField tseccion;
    // End of variables declaration//GEN-END:variables

}
