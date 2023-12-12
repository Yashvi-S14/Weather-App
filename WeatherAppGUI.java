import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {
    private JSONObject weatherData;
    public WeatherAppGUI(){
        super("Weather APP");
        repaint();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        getContentPane().setBackground(new Color(76, 123, 111));
        addGuiComponents();
    }
     private void addGuiComponents(){
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 351, 45);
        searchTextField.setFont(new Font("Roboto", Font.PLAIN, 24));
        add(searchTextField);
        searchTextField.setOpaque(true);

        JLabel weatherConditionImage = new JLabel((loadImage("src/pictures/cloud.png")));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        JLabel temperatureText = new JLabel(("10 C"));
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Roboto", Font.BOLD, 50));

        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        JLabel weatherConditionDescription = new JLabel("Cloudy");
        weatherConditionDescription.setBounds(0, 405, 450, 36);
        weatherConditionDescription.setFont(new Font("Roboto", Font.PLAIN, 30));
        weatherConditionDescription.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDescription);

        JLabel humidityImage = new JLabel(loadImage("src/pictures/GVCfLQf0.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        JLabel humidityText = new JLabel(("100%"));
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Roboto", Font.PLAIN, 16));
        add(humidityText);

        JLabel windspeedImage = new JLabel((loadImage("src/pictures/UBvC_Q_M.png")));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        JLabel windspeedText = new JLabel(("15km/h"));
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Roboto", Font.PLAIN, 16));
        add(windspeedText);

         JButton searchButton = new JButton(loadImage("src/pictures/bq7J8qAo.png"));
         searchButton.setCursor(Cursor.getPredefinedCursor((Cursor.HAND_CURSOR)));
         searchButton.setBounds(375, 15, 47, 45);
         add(searchButton);
         searchButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
               String userInput = searchTextField.getText();
               if(userInput.replaceAll("\\s", "").length()<=0){
                   return;
               }
               weatherData = WeatherAPI.getWeatherData(userInput);
               String weatherCondition = (String) weatherData.get("weather_condition");
               switch(weatherCondition){
                   case "Clear":
                       weatherConditionImage.setIcon(loadImage("src/pictures/sun.png"));
                       break;
                   case "Cloudy":
                       weatherConditionImage.setIcon(loadImage("src/pictures/cloud.png"));
                       break;
                   case "Rain":
                       weatherConditionImage.setIcon(loadImage("src/pictures/rain.png"));
                       break;
                   case "Snow":
                       weatherConditionImage.setIcon(loadImage("src/pictures/snow.png"));
                       break;
               }

               double temperature = (double) weatherData.get("temperature");
               temperatureText.setText((temperature + " C"));
               weatherConditionDescription.setText((weatherCondition));
               long humidity = (long) weatherData.get("humidity");
               humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

               double windspeed = (double) weatherData.get("windspeed");
               windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
             }
         });
     }

     private  ImageIcon loadImage(String resourcePath){
        try{
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("resource path not found");
        return null;
     }
}
