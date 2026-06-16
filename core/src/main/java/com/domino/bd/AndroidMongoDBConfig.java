package com.domino.bd;

/**
 * Configuração para fazer MongoDB funcionar no Android
 * Desabilita JNDI e força DNS direto com timeouts maiores
 */
public class AndroidMongoDBConfig {
    static {
        // Usar dnsjava para resolver DNS no Android
        System.setProperty("com.mongodb.dns_resolver", "dnsjava");

        // Aumentar timeouts para DNS SRV lookup (Android pode ser lento)
        System.setProperty("dnsjava.udp.size", "4096");
        System.setProperty("dnsjava.tcp.timeoutConnect", "10000");  // 10 segundos
        System.setProperty("dnsjava.query.timeout", "10000");       // 10 segundos

        // Configurações adicionais para estabilidade
        System.setProperty("sun.net.inetaddr.ttl", "300");          // Cache DNS por 5 minutos
    }

    public static void initialize() {
        System.out.println("✓ AndroidMongoDBConfig inicializado com DNS e timeouts configurados");
    }
}
