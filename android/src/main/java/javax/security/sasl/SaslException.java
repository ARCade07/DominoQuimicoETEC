package javax.security.sasl;

import java.io.IOException;

/**
 * Android não inclui o pacote javax.security.sasl (presente no JDK padrão).
 * O driver síncrono do MongoDB implementa o mecanismo SCRAM internamente e só
 * depende dessa interface/exceção existirem no classpath — não usa
 * javax.security.sasl.Sasl nem precisa de um provider JCA registrado.
 * Esta classe reproduz a assinatura pública da javax.security.sasl.SaslException do JDK.
 */
public class SaslException extends IOException {

    public SaslException() {
        super();
    }

    public SaslException(String detail) {
        super(detail);
    }

    public SaslException(String detail, Throwable ex) {
        super(detail, ex);
    }
}
