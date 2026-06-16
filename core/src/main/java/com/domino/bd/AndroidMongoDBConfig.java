package com.domino.bd;

/**
 * Configuração para fazer MongoDB funcionar no Android
 * Desabilita JNDI e força DNS direto
 */
public class AndroidMongoDBConfig {
    static {
        // Desabilitar JNDI no MongoDB driver para Android
        System.setProperty("com.mongodb.dns_resolver", "dnsjava");
        System.setProperty("dnsjava.udp.size", "4096");
        System.setProperty("dnsjava.tcp.timeoutConnect", "5000");
    }

    public static void initialize() {
        // Apenas chama o static initializer
    }
}
