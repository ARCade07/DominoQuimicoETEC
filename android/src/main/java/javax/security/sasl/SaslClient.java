package javax.security.sasl;

/**
 * Android não inclui o pacote javax.security.sasl (presente no JDK padrão).
 * Esta interface reproduz a assinatura pública da javax.security.sasl.SaslClient do JDK,
 * permitindo que o driver síncrono do MongoDB (que implementa SCRAM-SHA-1/256 internamente
 * através de uma classe que "implements SaslClient") carregue normalmente em runtime no Android.
 */
public interface SaslClient {

    String getMechanismName();

    boolean hasInitialResponse();

    byte[] evaluateChallenge(byte[] challenge) throws SaslException;

    boolean isComplete();

    byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException;

    byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException;

    Object getNegotiatedProperty(String propName);

    void dispose() throws SaslException;
}
