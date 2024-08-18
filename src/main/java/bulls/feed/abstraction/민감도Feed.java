package bulls.feed.abstraction;

public interface 민감도Feed {
    String getCode();
    int getTimeInteger(); // hhmmssxx
    double getDelta();
    double getGamma();
    double getTheta();
    double getRho();
    double getVega();
}
