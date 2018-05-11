package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

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

    GraphicsContext gc;
    List<Line> tablica = new ArrayList<Line>();
    Line line = new Line();

    public int flaga;
    public int flaga_blockX;
    public int flaga_blockY;
    public double prevX[];
    public double prevY[];
    public double startX;
    public double startY;
    public double precision; // jak blisko od startowego punktu ma wyłapywać koniec lini i łaczyc z początkiem
    public int rysujemy;

    public final String color[] = {"#01024A", "#002374", "#004EB7", "#0075DD", "#40A96E", "#D6D402", "#FD9D18", "#FE2E39", "#F81D4E", "#FD777B"};



    public void initialize()
    {
        flaga = 0;
        flaga_blockX = 0;
        flaga_blockY = 0;
        prevX= new double[2];
        prevY = new double[2];
        startX=0;
        startY=0;
        precision = 40;
        rysujemy = 0;

        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
    }


    public void onActionSave(ActionEvent actionEvent) {
    }

    public void onActionStart(ActionEvent actionEvent) {
    }

    public void onActionReset(ActionEvent actionEvent) {
    }


    public void CanvasOnMouseClicked(MouseEvent mouseEvent) {

        if(rysujemy == 0 ) {
            gc.setStroke(new ColorPicker(Color.BLACK).getValue());
            line.setStartX(mouseEvent.getX());
            line.setStartY(mouseEvent.getY());  // uwaga bo mozna kliknąc ale tez przeciagnac myszke i moze byc rozne wartosci getX/Y sie popsuć moze.

            if(flaga==0) // rysowanie pierwszego punktu...
            {
                startX = mouseEvent.getX();
                startY = mouseEvent.getY();
                line.setEndX(mouseEvent.getX());
                line.setEndY(mouseEvent.getY());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(),line.getEndY());
                prevX[0]=mouseEvent.getX();
                prevX[1]=mouseEvent.getX();
                prevY[0]=mouseEvent.getY();
                prevY[1]=mouseEvent.getY();
                flaga=1;
            }

            double odleglosc_klikniecia_od_ostatniego_pktX =  abs(mouseEvent.getX()-line.getEndX());
            double odleglosc_klikniecia_od_ostatniego_pktY =  abs(mouseEvent.getY()-line.getEndY());

            double odleglosc_klikniecia_od_poczatkowego_pktX = abs(mouseEvent.getX() - startX);
            double odleglosc_klikniecia_od_poczatkowego_pktY = abs(mouseEvent.getY()-startY);

            if ( odleglosc_klikniecia_od_poczatkowego_pktX <= precision && odleglosc_klikniecia_od_poczatkowego_pktY <= precision)
            {
                double lastY = line.getEndY();
                double lastX = line.getEndX();
                if(lastY < startY)
                {
                    gc.strokeLine(lastX, lastY, startX, lastY);
                    tablica.add(new Line(lastX, lastY, startX, lastY));
                    gc.strokeLine(startX, lastY, startX, startY);
                    tablica.add(new Line(startX, lastY, startX, startY));
                    rysujemy=1;
                }

                else if(lastY > startY)
                {
                    if(prevX[0] == prevX[1] && prevY[0] < prevY[1])
                        return;

                    gc.strokeLine(lastX, lastY, lastX, startY);
                    tablica.add(new Line(lastX, lastY, lastX, startY));
                    gc.strokeLine(lastX, startY, startX, startY);
                    tablica.add(new Line(lastX, startY, startX, startY));
                    rysujemy=1;
                }
                else
                {
                    gc.strokeLine(lastX, lastY, startX, startY);
                    tablica.add(new Line(lastX, lastY, startX, startY));
                }
            }

            // u gory klikniecie blisko poczatku. ====================================

            else if( odleglosc_klikniecia_od_ostatniego_pktX >= odleglosc_klikniecia_od_ostatniego_pktY)
            {
                flaga_blockY = 0;
                if( prevX[0] <= line.getEndX() && line.getEndX() < mouseEvent.getX() )
                {

                    if(flaga_blockX != 2)
                    {
                        gc.strokeLine(line.getEndX(), line.getEndY(), mouseEvent.getX(), line.getEndY());
                        tablica.add(new Line(line.getEndX(), line.getEndY(), mouseEvent.getX(), line.getEndY()));
                        line.setEndX(mouseEvent.getX());
                        line.setEndY(line.getEndY());
                        flaga_blockX = 1;
                    }
                }
                else if ( mouseEvent.getX() < line.getEndX() && prevX[0] >= line.getEndX())
                {

                    if(flaga_blockX != 1)
                    {
                        gc.strokeLine(line.getEndX(), line.getEndY(), mouseEvent.getX(), line.getEndY());
                        tablica.add(new Line(line.getEndX(), line.getEndY(), mouseEvent.getX(), line.getEndY()));
                        line.setEndX(mouseEvent.getX());
                        line.setEndY(line.getEndY());

                        flaga_blockX = 2;
                    }
                }
            }
            else
            {
                flaga_blockX = 0;
                if( prevY[0] <= line.getEndY() && mouseEvent.getY() > line.getEndY())
                {
                    if(flaga_blockY != 2)
                    {
                        gc.strokeLine(line.getEndX(), line.getEndY(), line.getEndX(), mouseEvent.getY());
                        tablica.add(new Line(line.getEndX(), line.getEndY(), line.getEndX(), mouseEvent.getY()));
                        line.setEndX(line.getEndX());
                        line.setEndY( mouseEvent.getY());
                        flaga_blockY = 1;
                    }
                }

                else if ( mouseEvent.getY() < line.getEndY() && prevY[0] >= line.getEndY())
                {
                    if(flaga_blockY != 1)
                    {
                        gc.strokeLine(line.getEndX(), line.getEndY(), line.getEndX(), mouseEvent.getY());
                        tablica.add(new Line(line.getEndX(), line.getEndY(), line.getEndX(), mouseEvent.getY()));
                        line.setEndX(line.getEndX());
                        line.setEndY( mouseEvent.getY());
                        flaga_blockY = 2;
                    }
                }
            }

            prevX[0] = prevX[1];
            prevX[1] = line.getEndX();

            prevY[0] = prevY[1];
            prevY[1] = line.getEndY();

        }
        if(rysujemy==1)
        {
            //button hidden false
        }
    }

    public WritableImage getWritableImageFromCanvas(Canvas canvas){

        WritableImage writableImage=new  WritableImage(1080, 790);
        return canvas.snapshot(null,writableImage);
    }
}

