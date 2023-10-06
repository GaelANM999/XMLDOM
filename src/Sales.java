import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class Sales {
    static final String CLASS_NAME = Sales.class.getSimpleName();
    static final Logger LOG = Logger.getLogger(CLASS_NAME);

    public static void main(String argv[]) {
        if (argv.length != 1) {
            LOG.severe("Falta archivo XML como argumento.");
            System.exit(1);
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new File(argv[0]));

            doc.getDocumentElement().normalize();
            //se aplica el metodo para aumentar el porcentaje y guardar el xml
            incremento(doc);
            saveDocument(doc, "new_sales.xml");




        } catch (ParserConfigurationException e) {
            LOG.severe(e.getMessage());
        } catch (IOException e) {
            LOG.severe(e.getMessage());
        } catch (SAXException e) {
            LOG.severe(e.getMessage());
        }


    }
    //Empieza el metodo para aumentar el porcentaje a las ventas
    public  static void incremento(Document doc){
        Element root = doc.getDocumentElement();

       
        //el usuario ingresa el porcentaje que quiere que aumenten las ventas
        double cuanto = Integer.parseInt(JOptionPane.showInputDialog(null, "Escribe un porcentaje entre 5% y 15%" +
                " para incrementar el valor de las ventas: "));
        //valida que sea un valor entre 5 y 15
        if (cuanto < 5 || cuanto> 15){
            JOptionPane.showMessageDialog(null,"Introduce un valor válido");
        }else {
            //el usuario ingresa a cual departamento hacerle los cambios
            String cual = JOptionPane.showInputDialog(null, "Escribe el departamento al " +
                    "que se le harán los cambios: ");
            String department;
            String sales;

            //toma el nodo para ver los elementos de este
            NodeList salesData = root.getElementsByTagName("sale_record");

            for (int i = 0; i < salesData.getLength(); i++) {

                Node node = salesData.item(i);

                    Element element = (Element) node;

                        //toma los atributos de los elementos que le decimos
                    department = element.getElementsByTagName("department").item(0).getTextContent();
                    sales = element.getElementsByTagName("sales").item(0).getTextContent();
                    
                    //toma el valor que se ingresó y lo convierte para usarlo en operaciones
                    double porcentaje= cuanto * 0.01;



                            //verifica que el departamento ingresado exista
                        if (department.equals(cual)) {
                            //toma lo que hay en sales del departamento especificado
                            sales = element.getElementsByTagName("sales").item(0).getTextContent();

                            //hace los calculos necesarios para sacar el porcentaje de cada venta y aumentarselo
                            Double salesD = Double.parseDouble(sales);
                            double salesPor = salesD * porcentaje;
                            double salesFinal = salesD + salesPor;
                            //escribe los sales nuevos en el documento
                            element.getElementsByTagName("sales").item(0).setTextContent(String.valueOf(salesFinal));
                            System.out.println("Ventas de " + cual + " " + (i + 1) + " " + salesFinal);


                        }


                }
            }
        }


//sobreescribe el xml con los cambios de sales

    public static void saveDocument(Document doc, String fileName) {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);

            FileWriter writer = new FileWriter(new File(fileName), false);
            StreamResult result = new StreamResult(writer);

            transformer.transform(source, result);

        } catch (TransformerConfigurationException | IOException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}