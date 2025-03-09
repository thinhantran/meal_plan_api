package fr.univartois.fixtures;

public abstract class Fixture {

    public abstract void generateRealData();

    public abstract void generateSingleFakeData();

    public void generateFakeData() {
        for (int i = 0; i < 10; i++) {
            generateSingleFakeData();
        }
    }
}
