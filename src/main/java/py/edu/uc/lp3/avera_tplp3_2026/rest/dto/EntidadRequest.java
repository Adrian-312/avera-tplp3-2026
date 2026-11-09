package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

import java.util.List;

public class EntidadRequest {

    private String tipo;
    private String nombre;
    private Integer vida;
    private Double altura;
    private Integer hambre;
    private Boolean controlable;
    private Boolean hostil;
    private Boolean puedeEstarArmado;
    private Boolean domable;
    private Boolean pacifico;
    private Boolean crecen;
    private Integer potenciaExplosiva;
    private List<String> inventario;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getVida() {
        return vida;
    }

    public void setVida(Integer vida) {
        this.vida = vida;
    }

    public Double getAltura() {
        return altura;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }

    public Integer getHambre() {
        return hambre;
    }

    public void setHambre(Integer hambre) {
        this.hambre = hambre;
    }

    public Boolean getControlable() {
        return controlable;
    }

    public void setControlable(Boolean controlable) {
        this.controlable = controlable;
    }

    public Boolean getHostil() {
        return hostil;
    }

    public void setHostil(Boolean hostil) {
        this.hostil = hostil;
    }

    public Boolean getPuedeEstarArmado() {
        return puedeEstarArmado;
    }

    public void setPuedeEstarArmado(Boolean puedeEstarArmado) {
        this.puedeEstarArmado = puedeEstarArmado;
    }

    public Boolean getDomable() {
        return domable;
    }

    public void setDomable(Boolean domable) {
        this.domable = domable;
    }

    public Boolean getPacifico() {
        return pacifico;
    }

    public void setPacifico(Boolean pacifico) {
        this.pacifico = pacifico;
    }

    public Boolean getCrecen() {
        return crecen;
    }

    public void setCrecen(Boolean crecen) {
        this.crecen = crecen;
    }

    public Integer getPotenciaExplosiva() {
        return potenciaExplosiva;
    }

    public void setPotenciaExplosiva(Integer potenciaExplosiva) {
        this.potenciaExplosiva = potenciaExplosiva;
    }

    public List<String> getInventario() {
        return inventario;
    }

    public void setInventario(List<String> inventario) {
        this.inventario = inventario;
    }
}
