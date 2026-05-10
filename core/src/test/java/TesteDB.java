import com.domino.bd.ConnectionFactory;
import com.domino.dao.UsuarioDao;
import com.domino.modelos.Usuario;
import org.bson.types.ObjectId;

public class TesteDB {
    public static void main(String[] args) {
        ConnectionFactory connection = new ConnectionFactory();
        UsuarioDao operations = new UsuarioDao(connection);

        // Registrando um usuário
        Usuario usuarioRegisto = new Usuario();
        operations.registrarUsuario(usuarioRegisto);
    }
}
