package com.idn.ducibus.artefact;

public class ArtefactDescriptor {
    private String artefactId;
    private String museumId;

    public ArtefactDescriptor(String museumId, String artefactId){
        this.artefactId = artefactId;
        this.museumId = museumId;
    }

    public String getArtefactId() {
        return artefactId;
    }

    public void setArtefactId(String artefactId) {
        this.artefactId = artefactId;
    }

    public String getMuseumId() {
        return museumId;
    }

    public void setMuseumId(String museumId) {
        this.museumId = museumId;
    }
}
