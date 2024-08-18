package bulls.staticData;

import bulls.feed.abstraction.민감도Feed;
import org.bson.Document;

public final class GreekData {
    public final double delta;
    public final double gamma;
    public final double rho;
    public final double vega;
    public final double theta;
    public final int time;

    public GreekData(int time, double delta, double gamma, double rho, double vega, double theta) {
        this.time = time;
        this.delta = delta;
        this.gamma = gamma;
        this.rho = rho;
        this.vega = vega;
        this.theta = theta;
    }

    public static GreekData of(Document d) {
        int time = d.getInteger("생성시각");
        double delta = d.getDouble("Delta");
        double gamma = d.getDouble("Gamma");
        double theta = d.getDouble("Theta");
        double rho = d.getDouble("Rho");
        double vega = d.getDouble("Vega");

        return new GreekData(time, delta, gamma, rho, vega, theta);
    }

    public static GreekData of(민감도Feed feed) {
        int time = feed.getTimeInteger();
        double delta = feed.getDelta();
        double gamma = feed.getGamma();
        double theta = feed.getTheta();
        double rho = feed.getRho();
        double vega = feed.getVega();

        return new GreekData(time, delta, gamma, rho, vega, theta);
    }
}

