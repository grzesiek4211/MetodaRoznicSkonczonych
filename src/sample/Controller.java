package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.round;

public class Controller {
    @FXML
    public TextField beta;
    @FXML
    public TextField epsilon;
    @FXML
    public Button save;
    @FXML
    public TextField potential;
    @FXML
    public Button start;
    @FXML
    public Button reset;
    @FXML
    public Label iterations;
    @FXML
    public Canvas canvas;
    @FXML
    public Label label1;
    @FXML
    public Label label2;
    @FXML
    public Label label3;
    @FXML
    public Label label4;
    @FXML
    public Label label5;
    @FXML
    public Label label6;
    @FXML
    public Label label7;
    @FXML
    public Label label8;
    @FXML
    public Label label9;
    @FXML
    public Label label10;
    @FXML
    public Label message;

    GraphicsContext gc;
    List<Line> tablica = new ArrayList<Line>();
    List interval = new ArrayList();
    Line line = new Line();
    Polygon polygon = new Polygon();

    public int flaga;
    public int flaga_blockX;
    public int flaga_blockY;
    public double prevX[];
    public double prevY[];
    public double startX;
    public double startY;
    public double precision; // jak blisko od startowego punktu ma wyłapywać koniec lini i łaczyc z początkiem
    public int rysujemy;
    public double currentlyPotential;
    public double minX;
    public double minY;
    public double maxX;
    public double maxY;
    public double depsilon;
    public double dbeta;
    public int counterIteration;

    public double descriptionLabels[] = new double[11];

    public final String color[] = {"#01024A", "#002374", "#004EB7", "#0075DD", "#40A96E", "#D6D402", "#FD9D18", "#FE2E39", "#F81D4E", "#FD777B"};


    public void initialize() {
        flaga = 0;
        flaga_blockX = 0;
        flaga_blockY = 0;
        prevX = new double[2];
        prevY = new double[2];
        startX = 0;
        startY = 0;
        precision = 5;
        rysujemy = 0;
        dbeta = 1.5;
        depsilon = 0.001;

        gc = canvas.getGraphicsContext2D();
        //gc.setLineWidth(1);
        this.start.setDisable(true);
    }


    public void onActionSave(ActionEvent actionEvent) {
        if(this.beta.getText().equals("")||this.epsilon.getText().equals(""))
            return;

        this.dbeta = Double.parseDouble(this.beta.getText());
        this.depsilon = Double.parseDouble(this.epsilon.getText());

        this.beta.setDisable(true);
        this.epsilon.setDisable(true);
        this.start.setDisable(false);
        this.message.setText("");
    }

    public void onActionStart(ActionEvent actionEvent) {

        CreatePolygon();
        WritableImage writableImage = getWritableImageFromCanvas(this.canvas);
        System.out.println("Rozpoczynam obliczenia!");
        double pixels[][] = countValuePixel(writableImage);
        setInterval(tablica, pixels);
        System.out.println("Kolorowanie wielokąta!");
        setDescriptionLabels();
        writableImage = PaintCanvas(pixels);
        gc.drawImage(writableImage, 0, 0);

        iterations.setText(Integer.toString(counterIteration));
    }

    public void onActionReset(ActionEvent actionEvent) {
        //this.epsilon.clear();
        this.beta.clear();
        this.beta.setDisable(false);
        this.start.setDisable(true);
        //this.epsilon.setDisable(false);
        counterIteration = 0;
        this.iterations.setText("");
        this.message.setText("Kliknij Save");

        this.label1.setText("");
        this.label2.setText("");
        this.label3.setText("");
        this.label4.setText("");
        this.label5.setText("");
        this.label6.setText("");
        this.label7.setText("");
        this.label8.setText("");
        this.label9.setText("");
        this.label10.setText("");

        WritableImage writableImage;// = getWritableImageFromCanvas(this.canvas);
        writableImage = resetCanvas();
        gc.drawImage(writableImage, 0, 0);
    }


    public void CanvasOnMouseClicked(MouseEvent mouseEvent) {

        if (rysujemy == 0) {
            gc.setStroke(new ColorPicker(Color.BLACK).getValue());
            line.setStartX(mouseEvent.getX());
            line.setStartY(mouseEvent.getY());  // uwaga bo mozna kliknąc ale tez przeciagnac myszke i moze byc rozne wartosci getX/Y sie popsuć moze.

            if (flaga == 0) // rysowanie pierwszego punktu...
            {
                startX = mouseEvent.getX();
                startY = mouseEvent.getY();
                line.setEndX(mouseEvent.getX());
                line.setEndY(mouseEvent.getY());
                prevX[0] = mouseEvent.getX();
                prevX[1] = mouseEvent.getX();
                prevY[0] = mouseEvent.getY();
                prevY[1] = mouseEvent.getY();
                flaga = 1;

                prevX[0] = prevX[1];
                prevX[1] = line.getEndX();

                prevY[0] = prevY[1];
                prevY[1] = line.getEndY();
                return;
            }

            double odleglosc_klikniecia_od_ostatniego_pktX = abs(mouseEvent.getX() - line.getEndX());
            double odleglosc_klikniecia_od_ostatniego_pktY = abs(mouseEvent.getY() - line.getEndY());

            double odleglosc_klikniecia_od_poczatkowego_pktX = abs(mouseEvent.getX() - startX);
            double odleglosc_klikniecia_od_poczatkowego_pktY = abs(mouseEvent.getY() - startY);

            if (odleglosc_klikniecia_od_poczatkowego_pktX <= precision && odleglosc_klikniecia_od_poczatkowego_pktY <= precision) {
                double lastY = line.getEndY();
                double lastX = line.getEndX();
                if (lastY < startY) {
                    //currentlyPotential = Double.parseDouble(potential.getText());
                    gc.strokeLine(lastX, lastY, startX, lastY);
                    tablica.add(new Line(lastX, lastY, startX, lastY));
                    tablica.get(tablica.size() - 1).setUserData(currentlyPotential);
                    //System.out.println(tablica.get(tablica.size() - 1).getUserData());
                    currentlyPotential = Double.parseDouble(potential.getText());
                    gc.strokeLine(startX, lastY, startX, startY);
                    tablica.add(new Line(startX, lastY, startX, startY));
                    tablica.get(tablica.size() - 1).setUserData(currentlyPotential);
                   // System.out.println(tablica.get(tablica.size() - 1).getUserData());
                    rysujemy = 1;
                } else if (lastY > startY) {
                    if (prevX[0] == prevX[1] && prevY[0] < prevY[1])
                        return;

                    //currentlyPotential = Double.parseDouble(potential.getText());
                    gc.strokeLine(lastX, lastY, lastX, startY);
                    tablica.add(new Line(lastX, lastY, lastX, startY));
                    tablica.get(tablica.size() - 1).setUserData(currentlyPotential);
                    //System.out.println(tablica.get(tablica.size() - 1).getUserData());
                    currentlyPotential = Double.parseDouble(potential.getText());
                    gc.strokeLine(lastX, startY, startX, startY);
                    tablica.add(new Line(lastX, startY, startX, startY));
                    tablica.get(tablica.size() - 1).setUserData(currentlyPotential);
                    //System.out.println(tablica.get(tablica.size() - 1).getUserData());
                    rysujemy = 1;
                } else {
                    currentlyPotential = Double.parseDouble(potential.getText());
                    gc.strokeLine(lastX, lastY, startX, startY);
                    tablica.add(new Line(lastX, lastY, startX, startY));
                    tablica.get(tablica.size() - 1).setUserData(currentlyPotential);
                    //System.out.println(tablica.get(tablica.size() - 1).getUserData());
                }
            }

            // u gory klikniecie blisko poczatku. ====================================

            else if (odleglosc_klikniecia_od_ostatniego_pktX >= odleglosc_klikniecia_od_ostatniego_pktY) {
                flaga_blockY = 0;
                if (prevX[0] <= line.getEndX() && line.getEndX() < mouseEvent.getX()) {

                    if (flaga_blockX != 2) {
                        currentlyPotential = Double.parseDouble(potential.getText());
                        gc.strokeLine(line.getEndX(), line.getEndY(), mouseEvent.getX(), line.getEndY());
                        tablica.add(new Line(line.getEndX(), line.getEndY(), mouseEvent.getX(), line.getEndY()));
                        tablica.get(tablica.size() - 1).setUserData(currentlyPotential);
                        //System.out.println(tablica.get(tablica.size() - 1).getUserData());
                        line.setEndX(mouseEvent.getX());
                        line.setEndY(line.getEndY());
                        flaga_blockX = 1;
                    }
                } else if (mouseEvent.getX() < line.getEndX() && prevX[0] >= line.getEndX()) {

                    if (flaga_blockX != 1) {
                        currentlyPotential = Double.parseDouble(potential.getText());
                        gc.strokeLine(line.getEndX(), line.getEndY(), mouseEvent.getX(), line.getEndY());
                        tablica.add(new Line(line.getEndX(), line.getEndY(), mouseEvent.getX(), line.getEndY()));
                        tablica.get(tablica.size() - 1).setUserData(currentlyPotential);
                        //System.out.println(tablica.get(tablica.size() - 1).getUserData());
                        line.setEndX(mouseEvent.getX());
                        line.setEndY(line.getEndY());

                        flaga_blockX = 2;
                    }
                }
            } else {
                flaga_blockX = 0;
                if (prevY[0] <= line.getEndY() && mouseEvent.getY() > line.getEndY()) {
                    if (flaga_blockY != 2) {
                        currentlyPotential = Double.parseDouble(potential.getText());
                        gc.strokeLine(line.getEndX(), line.getEndY(), line.getEndX(), mouseEvent.getY());
                        tablica.add(new Line(line.getEndX(), line.getEndY(), line.getEndX(), mouseEvent.getY()));
                        tablica.get(tablica.size() - 1).setUserData(currentlyPotential);
                        //System.out.println(tablica.get(tablica.size() - 1).getUserData());
                        line.setEndX(line.getEndX());
                        line.setEndY(mouseEvent.getY());
                        flaga_blockY = 1;
                    }
                } else if (mouseEvent.getY() < line.getEndY() && prevY[0] >= line.getEndY()) {
                    if (flaga_blockY != 1) {
                        currentlyPotential = Double.parseDouble(potential.getText());
                        gc.strokeLine(line.getEndX(), line.getEndY(), line.getEndX(), mouseEvent.getY());
                        tablica.add(new Line(line.getEndX(), line.getEndY(), line.getEndX(), mouseEvent.getY()));
                        tablica.get(tablica.size() - 1).setUserData(currentlyPotential);
                        //System.out.println(tablica.get(tablica.size() - 1).getUserData());
                        line.setEndX(line.getEndX());
                        line.setEndY(mouseEvent.getY());
                        flaga_blockY = 2;
                    }
                }
            }

            prevX[0] = prevX[1];
            prevX[1] = line.getEndX();

            prevY[0] = prevY[1];
            prevY[1] = line.getEndY();

        }
    }

    public WritableImage getWritableImageFromCanvas(Canvas canvas) {

        WritableImage writableImage = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
        return canvas.snapshot(null, writableImage);
    }

    public void minAndMaxCoOrdinates(List<Line> tablica) {
        if (tablica.isEmpty()) return;
        minX = tablica.get(0).getStartX();
        maxX = tablica.get(0).getStartX();
        minY = tablica.get(0).getStartY();
        maxY = tablica.get(0).getStartY();

        for (int i = 1; i < tablica.size(); i++) {
            if (tablica.get(i).getStartX() > maxX) maxX = tablica.get(i).getStartX();
            if (tablica.get(i).getStartX() < minX) minX = tablica.get(i).getStartX();
            if (tablica.get(i).getStartY() > maxY) maxY = tablica.get(i).getStartY();
            if (tablica.get(i).getStartY() < minY) minY = tablica.get(i).getStartY();
        }
    }

    public boolean isInside1(List<Line> tablica, int x, int y) {

        double up = 0; // ile linii jest nad pixelem
        double down = 0; // ile linii jest pod pixelem
        double right = 0; // ile linii jest z prawej pixelem
        double left = 0; // ile linii jest z lewej pixelem
        for(Line l : tablica)
        {
            if( ( x >= l.getStartX() && x <= l.getEndX() ) || ( x <= l.getStartX() && x >= l.getEndX() ) )
            {
                if (y > l.getStartY() && y > l.getEndY()) down++;
                else if (y < l.getEndY() && y < l.getEndY()) up++;
            }

            if( ( y >= l.getStartY() && y <= l.getEndY() ) || ( y <= l.getStartY() && y >= l.getEndY() ) )
            {
                if (x > l.getStartX() && x > l.getEndX()) left++;
                else if (x < l.getStartX() && x < l.getEndX()) right++;
            }
        }


        /*List lineHorizontal = new ArrayList(); // będę tu trzymał indeksy lini poziomych, które leżą pod lub nad pixelem
        List lineVertical = new ArrayList(); // będę tu trzymał indeksy lini pionowych, które leżą obok pixela
        for (int i = 0; i < tablica.size(); i++) {
            if (tablica.get(i).getStartX() == tablica.get(i).getEndX()) {
                if ( ( y > tablica.get(i).getStartY() && y < tablica.get(i).getEndY() ) || ( y < tablica.get(i).getStartY() && y > tablica.get(i).getEndY() ) )
                    lineVertical.add(i);
            } else if (tablica.get(i).getStartY() == tablica.get(i).getEndY()) {
                if ( ( x > tablica.get(i).getStartX() && x < tablica.get(i).getEndX() ) || ( x < tablica.get(i).getStartX() && x > tablica.get(i).getEndX() ) )
                    lineHorizontal.add(i);
            }
        }

        double up = 0; // ile linii jest nad pixelem
        double down = 0; // ile linii jest pod pixelem

        for (int i = 0; i < lineHorizontal.size(); i++) {
            if ((tablica.get((int) lineHorizontal.get(i)).getStartY() - y) > 0) up++;
            if ((tablica.get((int) lineHorizontal.get(i)).getStartY() - y) < 0) down++;
        }

        if (up % 2 == 0) return false; // nad jest parzysta liczba linii, czyli jest
        if (down % 2 == 0) return false;

        double right = 0; // ile linii jest z prawej pixelem
        double left = 0; // ile linii jest z lewej pixelem

        for (int i = 0; i < lineVertical.size(); i++) {
            if ((tablica.get((int) lineVertical.get(i)).getStartX() - x) > 0) left++;
            if ((tablica.get((int) lineVertical.get(i)).getStartX() - x) < 0) right++;
        }

        if (left % 2 == 0) return false;
        if (right % 2 == 0) return false;*/

        if (up % 2 == 0) return false; // nad jest parzysta liczba linii, czyli jest
        if (down % 2 == 0) return false;
        if (left % 2 == 0) return false;
        //if (right % 2 == 0) return false;
        return true;
    }
    public boolean isInside(int x, int y) {

        return polygon.contains(x,y);
    }
    public boolean isInside2(List<Line> tablica, int x, int y) {

        List lineHorizontal = new ArrayList(); // będę tu trzymał indeksy lini poziomych, które leżą pod lub nad pixelem
        List lineVertical = new ArrayList(); // będę tu trzymał indeksy lini pionowych, które leżą obok pixela
        for (int i = 0; i < tablica.size(); i++) {
            if (tablica.get(i).getStartX() == tablica.get(i).getEndX()) {
                if ( ( y > tablica.get(i).getStartY() && y < tablica.get(i).getEndY() ) || ( y < tablica.get(i).getStartY() && y > tablica.get(i).getEndY() ) )
                    lineVertical.add(i);
            } else if (tablica.get(i).getStartY() == tablica.get(i).getEndY()) {
                if ( ( x > tablica.get(i).getStartX() && x < tablica.get(i).getEndX() ) || ( x < tablica.get(i).getStartX() && x > tablica.get(i).getEndX() ) )
                    lineHorizontal.add(i);
            }
        }

        double up = 0; // ile linii jest nad pixelem
        double down = 0; // ile linii jest pod pixelem

        for (int i = 0; i < lineHorizontal.size(); i++) {
            if ((tablica.get((int) lineHorizontal.get(i)).getStartY() - y) > 0) up++;
            if ((tablica.get((int) lineHorizontal.get(i)).getStartY() - y) < 0) down++;
        }

        if (up % 2 == 0) return false; // nad jest parzysta liczba linii, czyli jest
        if (down % 2 == 0) return false;

        double right = 0; // ile linii jest z prawej pixelem
        double left = 0; // ile linii jest z lewej pixelem

        for (int i = 0; i < lineVertical.size(); i++) {
            if ((tablica.get((int) lineVertical.get(i)).getStartX() - x) > 0) left++;
            if ((tablica.get((int) lineVertical.get(i)).getStartX() - x) < 0) right++;
        }

        if (left % 2 == 0) return false;
        if (right % 2 == 0) return false;

        return true;
    }

    public void setInterval(List<Line> tablica, double[][] pixels) {
        if (tablica.size() <= 1) return;
        double min;
        double max;
        min = Double.parseDouble(tablica.get(0).getUserData().toString());
        max = Double.parseDouble(tablica.get(0).getUserData().toString());

        for  (int k = (int)minY + 1; k < (int)maxY; k++)
            for (int i = (int)minX + 1; i < (int)maxX; i++)
                if (isInside(i, k))
                    if(min > pixels[k][i])
                        min = pixels[k][i];

        for (int i = 1; i < tablica.size(); i++) {
            if (Double.parseDouble(tablica.get(i).getUserData().toString()) > max)
                max = Double.parseDouble(tablica.get(i).getUserData().toString());
            if (Double.parseDouble(tablica.get(i).getUserData().toString()) < min)
                min = Double.parseDouble(tablica.get(i).getUserData().toString());
        }

        double space = (max - min) / 10;
        interval.add(min);
        for (int i = 1; i < 10; i++) {
            interval.add(min + i * space);
        }
        interval.add(max);
    }

    public double[][] countValuePixel(WritableImage writableImage) {
        counterIteration = 1;
        minAndMaxCoOrdinates(tablica);

        int sizeX = (int) writableImage.getWidth();
        int sizeY = (int) writableImage.getHeight();
        double pixels[][] = new double[sizeY][sizeX];
        double pixelsPrev[][] = new double[sizeY][sizeX];

        // ustawienie wartosci na obwodzie
        pixels = setValueOnBoundary(pixels, sizeX, sizeY);
        pixelsPrev = setValueOnBoundary(pixelsPrev, sizeX, sizeY);

        pixels = setPixelArray(sizeX, sizeY, pixelsPrev, (int)minX, (int)minY, (int)maxX, (int)maxY);


        while(!fulfillCondition(pixelsPrev, pixels, (int)minX, (int)minY, (int)maxX, (int)maxY))
        {
            counterIteration++;
            pixelsPrev = setPrevPixelArray(sizeX, sizeY, pixels, (int)minX, (int)minY, (int)maxX, (int)maxY);
            pixels = setPixelArray(sizeX, sizeY, pixelsPrev, (int)minX, (int)minY, (int)maxX, (int)maxY);
        }

        return pixels;
    }

    public boolean fulfillCondition(double[][] tabPrev, double[][] tab, int mnX, int mnY, int mxX, int mxY)
    {
        for (int k = mnY + 1; k <  mxY; k++)
            for  (int i = mnX + 1; i < mxX; i++){
                if (!isInside(i, k)) continue;
                if (depsilon < abs(tab[k][i] - tabPrev[k][i])) return false;
            }

        return true;
    }

    public double[][] setPrevPixelArray(int sizeX, int sizeY, double[][] tab, int mnX, int mnY, int mxX, int mxY)
    {
        double pixelsPrev[][] = new double[sizeX][sizeY];
        for (int k = mnY; k < mxY+1; k++)
            for  (int i = mnX; i < mxX+1; i++)
                pixelsPrev[k][i] = tab[k][i];
        return pixelsPrev;
    }

    public double[][] setPixelArray(int sizeX, int sizeY, double[][] pixelsPrev, int mnX, int mnY, int mxX, int mxY)
    {
        double pixels[][] = new double[sizeX][sizeY];
        for(int j = mnY; j < mxY+1; j++)
            for(int i = mnX; i < mxX+1; i++)
                pixels[j][i] = pixelsPrev[j][i];

        for  (int k = mnY + 1; k < mxY; k++)
            for (int i = mnX + 1; i < mxX; i++)
                if (isInside(i, k))
                {
                    double Asr = 1.0 / 4.0 * (pixels[k + 1][i] + pixels[k][i + 1] + pixels[k - 1][i] + pixels[k][i - 1]);
                    pixels[k][i] = pixelsPrev[k][i] + dbeta * (Asr - pixelsPrev[k][i]);
                }
        return pixels;
    }

    public WritableImage PaintCanvas(double[][] pixels)
    {
        WritableImage writableImage = getWritableImageFromCanvas(this.canvas);

        // colorowanie środka
        for(int j = (int)minY; j < (int)maxY+1; j++) {
            for(int i = (int)minX ; i < (int)maxX+1; i++)
            {
                if (!isInside(i, j)) continue;
                for(int k = 0; k < interval.size(); k++)
                {
                    if(pixels[j][i] < Double.parseDouble(interval.get(k).toString()))
                    {
                        if(pixels[j][i] < Double.parseDouble(interval.get(0).toString())) { // to jest tylko dlatego ze wczesniej jest cos zepsute i wyjatkiem rzuca
                            writableImage.getPixelWriter().setColor(i, j, Color.web("#000000"));
                            break;
                        }
                        else {
                            writableImage.getPixelWriter().setColor(i, j, Color.web(color[k - 1]));
                            break;
                        }
                    }
                }

            }
        }
        return writableImage;
    }

    public WritableImage resetCanvas()
    {
        counterIteration = 0;

        WritableImage writableImage = getWritableImageFromCanvas(this.canvas);

        // colorowanie środka
        for(int i = (int)minX-1 ; i < (int)maxX+2; i++){
            for(int j = (int)minY-1; j < (int)maxY+2; j++)
            {
                if (!isInside(i, j)) continue;
                for(int k = 0; k < interval.size(); k++)
                {
                    writableImage.getPixelWriter().setColor(i,j, Color.web("#ffffff"));
                }

            }
        }

        return writableImage;

    }

    public void CreatePolygon()
    {
        /*for(Line l : tablica)
        {
            polygon.getPoints().addAll(l.getEndX(), l.getEndY());
        }*/
        for(Line l : tablica)
        {
            polygon.getPoints().add(l.getEndX());
            polygon.getPoints().add(l.getEndY());
        }
    }

    public double[][] setValueOnBoundary(double pixels[][], int sizeX, int sizeY)
    {
        for (Line item : tablica) {
            double a = Double.parseDouble(item.getUserData().toString());

            int startX = (int)item.getStartX();
            int endX = (int)item.getEndX();
            int startY = (int)item.getStartY();
            int endY = (int)item.getEndY();
            if(startX == endX) {
                if (startY > endY)
                    for (int j = startY; j > endY; j--) {
                        pixels[j][startX] = a;
                        pixels[j][startX+1] = a; //teoretycznie moze wyjsc po za zakres, trzeba iffy tu wszedzie dorobic
                        pixels[j][startX-1] = a;
                    }
                else
                    for (int j = startY; j < endY; j++)
                    {
                        pixels[j][startX] = a;
                        pixels[j][startX+1] = a;
                        pixels[j][startX-1] = a;
                    }
            }
            else if (startY == endY)
            {
                if (startX > endX)
                    for (int j = startX; j > endX; j--) {
                        pixels[startY][j] = a;
                        pixels[startY+1][j] = a;
                        pixels[startY-1][j] = a;
                    }
                else
                    for (int j = startX; j < endX; j++) {
                        pixels[startY][j] = a;
                        pixels[startY + 1][j] = a;
                        pixels[startY - 1][j] = a;
                    }
            }
        }

        return pixels;
    }

    public void setDescriptionLabels()
    {
        System.out.println(round(Double.parseDouble(interval.get(0).toString()),3));
        Double temp1 = round(Double.parseDouble(interval.get(0).toString()),3);
        Double temp2 = round(Double.parseDouble(interval.get(1).toString()),3);
        this.label1.setText(temp1.toString()+" - "+temp2.toString());
        temp1 = temp2;
        temp2 = round(Double.parseDouble(interval.get(2).toString()),3);
        this.label2.setText(temp1.toString()+" - "+temp2.toString());
        temp1 = temp2;
        temp2 = round(Double.parseDouble(interval.get(3).toString()),3);
        this.label3.setText(temp1.toString()+" - "+temp2.toString());
        temp1 = temp2;
        temp2 = round(Double.parseDouble(interval.get(4).toString()),3);
        this.label4.setText(temp1.toString()+" - "+temp2.toString());
        temp1 = temp2;
        temp2 = round(Double.parseDouble(interval.get(5).toString()),3);
        this.label5.setText(temp1.toString()+" - "+temp2.toString());
        temp1 = temp2;
        temp2 = round(Double.parseDouble(interval.get(6).toString()),3);
        this.label6.setText(temp1.toString()+" - "+temp2.toString());
        temp1 = temp2;
        temp2 = round(Double.parseDouble(interval.get(7).toString()),3);
        this.label7.setText(temp1.toString()+" - "+temp2.toString());
        temp1 = temp2;
        temp2 = round(Double.parseDouble(interval.get(8).toString()),3);
        this.label8.setText(temp1.toString()+" - "+temp2.toString());
        temp1 = temp2;
        temp2 = round(Double.parseDouble(interval.get(9).toString()),3);
        this.label9.setText(temp1.toString()+" - "+temp2.toString());
        temp1 = temp2;
        temp2 = round(Double.parseDouble(interval.get(10).toString()),3);
        this.label10.setText(temp1.toString()+" - "+temp2.toString());
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}

