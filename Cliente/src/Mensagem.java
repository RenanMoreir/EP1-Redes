import java.io.Serializable;
import java.net.Socket;

public class Mensagem implements Serializable {
    private String sala;
    private char tipoMensagem;
    private Object conteudo;

    public Mensagem(Socket socketRemetente, String sala, char tipoMensagem, Object conteudo){
        this.sala = sala;
        this.tipoMensagem = tipoMensagem;
        this.conteudo = conteudo;
    }

    public Mensagem(String sala, char tipoMensagem, Object conteudo){
        this.sala = sala;
        this.tipoMensagem = tipoMensagem;
        this.conteudo = conteudo;
    }

    public char getTipoMensagem() {
        return tipoMensagem;
    }

    public Object getConteudo() {
        return conteudo;
    }


    public String getSala() {
        return sala;
    }
}
