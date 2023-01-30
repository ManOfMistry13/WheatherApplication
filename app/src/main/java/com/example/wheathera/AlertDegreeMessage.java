package com.example.wheathera;

public class AlertDegreeMessage implements  AlertMessages{
    private City city;

    public AlertDegreeMessage(City city) {
        this.city = city;
    }


    @Override
    public void degreeCheck() {
        final double minTemp = -5; //par défault on a mis des valeurs
        final double maxTemp = 45;
        try{
            if(city.getTemp() < minTemp || city.getTemp() > maxTemp) throw new Exception("");

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void windCheck() {
        try{
            if(city.getSpeed() < 100) throw new Exception("Rester vigilant");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /*
    @Override
    public void descriptionCheck() {
        try{
            if (city.getCityName()) throw new Exception("Alerte Sounami"));
            else{
                throw new Exception("Alerte Volcan");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/
}
