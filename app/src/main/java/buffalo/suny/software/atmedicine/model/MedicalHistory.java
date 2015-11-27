package buffalo.suny.software.atmedicine.model;

public class MedicalHistory {
    private String symptom, lastHappen, date;

    public MedicalHistory() {

    }

    public MedicalHistory(String symptom, String lastHappen, String date) {
        this.symptom = symptom;
        this.lastHappen = lastHappen;
        this.date = date;
    }

    public String getSymptom() {
        return symptom;
    }

    public String getLastHappen() {
        return lastHappen;
    }

    public String getDate() {
        return date;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public void setLastHappen(String lastHappen) {
        this.lastHappen = lastHappen;
    }

    public void setDate(String date) {
        this.date = date;
    }
}