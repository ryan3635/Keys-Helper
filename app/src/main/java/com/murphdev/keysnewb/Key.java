package com.murphdev.keysnewb;

public class Key {
    String keyId;
    boolean keyScale;
    boolean keyChord;

    public Key(String id, boolean keySelected, boolean keyIsChord){
        keyId = id;
        keyScale = keySelected;
        keyChord = keyIsChord;
    }

    public String idCheck() {
        return keyId;
    }

    public boolean scaleCheck() {
        return keyScale;
    }

    public boolean chordCheck() {
        return keyChord;
    }

    public void setScale() {
        keyScale = true;
    }

    public void setChord() {
        keyChord = true;
    }

    public void clearChord() {keyChord = false;}

}
